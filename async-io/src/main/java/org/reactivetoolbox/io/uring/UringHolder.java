package org.reactivetoolbox.io.uring;

import org.reactivetoolbox.core.lang.Tuple;
import org.reactivetoolbox.core.lang.Tuple.Tuple2;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.Bitmask;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.FileDescriptor;
import org.reactivetoolbox.io.async.SizeT;
import org.reactivetoolbox.io.async.net.*;
import org.reactivetoolbox.io.raw.RawMemory;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapSocketAddress;
import org.reactivetoolbox.io.uring.struct.raw.CompletionQueueEntry;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;

import java.util.EnumSet;
import java.util.function.Consumer;

public class UringHolder implements AutoCloseable {
    public static final int DEFAULT_QUEUE_SIZE = 128; //128 looks like a "sweet spot" for my HW/kernel
    public static final int DEFAULT_QUEUE_FLAGS = 0;

    private static final int ENTRY_SIZE = 8;    // each entry is a 64-bit pointer

    private final long ringBase;
    private final int numEntries;

    private final int completionEntries;
    private final long completionBuffer;

    private final CompletionQueueEntry cqEntry;
    private final SubmitQueueEntry sqEntry;

    private boolean closed = false;

    private UringHolder(final int numEntries, final long ringBase) {
        this.numEntries = numEntries;
        completionEntries = numEntries * 2;
        this.ringBase = ringBase;
        completionBuffer = RawMemory.allocate(completionEntries * ENTRY_SIZE);
        cqEntry = CompletionQueueEntry.at(0);
        sqEntry = SubmitQueueEntry.at(0);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        Uring.close(ringBase);
        RawMemory.dispose(completionBuffer);
        RawMemory.dispose(ringBase);
        closed = true;
    }

    public UringHolder processCompletions(final Consumer<CompletionQueueEntry> consumer) {
        long ready = 0;

        try {
            ready = Uring.peekCQ(ringBase, completionBuffer, completionEntries);
            for (long i = 0, address = completionBuffer; i < ready; i++, address += ENTRY_SIZE) {
                cqEntry.reposition(RawMemory.getLong(address));
                consumer.accept(cqEntry);
            }
        } finally {
            if (ready > 0) {
                Uring.advanceCQ(ringBase, ready);
            }
        }
        return this;
    }

    public int availableSQSpace() {
        return (int) Uring.spaceLeft(ringBase);
    }

    //TODO: error checking
    public UringHolder submit(final Consumer<SubmitQueueEntry> submitter) {
        sqEntry.reposition(Uring.nextSQEntry(ringBase));
        submitter.accept(sqEntry.clear());
        return this;
    }

    public UringHolder notifySubmission() {
        Uring.submitAndWait(ringBase, 0);
        return this;
    }

    //TODO: add support for ring open flags
    public static Result<UringHolder> create(final int requestedEntries) {
        final long ringBase = RawMemory.allocate(Uring.SIZE);
        final int numEntries = calculateNumEntries(requestedEntries);
        final int rc = Uring.init(numEntries, ringBase, DEFAULT_QUEUE_FLAGS);

        if (rc != 0) {
            RawMemory.dispose(ringBase);
            return Result.fail(NativeError.nativeError(rc).asFailure());
        }

        return Result.ok(new UringHolder(numEntries, ringBase));
    }

    private static int calculateNumEntries(final int size) {
        if (size <= 0) {
            return DEFAULT_QUEUE_SIZE;
        }
        //Round up to nearest power of two
        return 1 << (32 - Integer.numberOfLeadingZeros(size - 1));
    }

    public int numEntries() {
        return numEntries;
    }

    public static Result<FileDescriptor> socket(final AddressFamily addressFamily,
                                                final SocketType socketType,
                                                final EnumSet<SocketFlag> openFlags,
                                                final EnumSet<SocketOption> options) {
        return NativeError.result(Uring.socket(addressFamily.code(),
                                               socketType.code() | Bitmask.combine(openFlags),
                                               Bitmask.combine(options)),
                                  FileDescriptor::socket);
    }

    public static Result<ServerConnector> server(final SocketAddress<?> socketAddress,
                                                 final SocketType socketType,
                                                 final EnumSet<SocketFlag> openFlags,
                                                 final EnumSet<SocketOption> options,
                                                 final SizeT queueDepth) {
        return socket(socketAddress.family(), socketType, openFlags, options)
                .flatMap(fileDescriptor -> configureForListen(fileDescriptor, socketAddress, (int) queueDepth.value()))
                .map(tuple -> tuple.map(ServerConnector::connector));
    }

    private static Result<Tuple2<FileDescriptor, SocketAddress<?>>> configureForListen(final FileDescriptor fileDescriptor,
                                                                                       final SocketAddress<?> socketAddress,
                                                                                       final int queueDepth) {
        int rc;
        if (socketAddress instanceof SocketAddressIn socketAddressIn) {
            var addressIn = OffHeapSocketAddress.addressIn(socketAddressIn);

            rc = Uring.prepareForListen(fileDescriptor.descriptor(),
                                        addressIn.address(),
                                        addressIn.size(),
                                        queueDepth);

        } else if (socketAddress instanceof SocketAddressIn6 socketAddressIn6) {
            var addressIn6 = OffHeapSocketAddress.addressIn6(socketAddressIn6);

            rc = Uring.prepareForListen(fileDescriptor.descriptor(),
                                        addressIn6.address(),
                                        addressIn6.size(),
                                        queueDepth);
        } else {
            return Result.fail(NativeError.EPFNOSUPPORT.asFailure());
        }

        return rc == 0 ? Result.ok(Tuple.tuple(fileDescriptor, socketAddress)) : NativeError.failure(rc);
    }
}

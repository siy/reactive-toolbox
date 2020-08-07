package org.reactivetoolbox.io.uring;

import org.reactivetoolbox.core.lang.Tuple.Tuple3;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.Bitmask;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.impl.PromiseImpl;
import org.reactivetoolbox.io.async.net.AddressFamily;
import org.reactivetoolbox.io.async.net.SocketAddress;
import org.reactivetoolbox.io.async.net.SocketAddressIn;
import org.reactivetoolbox.io.async.net.SocketAddressIn6;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketOption;
import org.reactivetoolbox.io.async.net.SocketType;
import org.reactivetoolbox.io.async.net.context.ServerContext;
import org.reactivetoolbox.io.raw.RawMemory;
import org.reactivetoolbox.io.uring.struct.raw.CompletionQueueEntry;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.ObjectHeap;

import java.util.Deque;
import java.util.EnumSet;
import java.util.function.Consumer;

import static org.reactivetoolbox.core.lang.Tuple.tuple;
import static org.reactivetoolbox.core.lang.functional.Result.fail;
import static org.reactivetoolbox.io.NativeError.ENOTSOCK;
import static org.reactivetoolbox.io.NativeError.EPFNOSUPPORT;
import static org.reactivetoolbox.io.NativeError.result;
import static org.reactivetoolbox.io.uring.struct.offheap.OffHeapSocketAddress.addressIn;
import static org.reactivetoolbox.io.uring.struct.offheap.OffHeapSocketAddress.addressIn6;

public class UringHolder implements AutoCloseable {
    //    public static final int DEFAULT_QUEUE_SIZE = 4; // 1 - ~39-40K, 10 - ~126-132K
//    public static final int DEFAULT_QUEUE_SIZE = 8; // 1 - ~44-45K, 10 - ~137K
//    public static final int DEFAULT_QUEUE_SIZE = 16; // 1 - ~39-40K, 10 - ~129-134K, 100 - ~250-253K
//    public static final int DEFAULT_QUEUE_SIZE = 32; // 1 - ~37-39K, 10 - ~121-127K, 100 - ~231-236K
//    public static final int DEFAULT_QUEUE_SIZE = 64; // 1 - ~37-40K, 10 - ~120-129K, 100 - ~237K
//    public static final int DEFAULT_QUEUE_SIZE = 128; // 1 - ~37-38K, 10 - ~123-134K, 100 - ~233K
//    public static final int DEFAULT_QUEUE_SIZE = 256; // 1 - ~37-38K, 10 - ~118-126K, 100 - ~231K
    public static final int DEFAULT_QUEUE_SIZE = 1024; // 1 - , 10 - , 100 -

    private static final int ENTRY_SIZE = 8;    // each entry is a 64-bit pointer

    private final long ringBase;
    private final int submissionEntries;
    private final long submissionBuffer;

    private final int completionEntries;
    private final long completionBuffer;

    private final CompletionQueueEntry cqEntry;
    private final SubmitQueueEntry sqEntry;

    private boolean closed = false;

    private UringHolder(final int numEntries, final long ringBase) {
        submissionEntries = numEntries;
        completionEntries = numEntries * 2;
        this.ringBase = ringBase;
        submissionBuffer = RawMemory.allocate(submissionEntries * ENTRY_SIZE);
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
        RawMemory.dispose(submissionBuffer);
        RawMemory.dispose(completionBuffer);
        RawMemory.dispose(ringBase);
        closed = true;
    }

    public void processCompletions(final ObjectHeap<Consumer<DetachedCQEntry>> pendingCompletions) {
        final long ready = Uring.peekCQ(ringBase, completionBuffer, completionEntries);

        for (long i = 0, address = completionBuffer; i < ready; i++, address += ENTRY_SIZE) {
            cqEntry.reposition(RawMemory.getLong(address));
//            pendingCompletions.releaseUnsafe((int) cqEntry.userData())
//                              .accept(cqEntry.extract());
            final var entry = cqEntry.extract();
            final var completion = pendingCompletions.releaseUnsafe((int) cqEntry.userData());
            PromiseImpl.SingletonHolder.scheduler().submit(() -> completion.accept(entry));
        }

        if (ready > 0) {
            Uring.advanceCQ(ringBase, ready);
        }
    }

    public void processSubmissions(final Deque<Consumer<SubmitQueueEntry>> queue) {
        final int available = Uring.peekSQEntries(ringBase,
                                                  submissionBuffer,
                                                  Math.min(queue.size(), submissionEntries));

        for (long i = 0, address = submissionBuffer; i < available; i++, address += ENTRY_SIZE) {
            final long sqAddress = RawMemory.getLong(address);
            sqEntry.reposition(sqAddress);
            queue.removeFirst().accept(sqEntry.clear());
        }

        Uring.submitAndWait(ringBase, 0);
    }

    public static Result<UringHolder> create(final int requestedEntries, final EnumSet<UringSetupFlags> openFlags) {
        final long ringBase = RawMemory.allocate(Uring.SIZE);
        final int numEntries = calculateNumEntries(requestedEntries);
        final int rc = Uring.init(numEntries, ringBase, Bitmask.combine(openFlags));

        if (rc != 0) {
            RawMemory.dispose(ringBase);
            return fail(NativeError.fromCode(rc).asFailure());
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
        return submissionEntries;
    }

    public static Result<FileDescriptor> socket(final AddressFamily addressFamily,
                                                final SocketType socketType,
                                                final EnumSet<SocketFlag> openFlags,
                                                final EnumSet<SocketOption> options) {
        return result(Uring.socket(addressFamily.familyId(),
                                   socketType.code() | Bitmask.combine(openFlags),
                                   Bitmask.combine(options)),
                      (addressFamily == AddressFamily.INET6) ? FileDescriptor::socket6
                                                             : FileDescriptor::socket);
    }

    public static Result<ServerContext<?>> server(final SocketAddress<?> socketAddress,
                                                  final SocketType socketType,
                                                  final EnumSet<SocketFlag> openFlags,
                                                  final EnumSet<SocketOption> options,
                                                  final SizeT queueDepth) {
        return socket(socketAddress.family(), socketType, openFlags, options)
                .flatMap(fileDescriptor -> configureForListen(fileDescriptor, socketAddress, (int) queueDepth.value()))
                .map(tuple -> tuple.map(ServerContext::connector));
    }

    private static Result<Tuple3<FileDescriptor, SocketAddress<?>, Integer>> configureForListen(final FileDescriptor fileDescriptor,
                                                                                                final SocketAddress<?> socketAddress,
                                                                                                final int queueDepth) {

        if (!fileDescriptor.isSocket()) {
            return fail(ENOTSOCK.asFailure());
        }

        final var rc = switch (socketAddress.family()) {
            case INET -> configureForInet(fileDescriptor, socketAddress, queueDepth);
            case INET6 -> configureForInet6(fileDescriptor, socketAddress, queueDepth);
            default -> EPFNOSUPPORT.typeCode();
        };

        return result(rc, $ -> tuple(fileDescriptor, socketAddress, queueDepth));
    }

    private static int configureForInet6(final FileDescriptor fileDescriptor, final SocketAddress<?> socketAddress, final int queueDepth) {
        if (!fileDescriptor.isSocket6() || socketAddress.family() != AddressFamily.INET6) {
            return -EPFNOSUPPORT.typeCode();
        }

        if (socketAddress instanceof SocketAddressIn6 socketAddressIn6) {
            final var addressIn6 = addressIn6(socketAddressIn6);

            return Uring.prepareForListen(fileDescriptor.descriptor(),
                                          addressIn6.sockAddrPtr(),
                                          addressIn6.sockAddrSize(),
                                          queueDepth);
        }

        return -EPFNOSUPPORT.typeCode();
    }

    private static int configureForInet(final FileDescriptor fileDescriptor, final SocketAddress<?> socketAddress, final int queueDepth) {
        if (fileDescriptor.isSocket6() || socketAddress.family() == AddressFamily.INET6) {
            return -EPFNOSUPPORT.typeCode();
        }

        if (socketAddress instanceof SocketAddressIn socketAddressIn) {
            final var addressIn = addressIn(socketAddressIn);

            return Uring.prepareForListen(fileDescriptor.descriptor(),
                                          addressIn.sockAddrPtr(),
                                          addressIn.sockAddrSize(),
                                          queueDepth);

        }

        return -EPFNOSUPPORT.typeCode();
    }
}

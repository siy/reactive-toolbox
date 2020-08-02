package org.reactivetoolbox.io;

import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.common.OffsetT;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.file.FilePermission;
import org.reactivetoolbox.io.async.file.OpenFlags;
import org.reactivetoolbox.io.async.file.SpliceDescriptor;
import org.reactivetoolbox.io.async.file.stat.FileStat;
import org.reactivetoolbox.io.async.file.stat.StatFlag;
import org.reactivetoolbox.io.async.file.stat.StatMask;
import org.reactivetoolbox.io.async.net.AddressFamily;
import org.reactivetoolbox.io.async.net.ClientConnection;
import org.reactivetoolbox.io.async.net.SocketAddress;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketOption;
import org.reactivetoolbox.io.async.net.SocketType;
import org.reactivetoolbox.io.async.net.context.ServerContext;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;
import org.reactivetoolbox.io.scheduler.Timeout;
import org.reactivetoolbox.io.uring.AsyncOperation;
import org.reactivetoolbox.io.uring.UringHolder;
import org.reactivetoolbox.io.uring.UringSetupFlags;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapCString;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapFileStat;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapIoVector;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapSocketAddress;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapTimeSpec;
import org.reactivetoolbox.io.uring.struct.raw.CompletionQueueEntry;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.ObjectHeap;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Deque;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.function.Consumer;

import static org.reactivetoolbox.core.lang.functional.Result.flatten;
import static org.reactivetoolbox.core.lang.functional.Result.ok;
import static org.reactivetoolbox.core.lang.functional.Unit.unit;
import static org.reactivetoolbox.io.async.common.SizeT.sizeT;
import static org.reactivetoolbox.io.scheduler.Timeout.timeout;
import static org.reactivetoolbox.io.uring.UringHolder.DEFAULT_QUEUE_SIZE;
import static org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntryFlags.IOSQE_IO_LINK;

/**
 * Input/Output Proactor.
 * <p>
 * This class provides implementation of {@link Submitter} interface using <a href="https://en.wikipedia.org/wiki/Proactor_pattern">Proactor</a> pattern.
 * <p>
 * WARNING: this class is part of low level internal classes. It's written with specific assumptions in mind and most likely will be hard or inconvenient to use in other
 * environment. In particular class is designed to be used by single thread at a time and does not perform synchronization at all.
 */
public class Proactor implements Submitter {
    private static final Result<Unit> UNIT_RESULT = ok(unit());
    private static final int AT_FDCWD = -100; // Special value used to indicate the openat/statx functions should use the current working directory.

    private final UringHolder uringHolder;
    private final ObjectHeap<CompletionHandler> pendingCompletions;
    private final Deque<Consumer<SubmitQueueEntry>> queue = new LinkedList<>();

    private Proactor(final UringHolder uringHolder) {
        this.uringHolder = uringHolder;
        pendingCompletions = ObjectHeap.objectHeap(uringHolder.numEntries());
    }

    public static Proactor proactor() {
        return proactor(DEFAULT_QUEUE_SIZE, UringSetupFlags.defaultFlags());
    }

    public static Proactor proactor(final int queueSize) {
        return proactor(queueSize, UringSetupFlags.defaultFlags());
    }

    static Proactor proactor(final int queueSize, final EnumSet<UringSetupFlags> openFlags) {
        return new Proactor(UringHolder.create(queueSize, openFlags)
                                       .fold(f -> {
                                                 throw new IllegalStateException("Unable to initialize IO_URING interface");
                                             },
                                             h -> h));
    }

    @FunctionalInterface
    private interface CompletionHandler {
        void handleCompletion(final CompletionQueueEntry entry);
    }

    @Override
    public void close() {
        uringHolder.close();
    }

    /**
     * This method should be periodically called to perform submissions and handle completions to/from IO_URING.
     */
    public Proactor processIO() {
        if (!queue.isEmpty()) {
            //TODO: how to conveniently handle submissions with timeouts (i.e. linked pairs of submissions)?
            //      and should we? (it is possible that kernel will handle subsequent timeout even if it delivered later).
            processSubmissions();
        }

        if (pendingCompletions.count() > 0) {
            uringHolder.processCompletions(this::handleCompletions);
        }
        return this;
    }

    private void processSubmissions() {
        int count = uringHolder.availableSQSpace();

        while (count > 0 && !queue.isEmpty()) {
            uringHolder.submit(queue.removeFirst());
            count--;
        }

        uringHolder.notifySubmission();
    }

    private void handleCompletions(final CompletionQueueEntry entry) {
        pendingCompletions.release((int) entry.userData())
                          .whenPresent(handler -> handler.handleCompletion(entry));
    }

    @Override
    public Promise<Unit> nop(final Promise<Unit> completion) {

        final int key = pendingCompletions.allocKey((entry -> completion.syncResolve(UNIT_RESULT)));

        queue.add(sqe -> sqe.userData(key)
                            .opcode(AsyncOperation.IORING_OP_NOP.opcode()));
        return completion;
    }

    @Override
    public Promise<Duration> delay(final Promise<Duration> completion, final Timeout timeout) {
        final var timeSpec = OffHeapTimeSpec.forTimeout(timeout);
        final var startNanos = System.nanoTime();
        final var key = pendingCompletions.allocKey(entry -> {
            timeSpec.dispose();

            final var totalNanos = System.nanoTime() - startNanos;

            if (Math.abs(entry.res()) != NativeError.ETIME.typeCode()) {
                completion.syncFail(NativeError.fromCode(entry.res())
                                               .asFailure());
            } else {
                completion.syncOk(timeout(totalNanos)
                                          .nanos()
                                          .asDuration());
            }
        });

        queue.add(sqe -> sqe.userData(key)
                            .opcode(AsyncOperation.IORING_OP_TIMEOUT.opcode())
                            .addr(timeSpec.address())
                            .fd(-1)
                            .len(1)
                            .off(1));
        return completion;
    }

    @Override
    public Promise<Unit> closeFileDescriptor(final Promise<Unit> completion, final FileDescriptor fd, final Option<Timeout> timeout) {
        final var key = pendingCompletions.allocKey(entry -> completion.syncResolve(entry.result(v -> unit())));

        queue.add(sqe -> sqe.userData(key)
                            .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                            .opcode(AsyncOperation.IORING_OP_CLOSE.opcode())
                            .fd(fd.descriptor()));

        timeout.whenPresent(this::appendTimeout);

        return completion;
    }

    @Override
    public Promise<SizeT> read(final Promise<SizeT> completion,
                               final FileDescriptor fd,
                               final OffHeapBuffer buffer,
                               final OffsetT offset,
                               final Option<Timeout> timeout) {
        final var key = pendingCompletions.allocKey(entry -> completion.syncResolve(entry.result(bytesRead -> sizeT(buffer.used(bytesRead).used()))));

        queue.add(sqe -> sqe.userData(key)
                            .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                            .opcode(AsyncOperation.IORING_OP_READ.opcode())
                            .fd(fd.descriptor())
                            .addr(buffer.address())
                            .len(buffer.size())
                            .off(offset.value()));

        timeout.whenPresent(this::appendTimeout);
        return completion;
    }

    @Override
    public Promise<SizeT> write(final Promise<SizeT> completion,
                                final FileDescriptor fd,
                                final OffHeapBuffer buffer,
                                final OffsetT offset,
                                final Option<Timeout> timeout) {
        final var key = pendingCompletions.allocKey(entry -> completion.syncResolve(entry.result(SizeT::sizeT)));

        queue.add(sqe -> sqe.userData(key)
                            .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                            .opcode(AsyncOperation.IORING_OP_WRITE.opcode())
                            .fd(fd.descriptor())
                            .addr(buffer.address())
                            .len(buffer.used())
                            .off(offset.value()));

        timeout.whenPresent(this::appendTimeout);
        return completion;
    }

    @Override
    public Promise<SizeT> splice(final Promise<SizeT> completion, final SpliceDescriptor descriptor, final Option<Timeout> timeout) {

        final var key = pendingCompletions.allocKey(entry -> completion.syncResolve(entry.result(SizeT::sizeT)));

        queue.add(sqe -> sqe.userData(key)
                            .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                            .opcode(AsyncOperation.IORING_OP_SPLICE.opcode())
                            .fd(descriptor.toDescriptor().descriptor())
                            .len((int) descriptor.bytesToCopy().value())
                            .off(descriptor.toOffset().value())
                            .spliceFdIn(descriptor.fromDescriptor().descriptor())
                            .spliceOffIn(descriptor.fromOffset().value())
                            .spliceFlags(Bitmask.combine(descriptor.flags())));

        timeout.whenPresent(this::appendTimeout);
        return completion;
    }

    @Override
    public Promise<FileDescriptor> open(final Promise<FileDescriptor> completion,
                                        final Path path,
                                        final EnumSet<OpenFlags> flags,
                                        final EnumSet<FilePermission> mode,
                                        final Option<Timeout> timeout) {
        final var rawPath = OffHeapCString.cstring(path.toString());
        final var key = pendingCompletions.allocKey(entry -> {
            completion.syncResolve(entry.result(FileDescriptor::file));
            rawPath.dispose();
        });

        queue.add(sqe -> sqe.userData(key)
                            .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                            .opcode(AsyncOperation.IORING_OP_OPENAT.opcode())
                            .fd(AT_FDCWD)
                            .addr(rawPath.address())
                            .len(Bitmask.combine(mode))
                            .openFlags(Bitmask.combine(flags)));

        timeout.whenPresent(this::appendTimeout);
        return completion;
    }

    @Override
    public Promise<FileDescriptor> socket(final Promise<FileDescriptor> completion,
                                          final AddressFamily addressFamily,
                                          final SocketType socketType,
                                          final EnumSet<SocketFlag> openFlags,
                                          final EnumSet<SocketOption> options) {
        nop(Promise.promise())
                .thenDo(() -> completion.resolve(UringHolder.socket(addressFamily,
                                                                    socketType,
                                                                    openFlags,
                                                                    options)));
        return completion;
    }

    @Override
    public Promise<ServerContext<?>> server(final Promise<ServerContext<?>> completion,
                                            final SocketAddress<?> socketAddress,
                                            final SocketType socketType,
                                            final EnumSet<SocketFlag> openFlags,
                                            final SizeT queueDepth,
                                            final EnumSet<SocketOption> options) {
        nop(Promise.promise())
                .thenDo(() -> completion.resolve(UringHolder.server(socketAddress,
                                                                    socketType,
                                                                    openFlags,
                                                                    options,
                                                                    queueDepth)));
        return completion;
    }

    @Override
    public Promise<ClientConnection<?>> accept(final Promise<ClientConnection<?>> completion,
                                               final FileDescriptor socket,
                                               final EnumSet<SocketFlag> flags) {
        //TODO: add support for v6
        final var clientAddress = OffHeapSocketAddress.addressIn();
        final var key = pendingCompletions.allocKey(entry -> completion.syncResolve(entry.result(FileDescriptor::socket)
                                                                                         .flatMap(fd -> flatten(ok(fd), clientAddress.extract())
                                                                                                 .map(tuple -> tuple.map(ClientConnection::connectionIn))))
                                                                       .thenDo(clientAddress::dispose));

        queue.add(sqe -> sqe.userData(key)
                            .opcode(AsyncOperation.IORING_OP_ACCEPT.opcode())
                            .fd(socket.descriptor())
                            .addr(clientAddress.sockAddrPtr())
                            .off(clientAddress.sizePtr())
                            .acceptFlags(Bitmask.combine(flags)));
        return completion;
    }

    @Override
    public Promise<FileDescriptor> connect(final Promise<FileDescriptor> completion,
                                           final FileDescriptor socket,
                                           final SocketAddress<?> address,
                                           final Option<Timeout> timeout) {
        final var clientAddress = OffHeapSocketAddress.unsafeSocketAddress(address);

        if (clientAddress == null) {
            return completion.syncFail(NativeError.EPFNOSUPPORT.asFailure());
        }

        final var key = pendingCompletions.allocKey((entry -> {
            completion.syncResolve(entry.result(v -> socket));
            clientAddress.dispose();
        }));

        queue.add(sqe -> sqe.userData(key)
                            .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                            .opcode(AsyncOperation.IORING_OP_CONNECT.opcode())
                            .fd(socket.descriptor())
                            .addr(clientAddress.sockAddrPtr())
                            .off(clientAddress.sockAddrSize()));

        timeout.whenPresent(this::appendTimeout);
        return completion;
    }

    @Override
    public Promise<FileStat> stat(final Promise<FileStat> completion,
                                  final Path path,
                                  final EnumSet<StatFlag> flags,
                                  final EnumSet<StatMask> mask) {
        //Reset EMPTY_PATH and force use the path.
        final var statFlags = Bitmask.combine(flags) & ~StatFlag.EMPTY_PATH.mask();
        final var statMask = Bitmask.combine(mask);
        final var fileStat = OffHeapFileStat.fileStat();
        final var rawPath = OffHeapCString.cstring(path.toString());
        final var key = pendingCompletions.allocKey(entry -> completion.syncResolve(entry.result($ -> fileStat.extract()))
                                                                       .thenDo(fileStat::dispose)
                                                                       .thenDo(rawPath::dispose));

        queue.add(sqe -> sqe.userData(key)
                            .fd(AT_FDCWD)
                            .addr(rawPath.address())
                            .len(statMask)
                            .off(fileStat.address())
                            .statxFlags(statFlags)
                            .opcode(AsyncOperation.IORING_OP_STATX.opcode()));
        return completion;
    }

    @Override
    public Promise<FileStat> stat(final Promise<FileStat> completion,
                                  final FileDescriptor fd,
                                  final EnumSet<StatFlag> flags,
                                  final EnumSet<StatMask> mask) {
        //Set EMPTY_PATH and force use of file descriptor.
        final var statFlags = Bitmask.combine(flags) | StatFlag.EMPTY_PATH.mask();
        final var statMask = Bitmask.combine(mask);
        final var fileStat = OffHeapFileStat.fileStat();
        final var rawPath = OffHeapCString.cstring("");
        final var key = pendingCompletions.allocKey(entry -> completion.syncResolve(entry.result($ -> fileStat.extract()))
                                                                       .thenDo(fileStat::dispose)
                                                                       .thenDo(rawPath::dispose));

        queue.add(sqe -> sqe.userData(key)
                            .fd(fd.descriptor())
                            .addr(rawPath.address())
                            .len(statMask)
                            .off(fileStat.address())
                            .statxFlags(statFlags)
                            .opcode(AsyncOperation.IORING_OP_STATX.opcode()));
        return completion;
    }

    @Override
    public Promise<SizeT> readVector(final Promise<SizeT> completion,
                                     final FileDescriptor fileDescriptor,
                                     final OffsetT offset,
                                     final Option<Timeout> timeout,
                                     final OffHeapBuffer... buffers) {
        final var ioVector = OffHeapIoVector.withBuffers(buffers);
        final var key = pendingCompletions.allocKey(entry -> completion.syncResolve(entry.result(SizeT::sizeT))
                                                                       .thenDo(ioVector::dispose));

        queue.add(sqe -> sqe.userData(key)
                            .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                            .opcode(AsyncOperation.IORING_OP_READV.opcode())
                            .fd(fileDescriptor.descriptor())
                            .addr(ioVector.address())
                            .len(ioVector.length())
                            .off(offset.value()));

        timeout.whenPresent(this::appendTimeout);
        return completion;
    }

    @Override
    public Promise<SizeT> writeVector(final Promise<SizeT> completion,
                                      final FileDescriptor fileDescriptor,
                                      final OffsetT offset,
                                      final Option<Timeout> timeout,
                                      final OffHeapBuffer... buffers) {

        final var ioVector = OffHeapIoVector.withBuffers(buffers);
        final var key = pendingCompletions.allocKey(entry -> completion.syncResolve(entry.result(SizeT::sizeT))
                                                                       .thenDo(ioVector::dispose));

        queue.add(sqe -> sqe.userData(key)
                            .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                            .opcode(AsyncOperation.IORING_OP_WRITEV.opcode())
                            .fd(fileDescriptor.descriptor())
                            .addr(ioVector.address())
                            .len(ioVector.length())
                            .off(offset.value()));

        timeout.whenPresent(this::appendTimeout);
        return completion;
    }

    private void appendTimeout(final Timeout t) {
        final var timeSpec = OffHeapTimeSpec.forTimeout(t);
        final var key = pendingCompletions.allocKey(entry -> timeSpec.dispose());

        queue.add(sqe -> sqe.userData(key)
                            .fd(-1)
                            .addr(timeSpec.address())
                            .len(1)
                            .opcode(AsyncOperation.IORING_OP_LINK_TIMEOUT.opcode()));
    }
}

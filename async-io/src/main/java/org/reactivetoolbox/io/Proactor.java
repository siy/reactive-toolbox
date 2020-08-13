package org.reactivetoolbox.io;

import org.reactivetoolbox.core.lang.functional.Failure;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.lang.functional.Unit;
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
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.ObjectHeap;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.function.Consumer;

import static org.reactivetoolbox.core.lang.functional.Result.ok;
import static org.reactivetoolbox.core.lang.functional.Unit.unit;
import static org.reactivetoolbox.io.async.net.ClientConnection.connectionIn;
import static org.reactivetoolbox.io.scheduler.Timeout.timeout;
import static org.reactivetoolbox.io.uring.UringHolder.DEFAULT_QUEUE_SIZE;
import static org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntryFlags.IOSQE_IO_LINK;

/**
 * Input/Output Proactor.
 * <p>
 * This class provides implementation of {@link Submitter} interface using <a href="https://en.wikipedia.org/wiki/Proactor_pattern">Proactor</a> pattern.
 * <p>
 * <pre>
 * WARNING!
 * This class is part of low level internal classes. It's written with specific assumptions in mind and most likely will be hard
 * or inconvenient to use in other environment. In particular class is designed to be used by single thread at a time and does not perform
 * synchronization at all.
 * </pre>
 */
//TODO: make API more consistent - add timeout to all calls
public class Proactor implements Submitter {
    private static final Result<Unit> UNIT_RESULT = ok(unit());
    private static final int AT_FDCWD = -100; // Special value used to indicate the openat/statx functions should use the current working directory.
    private static final Failure EOF = NativeError.ENODATA.asFailure();
    private static final Result<SizeT> EOF_RESULT = Result.fail(EOF);
    private static final int RESULT_SIZET_POOL_SIZE = 65536;
    @SuppressWarnings("rawtypes")
    private static final Result[] RESULT_SIZET_POOL;

    static {
        RESULT_SIZET_POOL = new Result[RESULT_SIZET_POOL_SIZE + 1];

        for (int i = 0; i < RESULT_SIZET_POOL.length; i++) {
            RESULT_SIZET_POOL[i] = Result.ok(new SizeT(i));
        }
    }

    private final UringHolder uringHolder;
    private final ObjectHeap<CompletionHandler> pendingCompletions;
    private final Deque<Consumer<SubmitQueueEntry>> queue = new ArrayDeque<>();

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

    static Proactor proactor(final int queueSize, final Set<UringSetupFlags> openFlags) {
        return new Proactor(UringHolder.create(queueSize, openFlags)
                                       .fold(f -> {
                                                 throw new IllegalStateException("Unable to initialize IO_URING interface");
                                             },
                                             h -> h));
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
            uringHolder.processSubmissions(queue);
        }

        if (pendingCompletions.count() > 0) {
            uringHolder.processCompletions(pendingCompletions);
        }
        return this;
    }

    @Override
    public void nop(final Consumer<Result<Unit>> completion) {
        final int key = pendingCompletions.allocKey((res, flags) -> completion.accept(UNIT_RESULT));

        queue.add(sqe -> sqe.userData(key)
                            .opcode(AsyncOperation.IORING_OP_NOP.opcode()));
    }

    @Override
    public void delay(final Consumer<Result<Duration>> completion, final Timeout timeout) {
        final var timeSpec = OffHeapTimeSpec.forTimeout(timeout);
        final var startNanos = System.nanoTime();
        final var key = pendingCompletions.allocKey((res, flags) -> {
            timeSpec.dispose();
            final var totalNanos = System.nanoTime() - startNanos;

            final var result = Math.abs(res) != NativeError.ETIME.typeCode()
                               ? NativeError.<Duration>result(res)
                               : Result.ok(timeout(totalNanos).nanos().asDuration());

            completion.accept(result);
        });

        queue.add(sqe -> sqe.userData(key)
                            .opcode(AsyncOperation.IORING_OP_TIMEOUT.opcode())
                            .addr(timeSpec.address())
                            .fd(-1)
                            .len(1)
                            .off(1));
    }

    @Override
    public void closeFileDescriptor(final Consumer<Result<Unit>> completion,
                                    final FileDescriptor fd,
                                    final Option<Timeout> timeout) {
        final var key = pendingCompletions.allocKey((res, flags) -> completion.accept(res == 0
                                                                                      ? UNIT_RESULT
                                                                                      : NativeError.result(res)));

        queue.add(sqe -> sqe.userData(key)
                            .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                            .opcode(AsyncOperation.IORING_OP_CLOSE.opcode())
                            .fd(fd.descriptor()));

        timeout.whenPresent(this::appendTimeout);
    }

    @Override
    public void read(final Consumer<Result<SizeT>> completion,
                     final FileDescriptor fd,
                     final OffHeapBuffer buffer,
                     final OffsetT offset,
                     final Option<Timeout> timeout) {
        //TODO: candidate for cleanup
        final var key = pendingCompletions.allocKey((res, flags) -> completion.accept(bytesReadToResult(res)
                                                                                              .onSuccess(bytesRead -> buffer.used((int) bytesRead.value()))));

        //TODO: candidate for cleanup
        queue.add(sqe -> sqe.userData(key)
                            .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                            .opcode(AsyncOperation.IORING_OP_READ.opcode())
                            .fd(fd.descriptor())
                            .addr(buffer.address())
                            .len(buffer.size())
                            .off(offset.value()));

        timeout.whenPresent(this::appendTimeout);
    }

    @Override
    public void write(final Consumer<Result<SizeT>> completion,
                      final FileDescriptor fd,
                      final OffHeapBuffer buffer,
                      final OffsetT offset,
                      final Option<Timeout> timeout) {
        //TODO: candidate for cleanup
        final var key = pendingCompletions.allocKey((res, flags) -> completion.accept(byteCountToResult(res)));

        //TODO: candidate for cleanup
        queue.add(sqe -> sqe.userData(key)
                            .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                            .opcode(AsyncOperation.IORING_OP_WRITE.opcode())
                            .fd(fd.descriptor())
                            .addr(buffer.address())
                            .len(buffer.used())
                            .off(offset.value()));

        timeout.whenPresent(this::appendTimeout);
    }

    @Override
    public void splice(final Consumer<Result<SizeT>> completion,
                       final SpliceDescriptor descriptor,
                       final Option<Timeout> timeout) {
        final var key = pendingCompletions.allocKey((res, flags) -> completion.accept(byteCountToResult(res)));

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
    }

    @Override
    public void open(final Consumer<Result<FileDescriptor>> completion,
                     final Path path,
                     final Set<OpenFlags> flags,
                     final Set<FilePermission> mode,
                     final Option<Timeout> timeout) {
        final var rawPath = OffHeapCString.cstring(path.toString());
        final var key = pendingCompletions.allocKey((res, cqFlags) -> {
            rawPath.dispose();

            final var result = res < 0 ? NativeError.<FileDescriptor>result(res)
                                       : Result.ok(FileDescriptor.file(res));
            completion.accept(result);
        });

        queue.add(sqe -> sqe.userData(key)
                            .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                            .opcode(AsyncOperation.IORING_OP_OPENAT.opcode())
                            .fd(AT_FDCWD)
                            .addr(rawPath.address())
                            .len(Bitmask.combine(mode))
                            .openFlags(Bitmask.combine(flags)));

        timeout.whenPresent(this::appendTimeout);
    }

    @Override
    public void socket(final Consumer<Result<FileDescriptor>> completion,
                       final AddressFamily addressFamily,
                       final SocketType socketType,
                       final Set<SocketFlag> openFlags,
                       final Set<SocketOption> options) {

        nop($ -> completion.accept(UringHolder.socket(addressFamily,
                                                      socketType,
                                                      openFlags,
                                                      options)));
    }

    @Override
    public void server(final Consumer<Result<ServerContext<?>>> completion,
                       final SocketAddress<?> socketAddress,
                       final SocketType socketType,
                       final Set<SocketFlag> openFlags,
                       final SizeT queueDepth,
                       final Set<SocketOption> options) {

        nop($ -> completion.accept(UringHolder.server(socketAddress,
                                                      socketType,
                                                      openFlags,
                                                      options,
                                                      queueDepth)));
    }

    @Override
    public void accept(final Consumer<Result<ClientConnection<?>>> completion,
                       final FileDescriptor socket,
                       final Set<SocketFlag> flags) {
        //TODO: add support for v6
        final var clientAddress = OffHeapSocketAddress.addressIn();
        final var key = pendingCompletions.allocKey((res, cqFlags) -> {
            if (res > 0) {
                completion.accept(clientAddress.extract()
                                               .map(addr -> connectionIn(FileDescriptor.socket(res), addr)));
            } else {
                completion.accept(NativeError.result(res));
            }
            clientAddress.dispose();
        });

        queue.add(sqe -> sqe.userData(key)
                            .opcode(AsyncOperation.IORING_OP_ACCEPT.opcode())
                            .fd(socket.descriptor())
                            .addr(clientAddress.sockAddrPtr())
                            .off(clientAddress.sizePtr())
                            .acceptFlags(Bitmask.combine(flags)));
    }

    @Override
    public void connect(final Consumer<Result<FileDescriptor>> completion,
                        final FileDescriptor socket,
                        final SocketAddress<?> address,
                        final Option<Timeout> timeout) {
        final var clientAddress = OffHeapSocketAddress.unsafeSocketAddress(address);

        if (clientAddress == null) {
            completion.accept(NativeError.EPFNOSUPPORT.asResult());
            return;
        }

        final var key = pendingCompletions.allocKey(((res, flags) -> {
            final var result = res < 0
                               ? NativeError.<FileDescriptor>result(res)
                               : Result.ok(socket);
            completion.accept(result);
            clientAddress.dispose();
        }));

        queue.add(sqe -> sqe.userData(key)
                            .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                            .opcode(AsyncOperation.IORING_OP_CONNECT.opcode())
                            .fd(socket.descriptor())
                            .addr(clientAddress.sockAddrPtr())
                            .off(clientAddress.sockAddrSize()));

        timeout.whenPresent(this::appendTimeout);
    }

    @Override
    public void stat(final Consumer<Result<FileStat>> completion,
                     final Path path,
                     final Set<StatFlag> flags,
                     final Set<StatMask> mask) {
        //Reset EMPTY_PATH and force use the path.
        final var statFlags = Bitmask.combine(flags) & ~StatFlag.EMPTY_PATH.mask();
        final var statMask = Bitmask.combine(mask);
        final var fileStat = OffHeapFileStat.fileStat();
        final var rawPath = OffHeapCString.cstring(path.toString());
        final var key = pendingCompletions.allocKey((res, cqFlags) -> {
            completion.accept(fileStatToResult(res, fileStat));
            fileStat.dispose();
            rawPath.dispose();
        });

        queue.add(sqe -> sqe.userData(key)
                            .fd(AT_FDCWD)
                            .addr(rawPath.address())
                            .len(statMask)
                            .off(fileStat.address())
                            .statxFlags(statFlags)
                            .opcode(AsyncOperation.IORING_OP_STATX.opcode()));
    }

    @Override
    public void stat(final Consumer<Result<FileStat>> completion,
                     final FileDescriptor fd,
                     final Set<StatFlag> flags,
                     final Set<StatMask> mask) {
        //Set EMPTY_PATH and force use of file descriptor.
        final var statFlags = Bitmask.combine(flags) | StatFlag.EMPTY_PATH.mask();
        final var statMask = Bitmask.combine(mask);
        final var fileStat = OffHeapFileStat.fileStat();
        final var rawPath = OffHeapCString.cstring("");
        final var key = pendingCompletions.allocKey((res, cqFlags) -> {
            completion.accept(fileStatToResult(res, fileStat));
            fileStat.dispose();
            rawPath.dispose();
        });

        queue.add(sqe -> sqe.userData(key)
                            .fd(fd.descriptor())
                            .addr(rawPath.address())
                            .len(statMask)
                            .off(fileStat.address())
                            .statxFlags(statFlags)
                            .opcode(AsyncOperation.IORING_OP_STATX.opcode()));
    }

    @Override
    public void readVector(final Consumer<Result<SizeT>> completion,
                           final FileDescriptor fileDescriptor,
                           final OffsetT offset,
                           final Option<Timeout> timeout,
                           final OffHeapBuffer... buffers) {
        final var ioVector = OffHeapIoVector.withBuffers(buffers);
        final var key = pendingCompletions.allocKey((res, flags) -> {
            completion.accept(byteCountToResult(res));
            ioVector.dispose();
        });

        queue.add(sqe -> sqe.userData(key)
                            .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                            .opcode(AsyncOperation.IORING_OP_READV.opcode())
                            .fd(fileDescriptor.descriptor())
                            .addr(ioVector.address())
                            .len(ioVector.length())
                            .off(offset.value()));

        timeout.whenPresent(this::appendTimeout);
    }

    @Override
    public void writeVector(final Consumer<Result<SizeT>> promise,
                            final FileDescriptor fileDescriptor,
                            final OffsetT offset,
                            final Option<Timeout> timeout,
                            final OffHeapBuffer... buffers) {

        final var ioVector = OffHeapIoVector.withBuffers(buffers);
        final var key = pendingCompletions.allocKey((res, flags) -> {
            promise.accept(byteCountToResult(res));
            ioVector.dispose();
        });

        queue.add(sqe -> sqe.userData(key)
                            .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                            .opcode(AsyncOperation.IORING_OP_WRITEV.opcode())
                            .fd(fileDescriptor.descriptor())
                            .addr(ioVector.address())
                            .len(ioVector.length())
                            .off(offset.value()));

        timeout.whenPresent(this::appendTimeout);
    }

    private void appendTimeout(final Timeout t) {
        final var timeSpec = OffHeapTimeSpec.forTimeout(t);
        final var key = pendingCompletions.allocKey((res, flags) -> timeSpec.dispose());

        queue.add(sqe -> sqe.userData(key)
                            .fd(-1)
                            .addr(timeSpec.address())
                            .len(1)
                            .opcode(AsyncOperation.IORING_OP_LINK_TIMEOUT.opcode()));
    }

    private Result<SizeT> byteCountToResult(final int res) {
        return res > 0
               ? sizeResult(res)
               : NativeError.result(res);
    }

    private Result<SizeT> bytesReadToResult(final int res) {
        return res == 0 ? EOF_RESULT
                        : res > 0 ? sizeResult(res)
                                  : NativeError.result(res);
    }

    private Result<FileStat> fileStatToResult(final int res, final OffHeapFileStat fileStat) {
        return res < 0
               ? NativeError.result(res)
               : Result.ok(fileStat.extract());
    }

    @SuppressWarnings("unchecked")
    private static Result<SizeT> sizeResult(final int res) {
        return res < RESULT_SIZET_POOL.length
               ? RESULT_SIZET_POOL[res]
               : Result.ok(new SizeT(res));
    }
}

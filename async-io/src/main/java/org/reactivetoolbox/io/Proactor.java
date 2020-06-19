package org.reactivetoolbox.io;

import org.reactivetoolbox.core.lang.Tuple.Tuple2;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.common.OffsetT;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.file.OpenFlags;
import org.reactivetoolbox.io.async.file.FilePermission;
import org.reactivetoolbox.io.async.file.SpliceDescriptor;
import org.reactivetoolbox.io.async.file.stat.FileStat;
import org.reactivetoolbox.io.async.file.stat.StatFlag;
import org.reactivetoolbox.io.async.file.stat.StatMask;
import org.reactivetoolbox.io.async.net.AddressFamily;
import org.reactivetoolbox.io.async.net.ClientConnection;
import org.reactivetoolbox.io.async.net.ServerConnector;
import org.reactivetoolbox.io.async.net.SocketAddress;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketOption;
import org.reactivetoolbox.io.async.net.SocketType;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;
import org.reactivetoolbox.io.scheduler.Timeout;
import org.reactivetoolbox.io.uring.AsyncOperation;
import org.reactivetoolbox.io.uring.UringHolder;
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

import static java.time.Duration.ofSeconds;
import static org.reactivetoolbox.core.lang.Tuple.tuple;
import static org.reactivetoolbox.core.lang.functional.Result.ok;
import static org.reactivetoolbox.io.async.common.SizeT.sizeT;
import static org.reactivetoolbox.io.uring.UringHolder.DEFAULT_QUEUE_SIZE;
import static org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntryFlags.IOSQE_IO_LINK;

public class Proactor implements Submitter, AutoCloseable {
    private static final Result<Unit> UNIT = ok(Unit.UNIT);
    private static final int AT_FDCWD = -100; // Special value used to indicate the openat/statx functions should use the current working directory.

    private final UringHolder uring;
    private final ObjectHeap<CompletionHandler> pendingCompletions;
    private final Deque<Consumer<SubmitQueueEntry>> queue = new LinkedList<>();

    private Proactor(final int queueLen) {
        uring = UringHolder.create(queueLen).fold(f -> null, h -> h);
        pendingCompletions = ObjectHeap.objectHeap(uring.numEntries());
    }

    public static Proactor proactor() {
        return proactor(DEFAULT_QUEUE_SIZE);
    }

    public static Proactor proactor(final int queueSize) {
        return new Proactor(queueSize);
    }

    @FunctionalInterface
    private interface CompletionHandler {
        void handleCompletion(final CompletionQueueEntry entry);
    }

    @Override
    public void close() {
        uring.close();
    }

    public Proactor processIO() {
        uring.processCompletions(this::handleCompletions);

        //TODO: how to conveniently handle submissions with timeouts (i.e. linked pairs of submissions)?
        //      and should we? (it is possible that kernel will handle subsequent timeout even if it delivered later).
        processSubmissions();

        return this;
    }

    private void processSubmissions() {
        int count = uring.availableSQSpace();

        while (count > 0 && !queue.isEmpty()) {
            uring.submit(queue.removeFirst());
            count--;
        }

        uring.notifySubmission();
    }

    private void handleCompletions(final CompletionQueueEntry entry) {
        pendingCompletions.release((int) entry.userData())
                          .whenPresent(handler -> handler.handleCompletion(entry));
    }

    @Override
    public Promise<Unit> nop() {
        return Promise.promise(promise -> {
            final int key = pendingCompletions.allocKey((entry -> promise.resolve(UNIT)));

            queue.add(sqe -> sqe.clear()
                                .userData(key)
                                .opcode(AsyncOperation.IORING_OP_NOP.opcode()));
        });
    }

    @Override
    public Promise<Duration> delay(final Timeout timeout) {
        return Promise.promise(promise -> {
            final OffHeapTimeSpec timeSpec = OffHeapTimeSpec.forTimeout(timeout);

            final long startNanos = System.nanoTime();

            final int key = pendingCompletions.allocKey(entry -> {
                timeSpec.dispose();

                final long endNanos = System.nanoTime();
                final long totalNanos = endNanos - startNanos;

                if (Math.abs(entry.res()) != NativeError.ETIME.typeCode()) {
                    promise.fail(NativeError.fromCode(entry.res()).asFailure());
                } else {
                    promise.ok(ofSeconds(totalNanos / OffHeapTimeSpec.NANO_SCALE,
                                         totalNanos % OffHeapTimeSpec.NANO_SCALE));
                }
            });

            queue.add(sqe -> sqe.clear()
                                .userData(key)
                                .opcode(AsyncOperation.IORING_OP_TIMEOUT.opcode())
                                .addr(timeSpec.address())
                                .fd(-1)
                                .len(1)
                                .off(1));
        });
    }

    @Override
    public Promise<Unit> closeFileDescriptor(final FileDescriptor fd, final Option<Timeout> timeout) {
        return Promise.promise(promise -> {
            final int key = pendingCompletions.allocKey(entry -> promise.resolve(entry.result(v -> Unit.UNIT)));

            queue.add(sqe -> sqe.clear()
                                .userData(key)
                                .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                                .opcode(AsyncOperation.IORING_OP_CLOSE.opcode())
                                .fd(fd.descriptor()));

            timeout.whenPresent(this::appendTimeout);
        });
    }

    @Override
    public Promise<Tuple2<FileDescriptor, SizeT>> read(final FileDescriptor fd,
                                                       final OffHeapBuffer buffer,
                                                       final OffsetT offset,
                                                       final Option<Timeout> timeout) {
        return Promise.promise(promise -> {
            final int key = pendingCompletions.allocKey(entry -> {
                promise.resolve(entry.result(bytesRead -> {
                    buffer.used(bytesRead);
                    return tuple(fd, sizeT(bytesRead));
                }));
            });

            queue.add(sqe -> sqe.clear()
                                .userData(key)
                                .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                                .opcode(AsyncOperation.IORING_OP_READ.opcode())
                                .fd(fd.descriptor())
                                .addr(buffer.address())
                                .len(buffer.size())
                                .off(offset.value()));

            timeout.whenPresent(this::appendTimeout);
        });
    }

    @Override
    public Promise<Tuple2<FileDescriptor, SizeT>> write(final FileDescriptor fd,
                                                        final OffHeapBuffer buffer,
                                                        final OffsetT offset,
                                                        final Option<Timeout> timeout) {
        return Promise.promise(promise -> {
            final int key = pendingCompletions.allocKey(entry -> promise.resolve(entry.result(bytesWritten -> tuple(fd, sizeT(bytesWritten)))));

            queue.add(sqe -> sqe.clear()
                                .userData(key)
                                .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                                .opcode(AsyncOperation.IORING_OP_WRITE.opcode())
                                .fd(fd.descriptor())
                                .addr(buffer.address())
                                .len(buffer.used())
                                .off(offset.value()));

            timeout.whenPresent(this::appendTimeout);
        });
    }

    @Override
    public Promise<SizeT> splice(final SpliceDescriptor descriptor, final Option<Timeout> timeout) {
        return Promise.promise(promise -> {
            final int key = pendingCompletions.allocKey(entry -> promise.resolve(entry.result(SizeT::sizeT)));

            queue.add(sqe -> sqe.clear()
                                .userData(key)
                                .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                                .opcode(AsyncOperation.IORING_OP_SPLICE.opcode())
                                .fd(descriptor.to().descriptor())
                                .len((int) descriptor.toCopy().value())
                                .off(descriptor.toOffset().value())
                                .spliceFdIn(descriptor.from().descriptor())
                                .spliceOffIn(descriptor.fromOffset().value())
                                .spliceFlags(Bitmask.combine(descriptor.flags())));

            timeout.whenPresent(this::appendTimeout);
        });
    }

    @Override
    public Promise<FileDescriptor> open(final Path path,
                                        final EnumSet<OpenFlags> flags,
                                        final EnumSet<FilePermission> mode,
                                        final Option<Timeout> timeout) {
        return Promise.promise(promise -> {
            final OffHeapCString rawPath = OffHeapCString.cstring(path.toString());

            final int key = pendingCompletions.allocKey(entry -> {
                promise.resolve(entry.result(FileDescriptor::file));
                rawPath.dispose();
            });

            queue.add(sqe -> sqe.clear()
                                .userData(key)
                                .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                                .opcode(AsyncOperation.IORING_OP_OPENAT.opcode())
                                .fd(AT_FDCWD)
                                .addr(rawPath.address())
                                .len(Bitmask.combine(mode))
                                .openFlags(Bitmask.combine(flags)));

            timeout.whenPresent(this::appendTimeout);
        });
    }

    @Override
    public Promise<FileDescriptor> socket(final AddressFamily addressFamily,
                                          final SocketType socketType,
                                          final EnumSet<SocketFlag> openFlags,
                                          final EnumSet<SocketOption> options) {
        return nop().flatMap($ -> UringHolder.socket(addressFamily, socketType, openFlags, options));
    }

    @Override
    public Promise<ServerConnector> server(final SocketAddress<?> socketAddress,
                                           final SocketType socketType,
                                           final EnumSet<SocketFlag> openFlags,
                                           final SizeT queueDepth,
                                           final EnumSet<SocketOption> options) {
        return nop().flatMap($ -> UringHolder.server(socketAddress, socketType, openFlags, options, queueDepth));
    }

    @Override
    public Promise<ClientConnection<?>> accept(final FileDescriptor socket, final EnumSet<SocketFlag> flags) {
        return Promise.promise(promise -> {
            //TODO: add support for v6
            final var clientAddress = OffHeapSocketAddress.addressIn();

            final int key = pendingCompletions.allocKey((entry -> {
                promise.resolve(entry.result(FileDescriptor::file)
                                     .flatMap(fd -> Result.flatten(Result.ok(fd), clientAddress.extract())
                                                          .map(tuple -> tuple.map(ClientConnection::connectionIn))));
                clientAddress.dispose();
            }));

            queue.add(sqe -> sqe.clear()
                                .userData(key)
                                .opcode(AsyncOperation.IORING_OP_ACCEPT.opcode())
                                .fd(socket.descriptor())
                                .addr(clientAddress.sockAddrPtr())
                                .off(clientAddress.sizePtr())
                                .acceptFlags(Bitmask.combine(flags)));
        });
    }

    @Override
    public Promise<FileDescriptor> connect(final FileDescriptor socket, final SocketAddress<?> address, final Option<Timeout> timeout) {
        return Promise.promise(promise -> {
            final var clientAddress = OffHeapSocketAddress.unsafeSocketAddress(address);

            if (clientAddress == null) {
                promise.fail(NativeError.EPFNOSUPPORT.asFailure());
                return;
            }

            final int key = pendingCompletions.allocKey((entry -> {
                promise.resolve(entry.result(v -> socket));
                clientAddress.dispose();
            }));

            queue.add(sqe -> sqe.clear()
                                .userData(key)
                                .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                                .opcode(AsyncOperation.IORING_OP_CONNECT.opcode())
                                .fd(socket.descriptor())
                                .addr(clientAddress.sockAddrPtr())
                                .off(clientAddress.sockAddrSize()));

            timeout.whenPresent(this::appendTimeout);
        });
    }

    @Override
    public Promise<FileStat> stat(final Path path, final EnumSet<StatFlag> flags, final EnumSet<StatMask> mask) {
        return Promise.promise(promise -> {
            //Reset EMPTY_PATH and enforce use of path instead.
            final int statFlags = Bitmask.combine(flags) & ~StatFlag.EMPTY_PATH.mask();
            final int statMask = Bitmask.combine(mask);

            final OffHeapFileStat fileStat = OffHeapFileStat.fileStat();
            final OffHeapCString rawPath = OffHeapCString.cstring(path.toString());

            queue.add(sqe -> sqe.clear()
                                .userData(pendingCompletions.allocKey(entry -> {
                                    promise.resolve(entry.result($ -> fileStat.extract()));
                                    fileStat.dispose();
                                    rawPath.dispose();
                                }))
                                .fd(AT_FDCWD)
                                .addr(rawPath.address())
                                .len(statMask)
                                .off(fileStat.address())
                                .statxFlags(statFlags)
                                .opcode(AsyncOperation.IORING_OP_STATX.opcode()));
        });
    }

    @Override
    public Promise<FileStat> stat(final FileDescriptor fd, final EnumSet<StatFlag> flags, final EnumSet<StatMask> mask) {
        return Promise.promise(promise -> {
            //Set EMPTY_PATH and enforce use of file descriptor instead.
            final int statFlags = Bitmask.combine(flags) | StatFlag.EMPTY_PATH.mask();
            final int statMask = Bitmask.combine(mask);

            final OffHeapFileStat fileStat = OffHeapFileStat.fileStat();
            final OffHeapCString rawPath = OffHeapCString.cstring("");

            queue.add(sqe -> sqe.clear()
                                .userData(pendingCompletions.allocKey(entry -> {
                                    promise.resolve(entry.result($ -> fileStat.extract()));
                                    fileStat.dispose();
                                    rawPath.dispose();
                                }))
                                .fd(fd.descriptor())
                                .addr(rawPath.address())
                                .len(statMask)
                                .off(fileStat.address())
                                .statxFlags(statFlags)
                                .opcode(AsyncOperation.IORING_OP_STATX.opcode()));
        });
    }

    @Override
    public Promise<Tuple2<FileDescriptor, SizeT>> readVector(final FileDescriptor fileDescriptor,
                                                             final OffsetT offset,
                                                             final Option<Timeout> timeout,
                                                             final OffHeapBuffer... buffers) {
        return Promise.promise(promise -> {
            final OffHeapIoVector ioVector = OffHeapIoVector.withBuffers(buffers);
            final int key = pendingCompletions.allocKey(entry -> {
                promise.resolve(entry.result(bytesRead -> tuple(fileDescriptor, sizeT(bytesRead))));
                ioVector.dispose();
            });

            queue.add(sqe -> sqe.clear()
                                .userData(key)
                                .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                                .opcode(AsyncOperation.IORING_OP_READV.opcode())
                                .fd(fileDescriptor.descriptor())
                                .addr(ioVector.address())
                                .len(ioVector.length())
                                .off(offset.value()));

            timeout.whenPresent(this::appendTimeout);
        });
    }

    @Override
    public Promise<Tuple2<FileDescriptor, SizeT>> writeVector(final FileDescriptor fileDescriptor,
                                                              final OffsetT offset,
                                                              final Option<Timeout> timeout,
                                                              final OffHeapBuffer... buffers) {
        return Promise.promise(promise -> {
            final OffHeapIoVector ioVector = OffHeapIoVector.withBuffers(buffers);
            final int key = pendingCompletions.allocKey(entry -> {
                promise.resolve(entry.result(bytesWritten -> tuple(fileDescriptor, sizeT(bytesWritten))));
                ioVector.dispose();
            });

            queue.add(sqe -> sqe.clear()
                                .userData(key)
                                .flags(timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK)
                                .opcode(AsyncOperation.IORING_OP_WRITEV.opcode())
                                .fd(fileDescriptor.descriptor())
                                .addr(ioVector.address())
                                .len(ioVector.length())
                                .off(offset.value()));

            timeout.whenPresent(this::appendTimeout);
        });
    }

    private void appendTimeout(Timeout t) {
        final OffHeapTimeSpec timeSpec = OffHeapTimeSpec.forTimeout(t);

        queue.add(sqe -> sqe.clear()
                            .userData(pendingCompletions.allocKey(entry -> timeSpec.dispose()))
                            .fd(-1)
                            .addr(timeSpec.address())
                            .len(1)
                            .opcode(AsyncOperation.IORING_OP_LINK_TIMEOUT.opcode()));
    }
}

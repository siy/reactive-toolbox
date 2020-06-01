package org.reactivetoolbox.io;

import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.async.ClientConnection;
import org.reactivetoolbox.io.async.FileDescriptor;
import org.reactivetoolbox.io.async.OffsetT;
import org.reactivetoolbox.io.async.OpenFlags;
import org.reactivetoolbox.io.async.OpenMode;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.SizeT;
import org.reactivetoolbox.io.async.SpliceDescriptor;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.net.AddressFamily;
import org.reactivetoolbox.io.async.net.SocketAddress;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketType;
import org.reactivetoolbox.io.scheduler.Timeout;
import org.reactivetoolbox.io.uring.AsyncOperation;
import org.reactivetoolbox.io.uring.UringHolder;
import org.reactivetoolbox.io.uring.structs.CompletionQueueEntry;
import org.reactivetoolbox.io.uring.structs.RawCString;
import org.reactivetoolbox.io.uring.structs.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.structs.TimeSpec;
import org.reactivetoolbox.io.uring.utils.ObjectHeap;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Deque;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.function.Consumer;

import static org.reactivetoolbox.core.lang.functional.Result.ok;
import static org.reactivetoolbox.io.NativeError.nativeResult;
import static org.reactivetoolbox.io.async.FileDescriptor.*;
import static org.reactivetoolbox.io.uring.UringHolder.DEFAULT_QUEUE_SIZE;

public class Proactor implements Submitter, AutoCloseable {
    private static final Result<Unit> UNIT = ok(Unit.UNIT);

    private final UringHolder uring;
    private final ObjectHeap<CompletionHandler> pendingCompletions;
    private final Deque<Consumer<SubmitQueueEntry>> queue = new LinkedList<>();

    private Proactor(final int queueLen) {
        uring = UringHolder.create(queueLen).fold((f) -> null, (h) -> h);
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

            queue.add(sqe -> sqe.userData(key)
                                .opcode(AsyncOperation.IORING_OP_NOP.opcode()));
        });
    }

    @Override
    public Promise<Duration> delay(final Timeout timeout) {
        return Promise.promise(promise -> {
            final TimeSpec timeSpec = TimeSpec.forTimeout(timeout);
            promise.onResult((r) -> timeSpec.dispose());

            final long startNanos = System.nanoTime();

            final int key = pendingCompletions.allocKey((entry -> {
                final long endNanos = System.nanoTime();
                final long totalNanos = endNanos - startNanos;
                //TODO: add error handling
                promise.ok(Duration.ofSeconds(totalNanos / TimeSpec.NANO_SCALE,
                                              totalNanos % TimeSpec.NANO_SCALE));
            }));

            queue.add(sqe -> sqe.clear()
                                .opcode(AsyncOperation.IORING_OP_TIMEOUT.opcode())
                                .userData(key)
                                .addr(timeSpec.address())
                                .fd(-1)
                                .len(1)
                                .off(1));
        });
    }

    @Override
    public Promise<SizeT> splice(final SpliceDescriptor descriptor, final Option<Timeout> timeout) {
        return null;
    }

    @Override
    public Promise<SizeT> read(final FileDescriptor fdIn, final ByteBuffer buffer, final OffsetT offset, final Option<Timeout> timeout) {
        return null;
    }

    @Override
    public Promise<SizeT> write(final FileDescriptor fdOut, final ByteBuffer buffer, final OffsetT offset, final Option<Timeout> timeout) {
        return null;
    }

    @Override
    public Promise<Unit> closeFileDescriptor(final FileDescriptor fd, final Option<Timeout> timeout) {
        //TODO: add handling for timeout

        return Promise.promise(promise -> {
            final int key = pendingCompletions.allocKey(entry -> {
                promise.resolve(entry.result(v -> Unit.UNIT));
            });

            queue.add(sqe -> sqe.clear()
                                .opcode(AsyncOperation.IORING_OP_CLOSE.opcode())
                                .fd(fd.descriptor()));
        });
    }

    @Override
    public Promise<FileDescriptor> open(final Path path,
                                        final EnumSet<OpenFlags> flags,
                                        final EnumSet<OpenMode> mode,
                                        final Option<Timeout> timeout) {
        return Promise.promise(promise -> {
            final RawCString rawPath = RawCString.rawCString(path.toString());
            promise.onResult(v -> rawPath.dispose());

            final int key = pendingCompletions.allocKey((entry -> {
                promise.resolve(entry.result(FileDescriptor::file));
            }));

            queue.add(sqe -> sqe.clear()
                                .opcode(AsyncOperation.IORING_OP_OPENAT.opcode())
                                .fd(-100) // AT_FDCWD = -100
                                .addr(rawPath.address())
                                .len(Bitmask.combine(mode))
                                .openFlags(Bitmask.combine(flags)));
        });
    }

    @Override
    public Promise<FileDescriptor> socket(final AddressFamily addressFamily, final SocketType socketType, final EnumSet<SocketFlag> openFlags) {
        return null;
    }

    @Override
    public Promise<FileDescriptor> serverSocket(final FileDescriptor socket, final Option<SocketAddress<?>> address, final SizeT backLog) {
        return null;
    }

    @Override
    public Promise<ClientConnection> accept(final FileDescriptor socket, final EnumSet<SocketFlag> flags) {
        return null;
    }

    @Override
    public Promise<Unit> connect(final FileDescriptor socket, final SocketAddress<?> address) {
        return null;
    }
}

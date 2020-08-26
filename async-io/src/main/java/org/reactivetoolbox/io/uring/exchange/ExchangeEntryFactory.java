package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.Bitmask;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.common.OffsetT;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.file.FilePermission;
import org.reactivetoolbox.io.async.file.OpenFlags;
import org.reactivetoolbox.io.async.file.SpliceDescriptor;
import org.reactivetoolbox.io.async.file.stat.FileStat;
import org.reactivetoolbox.io.async.net.AddressFamily;
import org.reactivetoolbox.io.async.net.ClientConnection;
import org.reactivetoolbox.io.async.net.SocketAddress;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketOption;
import org.reactivetoolbox.io.async.net.SocketType;
import org.reactivetoolbox.io.async.net.context.ServerContext;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;
import org.reactivetoolbox.io.scheduler.Timeout;
import org.reactivetoolbox.io.uring.struct.ExternalRawStructure;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapCString;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapIoVector;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapSocketAddress;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntryFlags.IOSQE_IO_LINK;

public class ExchangeEntryFactory {
    private final PlainObjectPool<NopExchangeEntry> nopPool = new PlainObjectPool<>(NopExchangeEntry::new);
    private final PlainObjectPool<DelayExchangeEntry> delayPool = new PlainObjectPool<>(DelayExchangeEntry::new);
    private final PlainObjectPool<CloseExchangeEntry> closePool = new PlainObjectPool<>(CloseExchangeEntry::new);
    private final PlainObjectPool<TimeoutExchangeEntry> timeoutPool = new PlainObjectPool<>(TimeoutExchangeEntry::new);
    private final PlainObjectPool<ReadExchangeEntry> readPool = new PlainObjectPool<>(ReadExchangeEntry::new);
    private final PlainObjectPool<WriteExchangeEntry> writePool = new PlainObjectPool<>(WriteExchangeEntry::new);
    private final PlainObjectPool<SpliceExchangeEntry> splicePool = new PlainObjectPool<>(SpliceExchangeEntry::new);
    private final PlainObjectPool<OpenExchangeEntry> openPool = new PlainObjectPool<>(OpenExchangeEntry::new);
    private final PlainObjectPool<SocketExchangeEntry> socketPool = new PlainObjectPool<>(SocketExchangeEntry::new);
    private final PlainObjectPool<ServerExchangeEntry> serverPool = new PlainObjectPool<>(ServerExchangeEntry::new);
    private final PlainObjectPool<AcceptExchangeEntry> acceptPool = new PlainObjectPool<>(AcceptExchangeEntry::new);
    private final PlainObjectPool<ConnectExchangeEntry> connectPool = new PlainObjectPool<>(ConnectExchangeEntry::new);
    private final PlainObjectPool<StatExchangeEntry> statPool = new PlainObjectPool<>(StatExchangeEntry::new);
    private final PlainObjectPool<ReadVectorExchangeEntry> readVectorPool = new PlainObjectPool<>(ReadVectorExchangeEntry::new);
    private final PlainObjectPool<WriteVectorExchangeEntry> writeVectorPool = new PlainObjectPool<>(WriteVectorExchangeEntry::new);

    public NopExchangeEntry forNop(final BiConsumer<Result<Unit>, Submitter> completion) {
        return nopPool.alloc()
                      .prepare(completion);
    }

    public TimeoutExchangeEntry forTimeout(final Timeout timeout) {
        return timeoutPool.alloc()
                          .prepare(timeout);
    }

    public DelayExchangeEntry forDelay(final BiConsumer<Result<Duration>, Submitter> completion, final Timeout timeout) {
        return delayPool.alloc()
                        .prepare(completion, timeout);
    }

    public CloseExchangeEntry forClose(final BiConsumer<Result<Unit>, Submitter> completion,
                                       final FileDescriptor fd,
                                       final Option<Timeout> timeout) {
        return closePool.alloc()
                        .prepare(completion, fd.descriptor(), calculateFlags(timeout));
    }

    private byte calculateFlags(final Option<Timeout> timeout) {
        return timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK;
    }

    public ReadExchangeEntry forRead(final BiConsumer<Result<SizeT>, Submitter> completion,
                                     final FileDescriptor fd,
                                     final OffHeapBuffer buffer,
                                     final OffsetT offset,
                                     final Option<Timeout> timeout) {
        return readPool.alloc()
                       .prepare(completion, fd.descriptor(), buffer, offset.value(), calculateFlags(timeout));
    }

    public WriteExchangeEntry forWrite(final BiConsumer<Result<SizeT>, Submitter> completion,
                                       final FileDescriptor fd,
                                       final OffHeapBuffer buffer,
                                       final OffsetT offset,
                                       final Option<Timeout> timeout) {
        return writePool.alloc()
                        .prepare(completion, fd.descriptor(), buffer, offset.value(), calculateFlags(timeout));
    }

    public SpliceExchangeEntry forSplice(final BiConsumer<Result<SizeT>, Submitter> completion,
                                         final SpliceDescriptor descriptor,
                                         final Option<Timeout> timeout) {
        return splicePool.alloc()
                         .prepare(completion, descriptor, calculateFlags(timeout));
    }

    public OpenExchangeEntry forOpen(final BiConsumer<Result<FileDescriptor>, Submitter> completion,
                                     final Path path,
                                     final Set<OpenFlags> openFlags,
                                     final Set<FilePermission> mode,
                                     final Option<Timeout> timeout) {
        return openPool.alloc()
                       .prepare(completion, path, Bitmask.combine(openFlags), Bitmask.combine(mode), calculateFlags(timeout));
    }

    public SocketExchangeEntry forSocket(final BiConsumer<Result<FileDescriptor>, Submitter> completion,
                                         final AddressFamily addressFamily,
                                         final SocketType socketType,
                                         final Set<SocketFlag> openFlags,
                                         final Set<SocketOption> options) {
        return socketPool.alloc()
                         .prepare(completion, addressFamily, socketType, openFlags, options);
    }

    public ServerExchangeEntry forServer(final BiConsumer<Result<ServerContext<?>>, Submitter> completion,
                                         final SocketAddress<?> socketAddress,
                                         final SocketType socketType,
                                         final Set<SocketFlag> openFlags,
                                         final SizeT queueDepth,
                                         final Set<SocketOption> options) {
        return serverPool.alloc()
                         .prepare(completion, socketAddress, socketType, openFlags, queueDepth, options);
    }

    public AcceptExchangeEntry forAccept(final BiConsumer<Result<ClientConnection<?>>, Submitter> completion,
                                         final FileDescriptor socket,
                                         final Set<SocketFlag> flags) {
        return acceptPool.alloc()
                         .prepare(completion, socket.descriptor(), Bitmask.combine(flags));
    }

    public ConnectExchangeEntry forConnect(final BiConsumer<Result<FileDescriptor>, Submitter> completion,
                                           final FileDescriptor socket,
                                           final OffHeapSocketAddress<SocketAddress<?>, ExternalRawStructure<?>> clientAddress,
                                           final Option<Timeout> timeout) {
        return connectPool.alloc()
                          .prepare(completion, socket, clientAddress, calculateFlags(timeout));
    }

    public StatExchangeEntry forStat(final BiConsumer<Result<FileStat>, Submitter> completion,
                                     final int descriptor,
                                     final int statFlags,
                                     final int statMask,
                                     final OffHeapCString rawPath) {
        return statPool.alloc()
                       .prepare(completion, descriptor, statFlags, statMask, rawPath);
    }

    public ReadVectorExchangeEntry forReadVector(final BiConsumer<Result<SizeT>, Submitter> completion,
                                                 final FileDescriptor fileDescriptor,
                                                 final OffsetT offset,
                                                 final Option<Timeout> timeout,
                                                 final OffHeapIoVector ioVector) {
        return readVectorPool.alloc()
                             .prepare(completion, fileDescriptor.descriptor(), offset.value(), calculateFlags(timeout), ioVector);
    }

    public WriteVectorExchangeEntry forWriteVector(final BiConsumer<Result<SizeT>, Submitter> completion,
                                                   final FileDescriptor fileDescriptor,
                                                   final OffsetT offset,
                                                   final Option<Timeout> timeout,
                                                   final OffHeapIoVector ioVector) {
        return writeVectorPool.alloc()
                              .prepare(completion, fileDescriptor.descriptor(), offset.value(), calculateFlags(timeout), ioVector);
    }

    public void clear() {
        nopPool.clear();
        delayPool.clear();
        closePool.clear();
        timeoutPool.clear();
        readPool.clear();
        writePool.clear();
        splicePool.clear();
        openPool.clear();
        socketPool.clear();
        serverPool.clear();
        acceptPool.clear();
        connectPool.clear();
        statPool.clear();
        readVectorPool.clear();
        writeVectorPool.clear();
    }
}

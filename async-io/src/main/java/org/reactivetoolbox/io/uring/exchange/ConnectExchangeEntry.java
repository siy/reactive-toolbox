package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.net.SocketAddress;
import org.reactivetoolbox.io.scheduler.Timeout;
import org.reactivetoolbox.io.uring.struct.ExternalRawStructure;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapSocketAddress;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.util.function.Consumer;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_CONNECT;
import static org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntryFlags.IOSQE_IO_LINK;

public class ConnectExchangeEntry extends AbstractExchangeEntry<ConnectExchangeEntry, FileDescriptor> {
    private OffHeapSocketAddress<SocketAddress<?>, ExternalRawStructure<?>> clientAddress;
    private byte flags;
    private FileDescriptor socket;

    protected ConnectExchangeEntry(final PlainObjectPool<ConnectExchangeEntry> pool) {
        super(IORING_OP_CONNECT, pool);
    }

    @Override
    protected void doAccept(final int res, final int flags) {
        completion.accept(res < 0
                           ? NativeError.result(res)
                           : Result.ok(socket));

        clientAddress.dispose();
        clientAddress = null;
    }

    @Override
    public SubmitQueueEntry apply(final SubmitQueueEntry entry) {
        return super.apply(entry)
                    .fd(socket.descriptor())
                    .addr(clientAddress.sockAddrPtr())
                    .off(clientAddress.sockAddrSize());
    }

    public ConnectExchangeEntry prepare(final Consumer<Result<FileDescriptor>> completion,
                                        final FileDescriptor socket,
                                        final OffHeapSocketAddress<SocketAddress<?>, ExternalRawStructure<?>> clientAddress,
                                        final Option<Timeout> timeout) {
        this.clientAddress = clientAddress;
        this.socket = socket;
        flags = timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK;

        return super.prepare(completion);
    }
}

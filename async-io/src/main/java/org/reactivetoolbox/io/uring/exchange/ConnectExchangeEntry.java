package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.net.SocketAddress;
import org.reactivetoolbox.io.uring.struct.ExternalRawStructure;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapSocketAddress;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.util.function.BiConsumer;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_CONNECT;

public class ConnectExchangeEntry extends AbstractExchangeEntry<ConnectExchangeEntry, FileDescriptor> {
    private OffHeapSocketAddress<SocketAddress<?>, ExternalRawStructure<?>> clientAddress;
    private byte flags;
    private FileDescriptor descriptor;

    protected ConnectExchangeEntry(final PlainObjectPool<ConnectExchangeEntry> pool) {
        super(IORING_OP_CONNECT, pool);
    }

    @Override
    protected void doAccept(final int res, final int flags, final Submitter submitter) {
        completion.accept(res < 0
                           ? NativeError.result(res)
                           : Result.ok(descriptor),
                          submitter);

        clientAddress.dispose();
        clientAddress = null;
    }

    @Override
    public SubmitQueueEntry apply(final SubmitQueueEntry entry) {
        return super.apply(entry)
                    .fd(descriptor.descriptor())
                    .addr(clientAddress.sockAddrPtr())
                    .off(clientAddress.sockAddrSize());
    }

    public ConnectExchangeEntry prepare(final BiConsumer<Result<FileDescriptor>, Submitter> completion,
                                        final FileDescriptor descriptor,
                                        final OffHeapSocketAddress<SocketAddress<?>, ExternalRawStructure<?>> clientAddress,
                                        final byte flags) {
        this.clientAddress = clientAddress;
        this.descriptor = descriptor;
        this.flags = flags;

        return super.prepare(completion);
    }
}

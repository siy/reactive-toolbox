package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.Bitmask;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.net.ClientConnection;
import org.reactivetoolbox.io.async.net.SocketAddressIn;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapSocketAddress;
import org.reactivetoolbox.io.uring.struct.raw.RawSocketAddressIn;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.util.Set;
import java.util.function.Consumer;

import static org.reactivetoolbox.io.async.net.ClientConnection.connectionIn;
import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_ACCEPT;

//TODO: add support for v6
public class AcceptExchangeEntry extends AbstractExchangeEntry<AcceptExchangeEntry, ClientConnection<?>> {
    private final OffHeapSocketAddress<SocketAddressIn, RawSocketAddressIn> clientAddress = OffHeapSocketAddress.addressIn();
    private int descriptor;
    private int acceptFlags;

    protected AcceptExchangeEntry(final PlainObjectPool<AcceptExchangeEntry> pool) {
        super(IORING_OP_ACCEPT, pool);
    }

    @Override
    public void close() {
        clientAddress.dispose();
    }

    @Override
    protected void doAccept(final int res, final int flags) {
        if (res <= 0) {
            completion.accept(NativeError.result(res));
        }

        completion.accept(clientAddress.extract()
                                       .map(addr -> connectionIn(FileDescriptor.socket(res), addr)));

    }

    @Override
    public SubmitQueueEntry apply(final SubmitQueueEntry entry) {
        return super.apply(entry)
                    .fd(descriptor)
                    .addr(clientAddress.sockAddrPtr())
                    .off(clientAddress.sizePtr())
                    .acceptFlags(acceptFlags);
    }

    public AcceptExchangeEntry prepare(final Consumer<Result<ClientConnection<?>>> completion,
                                       final FileDescriptor socket,
                                       final Set<SocketFlag> flags) {
        descriptor = socket.descriptor();
        acceptFlags = Bitmask.combine(flags);
        clientAddress.reset();
        return super.prepare(completion);
    }
}

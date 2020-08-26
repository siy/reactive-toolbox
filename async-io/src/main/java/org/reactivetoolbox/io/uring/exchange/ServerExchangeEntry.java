package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.net.SocketAddress;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketOption;
import org.reactivetoolbox.io.async.net.SocketType;
import org.reactivetoolbox.io.async.net.context.ServerContext;
import org.reactivetoolbox.io.uring.UringHolder;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.util.Set;
import java.util.function.BiConsumer;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_NOP;

public class ServerExchangeEntry extends AbstractExchangeEntry<ServerExchangeEntry, ServerContext<?>> {
    private SocketAddress<?> socketAddress;
    private SocketType socketType;
    private Set<SocketFlag> openFlags;
    private SizeT queueDepth;
    private Set<SocketOption> options;

    protected ServerExchangeEntry(final PlainObjectPool<ServerExchangeEntry> pool) {
        super(IORING_OP_NOP, pool);
    }

    @Override
    protected void doAccept(final int result, final int flags, final Submitter submitter) {
        completion.accept(UringHolder.server(socketAddress,
                                             socketType,
                                             openFlags,
                                             options,
                                             queueDepth),
                          submitter);
    }

    public ServerExchangeEntry prepare(final BiConsumer<Result<ServerContext<?>>, Submitter> completion,
                                       final SocketAddress<?> socketAddress,
                                       final SocketType socketType,
                                       final Set<SocketFlag> openFlags,
                                       final SizeT queueDepth,
                                       final Set<SocketOption> options) {
        this.socketAddress = socketAddress;
        this.socketType = socketType;
        this.openFlags = openFlags;
        this.queueDepth = queueDepth;
        this.options = options;
        return super.prepare(completion);
    }
}

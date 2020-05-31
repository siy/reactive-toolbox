package org.reactivetoolbox.net.http.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.core.lang.functional.Failure;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.lang.Tuple.Tuple3;
import org.reactivetoolbox.core.lang.support.WebFailureTypes;
import org.reactivetoolbox.net.http.server.Server;
import org.reactivetoolbox.net.http.server.router.Router;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.reactivetoolbox.io.async.Promise.all;
import static org.reactivetoolbox.io.async.Promise.promise;
import static org.reactivetoolbox.io.async.Promise.readyFail;
import static org.reactivetoolbox.io.async.Promise.readyOk;
import static org.reactivetoolbox.core.lang.Tuple.tuple;

/**
 * The WebServer class is a convenience wrapper for the Netty HTTP server.
 */
public class NettyServer implements Server<ByteBuf> {
    private final Router router;
    private final ServerConfig config;
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicReference<EventLoopGroup> parentGroupHolder = new AtomicReference<>();
    private final AtomicReference<EventLoopGroup> loopGroupHolder = new AtomicReference<>();

    /**
     * Creates a new WebServer.
     *
     * @param config
     * @param router
     */
    public NettyServer(final ServerConfig config, final Router router) {
        this.router = router;
        this.config = config;
    }

    /**
     * Starts the web server.
     *
     * @return {@link Promise} instance which will be resolved once server is started.
     */
    @Override
    public Promise<Server<ByteBuf>> start() {
        return started.compareAndSet(false, true)
               ? chooseTransport().map(this::start)
               : readyOk(this);
    }

    //TODO: use internal scheduler
    private Tuple3<EventLoopGroup, EventLoopGroup, Class<? extends ServerChannel>> chooseTransport() {
        if (Epoll.isAvailable()) {
            return tuple(new EpollEventLoopGroup(1), new EpollEventLoopGroup(), EpollServerSocketChannel.class);
        }

        if (KQueue.isAvailable()) {
            return tuple(new KQueueEventLoopGroup(1), new EpollEventLoopGroup(), KQueueServerSocketChannel.class);
        }

        return tuple(new NioEventLoopGroup(1), new EpollEventLoopGroup(), NioServerSocketChannel.class);
    }

    /**
     * Stops the web server.
     *
     * @return {@link Promise} instance which will be resolved once server is stopped.
     */
    @Override
    public Promise<Server<ByteBuf>> stop() {
        if (started.get()) {
            return all(promise(parent -> parentGroupHolder.get().shutdownGracefully().addListener(v -> parent.ok(this))),
                       promise(loop -> loopGroupHolder.get().shutdownGracefully().addListener(v -> loop.ok(this))))
                    .map(result -> result.map((v1, v2) -> this));
        } else {
            return readyOk(this);
        }
    }

    private Promise<Server<ByteBuf>> start(final EventLoopGroup parentGroup,
                                  final EventLoopGroup loopGroup,
                                  final Class<? extends ServerChannel> serverChannelClass) {

        parentGroupHolder.set(parentGroup);
        loopGroupHolder.set(loopGroup);

        try {
            final ChannelFuture bindFuture = new ServerBootstrap()
                    .group(parentGroup, loopGroup)
                    .channel(serverChannelClass)
                    .childHandler(new NettyServerInitializer(router, config))
                    .bind(config.port());

            return promise(promise -> bindFuture.addListener(v -> promise.resolve(Result.ok(this))));

        } catch (final Exception e) {
            return readyFail(Failure.failure(WebFailureTypes.INTERNAL_SERVER_ERROR, "Server interrupted"));
        }
    }
}
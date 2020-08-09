package org.reactivetoolbox.io.async.net.server;

import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketType;
import org.reactivetoolbox.io.async.net.context.ActiveServerContext;
import org.reactivetoolbox.io.async.net.context.ServerContext;

import static java.util.stream.IntStream.range;
import static org.reactivetoolbox.io.async.Promise.asyncPromise;
import static org.reactivetoolbox.io.async.net.context.ActiveServerContext.activeContext;

public class TcpServer {
    private final TcpServerConfiguration configuration;

    private TcpServer(final TcpServerConfiguration configuration) {
        this.configuration = configuration;
    }

    public static TcpServer tcpServer(final TcpServerConfiguration configuration) {
        return new TcpServer(configuration);
    }

    public Promise<ActiveServerContext> start() {
        return asyncPromise(this::startListen)
                .flatMap(this::setupAcceptors);
    }

    private Promise<ActiveServerContext> setupAcceptors(final ServerContext<?> context) {
        final ActiveServerContext activeServerContext = activeContext(context, configuration);

        //range(0, context.queueDepth())
        range(0, 1)
                .forEach($ -> doAccept(activeServerContext));

        return Promise.readyOk(activeServerContext);
    }

    private void startListen(final Promise<ServerContext<?>> promise, final Submitter submitter) {
        submitter.server(promise,
                         configuration.address(),
                         SocketType.STREAM,
                         configuration.listenerFlags(),
                         configuration.backlogSize(),
                         configuration.listenerOptions());
    }

    private static void doAccept(final ActiveServerContext context) {
        if (!context.shutdownInProgress()) {
            Promise.asyncPromise((promise, submitter) ->
                                         submitter.accept(clientConnection -> clientConnection.onSuccessDo(() -> doAccept(context))
                                                                                              .onSuccess(context::handleConnection),
                                                          context.socket(),
                                                          SocketFlag.closeOnExec()));
        }
    }
}

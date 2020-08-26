package org.reactivetoolbox.io.async.net.server;

import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.net.ClientConnection;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketType;
import org.reactivetoolbox.io.async.net.context.ActiveServerContext;
import org.reactivetoolbox.io.async.net.context.ServerContext;

import java.util.stream.IntStream;

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

        IntStream.range(0, Promise.parallelism())
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
        context.shutdownPromise()
               .async((promise, submitter) -> submitter.accept((acceptResult, onResultSubmitter) -> handleAccept(acceptResult, context, onResultSubmitter),
                                                               context.socket(),
                                                               SocketFlag.closeOnExec()));
    }

    private static void handleAccept(final Result<ClientConnection<?>> acceptResult, final ActiveServerContext context, final Submitter submitter) {
        acceptResult.onSuccess($ -> submitter.accept((nextAcceptResult, onResultSubmitter) -> handleAccept(nextAcceptResult, context, onResultSubmitter),
                                                     context.socket(), SocketFlag.closeOnExec()))
                    .onSuccess(context::handleConnection)
                    .onFailure(failure -> context.logger().debug("Accept error {1} ", failure));
    }
}

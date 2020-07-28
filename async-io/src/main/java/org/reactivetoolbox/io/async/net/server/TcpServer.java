package org.reactivetoolbox.io.async.net.server;

import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketType;
import org.reactivetoolbox.io.async.net.context.ActiveServerContext;
import org.reactivetoolbox.io.async.net.context.ServerContext;

import static java.util.stream.IntStream.range;
import static org.reactivetoolbox.core.lang.functional.Unit.unit;
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
        return asyncPromise(this::startListen).flatMap(this::setupAcceptors);
    }

    private Promise<ActiveServerContext> setupAcceptors(final ServerContext<?> context) {
        final ActiveServerContext activeServerContext = activeContext(context, configuration);

        return asyncPromise((promise, submitter) -> {
            range(0, context.queueDepth()).forEach($ -> doAccept(submitter, activeServerContext));
            promise.ok(activeServerContext);
        });
    }

    private Promise<ServerContext<?>> startListen(final Promise<ServerContext<?>> promise, final Submitter submitter) {
        return submitter.server(configuration.address(),
                                SocketType.STREAM,
                                configuration.listenerFlags(),
                                configuration.backlogSize(),
                                configuration.listenerOptions())
                        .onResult(promise::resolve);
    }

    private static Promise<Unit> doAccept(final Submitter submitter, final ActiveServerContext context) {
        return (context.shutdownInProgress())
               ? Promise.readyOk(unit())
               : submitter.accept(context.socket(), SocketFlag.closeOnExec())
                          .onSuccess($ -> doAccept(submitter, context))
                          .flatMap(context::handleConnection);
    }
}

package org.reactivetoolbox.io.async.net.context;

import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.core.lang.support.ULID;
import org.reactivetoolbox.core.log.CoreLogger;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.common.OffsetT;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.net.ClientConnection;
import org.reactivetoolbox.io.async.net.SocketAddressIn;
import org.reactivetoolbox.io.async.net.lifecycle.LifeCycle;
import org.reactivetoolbox.io.async.net.server.TcpServerConfiguration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.reactivetoolbox.core.lang.functional.Option.empty;
import static org.reactivetoolbox.io.async.net.context.ConnectionContext.connectionContext;

//TODO: some redesign is required. get rid of lifecycle???
public class ActiveServerContext {
    private final ServerContext<?> serverContext;
    private final TcpServerConfiguration configuration;
    private final ConcurrentMap<ULID, ConnectionContext> connections = new ConcurrentHashMap<>();
    private final Promise<Unit> shutdownPromise = Promise.promise();

    private ActiveServerContext(final ServerContext<?> serverContext, final TcpServerConfiguration configuration) {
        this.serverContext = serverContext;
        this.configuration = configuration;
    }

    public SocketAddressIn serverAddress() {
        return configuration.address();
    }

    public static ActiveServerContext activeContext(final ServerContext<?> serverContext,
                                                    final TcpServerConfiguration configuration) {
        return new ActiveServerContext(serverContext, configuration);
    }

    public static Promise<Unit> defaultConnectionHandler(final ConnectionContext connectionContext) {
        final var registry = connectionContext.serverContext().registry().add(connectionContext);

        return Promise.promise(connectionContext::processConnection)
                      .thenDo(() -> registry.remove(connectionContext));
    }

    public LifeCycle lifeCycle() {
        return configuration.lifeCycle();
    }

    private ConnectionRegistry registry() {
        return ConnectionRegistry.withMap(connections);
    }

    public boolean shutdownInProgress() {
        return serverContext.shutdownInProgress();
    }

    public FileDescriptor socket() {
        return serverContext.socket();
    }

    public CoreLogger logger() {
        return shutdownPromise.logger();
    }

    public Promise<Unit> handleConnection(final ClientConnection<?> clientConnection) {
        return configuration.connectionHandler()
                            .apply(connectionContext(this, clientConnection));
    }

    public static Promise<SizeT> echo(final ReadConnectionContext context, final SizeT bytesRead, final Submitter submitter) {
        //context.onClose().logger().info("Read: {0}", bytesRead);
        return submitter.write(context.socket(), context.buffer(), OffsetT.ZERO, empty())
                        .onFailure(failure -> context.onClose()
                                                     .logger()
                                                     .info("Write error: {0}", failure))
                        .onSuccess(bytesWritten -> {
                            if (!bytesWritten.equals(bytesRead)) {
                                context.onClose()
                                       .logger()
                                       .info("Write != Read, {0} != {1}, buffer {2}", bytesWritten, bytesRead, context.buffer().used());
                            }
                        })
                //        .onSuccess(bytesWritten -> context.onClose().logger().info("Write: {0}", bytesWritten))
                ;
    }

    public void shutdown() {
        serverContext.shutdown(shutdownPromise);
    }

    public Promise<Unit> shutdownPromise() {
        return shutdownPromise;
    }
}

package org.reactivetoolbox.io.async.net.context;

import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.core.lang.support.ULID;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.net.ClientConnection;

public class ConnectionContext {
    private final ActiveServerContext serverContext;
    private final ClientConnection<?> clientConnection;
    private final ULID id;

    public ConnectionContext(final ActiveServerContext serverContext, final ClientConnection<?> clientConnection, final ULID id) {
        this.serverContext = serverContext;
        this.clientConnection = clientConnection;
        this.id = id;
    }

    public static ConnectionContext connectionContext(final ActiveServerContext context, final ClientConnection<?> clientConnection) {
        return new ConnectionContext(context, clientConnection, ULID.randomULID());
    }

    public ULID id() {
        return id;
    }

    public ActiveServerContext serverContext() {
        return serverContext;
    }

    public ClientConnection<?> clientConnection() {
        return clientConnection;
    }

    public void processConnection(final Promise<Unit> completionPromise) {
        serverContext.lifeCycle()
                     .process(this, completionPromise);
    }

    public FileDescriptor socket() {
        return clientConnection.socket();
    }
}

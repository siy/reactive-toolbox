package org.reactivetoolbox.io.async.context;

import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.net.SocketAddress;

/**
 * Initial context for every client connection.
 */
public class ConnectionContext {
    private final FileDescriptor socket;
    private final SocketAddress<?> serverAddress;
    private final SocketAddress<?> clientAddress;

    private ConnectionContext(final FileDescriptor socket, final SocketAddress<?> serverAddress, final SocketAddress<?> clientAddress) {
        this.socket = socket;
        this.serverAddress = serverAddress;
        this.clientAddress = clientAddress;
    }

    public static ConnectionContext context(final FileDescriptor socket, final SocketAddress<?> serverAddress, final SocketAddress<?> clientAddress) {
        return new ConnectionContext(socket, serverAddress, clientAddress);
    }
}

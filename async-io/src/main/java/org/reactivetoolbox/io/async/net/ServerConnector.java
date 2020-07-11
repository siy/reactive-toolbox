package org.reactivetoolbox.io.async.net;

import org.reactivetoolbox.io.async.file.FileDescriptor;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Server connector handles incoming external connections.
 */
//TODO: finish implementation
public class ServerConnector<T extends SocketAddress<?>> {
    private final FileDescriptor socket;
    private final T address;
    private final int queueDepth;
    private final AtomicBoolean stopInProgress = new AtomicBoolean(false);

    private ServerConnector(final FileDescriptor socket, final T address, final int queueDepth) {
        this.socket = socket;
        this.address = address;
        this.queueDepth = queueDepth;
    }

    @SuppressWarnings("unchecked")
    public static <T extends SocketAddress<?>> ServerConnector<T> connector(final FileDescriptor socket, final SocketAddress<?> address, final int queueDepth) {
        return new ServerConnector<>(socket, (T) address, queueDepth);
    }

    public FileDescriptor socket() {
        return socket;
    }

    public T address() {
        return address;
    }

    @Override
    public String toString() {
        return "ServerConnector(" + socket + ", " + address + ')';
    }
}

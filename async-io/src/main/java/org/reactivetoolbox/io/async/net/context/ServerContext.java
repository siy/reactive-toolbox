package org.reactivetoolbox.io.async.net.context;

import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.net.SocketAddress;

/**
 * Server connector handles incoming external connections.
 */
public class ServerContext<T extends SocketAddress<?>> {
    private final FileDescriptor socket;
    private final T address;
    private final int queueDepth;

    private ServerContext(final FileDescriptor socket, final T address, final int queueDepth) {
        this.socket = socket;
        this.address = address;
        this.queueDepth = queueDepth;
    }

    @SuppressWarnings("unchecked")
    public static <T extends SocketAddress<?>> ServerContext<T> connector(final FileDescriptor socket, final SocketAddress<?> address, final int queueDepth) {
        return new ServerContext<>(socket, (T) address, queueDepth);
    }

    public FileDescriptor socket() {
        return socket;
    }

    public T address() {
        return address;
    }

    @Override
    public String toString() {
        return "ServerContext(" + socket + ", " + address + ')';
    }
}

package org.reactivetoolbox.io.async.net;

import org.reactivetoolbox.io.async.file.FileDescriptor;

public class ClientConnection<T extends SocketAddress<?>> {
    private final FileDescriptor socket;
    private final T address;

    private ClientConnection(final FileDescriptor socket, final T address) {
        this.socket = socket;
        this.address = address;
    }

    public FileDescriptor socket() {
        return socket;
    }

    public T address() {
        return address;
    }

    public static ClientConnection<SocketAddressIn> connectionIn(final FileDescriptor fileDescriptor, final SocketAddressIn addressIn) {
        return new ClientConnection<>(fileDescriptor, addressIn);
    }

    public static ClientConnection<SocketAddressIn6> connectionIn6(final FileDescriptor fileDescriptor, final SocketAddressIn6 addressIn6) {
        return new ClientConnection<>(fileDescriptor, addressIn6);
    }
}

package org.reactivetoolbox.io.async.net;

import org.reactivetoolbox.io.async.file.FileDescriptor;

/**
 * Client connection descriptor holds client address and file descriptor which can be used to communicate with client.
 * Also, for connection tracking purposes every connection receives unique ID upon creation. The ID uniqueness is guaranteed
 * only within given VM.
 */
public class ClientConnection<T extends SocketAddress<?>> {
    //private final ConnectionId connectionId;
    private final FileDescriptor socket;
    private final T address;

    //private ClientConnection(final ConnectionId connectionId, final FileDescriptor socket, final T address) {
    private ClientConnection(final FileDescriptor socket, final T address) {
        //this.connectionId = connectionId;
        this.socket = socket;
        this.address = address;
    }

//    public ConnectionId connectionId() {
//        return connectionId;
//    }

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

    @Override
    public String toString() {
        return "ClientConnection(" + socket + ", " + address + ')';
    }
}

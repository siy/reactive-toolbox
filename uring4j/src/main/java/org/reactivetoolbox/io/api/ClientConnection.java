package org.reactivetoolbox.io.api;

import org.reactivetoolbox.io.api.net.SocketAddress;

public class ClientConnection {
    private final FileDescriptor handle;
    private final SocketAddress<?> address;

    protected ClientConnection(final FileDescriptor handle, final SocketAddress<?> address) {
        this.handle = handle;
        this.address = address;
    }

    public FileDescriptor handle() {
        return handle;
    }

    public SocketAddress<?> address() {
        return address;
    }
}

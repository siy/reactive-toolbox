package org.reactivetoolbox.io.async.net;

import org.reactivetoolbox.io.async.file.FileDescriptor;

public interface ServerConnector {
    //TODO: finish implementation
    static ServerConnector connector(FileDescriptor fileDescriptor, SocketAddress<?> socketAddress) {
        return null;
    }
}

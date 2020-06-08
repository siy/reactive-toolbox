package org.reactivetoolbox.io.async.net;

import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.async.FileDescriptor;

public interface ServerConnector {

    //TODO: finish implementation
    static ServerConnector connector(FileDescriptor fileDescriptor, SocketAddress<?> socketAddress) {
        return null;
    }
}

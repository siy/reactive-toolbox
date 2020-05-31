package org.reactivetoolbox.io.async.net;

public interface SocketAddress<T extends InetAddress> {
    AddressFamily family();
    InetPort port();
    T address();
}

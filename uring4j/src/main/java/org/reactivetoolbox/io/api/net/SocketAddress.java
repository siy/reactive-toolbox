package org.reactivetoolbox.io.api.net;

public interface SocketAddress<T extends InetAddress> {
    AddressFamily family();
    InetPort port();
    T address();
}

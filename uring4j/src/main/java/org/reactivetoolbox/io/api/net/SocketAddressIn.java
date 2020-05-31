package org.reactivetoolbox.io.api.net;

public interface SocketAddressIn extends SocketAddress {
    InetPort port();

    Inet4Address address();
}

package org.reactivetoolbox.io.async.net;

public interface SocketAddressIn extends SocketAddress {
    InetPort port();

    Inet4Address address();
}

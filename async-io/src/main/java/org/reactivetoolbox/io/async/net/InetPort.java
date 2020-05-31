package org.reactivetoolbox.io.async.net;

public class InetPort {
    private final short port;

    private InetPort(final short port) {
        this.port = port;
    }

    public static InetPort inetPort(final short port) {
        return new InetPort(port);
    }

    public short port() {
        return port;
    }
}

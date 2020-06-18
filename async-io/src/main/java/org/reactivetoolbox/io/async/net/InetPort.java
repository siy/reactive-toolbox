package org.reactivetoolbox.io.async.net;

public class InetPort {
    private final short port;

    private InetPort(final short port) {
        this.port = port;
    }

    public static InetPort inetPort(final int port) {
        return new InetPort((short) port);
    }

    public short port() {
        return port;
    }

    @Override
    public String toString() {
        return "InetPort(" + port + ")";
    }
}

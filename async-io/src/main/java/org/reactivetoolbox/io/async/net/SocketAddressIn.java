package org.reactivetoolbox.io.async.net;

public class SocketAddressIn implements SocketAddress<Inet4Address> {
    private final AddressFamily family;
    private final InetPort port;
    private final Inet4Address address;

    private SocketAddressIn(final AddressFamily family, final InetPort port, final Inet4Address address) {
        this.family = family;
        this.port = port;
        this.address = address;
    }

    @Override
    public AddressFamily family() {
        return family;
    }

    @Override
    public InetPort port() {
        return port;
    }

    @Override
    public Inet4Address address() {
        return address;
    }

    public static SocketAddressIn create(final AddressFamily family, final InetPort port, final Inet4Address address) {
        return new SocketAddressIn(family, port, address);
    }
}

package org.reactivetoolbox.io.async.net;

import java.util.StringJoiner;

import static org.reactivetoolbox.io.async.net.AddressFamily.INET;

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

    public static SocketAddressIn create(final InetPort port, final Inet4Address address) {
        return new SocketAddressIn(INET, port, address);
    }

    public static SocketAddressIn create(final AddressFamily addressFamily, final InetPort port, final Inet4Address address) {
        return new SocketAddressIn(addressFamily, port, address);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "SocketAddressIn(", ")")
                .add(family.toString())
                .add(port.toString())
                .add(address.toString())
                .toString();
    }
}

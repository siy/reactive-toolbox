package org.reactivetoolbox.io.async.net;

import java.util.Arrays;

public class Inet4Address implements InetAddress {
    public static final int SIZE = 4;
    public static final Inet4Address INADDR_ANY = new Inet4Address(new byte[SIZE]);

    private final byte[] address;

    private Inet4Address(final byte[] address) {
        this.address = address;
    }

    //TODO: switch to Option/Result?
    public static Inet4Address unsafeFrom(final byte[] address) {
        if (address.length != 4) {
            return null;
        }
        return new Inet4Address(Arrays.copyOf(address, SIZE));
    }

    @Override
    public byte[] asBytes() {
        return address;
    }
}

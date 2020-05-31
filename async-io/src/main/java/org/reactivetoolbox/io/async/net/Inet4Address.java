package org.reactivetoolbox.io.async.net;

import java.util.Arrays;

public class Inet4Address implements InetAddress {
    private final byte[] address;

    private Inet4Address(final byte[] address) {
        this.address = address;
    }

    //TODO: switch to Option/Result?
    public static Inet4Address unsafeFrom(final byte[] address) {
        if (address.length != 4) {
            return null;
        }
        return new Inet4Address(Arrays.copyOf(address, 4));
    }

    @Override
    public byte[] asBytes() {
        return address;
    }
}

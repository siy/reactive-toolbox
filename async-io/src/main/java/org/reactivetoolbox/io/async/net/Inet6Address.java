package org.reactivetoolbox.io.async.net;

import org.reactivetoolbox.core.lang.functional.Result;

import static org.reactivetoolbox.io.NativeError.EFAULT;

public class Inet6Address implements InetAddress {
    public static final int SIZE = 16;
    private final byte[] address;

    private Inet6Address(final byte[] address) {
        this.address = address;
    }

    public static Result<Inet6Address> inet6Address(final byte[] address) {
        if (address.length != SIZE) {
            return EFAULT.result();
        }
        return Result.ok(new Inet6Address(address));
    }

    @Override
    public byte[] asBytes() {
        return address;
    }
}

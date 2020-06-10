package org.reactivetoolbox.io.async.net;

import org.reactivetoolbox.core.lang.functional.Result;

import static org.reactivetoolbox.io.NativeError.EFAULT;

public class Inet4Address implements InetAddress {
    public static final int SIZE = 4;
    public static final Inet4Address INADDR_ANY = new Inet4Address(new byte[SIZE]);

    private final byte[] address;

    private Inet4Address(final byte[] address) {
        this.address = address;
    }

    public static Result<Inet4Address> inet4Address(final byte[] address) {
        if (address.length != SIZE) {
            return EFAULT.result();
        }
        return Result.ok(new Inet4Address(address));
    }

    @Override
    public byte[] asBytes() {
        return address;
    }
}

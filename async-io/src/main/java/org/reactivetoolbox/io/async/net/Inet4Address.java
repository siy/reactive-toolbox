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
        return address.length != SIZE
               ? EFAULT.asResult()
               : Result.ok(new Inet4Address(address));
    }

    @Override
    public byte[] asBytes() {
        return address;
    }

    @Override
    public String toString() {
        return String.format("Inet4Address(%d.%d.%d.%d)",
                             (int) address[0] & 0xFF,
                             (int) address[1] & 0xFF,
                             (int) address[2] & 0xFF,
                             (int) address[3] & 0xFF);
    }
}

package org.reactivetoolbox.io.uring.struct.raw;

import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.async.net.SocketAddressIn;
import org.reactivetoolbox.io.uring.struct.AbstractExternalRawStructure;
import org.reactivetoolbox.io.uring.struct.shape.SocketAddressInOffsets;

import static org.reactivetoolbox.core.lang.functional.Result.ok;
import static org.reactivetoolbox.io.async.net.AddressFamily.addressFamily;
import static org.reactivetoolbox.io.async.net.Inet4Address.inet4Address;
import static org.reactivetoolbox.io.async.net.InetPort.inetPort;
import static org.reactivetoolbox.io.uring.struct.shape.SocketAddressInOffsets.sin_addr;
import static org.reactivetoolbox.io.uring.struct.shape.SocketAddressInOffsets.sin_family;
import static org.reactivetoolbox.io.uring.struct.shape.SocketAddressInOffsets.sin_port;

public class RawSocketAddressIn extends AbstractExternalRawStructure<RawSocketAddressIn>
        implements RawSocketAddress<SocketAddressIn, RawSocketAddressIn> {
    private RawSocketAddressIn(final long address) {
        super(address, SocketAddressInOffsets.SIZE);
    }

    public static RawSocketAddressIn at(final long address) {
        return new RawSocketAddressIn(address);
    }

    public int inetAddress() {
        return getIntInNetOrder(sin_addr);
    }

    public RawSocketAddressIn inetAddress(final int address) {
        return putIntInNetOrder(sin_addr, address);
    }

    public short port() {
        return getShortInNetOrder(sin_port);
    }

    public RawSocketAddressIn port(final short port) {
        return putShortInNetOrder(sin_port, port);
    }

    public short family() {
        return getShort(sin_family);
    }

    public RawSocketAddressIn family(final short family) {
        return putShort(sin_family, family);
    }

    @Override
    public void assign(final SocketAddressIn addressIn) {
        family(addressIn.family().familyId());
        port(addressIn.port().port());
        putBytes(sin_addr, addressIn.address().asBytes());
    }

    @Override
    public Result<SocketAddressIn> extract() {
        return Result.flatten(addressFamily(family()),
                              ok(inetPort(port())),
                              inet4Address(getBytes(sin_addr)))
                     .map(tuple -> tuple.map(SocketAddressIn::create));
    }

    @Override
    public RawSocketAddressIn shape() {
        return this;
    }
}

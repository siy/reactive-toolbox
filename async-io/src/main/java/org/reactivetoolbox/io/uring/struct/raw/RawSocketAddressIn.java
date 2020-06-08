package org.reactivetoolbox.io.uring.struct.raw;

import org.reactivetoolbox.io.async.net.AddressFamily;
import org.reactivetoolbox.io.async.net.Inet4Address;
import org.reactivetoolbox.io.async.net.InetPort;
import org.reactivetoolbox.io.async.net.SocketAddressIn;
import org.reactivetoolbox.io.uring.struct.AbstractExternalRawStructure;
import org.reactivetoolbox.io.uring.struct.shape.SocketAddressInOffsets;

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
        family(addressIn.family().code());
        port(addressIn.port().port());
        putBytes(sin_addr, addressIn.address().asBytes());
    }

    @Override
    public SocketAddressIn extract() {
        return SocketAddressIn.create(AddressFamily.unsafeFromCode(family()),
                                      InetPort.inetPort(port()),
                                      Inet4Address.unsafeFrom(getBytes(sin_addr)));
    }

    @Override
    public RawSocketAddressIn shape() {
        return this;
    }
}

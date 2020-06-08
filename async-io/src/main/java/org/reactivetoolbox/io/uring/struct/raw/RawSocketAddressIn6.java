package org.reactivetoolbox.io.uring.struct.raw;

import org.reactivetoolbox.io.async.net.SocketAddressIn6;
import org.reactivetoolbox.io.uring.struct.AbstractExternalRawStructure;
import org.reactivetoolbox.io.uring.struct.shape.SocketAddressIn6Offsets;

import static org.reactivetoolbox.io.uring.struct.shape.SocketAddressIn6Offsets.sin6_addr;
import static org.reactivetoolbox.io.uring.struct.shape.SocketAddressIn6Offsets.sin6_family;
import static org.reactivetoolbox.io.uring.struct.shape.SocketAddressIn6Offsets.sin6_flowinfo;
import static org.reactivetoolbox.io.uring.struct.shape.SocketAddressIn6Offsets.sin6_port;
import static org.reactivetoolbox.io.uring.struct.shape.SocketAddressIn6Offsets.sin6_scope_id;

//TODO: finish implementation
public class RawSocketAddressIn6 extends AbstractExternalRawStructure<RawSocketAddressIn6>
        implements RawSocketAddress<SocketAddressIn6, RawSocketAddressIn6> {

    protected RawSocketAddressIn6(final long address) {
        super(address, SocketAddressIn6Offsets.SIZE);
    }

    public static RawSocketAddressIn6 at(final int address) {
        return new RawSocketAddressIn6(address);
    }

    public short family() {
        return getShort(sin6_family);
    }

    public short port() {
        return getShortInNetOrder(sin6_port);
    }

    public int flowinfo() {
        return getInt(sin6_flowinfo);
    }

    public int scopeId() {
        return getInt(sin6_scope_id);
    }

    public byte[] addr() {
        return getBytes(sin6_addr);
    }

    public RawSocketAddressIn6 family(final short family) {
        putShort(sin6_family, family);
        return this;
    }

    public RawSocketAddressIn6 port(final short port) {
        putShortInNetOrder(sin6_port, port);
        return this;
    }

    public RawSocketAddressIn6 flowinfo(final int flowinfo) {
        putInt(sin6_flowinfo, flowinfo);
        return this;
    }

    public RawSocketAddressIn6 scopeId(final int scopeId) {
        putInt(sin6_scope_id, scopeId);
        return this;
    }

    public RawSocketAddressIn6 addr(final byte[] addr) {
        putBytes(sin6_addr, addr);
        return this;
    }

    @Override
    public void assign(final SocketAddressIn6 input) {

    }

    @Override
    public SocketAddressIn6 extract() {
        return null;
    }

    @Override
    public RawSocketAddressIn6 shape() {
        return this;
    }
}

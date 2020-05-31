package org.reactivetoolbox.io.uring.structs;

import static org.reactivetoolbox.io.uring.structs.SocketAddressInOffsets.sin_addr;
import static org.reactivetoolbox.io.uring.structs.SocketAddressInOffsets.sin_family;
import static org.reactivetoolbox.io.uring.structs.SocketAddressInOffsets.sin_port;

public class RawSocketAddressIn extends AbstractExternalRawStructure<RawSocketAddressIn> {
    private RawSocketAddressIn(final long address) {
        super(address, SocketAddressInOffsets.SIZE);
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
}

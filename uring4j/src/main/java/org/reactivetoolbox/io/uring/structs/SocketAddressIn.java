package org.reactivetoolbox.io.uring.structs;

import org.reactivetoolbox.io.raw.RawMemory;

import static org.reactivetoolbox.io.uring.structs.SocketAddressInOffsets.sin_addr;
import static org.reactivetoolbox.io.uring.structs.SocketAddressInOffsets.sin_family;
import static org.reactivetoolbox.io.uring.structs.SocketAddressInOffsets.sin_port;

public class SocketAddressIn extends AbstractRawStructure<SocketAddressIn> {
    private SocketAddressIn(final long address) {
        super(address, SocketAddressInOffsets.SIZE);
    }

    public int address() {
        return getIntInNetOrder(sin_addr);
    }

    public SocketAddressIn address(final int addr) {
        return putIntInNetOrder(sin_addr, addr);
    }

    public short port() {
        return getShortInNetOrder(sin_port);
    }

    public SocketAddressIn port(final short port) {
        return putShortInNetOrder(sin_port, port);
    }

    //TODO: clarify family byte ordering
    public short family() {
        return getShort(sin_family);
    }

    //TODO: clarify family byte ordering
    public SocketAddressIn family(final short family) {
        return putShort(sin_family, family);
    }
}

package org.reactivetoolbox.io.uring.structs;

import org.reactivetoolbox.io.raw.RawMemory;

import static org.reactivetoolbox.io.uring.structs.SocketAddressInOffsets.sin_addr;
import static org.reactivetoolbox.io.uring.structs.SocketAddressInOffsets.sin_family;
import static org.reactivetoolbox.io.uring.structs.SocketAddressInOffsets.sin_port;

//TODO: make common API for such structs?
public class SocketAddressIn {
    private final long address;

    private SocketAddressIn(final long address) {
        this.address = address;
    }

    public int address() {
        return RawMemory.getIntInNetOrder(address + sin_addr.offset());
    }

    public SocketAddressIn address(final int addr) {
        RawMemory.putIntInNetOrder(address + sin_addr.offset(), addr);
        return this;
    }

    public short port() {
        return RawMemory.getShortInNetOrder(address + sin_port.offset());
    }

    public SocketAddressIn port(final short port) {
        RawMemory.putShortInNetOrder(address + sin_port.offset(), port);
        return this;
    }

    public short family() {
        return RawMemory.getShort(address + sin_family.offset());
    }

    public SocketAddressIn family(final short family) {
        RawMemory.putShort(address + sin_family.offset(), family);
        return this;
    }
}

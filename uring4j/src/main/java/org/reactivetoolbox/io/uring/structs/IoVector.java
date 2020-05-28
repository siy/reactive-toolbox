package org.reactivetoolbox.io.uring.structs;

import org.reactivetoolbox.io.raw.RawMemory;

import static org.reactivetoolbox.io.uring.structs.IoVectorOffsets.iov_base;
import static org.reactivetoolbox.io.uring.structs.IoVectorOffsets.iov_len;

//TODO: make common API for such structs?
public class IoVector {
    private final long address;

    private IoVector(final long address) {
        this.address = address;
    }

    public static IoVector at(final long address) {
        return new IoVector(address);
    }

    public long base() {
        return RawMemory.getLong(address + iov_base.offset());
    }

    public IoVector base(final long data) {
        RawMemory.putLong(address + iov_base.offset(), data);
        return this;
    }

    public long len() {
        return RawMemory.getLong(address + iov_len.offset());
    }

    public IoVector len(final long data) {
        RawMemory.putLong(address + iov_len.offset(), data);
        return this;
    }
}

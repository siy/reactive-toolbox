package org.reactivetoolbox.io.uring.structs;

import static org.reactivetoolbox.io.raw.UnsafeHolder.unsafe;
import static org.reactivetoolbox.io.uring.structs.IoVectorOffsets.iov_base;
import static org.reactivetoolbox.io.uring.structs.IoVectorOffsets.iov_len;

public class IoVector {
    private final long address;

    private IoVector(final long address) {
        this.address = address;
    }

    public static IoVector at(final long address) {
        return new IoVector(address);
    }

    public long base() {
        return unsafe().getLong(address + iov_base.offset());
    }

    public IoVector base(final long data) {
        unsafe().putLong(address + iov_base.offset(), data);
        return this;
    }

    public long len() {
        return unsafe().getLong(address + iov_len.offset());
    }

    public IoVector len(final long data) {
        unsafe().putLong(address + iov_len.offset(), data);
        return this;
    }
}

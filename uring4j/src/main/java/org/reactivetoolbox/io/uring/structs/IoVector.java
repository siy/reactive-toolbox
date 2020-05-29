package org.reactivetoolbox.io.uring.structs;

import static org.reactivetoolbox.io.uring.structs.IoVectorOffsets.iov_base;
import static org.reactivetoolbox.io.uring.structs.IoVectorOffsets.iov_len;

public class IoVector extends AbstractRawStructure<IoVector> {
    private IoVector(final long address) {
        super(address, IoVectorOffsets.SIZE);
    }

    public static IoVector at(final long address) {
        return new IoVector(address);
    }

    public long base() {
        return getLong(iov_base);
    }

    public IoVector base(final long data) {
        return putLong(iov_base, data);
    }

    public long len() {
        return getLong(iov_len);
    }

    public IoVector len(final long data) {
        return putLong(iov_len, data);
    }
}

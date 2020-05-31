package org.reactivetoolbox.io.uring.structs;

import static org.reactivetoolbox.io.uring.structs.StatxTimestampOffsets.tv_nsec;
import static org.reactivetoolbox.io.uring.structs.StatxTimestampOffsets.tv_sec;

public class RawStatxTimestamp extends AbstractExternalRawStructure<RawStatxTimestamp> {
    private RawStatxTimestamp(final long address) {
        super(address, StatxTimestampOffsets.SIZE);
    }

    public static RawStatxTimestamp at(final long address) {
        return new RawStatxTimestamp(address);
    }

    public long seconds() {
        return getLong(tv_sec);
    }

    public int nanos() {
        return getInt(tv_nsec);
    }
}

package org.reactivetoolbox.io.uring.structs;

import static org.reactivetoolbox.io.uring.structs.StatxTimestampOffsets.tv_nsec;
import static org.reactivetoolbox.io.uring.structs.StatxTimestampOffsets.tv_sec;

public class StatxTimestamp extends AbstractRawStructure<StatxTimestamp> {
    private StatxTimestamp(final long address) {
        super(address, StatxTimestampOffsets.SIZE);
    }

    public static StatxTimestamp at(final long address) {
        return new StatxTimestamp(address);
    }

    public long seconds() {
        return getLong(tv_sec);
    }

    public int nanos() {
        return getInt(tv_nsec);
    }
}

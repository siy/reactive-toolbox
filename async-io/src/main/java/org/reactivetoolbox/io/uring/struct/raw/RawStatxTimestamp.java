package org.reactivetoolbox.io.uring.struct.raw;

import org.reactivetoolbox.io.async.file.stat.StatTimestamp;
import org.reactivetoolbox.io.uring.struct.AbstractExternalRawStructure;
import org.reactivetoolbox.io.uring.struct.shape.StatxTimestampOffsets;

import static org.reactivetoolbox.io.uring.struct.shape.StatxTimestampOffsets.tv_nsec;
import static org.reactivetoolbox.io.uring.struct.shape.StatxTimestampOffsets.tv_sec;

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

    public StatTimestamp detach() {
        return StatTimestamp.timestamp(seconds(), nanos());
    }
}

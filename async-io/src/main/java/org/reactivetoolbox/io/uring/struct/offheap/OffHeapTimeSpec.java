package org.reactivetoolbox.io.uring.struct.offheap;

import org.reactivetoolbox.io.scheduler.Timeout;
import org.reactivetoolbox.io.uring.struct.AbstractOffHeapStructure;
import org.reactivetoolbox.io.uring.struct.shape.TimeSpecOffsets;

import static org.reactivetoolbox.io.uring.struct.shape.TimeSpecOffsets.tv_nsec;
import static org.reactivetoolbox.io.uring.struct.shape.TimeSpecOffsets.tv_sec;

/**
 * Container for data equivalent to {@code struct __kernel_timespec}.
 */
public class OffHeapTimeSpec extends AbstractOffHeapStructure<OffHeapTimeSpec> {
    public static final long NANO_SCALE = 1_000_000_000L;

    private OffHeapTimeSpec() {
        super(TimeSpecOffsets.SIZE);
    }

    public static OffHeapTimeSpec forTimeout(final Timeout timeout) {
        final long nanos = timeout.nanos();
        final long seconds = nanos / NANO_SCALE;

        return new OffHeapTimeSpec().seconds(seconds)
                                    .nanos(nanos % NANO_SCALE);
    }

    public long seconds() {
        return getLong(tv_sec);
    }

    public long nanos() {
        return getLong(tv_nsec);
    }

    public OffHeapTimeSpec seconds(final long seconds) {
        return putLong(tv_sec, seconds);
    }

    public OffHeapTimeSpec nanos(final long nanos) {
        return putLong(tv_nsec, nanos);
    }
}

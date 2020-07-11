package org.reactivetoolbox.io.uring.struct.offheap;

import org.reactivetoolbox.io.scheduler.Timeout;
import org.reactivetoolbox.io.uring.struct.shape.TimeSpecOffsets;

import static org.reactivetoolbox.io.uring.struct.shape.TimeSpecOffsets.tv_nsec;
import static org.reactivetoolbox.io.uring.struct.shape.TimeSpecOffsets.tv_sec;

/**
 * Container for data equivalent to {@code struct __kernel_timespec}.
 */
public class OffHeapTimeSpec extends AbstractOffHeapStructure<OffHeapTimeSpec> {
    private OffHeapTimeSpec() {
        super(TimeSpecOffsets.SIZE);
    }

    public static OffHeapTimeSpec forSecondsNanos(final long seconds, final long nanos) {
        return new OffHeapTimeSpec()
                .putLong(tv_sec, seconds)
                .putLong(tv_nsec, nanos);
    }

    public static OffHeapTimeSpec forTimeout(final Timeout timeout) {
        return timeout.asSecondsAndNanos()
                      .map(OffHeapTimeSpec::forSecondsNanos);
    }
}

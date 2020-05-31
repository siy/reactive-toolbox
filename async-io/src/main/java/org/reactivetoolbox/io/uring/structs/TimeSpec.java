package org.reactivetoolbox.io.uring.structs;

import org.reactivetoolbox.io.scheduler.Timeout;

import static org.reactivetoolbox.io.uring.structs.TimeSpecOffsets.tv_nsec;
import static org.reactivetoolbox.io.uring.structs.TimeSpecOffsets.tv_sec;

/**
 * Container for data equivalent to {@code struct __kernel_timespec}.
 */
public class TimeSpec extends AbstractDisposableStructure<TimeSpec> {
    public static final long NANO_SCALE = 1_000_000_000L;

    private TimeSpec() {
        super(TimeSpecOffsets.SIZE);
    }

    public static TimeSpec forTimeout(final Timeout timeout) {
        final long nanos = timeout.nanos();
        final long seconds = nanos / NANO_SCALE;

        return new TimeSpec().seconds(seconds)
                             .nanos(nanos % NANO_SCALE);
    }

    public long seconds() {
        return getLong(tv_sec);
    }

    public long nanos() {
        return getLong(tv_nsec);
    }

    public TimeSpec seconds(final long seconds) {
        return putLong(tv_sec, seconds);
    }

    public TimeSpec nanos(final long nanos) {
        return putLong(tv_nsec, nanos);
    }
}

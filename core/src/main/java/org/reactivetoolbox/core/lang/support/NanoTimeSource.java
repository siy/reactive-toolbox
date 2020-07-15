package org.reactivetoolbox.core.lang.support;

import java.time.Instant;

/**
 * Time source with nanosecond resolution.
 * <p>
 * WARNING: this time source is derived from {@link System#nanoTime()} and is not accurate!
 */
public final class NanoTimeSource {
    private static final long NANOS_PER_MILLI = 1_000_000;
    private static final long NANOS_PER_SECOND = 1_000_000_000;
    private static final long OFFSET = System.currentTimeMillis() * NANOS_PER_MILLI - System.nanoTime();

    private NanoTimeSource() {
    }

    public static long timestamp() {
        return OFFSET + System.nanoTime();
    }

    public static Instant instant() {
        long stamp = timestamp();

        return Instant.ofEpochSecond(stamp / NANOS_PER_SECOND,
                                     stamp % NANOS_PER_SECOND);
    }
}

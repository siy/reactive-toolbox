package org.reactivetoolbox.core.lang.support;

import org.reactivetoolbox.core.scheduler.Timeout;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple Clock suitable for testing purposes.
 * <br/>
 */

//TODO: does not work properly for steps below 1ms
public class ClockMock extends Clock {
    private final AtomicLong counter;
    private final long tickDuration;

    private ClockMock(final LocalDateTime base, final Timeout step) {
        this.counter = new AtomicLong(base.toInstant(ZoneOffset.UTC).toEpochMilli());
        this.tickDuration = step.timeout();
    }

    public static ClockMock with(final LocalDateTime startingPoint, final Timeout stepDuration) {
        return new ClockMock(startingPoint, stepDuration);
    }

    @Override
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(final ZoneId zoneId) {
        throw new UnsupportedOperationException("This clock does not support zones other than UTC");
    }

    @Override
    public Instant instant() {
        return Instant.ofEpochMilli(counter.addAndGet(tickDuration));
    }
}

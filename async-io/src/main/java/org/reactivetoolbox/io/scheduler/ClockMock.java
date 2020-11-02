/*
 * Copyright (c) 2020 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.reactivetoolbox.io.scheduler;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple Clock suitable for testing purposes. <br/>
 */
public class ClockMock extends Clock {
    private final AtomicLong counter;
    private final long tickDuration;

    private ClockMock(final LocalDateTime base, final Timeout step) {
        counter = new AtomicLong(base.toInstant(ZoneOffset.UTC).getNano());
        tickDuration = step.asNanos();
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
        return Timeout.timeout(counter.addAndGet(tickDuration))
                      .nanos()
                      .asSecondsAndNanos()
                      .map(Instant::ofEpochSecond);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof ClockMock clockMock) {
            return tickDuration == clockMock.tickDuration &&
                   counter.get() == clockMock.counter.get();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), counter, tickDuration);
    }
}

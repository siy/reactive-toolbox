package org.reactivetoolbox.core.scheduler;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

//TODO: requires rework
public final class Timeout implements Comparable<Timeout> {
    private final long timeout;
    private final long timestamp;

    private Timeout(final long timeout) {
        this.timeout = timeout;
        timestamp = System.currentTimeMillis() + timeout;
    }

    public static TimeoutBuilder of(final long value) {
        return new TimeoutBuilder(value);
    }

    public long timestamp() {
        return timestamp;
    }

    public long timeout() {
        return timeout;
    }

    @Override
    public int compareTo(final Timeout o) {
        final long res = timestamp - o.timestamp;
        return res == 0 ? 0 : res < 0 ? -1 : 1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Timeout timeout = (Timeout) o;
        return timestamp == timeout.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }

    @Override
    public String toString() {
        return "Timestamp=" + timestamp + " (" + timeout + ")";
    }

    public static final class TimeoutBuilder {
        private final long value;

        private TimeoutBuilder(final long value) {
            this.value = value;
        }

        public Timeout millis() {
            return new Timeout(value);
        }

        public Timeout seconds() {
            return new Timeout(TimeUnit.SECONDS.toMillis(value));
        }
    }
}

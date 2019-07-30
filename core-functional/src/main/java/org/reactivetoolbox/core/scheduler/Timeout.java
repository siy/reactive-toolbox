package org.reactivetoolbox.core.scheduler;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class Timeout implements Comparable<Timeout> {
    private final long timestamp;

    private Timeout(final long timestamp) {
        this.timestamp = timestamp;
    }

    public static TimeoutBuilder of(final long value) {
        return new TimeoutBuilder(value);
    }

    public long timestamp() {
        return timestamp;
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
        return "Timestamp=" + timestamp;
    }

    public static final class TimeoutBuilder {
        private final long value;

        private TimeoutBuilder(final long value) {
            this.value = value;
        }

        public Timeout millis() {
            return new Timeout(System.currentTimeMillis() + value);
        }

        public Timeout seconds() {
            return new Timeout(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(value));
        }
    }
}

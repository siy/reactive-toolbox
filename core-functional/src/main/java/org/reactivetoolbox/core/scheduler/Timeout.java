package org.reactivetoolbox.core.scheduler;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Task timeout
 */
public final class Timeout {
    private final long timeout;

    private Timeout(final long timeout) {
        this.timeout = timeout;
    }

    public static TimeoutBuilder of(final long value) {
        return new TimeoutBuilder(value);
    }

    public long timeout() {
        return timeout;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Timeout timeout1 = (Timeout) o;
        return timeout == timeout1.timeout;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeout);
    }

    @Override
    public String toString() {
        return "Timeout{ timeout=" + timeout + "ms }";
    }

    /**
     * Fluent interval conversion builder
     */
    public static final class TimeoutBuilder {
        private final long value;

        private TimeoutBuilder(final long value) {
            this.value = value;
        }

        /**
         * Create {@link Timeout} instance by interpreting value as milliseconds.
         *
         * @return Created instance
         */
        public Timeout millis() {
            return new Timeout(value);
        }

        /**
         * Create {@link Timeout} instance by interpreting value as seconds.
         *
         * @return Created instance
         */
        public Timeout sec() {
            return new Timeout(TimeUnit.SECONDS.toMillis(value));
        }

        /**
         * Create {@link Timeout} instance by interpreting value as minutes.
         *
         * @return Created instance
         */
        public Timeout min() {
            return new Timeout(TimeUnit.MINUTES.toMillis(value));
        }

        /**
         * Create {@link Timeout} instance by interpreting value as hours.
         *
         * @return Created instance
         */
        public Timeout hrs() {
            return new Timeout(TimeUnit.HOURS.toMillis(value));
        }
    }
}

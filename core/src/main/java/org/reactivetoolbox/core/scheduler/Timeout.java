package org.reactivetoolbox.core.scheduler;
/*
 * Copyright (c) 2017-2019 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Task timeout (in milliseconds)
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

    public long nanos() {
        return TimeUnit.MILLISECONDS.toNanos(timeout);
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

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
 * Simple nanosecond timestamp for scheduling purposes. Tightly coupled to current JVM high resolution time source
 * and has no relation to calendar time. Should not be used to anything related to date/time calculation nor
 * serialized/moved to other JVM.
 */
public class Timestamp implements Comparable<Timestamp> {
    private final long timestamp;

    private Timestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public static Timestamp of(final long timestamp) {
        return new Timestamp(timestamp);
    }

    public static Timestamp from(final Timeout timeout) {
        return new Timestamp(TimeUnit.MILLISECONDS.toNanos(timeout.timeout()) + System.nanoTime());
    }

    public long timestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(final Timestamp o) {
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
        final Timestamp timestamp1 = (Timestamp) o;
        return timestamp == timestamp1.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }

    @Override
    public String toString() {
        return "Timestamp {" +
               "timestamp=" + timestamp +
               '}';
    }
}

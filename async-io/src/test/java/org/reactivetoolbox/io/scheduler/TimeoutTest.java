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

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.reactivetoolbox.io.scheduler.Timeout.timeout;

class TimeoutTest {
    @Test
    void timeoutCreatedProperly() {
        assertEquals(1234, timeout(1_234_000_000L).nanos().asMillis());
        assertEquals(1234, timeout(1_234_000L).micros().asMillis());
        assertEquals(TimeUnit.SECONDS.toMillis(123), timeout(123).seconds().asMillis());
        assertEquals(TimeUnit.MINUTES.toMillis(12), timeout(12).minutes().asMillis());
        assertEquals(TimeUnit.HOURS.toMillis(32), timeout(32).hours().asMillis());
        assertEquals(TimeUnit.NANOSECONDS.toNanos(32), timeout(32).nanos().asNanos());
    }

    @Test
    void timeoutsAreEqualDespiteUnitUsedForCreation() {
        assertEquals(timeout(5).micros(), timeout(5_000).nanos());
        assertEquals(timeout(7).millis(), timeout(7_000).micros());
        assertEquals(timeout(1).seconds(), timeout(1000).millis());
        assertEquals(timeout(3600).seconds(), timeout(1).hours());
        assertEquals(timeout(600).minutes(), timeout(10).hours());
        assertEquals(timeout(72).hours(), timeout(3).days());
    }
}
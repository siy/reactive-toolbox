package org.reactivetoolbox.io.scheduler;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.reactivetoolbox.io.scheduler.Timeout.timeout;

class TimeoutTest {
    @Test
    void timeoutCreatedProperly() {
        assertEquals(1234, timeout(1_234_000_000L).nanos().millis());
        assertEquals(1234, timeout(1_234_000L).micros().millis());
        assertEquals(TimeUnit.SECONDS.toMillis(123), timeout(123).seconds().millis());
        assertEquals(TimeUnit.MINUTES.toMillis(12), timeout(12).minutes().millis());
        assertEquals(TimeUnit.HOURS.toMillis(32), timeout(32).hours().millis());
        assertEquals(TimeUnit.NANOSECONDS.toNanos(32), timeout(32).nanos().nanos());
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
package org.reactivetoolbox.core.scheduler;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.reactivetoolbox.core.scheduler.Timeout.timeout;

class TimeoutTest {
    @Test
    void timeoutCreatedProperly() {
        assertEquals(1234, timeout(1234).millis().timeout());
        assertEquals(TimeUnit.SECONDS.toMillis(123), timeout(123).seconds().timeout());
        assertEquals(TimeUnit.MINUTES.toMillis(12), timeout(12).minutes().timeout());
        assertEquals(TimeUnit.HOURS.toMillis(32), timeout(32).hours().timeout());
    }

    @Test
    void timeoutsAreEqualDespiteUnitUsedForCreation() {
        assertEquals(timeout(1).seconds(), timeout(1000).millis());
        assertEquals(timeout(3600).seconds(), timeout(1).hours());
        assertEquals(timeout(600).minutes(), timeout(10).hours());
    }
}
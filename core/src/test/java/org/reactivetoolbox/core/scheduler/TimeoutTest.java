package org.reactivetoolbox.core.scheduler;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeoutTest {
    @Test
    void timeoutCreatedProperly() {
        assertEquals(1234, Timeout.of(1234).millis().timeout());
        assertEquals(TimeUnit.SECONDS.toMillis(123), Timeout.of(123).sec().timeout());
        assertEquals(TimeUnit.MINUTES.toMillis(12), Timeout.of(12).min().timeout());
        assertEquals(TimeUnit.HOURS.toMillis(32), Timeout.of(32).hrs().timeout());
    }
}
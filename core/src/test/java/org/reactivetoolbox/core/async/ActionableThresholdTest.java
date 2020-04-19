package org.reactivetoolbox.core.async;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionableThresholdTest {
    @Test
    void actionIsTriggeredOnceThresholdIsReached() {
        final AtomicBoolean marker = new AtomicBoolean();
        final ActionableThreshold threshold = ActionableThreshold.threshold(3, () -> marker.compareAndSet(false, true));

        assertFalse(marker.get());

        threshold.registerEvent();
        assertFalse(marker.get());

        threshold.registerEvent();
        assertFalse(marker.get());

        threshold.registerEvent();
        assertTrue(marker.get());
    }

    @Test
    void subsequentEventsDontTriggerAction() {
        final AtomicInteger counter = new AtomicInteger(0);
        final ActionableThreshold threshold = ActionableThreshold.threshold(3, counter::incrementAndGet);

        assertEquals(0, counter.get());

        threshold.registerEvent();
        assertEquals(0, counter.get());

        threshold.registerEvent();
        assertEquals(0, counter.get());

        threshold.registerEvent();
        assertEquals(1, counter.get());

        threshold.registerEvent();
        assertEquals(1, counter.get());
    }
}
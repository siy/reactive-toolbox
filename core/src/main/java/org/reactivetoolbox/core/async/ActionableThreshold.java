package org.reactivetoolbox.core.async;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Helper class used to track number of events and trigger action once threshold is reached. The action is
 * triggered only once, when number of events exactly matches configured threshold.
 */
public class ActionableThreshold {
    private final AtomicInteger counter;
    private final Runnable action;

    private ActionableThreshold(final int count, final Runnable action) {
        counter = new AtomicInteger(count);
        this.action = action;
    }

    /**
     * Create an instance configured for threshold and action.
     *
     * @param count
     *        Number of events to register
     * @param action
     *        Action to perform
     *
     * @return Created instance
     */
    public static ActionableThreshold of(final int count, final Runnable action) {
        return new ActionableThreshold(count, action);
    }

    /**
     * Register event and perform action if threshold is reached. Once threshold is reached
     * no further events will trigger action execution.
     */
    public void registerEvent() {
        if (counter.get() <= 0) {
            return;
        }

        if (counter.decrementAndGet() == 0) {
            action.run();
        }
    }
}

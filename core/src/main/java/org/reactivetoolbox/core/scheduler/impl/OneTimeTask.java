package org.reactivetoolbox.core.scheduler.impl;

import org.reactivetoolbox.core.scheduler.Timeout;
import org.reactivetoolbox.core.scheduler.Timestamp;

/**
 * Container for the task which should be executed at some point in time in future.
 * Note that it is not based on calendar time and tightly coupled with current JVM
 * instance, so should not be serialized or moved to other JVM.
 */
public class OneTimeTask {
    private final Runnable runnable;
    private final Timestamp timestamp;

    private OneTimeTask(final Runnable runnable, final Timestamp timestamp) {
        this.runnable = runnable;
        this.timestamp = timestamp;
    }

    public static OneTimeTask of(final Runnable runnable, final Timeout timeout) {
        return new OneTimeTask(runnable, Timestamp.from(timeout));
    }

    public static OneTimeTask of(final Runnable runnable, final Timestamp timestamp) {
        return new OneTimeTask(runnable, timestamp);
    }

    public Timestamp timestamp() {
        return timestamp;
    }

    public Runnable runnable() {
        return runnable;
    }
}

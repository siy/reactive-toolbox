package org.reactivetoolbox.core.scheduler;

public class OneTimeTask {
    private final Timeout timeout;
    private final Runnable runnable;

    public OneTimeTask(final Runnable runnable, final Timeout timeout) {
        this.runnable = runnable;
        this.timeout = timeout;
    }

    public static OneTimeTask of(final Runnable runnable, final Timeout timeout) {
        return new OneTimeTask(runnable, timeout);
    }

    public Timeout timeout() {
        return timeout;
    }

    public Runnable runnable() {
        return runnable;
    }
}

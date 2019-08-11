package org.reactivetoolbox.core.scheduler.impl;

import org.reactivetoolbox.core.scheduler.Timeout;
import org.reactivetoolbox.core.scheduler.TimeoutHandle;

public class BufferEntryTimeoutHandle implements TimeoutHandle {
    private final BufferEntry<OneTimeTask> entry;

    private BufferEntryTimeoutHandle(final BufferEntry<OneTimeTask> entry) {
        this.entry = entry;
    }

    public static TimeoutHandle of(final BufferEntry<OneTimeTask> entry) {
        return new BufferEntryTimeoutHandle(entry);
    }

    @Override
    public TimeoutHandle submit(final Timeout timeout, final Runnable runnable) {
        entry.add(OneTimeTask.of(runnable, timeout));
        return this;
    }

    @Override
    public void release() {
        entry.release();
    }
}

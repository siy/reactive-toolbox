package org.reactivetoolbox.core.scheduler.impl;

import org.reactivetoolbox.core.scheduler.Handle;
import org.reactivetoolbox.core.scheduler.OneTimeTask;
import org.reactivetoolbox.core.scheduler.Timeout;

public class BufferEntryHandle implements Handle {
    private final BufferEntry<OneTimeTask> entry;

    private BufferEntryHandle(final BufferEntry<OneTimeTask> entry) {
        this.entry = entry;
    }

    public static Handle of(final BufferEntry<OneTimeTask> entry) {
        return new BufferEntryHandle(entry);
    }

    @Override
    public Handle submit(final Timeout timeout, final Runnable runnable) {
        entry.add(OneTimeTask.of(runnable, timeout));
        return this;
    }

    @Override
    public void release() {
        entry.release();
    }
}

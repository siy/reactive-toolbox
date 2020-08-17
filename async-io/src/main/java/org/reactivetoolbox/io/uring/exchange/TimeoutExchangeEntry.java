package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.scheduler.Timeout;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapTimeSpec;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_LINK_TIMEOUT;

public class TimeoutExchangeEntry extends AbstractExchangeEntry<TimeoutExchangeEntry, Unit> {
    private final OffHeapTimeSpec timeSpec = OffHeapTimeSpec.uninitialized();

    protected TimeoutExchangeEntry(final PlainObjectPool<TimeoutExchangeEntry> pool) {
        super(IORING_OP_LINK_TIMEOUT, pool);
    }

    @Override
    public void close() {
        timeSpec.dispose();
    }

    @Override
    protected void doAccept(final int res, final int flags) {
    }

    public TimeoutExchangeEntry prepare(final Timeout timeout) {
        timeout.asSecondsAndNanos()
               .map(timeSpec::setSecondsNanos);

        return this;
    }

    @Override
    public SubmitQueueEntry apply(final SubmitQueueEntry entry) {
        return super.apply(entry)
                    .addr(timeSpec.address())
                    .fd(-1)
                    .len(1);
    }
}

package org.reactivetoolbox.core.scheduler;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.scheduler.impl.RingBufferTimeoutScheduler;

public interface TimeoutScheduler {
    Either<? extends BaseError, Handle> request();

    static TimeoutScheduler with(final int size) {
        return RingBufferTimeoutScheduler.with(size);
    }
}

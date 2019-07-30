package org.reactivetoolbox.core.scheduler;

import org.reactivetoolbox.core.async.BaseError;

public enum SchedulerError implements BaseError {
    NO_FREE_SLOTS,
    TIMEOUT
}

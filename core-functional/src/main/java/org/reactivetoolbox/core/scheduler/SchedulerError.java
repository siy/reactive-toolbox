package org.reactivetoolbox.core.scheduler;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;

public enum SchedulerError implements BaseError {
    NO_FREE_SLOTS(500, "Internal error, no free slots in timeout scheduler"),
    TIMEOUT(408, "Processing timeout error");

    private final int code;
    private final String message;

    SchedulerError(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public <T> Either<SchedulerError, T> asFailure() {
        return Either.failure(this);
    }
}

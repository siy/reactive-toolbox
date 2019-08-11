package org.reactivetoolbox.value.conversion.type;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;

public enum TypeError implements BaseError {
    UNABLE_TO_RECOGNIZE_TYPE(500, "Internal server error: unable to recognize parameter type");

    private final int code;
    private final String message;

    TypeError(final int code, final String message) {
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
    public <T> Either<TypeError, T> asFailure() {
        return Either.failure(this);
    }
}

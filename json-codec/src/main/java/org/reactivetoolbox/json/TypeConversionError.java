package org.reactivetoolbox.json;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;

public enum TypeConversionError implements BaseError {
    UNKNOWN_TYPE(422, "Unable to convert type"),
    INCORRECT_USE_OF_OPTION(500, "Incorrect use of Option<> class"),
    INCORRECT_USE_OF_OPTIONAL(500, "Incorrect use of Optional<> class");
    private final int code;
    private final String message;

    TypeConversionError(final int code, final String message) {
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
    public <T> Either<TypeConversionError, T> asFailure() {
        return Either.failure(this);
    }
}

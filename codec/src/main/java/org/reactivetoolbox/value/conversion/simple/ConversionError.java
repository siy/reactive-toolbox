package org.reactivetoolbox.value.conversion.simple;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;

public enum ConversionError implements BaseError {
    NOT_AN_INTEGER_NUMBER(422, "Value is not an integer number"),
    NOT_A_LONG_INTEGER_NUMBER(422, "Value is not a long integer number"),
    NOT_A_DOUBLE_NUMBER(422, "Value is not a valid double precision floating point number"),
    NOT_A_VALID_UUID(422, "Value is not a valid UUID"),
    NOT_A_BOOLEAN(422, "Value is not a valid boolean"),
    NOT_A_NUMBER(422, "Value is not a number");

    private final int code;
    private final String message;

    ConversionError(final int code, final String message) {
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
    public <T> Either<ConversionError, T> asFailure() {
        return Either.failure(this);
    }
}

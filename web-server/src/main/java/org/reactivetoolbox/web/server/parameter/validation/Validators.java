package org.reactivetoolbox.web.server.parameter.validation;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;

import java.util.Optional;

public interface Validators {
    interface Validator<R, T> extends FN1<Either<? extends BaseError, R>, T> {
    }

    static <T> Either<ValidationError, T> notNull(final Optional<T> input) {
        return input.map(Either::<ValidationError, T>success)
                .orElseGet(() -> Either.failure(ValidationError.STRING_IS_NULL));
    }

    static Either<ValidationError, String> notNullOrEmpty(final Optional<String> input) {
        return input.map(val -> val.isBlank()
                ? Either.<ValidationError, String>failure(ValidationError.STRING_IS_EMPTY)
                : Either.<ValidationError, String>success(val))
                .orElseGet(() -> Either.failure(ValidationError.STRING_IS_NULL));
    }

    static <T extends Number> Either<ValidationError, T> range(final T input, final int min, final int max) {
        return input.intValue() < min
                ? Either.failure(ValidationError.NUMBER_IS_BELOW_LOWER_BOUND)
                : input.intValue() > max
                        ? Either.failure(ValidationError.NUMBER_IS_ABOVE_UPPER_BOUND)
                        : Either.success(input);
    }

    static <T extends Number> Either<ValidationError, T> range(final T input, final double min, final double max) {
        return input.doubleValue() < min
                ? Either.failure(ValidationError.NUMBER_IS_BELOW_LOWER_BOUND)
                : input.doubleValue() > max
                        ? Either.failure(ValidationError.NUMBER_IS_ABOVE_UPPER_BOUND)
                        : Either.success(input);
    }

    static Either<ValidationError, String> stringLen(final String input, final int minLen, final int maxLen) {
        return input.length() < minLen
                        ? Either.failure(ValidationError.STRING_TOO_SHORT)
                        : input.length() < maxLen
                                ? Either.failure(ValidationError.STRING_TOO_LONG)
                                : Either.success(input);
    }
}

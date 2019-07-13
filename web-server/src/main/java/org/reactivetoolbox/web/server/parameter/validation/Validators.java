package org.reactivetoolbox.web.server.parameter.validation;

import org.reactivetoolbox.core.functional.Either;

public interface Validators {
    static <T> Either<ValidationError, T> notNull(final T input) {
        return input == null ? Either.failure(ValidationError.STRING_IS_NULL) : Either.success(input);
    }

    static Either<ValidationError, String> notNullOrEmpty(final String input) {
        return input == null
                ? Either.failure(ValidationError.STRING_IS_NULL)
                : input.isBlank()
                        ? Either.failure(ValidationError.STRING_IS_EMPTY)
                        : Either.success(input);
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

    static Either<ValidationError, String> stringLenRange(final String input, final int minLen, final int maxLen) {
        return notNullOrEmpty(input).flatMap(string ->
                string.length() < minLen
                        ? Either.failure(ValidationError.STRING_TOO_SHORT)
                        : string.length() < maxLen
                                ? Either.failure(ValidationError.STRING_TOO_LONG)
                                : Either.success(string));
    }
}

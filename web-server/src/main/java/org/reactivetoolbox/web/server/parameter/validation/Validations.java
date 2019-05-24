package org.reactivetoolbox.web.server.parameter.validation;

import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.web.server.ErrorDescriptor;

public interface Validations {
    static <T> Either<ErrorDescriptor, T> notNull(final T input) {
        return input == null ? Either.failure(ErrorDescriptor.PARAMETER_IS_NULL) : Either.success(input);
    }

    static <T extends Number> Either<ErrorDescriptor, T> range(final T input, final int min, final int max) {
        if (input.intValue() < min) {
            return Either.failure(ErrorDescriptor.PARAMETER_IS_BELOW_RANGE);
        }
        if (input.intValue() > max) {
            return Either.failure(ErrorDescriptor.PARAMETER_IS_ABOVE_RANGE);
        }
        return Either.success(input);
    }
}

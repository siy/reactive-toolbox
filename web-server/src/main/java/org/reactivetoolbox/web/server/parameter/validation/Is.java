package org.reactivetoolbox.web.server.parameter.validation;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.web.server.parameter.auth.Authentication;
import org.reactivetoolbox.web.server.parameter.auth.Role;

import static org.reactivetoolbox.core.functional.Either.failure;

public interface Is {
    static Either<? extends BaseError, String> email(String email) {
        //TODO: implement e-mail validation
        return Either.success(email);
    }

    interface Validator<R, T> extends FN1<Either<? extends BaseError, R>, T> {
    }

    static <T> Either<ValidationError, T> notNull(final Option<T> input) {
        return input.map(Either::<ValidationError, T>success)
                .otherwise(() -> failure(ValidationError.STRING_IS_NULL));
    }

    static Either<ValidationError, String> notNullOrEmpty(final Option<String> input) {
        return input.map(val -> val.isBlank()
                ? Either.<ValidationError, String>failure(ValidationError.STRING_IS_EMPTY)
                : Either.<ValidationError, String>success(val))
                .otherwise(() -> failure(ValidationError.STRING_IS_NULL));
    }

    static <T extends Number> Either<ValidationError, T> between(final T input, final int min, final int max) {
        return input.intValue() < min
                ? failure(ValidationError.NUMBER_IS_BELOW_LOWER_BOUND)
                : input.intValue() > max
                        ? failure(ValidationError.NUMBER_IS_ABOVE_UPPER_BOUND)
                        : Either.success(input);
    }

    static <T extends Number> Either<ValidationError, T> between(final T input, final double min, final double max) {
        return input.doubleValue() < min
                ? failure(ValidationError.NUMBER_IS_BELOW_LOWER_BOUND)
                : input.doubleValue() > max
                        ? failure(ValidationError.NUMBER_IS_ABOVE_UPPER_BOUND)
                        : Either.success(input);
    }

    static Either<ValidationError, String> lenBetween(final Option<String> input1, final int minLen, final int maxLen) {
        return notNull(input1)
                .flatMap(input -> input.length() < minLen
                        ? failure(ValidationError.STRING_TOO_SHORT)
                        : input.length() < maxLen
                                ? failure(ValidationError.STRING_TOO_LONG)
                                : Either.success(input));
    }

    static Either<? extends BaseError, Authentication> loggedIn(final Option<Authentication> authentication) {
        return authentication.map(Authentication::token)
                .otherwise(() -> failure(ValidationError.USER_NOT_LOGGED_IN));
    }

    static Either<? extends BaseError, String> validatePassword(final String string) {
        //TODO: implement actual password quality check
        return Either.success(string);
    }

    static Either<? extends BaseError, Authentication> belongsToAll(final Authentication authentication, final Role... roles) {
        return authentication.hasAllRoles(roles);
    }

    static Either<? extends BaseError, Authentication> belongsToAny(final Authentication authentication, final Role... roles) {
        return authentication.hasAnyRoles(roles);
    }
}

package org.reactivetoolbox.web.server.parameter.validation;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.web.server.auth.Authentication;

import java.util.Optional;

public interface AuthValidators {
    static Either<? extends BaseError, Authentication> loggedIn(Optional<Authentication> authentication) {
        return authentication.map(Authentication::token).orElseGet(() -> Either.failure(ValidationError.USER_NOT_LOGGED_IN));
    }

    static Either<? extends BaseError, String> validatePassword(String string) {
        //TODO: implement actual password quality check
        return Either.success(string);
    }
}

package org.reactivetoolbox.web.server.parameter.validation;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.web.server.auth.Authentication;

public interface AuthValidators {
    static Either<? extends BaseError, Authentication> loggedIn(Authentication authentication) {
        return authentication.token();
    }

    static Either<? extends BaseError, String> validatePassword(String string) {
        //TODO: implement actual password quality check
        return Either.success(string);
    }
}

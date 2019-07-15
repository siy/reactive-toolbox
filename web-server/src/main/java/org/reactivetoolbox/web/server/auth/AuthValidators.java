package org.reactivetoolbox.web.server.auth;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;

import java.util.Optional;

public interface AuthValidators {
    static Either<? extends BaseError, Authentication> loggedIn(final Optional<Authentication> authentication) {
        return authentication.map(Authentication::token).orElseGet(() -> Either.failure(AuthenticationError.USER_NOT_LOGGED_IN));
    }

    static Either<? extends BaseError, String> validatePassword(final String string) {
        //TODO: implement actual password quality check
        return Either.success(string);
    }

    static Either<? extends BaseError, Authentication> hasAll(final Authentication authentication, final Role... roles) {
        return authentication.hasAllRoles(roles);
    }

    static Either<? extends BaseError, Authentication> hasAny(final Authentication authentication, final Role... roles) {
        return authentication.hasAnyRoles(roles);
    }
}

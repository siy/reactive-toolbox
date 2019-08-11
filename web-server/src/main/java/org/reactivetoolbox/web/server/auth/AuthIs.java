package org.reactivetoolbox.web.server.auth;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Option;

public interface AuthIs {

    /**
     * Validate that user is logged in. The validation is applicable to JWT authentication only and checks that
     * JWT token is valid and is not expired. This validation converts optional input value into actual value
     * if parameter is present and valid.
     *
     * @param authentication
     *        Input parameter
     * @return Validation result
     */
    static Either<? extends BaseError, Authentication> loggedIn(final Option<Authentication> authentication) {
        return authentication.map(Authentication::token)
                             .otherwiseGet(AuthError.USER_NOT_LOGGED_IN::asFailure);
    }

    /**
     * Check that user belongs to all roles specified as parameters. Note that this implementation is applicable
     * only to {@link Authentication} implementations which support role validation.
     *
     * @param authentication
     *        Input parameter
     * @param roles
     *        Roles to check
     * @return Validation result
     */
    static Either<? extends BaseError, Authentication> belongsToAll(final Authentication authentication, final Role... roles) {
        return authentication.hasAllRoles(roles);
    }

    /**
     * Check that user belongs to any roles specified as parameters. Note that this implementation is applicable
     * only to {@link Authentication} implementations which support role validation.
     *
     * @param authentication
     *        Input parameter
     * @param roles
     *        Roles to check
     * @return Validation result
     */
    static Either<? extends BaseError, Authentication> belongsToAny(final Authentication authentication, final Role... roles) {
        return authentication.hasAnyRoles(roles);
    }


}

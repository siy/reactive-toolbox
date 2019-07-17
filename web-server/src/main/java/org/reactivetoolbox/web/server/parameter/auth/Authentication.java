package org.reactivetoolbox.web.server.parameter.auth;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;

public interface Authentication {
    UserId userId();
    Either<? extends BaseError, Authentication> token();
    Either<? extends BaseError, Authentication> hasAllRoles(Role ... roles);
    Either<? extends BaseError, Authentication> hasAnyRoles(Role ... roles);
}

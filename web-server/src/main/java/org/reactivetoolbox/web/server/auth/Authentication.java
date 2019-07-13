package org.reactivetoolbox.web.server.auth;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;

public interface Authentication {
    UserId userId();
    Either<? extends BaseError, Authentication> token();
}

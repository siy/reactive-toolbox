package org.reactivetoolbox.web.server.parameter.auth;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;

import java.util.UUID;

public interface UserId {
    String asString();
    Either<? extends BaseError, UUID> asUUID();
}

package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;

public enum RoutingError implements BaseError {
    NO_SUCH_ROUTE,
    ;

    public static <T> Either<RoutingError, T> create(final RoutingError error) {
        return Either.failure(error);
    }
}

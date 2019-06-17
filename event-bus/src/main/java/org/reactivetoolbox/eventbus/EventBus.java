package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;

public interface EventBus {
    <E, R, T> Either<RoutingError, Promise<Either<E, R>>> send(Envelope<T> event);
//
}

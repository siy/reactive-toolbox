package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises2.Promise;
import org.reactivetoolbox.core.functional.Either;

public interface EventBus {
    <E extends BaseError, R, T> Either<? extends RouterError, Promise<E, R>> send(Envelope<T> event);

    <T> EventBus addRouter(Class<Envelope<T>> key, Router<T> router);
}

package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;

public interface Router<T> {
//    <R> Either<? extends RoutingError, Router<T>> addRoute(String path, FN1<Promise<? extends BaseError, R>, T> handler);
//
//    <E extends BaseError, T1> Either<? extends RoutingError, Promise<E, T1>> deliver(Envelope<T> event);
    <RE extends RoutingError, R, T> Either<RE, Promise<R>> deliver(final Envelope<T> event);
}

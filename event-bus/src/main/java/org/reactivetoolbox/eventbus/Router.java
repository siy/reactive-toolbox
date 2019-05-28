package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;

public interface Router<T> {
    <R> Either<? extends RouterError, Router<T>> addRoute(String path, FN1<Promise<? extends BaseError, R>, T> handler);

    <E extends BaseError, T1> Either<? extends RouterError, Promise<E, T1>> deliver(Envelope<T> event);
}

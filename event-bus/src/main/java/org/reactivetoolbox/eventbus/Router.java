package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.eventbus.impl.RouterImpl;

public interface Router<T> {
    <R> Either<? extends BaseError, Promise<Either<? extends BaseError, R>>> deliver(Envelope<T> event);

    Router<T> with(final RouteBase<T>... routes);

    static <T> Router<T> of() {
        return new RouterImpl<>();
    }

    @SafeVarargs
    static <T> Router<T> of(final RouteBase<T>... routes) {
        return new RouterImpl<T>().with(routes);
    }
}

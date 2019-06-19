package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;

public interface Router<T> {
    <R> Either<RoutingError, Promise<R>> deliver(final Envelope<T> event);

    <R> Router<T> add(final Path path, final FN1<Promise<R>, T> handler);
}

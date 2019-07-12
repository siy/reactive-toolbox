package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;

public class Route<T> {
    private final Path path;
    private final FN1<Either<? extends BaseError, Promise<Either<? extends BaseError, ?>>>, T> handler;

    private Route(final Path path, final FN1<Either<? extends BaseError, Promise<Either<? extends BaseError, ?>>>, T> handler) {
        this.path = path;
        this.handler = handler;
    }

    public static <R> Route<R> of(final Path path, final FN1<Either<? extends BaseError, Promise<Either<? extends BaseError, ?>>>, R> handler) {
        return new Route<>(path, handler);
    }

    public Path path() {
        return path;
    }

    public FN1<Either<? extends BaseError, Promise<Either<? extends BaseError, ?>>>, T> handler() {
        return handler;
    }
}

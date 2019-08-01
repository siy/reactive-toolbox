package org.reactivetoolbox.build;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.functional.Either;

import static org.reactivetoolbox.core.async.Promise.fulfilled;
import static org.reactivetoolbox.core.functional.Either.failure;
import static org.reactivetoolbox.core.functional.Either.success;

public interface Responses {
    static <T> Either<? extends BaseError, T> ok(final T value) {
        return success(value);
    }

    static <T> Promise<Either<? extends BaseError, T>> readyOk(final T value) {
        return fulfilled(ok(value));
    }

    static <T> Promise<Either<? extends BaseError, T>> readyFail(final BaseError failure) {
        return fulfilled(failure(failure));
    }
}

package org.reactivetoolbox.build;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN2;

/**
 * This interface describes function which enriches the output of route handler.
 *
 * @param <R> type of output value
 * @param <T> context type
 */
public interface Enricher<R, T> extends FN2<Promise<Either<? extends BaseError, R>>, T, Promise<Either<? extends BaseError, R>>> {
}

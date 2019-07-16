package org.reactivetoolbox.build;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN2;

/**
 * This interface describes function which enriches the output of route handler.
 *
 * @param <R1> new type for output value
 * @param <R> old type of output value
 * @param <T> context type
 */
public interface Enricher<R1, T, R> extends FN2<Either<? extends BaseError, Promise<Either<? extends BaseError, R1>>>, T, Promise<Either<? extends BaseError, R>>> {
}

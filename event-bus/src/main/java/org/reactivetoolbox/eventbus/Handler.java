package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;

public interface Handler<R, T> extends FN1<Either<? extends BaseError, Promise<Either<? extends BaseError, R>>>, T> {
}

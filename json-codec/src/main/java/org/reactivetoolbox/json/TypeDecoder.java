package org.reactivetoolbox.json;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Option;

import static org.reactivetoolbox.core.functional.Functions.FN1;

public interface TypeDecoder<T> extends FN1<Either<? extends BaseError, Option<T>>, JsonObject> {
}

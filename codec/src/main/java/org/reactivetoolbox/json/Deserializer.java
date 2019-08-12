package org.reactivetoolbox.json;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.value.conversion.ProcessingContext;

public interface Deserializer<T> extends FN1<Either<? extends BaseError, T>, ProcessingContext> {
}

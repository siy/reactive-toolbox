package org.reactivetoolbox.web.server.parameter.validation;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;

public interface Validator<T> extends FN1<Either<? extends BaseError, T>, T> {
}

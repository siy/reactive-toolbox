package org.reactivetoolbox.web.server.parameter.conversion;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.web.server.RequestContext;

public interface Converter<T> extends FN1<Either<? extends BaseError, T>, RequestContext> {
}

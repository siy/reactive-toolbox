package org.reactivetoolbox.web.server.parameter.validation;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.parameter.Parameters;
import org.reactivetoolbox.web.server.parameter.Parameters.P;

@FunctionalInterface
public interface Validator<R, T> extends FN1<Either<? extends BaseError, R>, T> {
    default Parameters.P<R> modify(final P<T> input) {
        return Parameters.of((RequestContext context) -> input.converter().apply(context).flatMap(this::apply), input.description().get());
    }
}

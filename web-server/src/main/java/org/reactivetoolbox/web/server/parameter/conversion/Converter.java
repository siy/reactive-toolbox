package org.reactivetoolbox.web.server.parameter.conversion;

import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.web.server.RequestContext;

public interface Converter<T> {
    Either<?, T> convert(RequestContext context);
}

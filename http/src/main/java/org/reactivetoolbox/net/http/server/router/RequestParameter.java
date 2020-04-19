package org.reactivetoolbox.net.http.server.router;

import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.net.http.server.RequestContext;

public interface RequestParameter<T> extends FN1<Result<T>, RequestContext> {
    default <R> RequestParameter<R> and(final FN1<Result<R>, T> function) {
        return (context) -> apply(context).flatMap(function);
    }
}

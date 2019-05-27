package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Functions;

public interface Router {
    <E extends BaseError, R, T> void addRoute(Scheme scheme, String path, Functions.FN1<Promise<E, R>, T> handler);
}

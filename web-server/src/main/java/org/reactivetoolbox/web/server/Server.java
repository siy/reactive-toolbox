package org.reactivetoolbox.web.server;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.web.server.builder.Http;
import org.reactivetoolbox.web.server.builder.PathBuilder;
import org.reactivetoolbox.web.server.parameter.Parameters.Parameter;

public interface Server {
    void run();

    <E extends BaseError, R, T> void registerHandler(Http method, String path, FN1<Promise<E, R>, T> handler, Parameter<?>[] parameters);

    default PathBuilder on() {
        return PathBuilder.of(this);
    }
}

package org.reactivetoolbox.web.server.undertow;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises2.Promise;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.web.server.Server;
import org.reactivetoolbox.web.server.builder.Http;
import org.reactivetoolbox.web.server.parameter.Parameters.Parameter;

public class UndertowServer implements Server {

    @Override
    public <E extends BaseError, R, T> void registerHandler(final Http method, final String path, final FN1<Promise<E, R>, T> handler,
                                          final Parameter<?>[] parameters) {
    }

    @Override
    public void run() {

    }
}

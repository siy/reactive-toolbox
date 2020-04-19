package org.reactivetoolbox.net.experiment;

import org.reactivetoolbox.core.lang.collection.List;
import org.reactivetoolbox.net.http.Method;

public class Request {
    private final Method method;
    private final String uri;
    private final List<?> parameters;

    public Request(final Method method, final String uri, final List<?> parameters) {

        this.method = method;
        this.uri = uri;
        this.parameters = parameters;
    }
}

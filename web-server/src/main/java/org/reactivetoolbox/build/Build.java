package org.reactivetoolbox.build;

import org.reactivetoolbox.web.server.HttpMethod;

public final class Build {
    private Build() {}

    public static ServerBuilder server() {
        return new ServerBuilder();
    }

    public static HttpRouteBuilder when(final HttpMethod method, final String path) {
        return HttpRouteBuilder.create(method, path);
    }
}

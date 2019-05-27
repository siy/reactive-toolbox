package org.reactivetoolbox.web.server.builder;

import org.reactivetoolbox.web.server.Server;

public class PathBuilder {
    private final Server server;

    private PathBuilder(final Server server) {
        this.server = server;
    }

    public static PathBuilder of(final Server server) {
        return new PathBuilder(server);
    }

    public HandlerBuilder get(final String path) {
        return new HandlerBuilder(Http.GET, server, path);
    }

    public HandlerBuilder put(final String path) {
        return new HandlerBuilder(Http.PUT, server, path);
    }

    public HandlerBuilder post(final String path) {
        return new HandlerBuilder(Http.POST, server, path);
    }

    public HandlerBuilder delete(final String path) {
        return new HandlerBuilder(Http.DELETE, server, path);
    }
}

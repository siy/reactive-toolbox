package org.reactivetoolbox.build;

import org.reactivetoolbox.eventbus.Handler;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.Route;
import org.reactivetoolbox.eventbus.RouteBase;
import org.reactivetoolbox.web.server.RequestContext;

public class RouteEnricher<T, R> implements RouteBase<T> {
    private final Path path;
    private final Handler<R, T> handler;

    private RouteEnricher(final Path path, final Handler<R, T> handler) {
        this.path = path;
        this.handler = handler;
    }

    public static <R, T> RouteEnricher<T, R> of(final Path path, final Handler<R, T> handler) {
        return new RouteEnricher<>(path, handler);
    }

    @Override
    public Route<T> asRoute() {
        return Route.of(path, handler);
    }
}

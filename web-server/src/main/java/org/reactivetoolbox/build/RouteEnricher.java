package org.reactivetoolbox.build;

import org.reactivetoolbox.eventbus.Handler;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.Route;
import org.reactivetoolbox.eventbus.RouteBase;

public class RouteEnricher<R, T> implements RouteBase<T> {
    private final Path path;
    private final Handler<R, T> handler;

    private RouteEnricher(final Path path, final Handler<R, T> handler) {
        this.path = path;
        this.handler = handler;
    }

    public static <R, T> RouteEnricher<R, T> of(final Path path, final Handler<R, T> handler) {
        return new RouteEnricher<>(path, handler);
    }

    public RouteEnricher<R, T> then(final Enricher<R, T> enricher) {
        return new RouteEnricher<>(path, context ->
                handler.apply(context).mapSuccess(result ->enricher.apply(context, result)));
    }

    @Override
    public Route<T> asRoute() {
        return Route.of(path, handler);
    }
}

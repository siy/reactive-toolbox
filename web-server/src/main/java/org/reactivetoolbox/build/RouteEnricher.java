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

    public <R1> RouteEnricher<R1, T> then(final Enricher<R1, T, R> enricher) {
        return new RouteEnricher<>(path, context ->
                handler.apply(context).flatMap(result ->enricher.apply(context, result)));
    }

    @Override
    public Route<T> asRoute() {
        return Route.of(path, handler);
    }
}

package org.reactivetoolbox.build;

import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.eventbus.Handler;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.Route;
import org.reactivetoolbox.eventbus.RouteBase;

import java.util.List;

public class RouteEnricher<R, T> implements RouteBase<T> {
    private final Path path;
    private final Handler<R, T> handler;
    private final String methodDescription;
    private final List<String> parameterDescriptions;

    private RouteEnricher(final Path path,
                          final String methodDescription,
                          final List<String> parameterDescriptions,
                          final Handler<R, T> handler) {
        this.path = path;
        this.handler = handler;
        this.methodDescription = methodDescription;
        this.parameterDescriptions = parameterDescriptions;
    }

    public static <R, T> RouteEnricher<R, T> of(final Path path,
                                                final String methodDescription,
                                                final List<String> parameterDescriptions,
                                                final Handler<R, T> handler) {
        return new RouteEnricher<>(path, methodDescription, parameterDescriptions, handler);
    }

    public RouteEnricher<R, T> after(final Enricher<R, T> enricher) {
        return new RouteEnricher<>(path, methodDescription, parameterDescriptions,
                                   context -> handler.apply(context).mapSuccess(result ->enricher.apply(context, result)));
    }

    @Override
    public Route<T> asRoute() {
        return Route.of(path, handler);
    }

    @Override
    public Option<String> routeDescription() {
        return Option.of(methodDescription);
    }

    @Override
    public List<String> parameterDescription() {
        return parameterDescriptions;
    }
}

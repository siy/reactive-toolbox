package org.reactivetoolbox.eventbus;

import java.util.Arrays;
import java.util.List;

public class Routes<T> {
    private final List<RouteBase<T>> routes;

    private Routes(final RouteBase<T>[] routes) {
        this.routes = Arrays.asList(routes);
    }

    public List<RouteBase<T>> routes() {
        return routes;
    }

    @SafeVarargs
    public static <T> Routes<T> of(final RouteBase<T> ... routes) {
        return new Routes<>(routes);
    }
}

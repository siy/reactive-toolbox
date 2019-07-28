package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.functional.Option;

import java.util.List;
import java.util.stream.Stream;

/**
 * Container for routes, optionally rooted at the common routing path.
 *
 * @param <T> route type
 */
public class Routes<T> implements RouteBase<T> {
    private final Option<String> root;
    private final List<RouteBase<T>> routes;

    private Routes(final String root, final RouteBase<T>[] routes) {
        this(Option.of(root), List.of(routes));
    }

    private Routes(final Option<String> root, final List<RouteBase<T>> routes) {
        this.root = root;
        this.routes = routes;
    }

    /**
     * Create new {@link Routes} instance from the set of routes. Each route
     * can represent either individual route or set of routes.
     * All routes will be rooted at provided root.
     *
     * @param routes
     *        Individual routes
     * @return created instance
     */
    @SafeVarargs
    public static <T> Routes<T> with(final String root, final RouteBase<T> ... routes) {
        return new Routes<>(root, routes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Route<T>> stream() {
        return routes.stream().map(route -> root(root)).flatMap(RouteBase::stream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Option<RouteDescription>> descriptions() {
        return routes.stream().flatMap(RouteBase::descriptions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RouteBase<T> root(final Option<String> root) {
        return new Routes<>(Option.of(Path.normalize(root.otherwise("/") + "/" + this.root.otherwise("/"))), routes);
    }
}

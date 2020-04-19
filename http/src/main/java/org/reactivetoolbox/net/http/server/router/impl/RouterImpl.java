package org.reactivetoolbox.net.http.server.router.impl;

import org.reactivetoolbox.core.lang.collection.List;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.net.http.Method;
import org.reactivetoolbox.net.http.server.router.Router;
import org.reactivetoolbox.net.http.server.router.Path;
import org.reactivetoolbox.net.http.server.router.Route;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import static org.reactivetoolbox.net.http.server.router.Utils.listCombiner;
import static org.reactivetoolbox.net.http.server.router.Utils.transform;

public class RouterImpl implements Router {
    private final EnumMap<Method, PathRouter> methodRoutes = new EnumMap<>(Method.class);

    public RouterImpl(final List<Route> routes) {
        final var temp = new HashMap<Method, java.util.List<Route>>();

        routes.apply(route -> temp.compute(route.path().method(),
                                           (key, oldValue) -> listCombiner(key, oldValue, route)));

        transform(temp).forEach((method, routeList) -> methodRoutes.put(method, new PathRouter(routeList)));
    }

    @Override
    public Option<Route> locate(final Method method, final String path) {
        return Option.option(methodRoutes.get(method))
                     .flatMap(router -> router.locate(path));
    }

    private static class PathRouter {
        private final Map<String, Route> exact = new HashMap<>();
        private final NavigableMap<String, List<Route>> parametrized = new TreeMap<>();

        PathRouter(final List<Route> routes) {
            final var temp = new HashMap<String, java.util.List<Route>>();

            routes.apply(route -> {
                if (route.path().exact()) {
                    exact.putIfAbsent(route.path().prefix(), route);
                } else {
                    temp.compute(route.path().prefix(), (key, oldValue) -> listCombiner(key, oldValue, route));
                }
            });

            parametrized.putAll(transform(temp));
        }

        Option<Route> locate(final String path) {
            final var normalizedPath = Path.normalize(path);

            return Option.option(exact.get(normalizedPath))
                         .or(() -> Option.option(parametrized.floorEntry(normalizedPath))
                                         .flatMap(entry -> entry.getValue()
                                                                .filter(route -> route.path().matches(normalizedPath))
                                                                .first()));
        }
    }
}

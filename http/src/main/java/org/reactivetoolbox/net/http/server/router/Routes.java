package org.reactivetoolbox.net.http.server.router;

import java.util.stream.Stream;

import static org.reactivetoolbox.core.lang.collection.List.list;

public interface Routes extends Prefixed<RouteSource>, RouteSource {
    static Routes routes(final Prefixed<? extends RouteSource> ... routes) {
        return routes("", routes);
    }

    static Routes routes(final String path, final Prefixed<? extends RouteSource> ... routes) {
        final var sources = list(routes).map(prefixed -> prefixed.prefix(path));

        return new Routes() {
            @Override
            public RouteSource prefix(final String prefix) {
                return new RouteSource() {
                    @Override
                    public Stream<Route> stream() {
                        return sources.stream().flatMap(RouteSource::stream).map(route -> route.prefix(prefix));
                    }
                };
            }

            @Override
            public Stream<Route> stream() {
                return sources.stream().flatMap(RouteSource::stream);
            }
        };
    }
}

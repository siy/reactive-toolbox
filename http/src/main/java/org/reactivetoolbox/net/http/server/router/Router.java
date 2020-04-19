package org.reactivetoolbox.net.http.server.router;

import org.reactivetoolbox.core.lang.collection.List;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.net.http.Method;
import org.reactivetoolbox.net.http.server.router.impl.RouterImpl;

import java.util.stream.Stream;

import static org.reactivetoolbox.core.lang.collection.List.list;

public interface Router {
    Option<Route> locate(final Method method, final String path);

    static Router router(final RouteSource routes) {
        return new RouterImpl(routes.stream().collect(List.toList()));
    }

    static Router router(final Prefixed<? extends RouteSource>... routes) {
        return router("", routes);
    }

    static Router router(final String commonRoot, final Prefixed<? extends RouteSource>... routes) {
        return router(new RouteSource() {
            @Override
            public Stream<Route> stream() {
                return list(routes).map(routeSource -> routeSource.prefix(commonRoot))
                                   .stream()
                                   .flatMap(RouteSource::stream);
            }
        });
    }
}

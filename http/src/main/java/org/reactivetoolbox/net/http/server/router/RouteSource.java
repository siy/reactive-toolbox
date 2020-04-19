package org.reactivetoolbox.net.http.server.router;

import java.util.stream.Stream;

public interface RouteSource {
    Stream<Route> stream();
}

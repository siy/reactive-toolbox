package org.reactivetoolbox.net.http.server.router.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.net.http.ContentType;
import org.reactivetoolbox.net.http.Method;
import org.reactivetoolbox.net.http.server.NativeBuffer;
import org.reactivetoolbox.net.http.server.RequestContext;
import org.reactivetoolbox.net.http.server.ServerErrors;
import org.reactivetoolbox.net.http.server.router.Route;
import org.reactivetoolbox.net.http.server.router.Routes;

import static org.junit.jupiter.api.Assertions.*;
import static org.reactivetoolbox.net.http.Method.*;
import static org.reactivetoolbox.net.http.server.router.Router.*;
import static org.reactivetoolbox.net.http.server.router.Path.*;
import static org.reactivetoolbox.net.http.server.router.Routes.routes;

class RouterImplTest {
    @Test
    void exactRoutesAreMatched() {
        final var router = router(route(GET, "/users"),
                                  route(PUT, "/users/all"));

        router.locate(GET, "/users")
              .whenEmpty(Assertions::fail);

        router.locate(PUT, "/users/all")
              .whenEmpty(Assertions::fail);

        router.locate(GET, "/users/all")
              .whenPresent(v -> fail());

        router.locate(PUT, "/users")
              .whenPresent(v -> fail());
    }

    @Test
    void parametrizedRoutesAreMatched() {
        final var router = router(route(GET, "/users/{id}"),
                                  route(PATCH, "/users/{id}"),
                                  route(GET, "/users"));

        router.locate(GET, "/users")
              .whenEmpty(Assertions::fail);

        router.locate(GET, "/users/one")
              .whenEmpty(Assertions::fail);

        router.locate(PATCH, "/users/one")
              .whenEmpty(Assertions::fail);

        router.locate(GET, "/users/one/two")
              .whenPresent(v -> fail());

        router.locate(PATCH, "/users/one/two")
              .whenPresent(v -> fail());

        router.locate(PATCH, "/users")
              .whenPresent(v -> fail());
    }

    @Test
    void nestedRoutesCanBeBuilt() {
        final var router = router("/v1",
                                  routes("/users",
                                         route(GET, ""),
                                         route(POST, ""),
                                         route(PATCH, "{id}"),
                                         route(DELETE, "{id}"),
                                         routes("{id}/followers",
                                                route(GET, ""),
                                                route(POST, "{follower}"),
                                                route(DELETE, "{follower}"))),
                                  route(GET, "/heartbeat")
        );

        router.locate(GET, "/v1/heartbeat").whenEmpty(Assertions::fail);
        router.locate(GET, "/v1/users").whenEmpty(Assertions::fail);
        router.locate(POST, "/v1/users").whenEmpty(Assertions::fail);
        router.locate(PATCH, "/v1/users/first").whenEmpty(Assertions::fail);
        router.locate(DELETE, "/v1/users/first").whenEmpty(Assertions::fail);
        router.locate(GET, "/v1/users/first/followers").whenEmpty(Assertions::fail);
        router.locate(POST, "/v1/users/first/followers/other").whenEmpty(Assertions::fail);
        router.locate(DELETE, "/v1/users/first/followers/other").whenEmpty(Assertions::fail);
    }

    private static Promise<NativeBuffer> handler(final RequestContext context) {
        return Promise.readyFail(ServerErrors.SSL_NOT_CONFIGURED);
    }

    private static Route route(final Method method, final String path) {
        return Route.route(path(method, path),
                           RouterImplTest::handler, ContentType.JSON,
                           ContentType.JSON);
    }
}
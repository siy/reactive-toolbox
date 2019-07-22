package org.reactivetoolbox.web.server;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.build.ServerAssembler;
import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.web.server.parameter.auth.AuthHeader;
import org.reactivetoolbox.web.server.parameter.auth.Authentication;
import org.reactivetoolbox.web.server.parameter.auth.UserId;
import org.reactivetoolbox.web.server.parameter.validation.Is;

import java.util.UUID;

import static org.reactivetoolbox.build.HttpRouteAssembler.when;
import static org.reactivetoolbox.build.HttpRouteTools.readyOk;
import static org.reactivetoolbox.build.HttpRouteTools.valid;
import static org.reactivetoolbox.eventbus.Routes.with;
import static org.reactivetoolbox.web.server.HttpMethod.GET;
import static org.reactivetoolbox.web.server.HttpMethod.POST;
import static org.reactivetoolbox.web.server.HttpMethod.PUT;
import static org.reactivetoolbox.web.server.parameter.Parameters.inAuthHeader;
import static org.reactivetoolbox.web.server.parameter.Parameters.inBody;
import static org.reactivetoolbox.web.server.parameter.Parameters.inPath;
import static org.reactivetoolbox.web.server.parameter.Parameters.inQuery;

class ServerTest {

    private static class UserProfile {
        public UserProfile(final UserId userId) {
        }
    }

    private static class AuthToken {
    }

    private static class UserService {
        public Promise<Either<? extends BaseError, UserProfile>> getProfile(final Authentication authentication) {
            return readyOk(new UserProfile(authentication.userId()));
        }

        public Promise<Either<? extends BaseError, AuthToken>> login(final String login, final String password) {
            return readyOk(new AuthToken());
        }
    }

    @Disabled
    @Test
    void serverCanBeCreated() {
        final var userService = new UserService();

        final var server = ServerAssembler.with(
            with("/v1/",
                 with("/clips",
                      when(GET, "/one/two/{param1}/{param2}")
                          .description("....")
                          .with(inPath(String.class, "param1").and(Is.required()),
                                inPath(String.class, "param2").and(Is.required()).description("Second parameter"),
                                inQuery(Integer.class, "limit").and(Is.required()),
                                inAuthHeader(AuthHeader.JWT).and(Is::loggedIn))
                          .then((param1, param2, limit, user) -> readyOk("[" + param1 + ", " + param2 + ", " + limit + ", " + user.userId() + "]"))
                          //Simple example of request postprocessing, in this case - setting of user-defined headers to response
                          .after((context, result) -> result.then(value -> context.response().setHeader("X-User-Defined", "processed"))),

                      when(POST, "/two/three/{param1}/{param2}/{param3}")
                          .description(".....")
                          .with(inPath(String.class, "param1").and(Is.required()),
                                inPath(UUID.class, "param2").and(Is.required()),
                                inPath(Integer.class, "param3").and(Is.required()),
                                inAuthHeader(AuthHeader.JWT).and(Is::loggedIn)
                                                            .and(Is::belongsToAny, TestRoles.REGULAR, TestRoles.ADMIN))
                          // Cross-parameter validation, here does nothing, but can be used to check if overall combination of parameters is valid
                          .and((param1, param2, param3, user) -> valid(param1, param2, param3, user))
                          .then((param1, param2, param3, user) -> readyOk("[" + user.userId() + "]:" + param1 + " " + param2 + " " + param3))),

                 with("/user",
                      when(PUT, "")
                          .description("Get profile of logged in user")
                          .with(inAuthHeader(AuthHeader.JWT).and(Is::loggedIn).and(Is::belongsToAll, TestRoles.REGULAR))
                          .then(userService::getProfile),

                      //User login request
                      when(POST, "/login")
                          .description("Login user into application")
                          .with(inBody(String.class, "login")
                                    .and(Is.notNullOrEmpty())
                                    .description("User login"),
                                inBody(String.class, "password")
                                    .and(Is.notNullOrEmpty())
                                    .description("User password"))
                          .then(userService::login),

                      //More or less traditional user registration request validation
                      when(POST, "/register")
                          .description("Register new user into system")
                          .with(inBody(String.class, "login")
                                    .and(Is.lenBetween(6, 128)).and(Is.email())
                                    .description("Desired user login"),
                                inBody(String.class, "password")
                                    .and(Is.lenBetween(6, 128))
                                    .and(Is.strongPassword())
                                    .description("Proposed user password"))
                          .then(userService::login))),

            //Simplest entrypoint description, independently attached to root
            when(GET, "/")
                .description("Root request")
                .withoutParameters()
                .then(() -> readyOk("Hello world!")),
            when(GET, "/health")
                .description("Healtcheck entrypoint")
                .withoutParameters()
                .then(() -> readyOk("{\"status\":\"UP\"}"))
        ).build();
    }
}

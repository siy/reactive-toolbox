package org.reactivetoolbox.web.server;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.build.ServerBuilder;
import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.web.server.parameter.auth.AuthHeader;
import org.reactivetoolbox.web.server.parameter.auth.Authentication;
import org.reactivetoolbox.web.server.parameter.auth.UserId;
import org.reactivetoolbox.web.server.parameter.validation.Is;

import java.util.UUID;

import static org.reactivetoolbox.build.HttpRouteBuilder.when;
import static org.reactivetoolbox.build.ResponseBuilder.readyOk;
import static org.reactivetoolbox.build.ResponseBuilder.valid;
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

        final var server = ServerBuilder.with(
            with("/v1/",
                 with("/clips",
                      when(GET, "/one/two/{param1}/{param2}")
                          .description("....")
                          .with(inPath(String.class, "param1").and(Is.required()),
                                inPath(String.class, "param2").description("Second parameter").and(Is.required()),
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
                          .with(inAuthHeader(AuthHeader.JWT).and(Is::loggedIn).and(Is::belongsToAll, TestRoles.REGULAR))
                          .then(userService::getProfile),

                      //User login request
                      when(POST, "/login")
                          .with(inBody(String.class, "login")
                                    .description("User login")
                                    .and(Is.notNullOrEmpty()),
                                inBody(String.class, "password")
                                    .description("User password")
                                    .and(Is.notNullOrEmpty()))
                          .then(userService::login),

                      //More or less traditional user registration request validation
                      when(POST, "/register")
                          .with(inBody(String.class, "login")
                                    .and(Is.lenBetween(3, 128)).and(Is::email),
                                inBody(String.class, "password")
                                    .and(Is.lenBetween(6, 128))
                                    .and(Is::validatePassword))
                          .then(userService::login))),

            //Simplest entrypoint description, independently attached to root
            when(GET, "/")
                .withoutParameters().then(() -> readyOk("Hello world!"))).build();
    }
}

package org.reactivetoolbox.web.server;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Tuples;
import org.reactivetoolbox.web.server.parameter.auth.AuthHeader;
import org.reactivetoolbox.web.server.parameter.auth.Authentication;
import org.reactivetoolbox.web.server.parameter.auth.UserId;
import org.reactivetoolbox.web.server.parameter.validation.Is;

import java.util.UUID;

import static org.reactivetoolbox.build.Build.*;
import static org.reactivetoolbox.core.async.Promises.*;
import static org.reactivetoolbox.core.functional.Either.*;
import static org.reactivetoolbox.web.server.HttpMethod.*;
import static org.reactivetoolbox.web.server.parameter.Parameters.*;

class ServerTest {

    private static class UserProfile {
        public UserProfile(final UserId userId) {
        }
    }

    private static class AuthToken {

    }

    private static class UserService {
        public Promise<Either<? extends BaseError, UserProfile>> getProfile(final Authentication authentication) {
            return fulfilled(success(new UserProfile(authentication.userId())));
        }

        public Promise<Either<? extends BaseError, AuthToken>> login(final String s, final String s1) {
            return fulfilled(success(new AuthToken()));
        }
    }

    @Disabled
    @Test
    void serverCanBeCreated() {
        var userService = new UserService();
        final P<String> param11 = inPath(String.class, "param1").and(Is::notNull);
        var server = server().with(
                when(GET, "/one/two/{param1}/{param2}")
                        .description("....")
                        .with(param11,
                              inPath(String.class, "param2").and(Is::notNull).description("...."),
                              inQuery(Integer.class, "limit").and(Is::notNull),
                              inAuthHeader(AuthHeader.JWT).and(Is::loggedIn))
                        .then((param1, param2, limit, user) -> fulfilled(success("Received: " + param1 + ", " + param2 + ", " + limit + " from user" + user.userId())))
                        //Simple example of request postprocessing, in this case - setting of user-defined headers to response
                        .after((context, result) -> result.then(value -> context.response().setHeader("X-User-Defined", "processed"))),

                when(POST, "/two/three/{param1}/{param2}/{param3}")
                        .description(".....")
                        .with(inPath(String.class, "param1").and(Is::notNull),
                              inPath(UUID.class, "param2").and(Is::notNull),
                              inPath(Integer.class, "param3").and(Is::notNull),
                              inAuthHeader(AuthHeader.JWT).and(Is::loggedIn).and(Is::belongsToAny, TestRoles.REGULAR, TestRoles.ADMIN))
                        // Cross-parameter validation, here does nothing, but can be used to check if overall combination of parameters is valid
                        .and((param1, param2, param3, user) -> success(Tuples.of(param1, param2, param3, user)))
                        .then((param1, param2, param3, user) -> fulfilled(success("[" + user.userId() + "]:" + param1 + " " + param2 + " " + param3))),

                when(PUT, "/user")
                        .with(inAuthHeader(AuthHeader.JWT)
                                                .and(Is::loggedIn)
                                                .and(Is::belongsToAll, TestRoles.REGULAR))
                        .then(userService::getProfile),

                //User login request
                when(POST, "/user/login")
                        .with(inBody(String.class, "login")
                                                .and(Is::notNullOrEmpty),
                              inBody(String.class, "password")
                                                .and(Is::notNullOrEmpty))
                        .then(userService::login),

                //More or less traditional user registration request validation
                when(POST, "/user/register")
                        .with(inBody(String.class, "login")
                                                .and(Is::lenBetween, 3, 128)
                                                .and(Is::email),
                              inBody(String.class, "password")
                                                .and(Is::lenBetween, 6, 128)
                                                .and(Is::validatePassword))
                        .then(userService::login),

                //Simplest entrypoint description
                when(GET, "/")
                        .withoutParameters()
                        .then(() -> fulfilled(success("Hello world!")))
        ).build();
    }
}
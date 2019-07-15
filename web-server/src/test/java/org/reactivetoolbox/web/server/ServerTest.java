package org.reactivetoolbox.web.server;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Tuples;
import org.reactivetoolbox.web.server.auth.AuthHeader;
import org.reactivetoolbox.web.server.auth.Authentication;
import org.reactivetoolbox.web.server.auth.UserId;
import org.reactivetoolbox.web.server.parameter.validation.AuthValidators;
import org.reactivetoolbox.web.server.parameter.validation.Validators;

import java.util.UUID;

import static org.reactivetoolbox.build.Build.*;
import static org.reactivetoolbox.core.async.Promises.*;
import static org.reactivetoolbox.core.functional.Either.*;
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
        var server = server().withRoutes(
                on(HttpMethod.GET)
                        .to("/one/two/{param1}/{param2}")
                        .withParameters(inPath(String.class, "param1").validate(Validators::notNull),
                                        inPath(String.class, "param2").validate(Validators::notNull),
                                        inAuthHeader(AuthHeader.JWT).validate(AuthValidators::loggedIn))
                        .invoke((param1, param2, user) -> fulfilled(success("Received: " + param1 + ", " + param2))),

                on(HttpMethod.POST)
                        .to("/two/three/{param1}/{param2}/{param3}")
                        .withParameters(inPath(String.class, "param1").validate(Validators::notNull),
                                        inPath(UUID.class, "param2").validate(Validators::notNull),
                                        inPath(Integer.class, "param3").validate(Validators::notNull))
                        .validate((param1, param2, param3) -> success(Tuples.of(param1, param2, param3)))
                        .invoke((param1, param2, param3) -> fulfilled(success(">>>" + param1 + " " + param2 + " " + param3))),

                on(HttpMethod.PUT)
                        .to("/user")
                        .withParameters(inAuthHeader(AuthHeader.JWT).validate(AuthValidators::loggedIn))
                        .invoke(userService::getProfile),

                on(HttpMethod.POST)
                        .to("/user/login")
                        .withParameters(inBody(String.class, "login").validate(Validators::notNullOrEmpty),
                                        inBody(String.class, "password").validate(Validators::notNullOrEmpty))
                        .invoke(userService::login),

                on(HttpMethod.POST)
                        .to("/user/register")
                        .withParameters(inBody(String.class, "login")
                                                .validate(Validators::notNull)
                                                .validate(Validators::stringLen, 4, 128),
                                        inBody(String.class, "password")
                                                .validate(Validators::notNull)
                                                .validate(Validators::stringLen, 4, 128)
                                                .validate(AuthValidators::validatePassword))
                        .invoke(userService::login),

                on(HttpMethod.GET)
                        .to("/")
                        .withoutParameters()
                        .invoke(() -> fulfilled(success("Hello world!")))
        ).build();
    }
}
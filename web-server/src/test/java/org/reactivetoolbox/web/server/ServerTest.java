package org.reactivetoolbox.web.server;

import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Tuples;
import org.reactivetoolbox.web.server.auth.AuthHeader;
import org.reactivetoolbox.web.server.parameter.Parameters;
import org.reactivetoolbox.web.server.parameter.validation.ValidationError;
import org.reactivetoolbox.web.server.parameter.validation.Validations;

import java.util.UUID;

import static org.reactivetoolbox.build.Build.*;
import static org.reactivetoolbox.core.async.Promises.*;
import static org.reactivetoolbox.core.functional.Either.*;
import static org.reactivetoolbox.web.server.parameter.Parameters.*;

class ServerTest {

    private static class UserProfile {
    }

    private static class UserService {

        public Promise<Either<? extends BaseError, ?>> getProfile(final AuthHeader param1) {
            return fulfilled(success(new UserProfile()));
        }
    }

    @Disabled
    @Test
    void serverCanBeCreated() {
        var userService = new UserService();
        var server = server().withRoutes(
                on(HttpMethod.GET)
                        .to("/one/two/{param1}/{param2}")
                        .ensure(Authentication::userLoggedIn)
                        .findParameters(inPath(String.class, "param1").validate(Validations::notNull),
                                        inPath(String.class, "param2").validate(Validations::notNull))
                        .invoke((param1, param2) -> fulfilled(success("Received: " + param1 + ", " + param2))),

                on(HttpMethod.POST)
                        .to("/two/three/{param1}/{param2}/{param3}")
                        .ensure(Authentication::userLoggedIn)
                        .findParameters(inPath(String.class, "param1").validate(Validations::notNull),
                                        inPath(UUID.class, "param2").validate(Validations::notNull),
                                        inPath(Integer.class, "param3").validate(Validations::notNull))
                        .validate((param1, param2, param3) -> success(Tuples.of(param1, param2, param3)))
                        .invoke((param1, param2, param3) -> fulfilled(success(">>>" + param1 + " " + param2 + " " + param3))),
                on(HttpMethod.PUT)
                        .to("/user")
                        .ensure(Authentication::userLoggedIn)
                        .findParameters(inAuthHeader(AuthHeader.JWT))
                        //TODO: try to fix type deduction for case of returning explicit type of promise
                        .invoke(userService::getProfile),

                on(HttpMethod.GET)
                        .to("/")
                        .withoutParameters()
                        .invoke(() -> fulfilled(success("Hello world!")))
        ).build();
    }
}
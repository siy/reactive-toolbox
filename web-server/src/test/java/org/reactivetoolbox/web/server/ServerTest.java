package org.reactivetoolbox.web.server;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.async.Promises;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Tuples;
import org.reactivetoolbox.web.server.auth.AuthHeader;
import org.reactivetoolbox.web.server.parameter.Parameters;
import org.reactivetoolbox.web.server.parameter.validation.Validations;

import java.util.UUID;

import static org.reactivetoolbox.build.Build.*;
import static org.reactivetoolbox.web.server.parameter.Parameters.*;

class ServerTest {

    private static class UserProfile {
    }

    private static class UserService {

        public Promise<UserProfile> getProfile(final AuthHeader param1) {
            return Promises.fulfilled(new UserProfile());
        }
    }

    @Test
    void serverCanBeCreated() {
        var userService = new UserService();
        var server = server().withRoutes(
                on(HttpMethod.GET)
                        .to("/one/two/{param1}/{param2}")
                        .ensure(Authentication::userLoggedIn)
                        .findParameters(inPath(String.class, "param1").validate(Validations::notNull),
                                        inPath(String.class, "param2").validate(Validations::notNull))
                        .invoke((param1, param2) -> Promises.fulfilled("Received: " + param1 + ", " + param2)),

                on(HttpMethod.POST)
                        .to("/two/three/{param1}/{param2}/{param3}")
                        .ensure(Authentication::userLoggedIn)
                        .findParameters(inPath(String.class, "param1").validate(Validations::notNull),
                                        inPath(UUID.class, "param2").validate(Validations::notNull),
                                        inPath(Integer.class, "param3").validate(Validations::notNull))
                        .validate((param1, param2, param3) -> Either.success(Tuples.of(param1, param2, param3)))
                        .invoke((param1, param2, param3) -> Promises.fulfilled(">>>" + param1 + " " + param2 + " " + param3)),
                on(HttpMethod.PUT)
                        .to("/user")
                        .ensure(Authentication::userLoggedIn)
                        .findParameters(Parameters.inAuthHeader(AuthHeader.JWT))
                        .invoke(param1 -> userService.getProfile(param1)),

                on(HttpMethod.GET)
                        .to("/")
                        .withoutParameters()
                        .invoke(() -> Promises.fulfilled("Hello world!"))
        ).build();
    }
}
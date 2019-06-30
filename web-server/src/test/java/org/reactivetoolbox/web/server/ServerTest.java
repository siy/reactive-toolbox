package org.reactivetoolbox.web.server;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.build.Build;
import org.reactivetoolbox.core.async.Promises;
import org.reactivetoolbox.web.server.parameter.Parameters;
import org.reactivetoolbox.web.server.parameter.validation.Validations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.reactivetoolbox.web.server.parameter.Parameters.*;

class ServerTest {
    @Test
    void serverCanBeCreated() {
        var server = Build.server().withRoutes(

                Build.on(HttpMethod.GET)
                        .withPath("/one/two/{param1}")
                        .with(inPath(String.class, "param1").validate(Validations::notNull))
                        .ensure(Authentication::userLoggedIn)
                        .thenHandleWith(param1 -> Promises.fulfilled("Received" + param1)),

                Build.on(HttpMethod.POST)
                        .withPath("/two/three/{param1}/{param2}/{param3}")
                        .with(inPath(String.class, "param1").validate(Validations::notNull),
                              inPath(UUID.class, "param2").validate(Validations::notNull),
                              inPath(Integer.class, "param3").validate(Validations::notNull))
                        .ensure(Authentication::userLoggedIn)
                        .thenHandleWith((param1, param2, param3) ->
                                                Promises.fulfilled(">>>" + param1 + " " + param2 + " " + param3))).build();
    }
}
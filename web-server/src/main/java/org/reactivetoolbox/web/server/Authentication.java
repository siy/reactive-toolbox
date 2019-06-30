package org.reactivetoolbox.web.server;

import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions;
import org.reactivetoolbox.core.functional.Functions.FN1;

public class Authentication {
    public static Either<AuthenticationError, RequestContext>  userLoggedIn(final RequestContext context) {
        //TODO: implement it
        return Either.success(context);
    }
}

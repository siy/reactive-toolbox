package org.reactivetoolbox.codec.validation;

import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.lang.support.WebFailureTypes;
import org.reactivetoolbox.net.http.server.router.Authentication;

import static org.reactivetoolbox.core.lang.functional.Failure.failure;
import static org.reactivetoolbox.core.lang.functional.Result.fail;

public interface AuthIs {
    static Result<Authentication> loggedIn(final Option<Authentication> input) {
        return input.fold(v -> fail(failure(WebFailureTypes.FORBIDDEN, "User is not logged in")),
                         Result::ok);
    }
}

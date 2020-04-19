package org.reactivetoolbox.core.examples.lang;

import org.reactivetoolbox.core.lang.functional.Failure;

public class UserProfileResponse {

    public static UserProfileResponse error(final Failure failure) {
        return new UserProfileResponse();
    }

    public static UserProfileResponse of(final User user, final UserProfileDetails details) {
        return new UserProfileResponse();
    }
}

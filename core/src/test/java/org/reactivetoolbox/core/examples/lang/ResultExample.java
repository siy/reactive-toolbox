package org.reactivetoolbox.core.examples.lang;

import org.reactivetoolbox.core.lang.functional.Failure;
import org.reactivetoolbox.core.lang.functional.Result;

public class ResultExample {
    private static final Failure USER_NOT_FOUND = null;

    private final ResultUserService resultUserService;
    private final ResultUserProfileService resultUserProfileService;

    public ResultExample(final ResultUserService resultUserService,
                         final ResultUserProfileService resultUserProfileService) {
        this.resultUserService = resultUserService;
        this.resultUserProfileService = resultUserProfileService;
    }

    public Result<UserProfileResponse> getUserProfileHandler(final User.Id userId) {
        return resultUserService.findById(userId)
                                .flatMap(user -> resultUserProfileService.findById(userId)
                                                                         .map(profile -> UserProfileResponse.of(user, profile)));
    }
}

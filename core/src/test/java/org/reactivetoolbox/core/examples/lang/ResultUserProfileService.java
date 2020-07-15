package org.reactivetoolbox.core.examples.lang;

import org.reactivetoolbox.core.lang.functional.Result;

public interface ResultUserProfileService {
    Result<UserProfileDetails> findById(User.Id userId);
}

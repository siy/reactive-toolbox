package org.reactivetoolbox.core.examples.lang;

import java.util.Optional;

public interface OptionalUserProfileService {
    Optional<UserProfileDetails> findById(User.Id userId);
}

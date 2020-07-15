package org.reactivetoolbox.core.examples.lang;

import java.util.Optional;

public interface OptionalUserService {
    Optional<User> findById(User.Id userId);
}

package org.reactivetoolbox.core.examples.lang;

import org.reactivetoolbox.core.lang.functional.Result;

public interface ResultUserService {
    Result<User> findById(User.Id userId);
}

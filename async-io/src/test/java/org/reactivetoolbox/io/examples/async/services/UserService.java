package org.reactivetoolbox.io.examples.async.services;

import org.reactivetoolbox.core.lang.collection.Collection;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.examples.async.domain.User;
import org.reactivetoolbox.io.examples.async.domain.UserProfile;

public interface UserService {
    Promise<UserProfile> userProfile(final User.Id userId);
    Promise<Collection<User>> followers(final User.Id userId);
}

package org.reactivetoolbox.core.examples.async.domain;

import org.reactivetoolbox.core.lang.collection.Collection;

public class UserDashboard {
    private final UserProfile user;
    private final Collection<User> followers;
    private final Collection<Article> article;
    private final Collection<Comment> comments;

    private UserDashboard(final UserProfile user,
                          final Collection<User> followers,
                          final Collection<Article> article,
                          final Collection<Comment> comments) {
        this.user = user;
        this.followers = followers;
        this.article = article;
        this.comments = comments;
    }

    public static UserDashboard with(final UserProfile user,
                                     final Collection<User> followers,
                                     final Collection<Article> article,
                                     final Collection<Comment> comments) {
        return new UserDashboard(user, followers, article, comments);
    }
}

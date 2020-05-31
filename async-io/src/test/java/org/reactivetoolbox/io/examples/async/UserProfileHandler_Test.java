package org.reactivetoolbox.io.examples.async;

import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.examples.async.domain.Order;
import org.reactivetoolbox.io.examples.async.domain.User;
import org.reactivetoolbox.io.examples.async.domain.UserDashboard;
import org.reactivetoolbox.io.examples.async.services.ArticleService;
import org.reactivetoolbox.io.examples.async.services.CommentService;
import org.reactivetoolbox.io.examples.async.services.UserService;

import static org.reactivetoolbox.io.async.Promise.all;

public class UserProfileHandler_Test {
    private UserService userService;
    private ArticleService articleService;
    private CommentService commentService;

    public Promise<UserDashboard> userProfileHandler(final User.Id userId) {
        return all(userService.userProfile(userId),
                   userService.followers(userId),
                   articleService.articlesByUser(userId, Order.DESC),
                   commentService.commentsByUser(userId, Order.DESC))
                .map(tuple -> tuple.map(UserDashboard::with));
    }
}

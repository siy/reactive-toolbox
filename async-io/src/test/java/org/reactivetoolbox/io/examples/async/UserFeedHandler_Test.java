package org.reactivetoolbox.io.examples.async;

import org.reactivetoolbox.core.Errors;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.examples.async.domain.Article;
import org.reactivetoolbox.io.examples.async.domain.Order;
import org.reactivetoolbox.io.examples.async.domain.Topic;
import org.reactivetoolbox.io.examples.async.domain.User;
import org.reactivetoolbox.io.examples.async.services.ArticleService;
import org.reactivetoolbox.io.examples.async.services.TopicService;
import org.reactivetoolbox.io.examples.async.services.UserService;
import org.reactivetoolbox.core.lang.collection.Collection;

import static org.reactivetoolbox.io.async.Promise.all;
import static org.reactivetoolbox.core.lang.functional.Result.fail;
import static org.reactivetoolbox.io.scheduler.Timeout.timeout;

public class UserFeedHandler_Test {
    private ArticleService articleService;
    private TopicService topicService;
    private UserService userService;

    public Promise<Collection<Article>> userFeedHandler(final User.Id userId) {
        return all(topicService.topicsByUser(userId, Order.ANY),
                   userService.followers(userId))
                .andThen(tuple -> tuple.map((topics, users) -> articleService.userFeed(topics.map(Topic::id), users.map(User::id))))
                .when(timeout(30).seconds(), fail(Errors.TIMEOUT));
    }
}

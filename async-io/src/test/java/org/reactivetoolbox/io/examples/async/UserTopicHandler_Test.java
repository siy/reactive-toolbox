package org.reactivetoolbox.io.examples.async;

import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.examples.async.domain.Article;
import org.reactivetoolbox.io.examples.async.domain.Order;
import org.reactivetoolbox.io.examples.async.domain.Topic;
import org.reactivetoolbox.io.examples.async.domain.User;
import org.reactivetoolbox.io.examples.async.services.ArticleService;
import org.reactivetoolbox.io.examples.async.services.TopicService;
import org.reactivetoolbox.core.lang.collection.Collection;

public class UserTopicHandler_Test {
    private ArticleService articleService;
    private TopicService topicService;

    public Promise<Collection<Article>> userTopicHandler(final User.Id userId) {
        return topicService.topicsByUser(userId, Order.ANY)
                           .andThen(topicsList -> articleService.articlesByUserTopics(userId, topicsList.map(Topic::id)));
    }
}

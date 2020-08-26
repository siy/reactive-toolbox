package org.reactivetoolbox.io.examples.async;

import org.reactivetoolbox.core.lang.collection.Collection;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.examples.async.domain.Article;
import org.reactivetoolbox.io.examples.async.domain.Order;
import org.reactivetoolbox.io.examples.async.domain.Topic;
import org.reactivetoolbox.io.examples.async.domain.User;
import org.reactivetoolbox.io.examples.async.services.ArticleService;
import org.reactivetoolbox.io.examples.async.services.TopicService;

public class UserTopicHandler_Test {
    private final ArticleService articleService;
    private final TopicService topicService;

    public UserTopicHandler_Test(final ArticleService articleService, final TopicService topicService) {
        this.articleService = articleService;
        this.topicService = topicService;
    }

    public Promise<Collection<Article>> userTopicHandler(final User.Id userId) {
        return topicService.topicsByUser(userId, Order.ANY)
                           .flatMap(topicsList -> articleService.articlesByUserTopics(userId, topicsList.map(Topic::id)));
    }
}

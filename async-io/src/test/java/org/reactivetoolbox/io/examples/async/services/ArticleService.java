package org.reactivetoolbox.io.examples.async.services;

import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.examples.async.domain.Article;
import org.reactivetoolbox.io.examples.async.domain.Order;
import org.reactivetoolbox.io.examples.async.domain.Topic;
import org.reactivetoolbox.io.examples.async.domain.User;
import org.reactivetoolbox.core.lang.collection.Collection;


public interface ArticleService {
    Promise<Collection<Article>> articlesByUser(final User.Id userId, final Order order);
    Promise<Collection<Article>> articlesByUserTopics(final User.Id userId, final Collection<Topic.Id> topicIds);
    // Returns list of articles for specified topics posted by specified users
    Promise<Collection<Article>> userFeed(final Collection<Topic.Id> topics, final Collection<User.Id> users);
}

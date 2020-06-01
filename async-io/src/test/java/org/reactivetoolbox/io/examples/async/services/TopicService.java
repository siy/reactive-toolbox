package org.reactivetoolbox.io.examples.async.services;

import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.examples.async.domain.Order;
import org.reactivetoolbox.io.examples.async.domain.Topic;
import org.reactivetoolbox.io.examples.async.domain.User;
import org.reactivetoolbox.core.lang.collection.Collection;

public interface TopicService {
    Promise<Collection<Topic>> topicsByUser(final User.Id userId, final Order order);
}
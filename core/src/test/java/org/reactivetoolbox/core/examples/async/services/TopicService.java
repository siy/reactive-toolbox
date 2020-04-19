package org.reactivetoolbox.core.examples.async.services;

import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.examples.async.domain.Order;
import org.reactivetoolbox.core.examples.async.domain.Topic;
import org.reactivetoolbox.core.examples.async.domain.User;
import org.reactivetoolbox.core.lang.collection.Collection;

public interface TopicService {
    Promise<Collection<Topic>> topicsByUser(final User.Id userId, final Order order);
}

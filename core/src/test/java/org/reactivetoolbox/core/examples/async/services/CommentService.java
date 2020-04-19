package org.reactivetoolbox.core.examples.async.services;

import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.examples.async.domain.Comment;
import org.reactivetoolbox.core.examples.async.domain.Order;
import org.reactivetoolbox.core.examples.async.domain.User;
import org.reactivetoolbox.core.lang.collection.Collection;

public interface CommentService {
    Promise<Collection<Comment>> commentsByUser(final User.Id userId, final Order order);
}

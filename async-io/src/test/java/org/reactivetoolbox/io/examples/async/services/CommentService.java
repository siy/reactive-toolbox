package org.reactivetoolbox.io.examples.async.services;

import org.reactivetoolbox.core.lang.collection.Collection;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.examples.async.domain.Comment;
import org.reactivetoolbox.io.examples.async.domain.Order;
import org.reactivetoolbox.io.examples.async.domain.User;

public interface CommentService {
    Promise<Collection<Comment>> commentsByUser(final User.Id userId, final Order order);
}

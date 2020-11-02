/*
 * Copyright (c) 2020 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.reactivetoolbox.io.examples.async;

import org.reactivetoolbox.core.Errors;
import org.reactivetoolbox.core.lang.collection.Collection;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.examples.async.domain.Article;
import org.reactivetoolbox.io.examples.async.domain.Order;
import org.reactivetoolbox.io.examples.async.domain.Topic;
import org.reactivetoolbox.io.examples.async.domain.User;
import org.reactivetoolbox.io.examples.async.services.ArticleService;
import org.reactivetoolbox.io.examples.async.services.TopicService;
import org.reactivetoolbox.io.examples.async.services.UserService;

import static org.reactivetoolbox.core.lang.functional.Result.fail;
import static org.reactivetoolbox.io.async.Promises.all;
import static org.reactivetoolbox.io.scheduler.Timeout.timeout;

public class UserFeedHandler_Test {
    private ArticleService articleService;
    private TopicService topicService;
    private UserService userService;

    public Promise<Collection<Article>> userFeedHandler(final User.Id userId) {
        return all(topicService.topicsByUser(userId, Order.ANY),
                   userService.followers(userId))
                .syncFlatMap(tuple -> tuple.map((topics, users) -> articleService.userFeed(topics.map(Topic::id), users.map(User::id))), null)
                .when(timeout(30).seconds(), fail(Errors.TIMEOUT));
    }
}

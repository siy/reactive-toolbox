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

import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.examples.async.domain.Order;
import org.reactivetoolbox.io.examples.async.domain.User;
import org.reactivetoolbox.io.examples.async.domain.UserDashboard;
import org.reactivetoolbox.io.examples.async.services.ArticleService;
import org.reactivetoolbox.io.examples.async.services.CommentService;
import org.reactivetoolbox.io.examples.async.services.UserService;

import static org.reactivetoolbox.io.async.Promises.all;

public class UserProfileHandler_Test {
    private UserService userService;
    private ArticleService articleService;
    private CommentService commentService;

    public Promise<UserDashboard> userProfileHandler(final User.Id userId) {
        return all(userService.userProfile(userId),
                   userService.followers(userId),
                   articleService.articlesByUser(userId, Order.DESC),
                   commentService.commentsByUser(userId, Order.DESC))
                .syncMap(tuple -> tuple.map(UserDashboard::with), null);
    }
}

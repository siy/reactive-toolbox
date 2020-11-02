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

package org.reactivetoolbox.io.examples.async.domain;

import org.reactivetoolbox.core.lang.collection.Collection;

public class UserDashboard {
    private final UserProfile user;
    private final Collection<User> followers;
    private final Collection<Article> article;
    private final Collection<Comment> comments;

    private UserDashboard(final UserProfile user,
                          final Collection<User> followers,
                          final Collection<Article> article,
                          final Collection<Comment> comments) {
        this.user = user;
        this.followers = followers;
        this.article = article;
        this.comments = comments;
    }

    public static UserDashboard with(final UserProfile user,
                                     final Collection<User> followers,
                                     final Collection<Article> article,
                                     final Collection<Comment> comments) {
        return new UserDashboard(user, followers, article, comments);
    }
}

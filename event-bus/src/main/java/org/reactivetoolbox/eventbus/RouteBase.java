package org.reactivetoolbox.eventbus;

/*
 * Copyright (c) 2017-2019 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.reactivetoolbox.core.functional.Option;

import java.util.stream.Stream;

/**
 * The base interface of all kinds of routes, either single or sets of routes.
 * Implementations might not be capable of performing handling of events by
 * itself, but should be able to build stream of instances of {@link Route},
 * which actually can handle events.
 *
 * @param <T>
 */
public interface RouteBase<T> {
    /**
     * Create a stream of instances of {@link Route}.
     *
     * @return {@link Stream} of {@link Route}s
     */
    Stream<Route<T>> stream();

    /**
     * Create a stream of descriptions for routes. Note that order and number of
     * elements in this stream must match order and number of {@link Route}s
     * produced by stream created by {@link #stream()}. If implementation does
     * not support this functionality, empty {@link Stream} should be returned.
     *
     * @return {@link Stream} of {@link RouteDescription}s
     */
    Stream<Option<RouteDescription>> descriptions();

    /**
     * Change root (path prefix) for this route.
     *
     * @param root
     *        New path prefix
     * @return new instance with modified root
     */
    RouteBase<T> root(Option<String> root);
}

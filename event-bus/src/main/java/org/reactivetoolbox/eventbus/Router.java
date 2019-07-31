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

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.eventbus.impl.RouterImpl;

/**
 * Common interface for event routers.
 * The router takes envelope of specified type and delivers it to event handler. If envelope contains path to defined
 * route, result of handler invocation is returned. Otherwise routing error is returned.
 * <br>
 * All routes are organized in hierarchy by route path.
 *
 * @param <T>
 *        Envelope type
 * @see Path
 */
public interface Router<T> {
    /**
     * Deliver envelope with message to particular handler. If envelope matches one of the defined routes then
     * {@link Either} with {@link Promise} (result of handler invocation) is returned. Otherwise {@link Either} with
     * routing error is returned.
     *
     * @param event
     *        The envelope with message and routing information
     *
     * @return {@link Either} with handler invocation result or with routing failure
     */
    <R> Either<? extends BaseError, Promise<Either<? extends BaseError, R>>> deliver(Envelope<T> event);

    /**
     * Add routes to this instance. If <code>root</code> parameter is not empty, then all added routes
     * are grouped under specified common path prefix.
     *
     * @param root
     *        Optional common path prefix for added routes
     * @param routes
     *        Routes to add
     * @return Current instance with added routes
     */
    Router<T> with(final Option<String> root, final RouteBase<T>... routes);

    /**
     * Create router with empty route configuration.
     *
     * @param <T>
     *        Envelope type
     *
     * @return Created router
     */
    static <T> Router<T> of() {
        return new RouterImpl<>();
    }

    /**
     * Create router with pre-configured routes.
     *
     * @param root
     *        Optional common path prefix for routes
     * @param routes
     *        Routes to add
     * @return Pre-configured router
     */
    @SafeVarargs
    static <T> Router<T> of(final Option<String> root, final RouteBase<T>... routes) {
        return new RouterImpl<T>().with(root, routes);
    }
}

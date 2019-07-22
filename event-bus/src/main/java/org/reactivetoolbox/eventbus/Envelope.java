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
import org.reactivetoolbox.core.functional.Either;

/**
 * Common interface for message envelopes. The envelope binds together message and its destination. Beside this
 * envelope enables specific pre-processing of the message according to destination route right before the
 * handler associated with the route will be invoked.
 *
 * @param <T>
 *        Type of contained message
 */
public interface Envelope<T> {
    /**
     * Perform envelope-specific actions right before invocation of the handler assigned to the route.
     * Note that passed instance of the {@link Route<T>} may contain different path than one contained
     * in the envelope itself (accessible via {@link #target()} method). This is because envelope always
     * contains exact path associated with particular request, while route definition may contain path
     * with parameter templates.
     *
     * @param route
     *        Matching route
     * @return {@link Either} with successful
     */
    Either<? extends BaseError, T> onDelivery(final Route<T> route);

    /**
     * Destination path for the routing.
     *
     * @return target {@link Path} of the request stored in this {@link Envelope}
     */
    Path target();
}

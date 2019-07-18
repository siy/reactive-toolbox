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
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.eventbus.impl.RouterImpl;

//TODO: Javadoc
public interface Router<T> {
    <R> Either<? extends BaseError, Promise<Either<? extends BaseError, R>>> deliver(Envelope<T> event);

    Router<T> with(final RouteBase<T>... routes);

    Router<T> with(final Routes<T>... routes);

    static <T> Router<T> of() {
        return new RouterImpl<>();
    }

    @SafeVarargs
    static <T> Router<T> of(final RouteBase<T>... routes) {
        return new RouterImpl<T>().with(routes);
    }
}

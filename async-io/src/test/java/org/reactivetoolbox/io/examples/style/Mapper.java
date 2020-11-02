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

package org.reactivetoolbox.io.examples.style;

import org.reactivetoolbox.core.lang.Tuple.Tuple1;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.io.async.Promise;

public interface Mapper<T1> {
    Promise<Tuple1<T1>> id();

    default <R> Promise<R> map(final FN1<R, T1> mapper) {
        return id().map(tuple -> tuple.map(mapper));
    }

    default <R> Promise<R> flatMap(final FN1<Promise<R>, T1> mapper) {
        return id().flatMap(tuple -> tuple.map(mapper));
    }
}

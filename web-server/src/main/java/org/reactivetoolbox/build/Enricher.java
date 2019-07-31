package org.reactivetoolbox.build;

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
import org.reactivetoolbox.core.functional.Functions.FN2;

/**
 * This interface describes function which enriches the output of route handler by postprocessing result.
 * Unlike route handler itself, this function has explicit access to the request context.
 *
 * @param <R> type of output value
 * @param <T> context type
 */
public interface Enricher<R, T> extends FN2<Promise<Either<? extends BaseError, R>>, T, Promise<Either<? extends BaseError, R>>> {
}

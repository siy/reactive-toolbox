package org.reactivetoolbox.web.server.parameter.conversion;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.parameter.TypeDescription;
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

/**
 * Interface for parameter converters
 *
 * @param <T>
 *        The type of the parameter value
 */
//TODO: detach conversion/validation from request context and move them to JSON module
@FunctionalInterface
public interface Converter<T> extends FN1<Either<? extends BaseError, T>, RequestContext> {
    default Option<TypeDescription> typeDescription() {
        return Option.empty();
    }
}

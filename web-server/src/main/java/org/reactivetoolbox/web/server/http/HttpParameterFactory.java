package org.reactivetoolbox.web.server.http;

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
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Functions.FN2;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.web.server.parameter.conversion.Converter;

/**
 * Set of convenience methods for composing various value converters for HTTP request parameters and HTTP request
 * context variables.
 */
public interface HttpParameterFactory {
    interface StringExtractor extends FN1<Either<? extends BaseError, Option<String>>, HttpProcessingContext> {}

    interface NamedStringExtractor extends FN2<Either<? extends BaseError, Option<String>>, HttpProcessingContext, String> {}

    NamedStringExtractor fromQuery = (context, name) -> Either.success(context.request().queryParameter(name));
    NamedStringExtractor fromPath = (context, name) -> Either.success(context.request().pathParameter(name));
    NamedStringExtractor fromHeader = (context, name) -> Either.success(context.request().header(name));
    NamedStringExtractor fromBody = (context, name) -> Either.success(context.request().bodyParameter(name));

    static StringExtractor forName(final NamedStringExtractor extractor, final String name) {
        return (context) -> extractor.apply(context, name);
    }

    static <T> Converter<Option<T>> composeConverter(final StringExtractor stringExtractor, final Class<T> type) {
        return (context) -> stringExtractor.apply((HttpProcessingContext) context)
                                           .flatMap((value) -> context.valueConverter(type)
                                                                      .apply(value));
    }

    static <T> Converter<Option<T>> pathParameter(final Class<T> type, final String name) {
        return composeConverter(forName(fromPath, name), type);
    }

    static <T> Converter<Option<T>> queryParameter(final Class<T> type, final String name) {
        return composeConverter(forName(fromQuery, name), type);
    }

    static <T> Converter<Option<T>> headerParameter(final Class<T> type, final String name) {
        return composeConverter(forName(fromHeader, name), type);
    }

    static <T> Converter<Option<T>> bodyParameter(final Class<T> type, final String name) {
        return composeConverter(forName(fromBody, name), type);
    }

    static <T> Converter<Option<T>> inContext(final Class<T> type) {
        return (context) -> Either.success(((HttpProcessingContext) context).contextComponent(type));
    }

    static <T> Converter<Option<T>> fullBody(final Class<T> type) {
        return composeConverter((context) -> Either.success(context.request().body()), type);
    }
}

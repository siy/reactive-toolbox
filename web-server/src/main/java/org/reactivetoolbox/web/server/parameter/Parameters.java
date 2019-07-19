package org.reactivetoolbox.web.server.parameter;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN2;
import org.reactivetoolbox.core.functional.Functions.FN3;
import org.reactivetoolbox.core.functional.Functions.FN4;
import org.reactivetoolbox.core.functional.Functions.FN5;
import org.reactivetoolbox.core.functional.Functions.FN6;
import org.reactivetoolbox.core.functional.Functions.FN7;
import org.reactivetoolbox.core.functional.Functions.FN8;
import org.reactivetoolbox.core.functional.Functions.FN9;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.parameter.auth.AuthHeader;
import org.reactivetoolbox.web.server.parameter.auth.Authentication;
import org.reactivetoolbox.web.server.parameter.conversion.Converter;
import org.reactivetoolbox.web.server.parameter.conversion.ConverterFactory;
import org.reactivetoolbox.web.server.parameter.validation.Is.Validator;

import java.util.ServiceLoader;

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

//TODO: Javadoc
public class Parameters {
    private static final ConverterFactory FACTORY = ServiceLoader.load(ConverterFactory.class)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Unable to find suitable ConverterFactory"));

    public static <T> P<Option<T>> inPath(final Class<T> type, final String name) {
        return new P<>(FACTORY.getParameterConverter(type, name), name);
    }

    public static <T> P<Option<T>> inQuery(final Class<T> type, final String name) {
        return new P<>(FACTORY.getParameterConverter(type, name), name);
    }

    public static <T> P<Option<T>> inBody(final Class<T> type, final String name) {
        return new P<>(FACTORY.getBodyValueConverter(type, name), name);
    }

    public static <T> P<Option<T>> inHeader(final Class<T> type, final HeaderName name) {
        return new P<>(FACTORY.getHeaderConverter(type, name), name.header());
    }

    public static <T> P<Option<T>> inContext(final Class<T> type) {
        return new P<>(FACTORY.getContextConverter(type));
    }

    public static P<Option<Authentication>> inAuthHeader(final AuthHeader header) {
        return new P<>(FACTORY.getHeaderConverter(Authentication.class, Headers.AUTHORIZATION, header));
    }

    public static class P<T> {
        private final Converter<T> converter;
        private final String name;
        private String description;

        private P(final Converter<T> converter) {
            this(converter, null);
        }

        private P(final Converter<T> converter, final String name) {
            this.converter = converter;
            this.name = name;
        }

        public Converter<T> converter() {
            return converter;
        }

        public P<T> description(final String description) {
            this.description = description;
            return this;
        }

        public Option<ParameterDescription> description() {
            return Option.of(name)
                         .map(paramName -> ParameterDescription.of(name,
                                                                   converter.typeDescription(),
                                                                   description));
        }

        public <R> P<R> and(final Validator<R, T> validator) {
            return new P<R>((RequestContext context) -> converter.apply(context).flatMap(validator::apply));
        }

        public <R, T1> P<R> and(final FN2<Either<? extends BaseError, R>, T, T1> validator, final T1 param1) {
            return and(input -> validator.apply(input, param1));
        }

        public <R, T1, T2> P<R> and(final FN3<Either<? extends BaseError, R>, T, T1, T2> validator, final T1 param1, final T2 param2) {
            return and(input -> validator.apply(input, param1, param2));
        }

        public <R, T1, T2, T3> P<R> and(final FN4<Either<? extends BaseError, R>, T, T1, T2, T3> validator, final T1 param1, final T2 param2, final T3 param3) {
            return and(input -> validator.apply(input, param1, param2, param3));
        }

        public <R, T1, T2, T3, T4> P<R> and(final FN5<Either<? extends BaseError, R>, T, T1, T2, T3, T4> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4) {
            return and(input -> validator.apply(input, param1, param2, param3, param4));
        }

        public <R, T1, T2, T3, T4, T5> P<R> and(final FN6<Either<? extends BaseError, R>, T, T1, T2, T3, T4, T5> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5) {
            return and(input -> validator.apply(input, param1, param2, param3, param4, param5));
        }

        public <R, T1, T2, T3, T4, T5, T6> P<R> and(final FN7<Either<? extends BaseError, R>, T, T1, T2, T3, T4, T5, T6> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6) {
            return and(input -> validator.apply(input, param1, param2, param3, param4, param5, param6));
        }

        public <R, T1, T2, T3, T4, T5, T6, T7> P<R> and(final FN8<Either<? extends BaseError, R>, T, T1, T2, T3, T4, T5, T6, T7> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6, final T7 param7) {
            return and(input -> validator.apply(input, param1, param2, param3, param4, param5, param6, param7));
        }

        public <R, T1, T2, T3, T4, T5, T6, T7, T8> P<R> and(final FN9<Either<? extends BaseError, R>, T, T1, T2, T3, T4, T5, T6, T7, T8> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6, final T7 param7, final T8 param8) {
            return and(input -> validator.apply(input, param1, param2, param3, param4, param5, param6, param7, param8));
        }
    }
}

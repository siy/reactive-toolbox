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
import org.reactivetoolbox.web.server.Request;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.Response;
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

/**
 * HTTP Request parameters.
 */
public class Parameters {
    private static final ConverterFactory FACTORY = ServiceLoader.load(ConverterFactory.class)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Unable to find suitable ConverterFactory"));

    /**
     * Define parameter passed as part of request path.
     *
     * @param type
     *        Parameter type
     * @param name
     *        Parameter name
     * @return Request parameter definition
     */
    public static <T> P<Option<T>> inPath(final Class<T> type, final String name) {
        return new P<>(FACTORY.getParameterConverter(type, name), name);
    }

    /**
     * Define parameter passed as part of query string.
     *
     * @param type
     *        Parameter type
     * @param name
     *        Parameter name
     * @return Request parameter definition
     */
    public static <T> P<Option<T>> inQuery(final Class<T> type, final String name) {
        return new P<>(FACTORY.getParameterConverter(type, name), name);
    }

    /**
     * Define parameter passed as part of request body.
     *
     * @param type
     *        Parameter type
     * @param name
     *        Parameter name
     * @return Request parameter definition
     */
    public static <T> P<Option<T>> inBody(final Class<T> type, final String name) {
        return new P<>(FACTORY.getBodyValueConverter(type, name), name);
    }

    /**
     * Define parameter passed as request header.
     *
     * @param type
     *        Parameter type
     * @param name
     *        Parameter name
     * @return Request parameter definition
     */
    public static <T> P<Option<T>> inHeader(final Class<T> type, final HeaderName name) {
        return new P<>(FACTORY.getHeaderConverter(type, name), name.header());
    }

    /**
     * Define parameter which will hold information from request context.
     * The {@code type} parameter can be one of the following classes:
     * <ul>
     *     <li>{@link RequestContext} - for full request processing context</li>
     *     <li>{@link Request} - for request part of the context</li>
     *     <li>{@link Response} - for response part of the context</li>
     * </ul>
     *
     * @param type
     *        Parameter type - one of the listed above
     * @return Request parameter definition
     */
    public static <T> P<Option<T>> inContext(final Class<T> type) {
        return new P<>(FACTORY.getContextConverter(type), (String) null);
    }

    /**
     * Define parameter derived from {@code "Authorization"} header.
     *
     * @param header
     *        Type of the recognized authorization. Basic and JWT types are supported
     * @return Request parameter definition
     */
    public static P<Option<Authentication>> inAuthHeader(final AuthHeader header) {
        return new P<>(FACTORY.getHeaderConverter(Authentication.class, Headers.AUTHORIZATION, header), (String) null);
    }

    public static <T> P<T> of(final Converter<T> converter, final ParameterDescription parameterDescription) {
        return new P<>(converter, parameterDescription);
    }

    /**
     * Container for parameter definition.
     *
     * @param <T>
     *        Type of the parameter value
     */
    public static class P<T> {
        private final Converter<T> converter;
        private final ParameterDescription parameterDescription;

        private P(final Converter<T> converter, final String name) {
            this(converter, ParameterDescription.of(name, converter.typeDescription(), null));
        }

        private P(final Converter<T> converter, final ParameterDescription parameterDescription) {
            this.converter = converter;
            this.parameterDescription = parameterDescription;
        }

        public Converter<T> converter() {
            return converter;
        }

        public P<T> description(final String description) {
            return new P<>(converter, parameterDescription.description(description));
        }

        public Option<ParameterDescription> description() {
            return Option.of(parameterDescription);
        }

        /**
         * Add validation to the parameter.
         *
         * @param validator
         *        The validator to be applied to parameter during conversion of parameter value
         * @param <R>
         *        New type of the parameter value
         * @return updated parameter definition
         */
        public <R> P<R> and(final Validator<R, T> validator) {
            return validator.modify(this);
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

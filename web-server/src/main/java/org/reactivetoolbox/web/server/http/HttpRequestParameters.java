package org.reactivetoolbox.web.server.http;

import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.web.server.parameter.HeaderName;
import org.reactivetoolbox.web.server.parameter.Headers;
import org.reactivetoolbox.web.server.parameter.auth.Authentication;
import org.reactivetoolbox.web.server.parameter.auth.AuthenticationConverter;
import org.reactivetoolbox.web.server.parameter.auth.AuthorizationHeaderType;
import org.reactivetoolbox.web.server.parameter.conversion.ProcessingContext;
import org.reactivetoolbox.web.server.parameter.conversion.var.Var;

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
public class HttpRequestParameters {
    /**
     * Define parameter passed as part of request path.
     *
     * @param type
     *        Parameter type
     * @param name
     *        Parameter name
     * @return Request parameter definition
     */
    public static <T> Var<Option<T>> inPath(final Class<T> type, final String name) {
        return Var.of(HttpParameterFactory.pathParameter(type, name), name);
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
    public static <T> Var<Option<T>> inQuery(final Class<T> type, final String name) {
        return Var.of(HttpParameterFactory.queryParameter(type, name), name);
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
    public static <T> Var<Option<T>> inBody(final Class<T> type, final String name) {
        return Var.of(HttpParameterFactory.bodyParameter(type, name), name);
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
    public static <T> Var<Option<T>> inHeader(final Class<T> type, final HeaderName name) {
        return Var.of(HttpParameterFactory.headerParameter(type, name.header()), name.header());
    }

    /**
     * Define parameter which will hold information from request context.
     * The {@code type} parameter can be one of the following classes:
     * <ul>
     *     <li>{@link ProcessingContext} - for full request processing context</li>
     *     <li>{@link Request} - for request part of the context</li>
     *     <li>{@link Response} - for response part of the context</li>
     * </ul>
     *
     * @param type
     *        Parameter type - one of the listed above
     * @return Request parameter definition
     */
    public static <T> Var<Option<T>> inContext(final Class<T> type) {
        return Var.of(HttpParameterFactory.inContext(type), (String) null);
    }

    /**
     * Define parameter derived from {@code "Authorization"} header.
     *
     * @param header
     *        Type of the recognized authorization. Basic and JWT types are supported
     * @return Request parameter definition
     */
    public static Var<Option<Authentication>> inAuthHeader(final AuthorizationHeaderType header) {
        return inHeader(String.class, Headers.AUTHORIZATION).and(AuthenticationConverter.create(header));
    }
}

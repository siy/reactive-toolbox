package org.reactivetoolbox.web.server;

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

import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.core.functional.Pair;

import java.util.List;
import java.util.Map;

/**
 * Generalized HTTP request interface
 */
public interface Request {
    /**
     * Set parameter values retrieved from request path.
     *
     * @param pairs
     *        List of name-value pairs for parameters
     * @return <code>this</code> for fluent call chaining
     */
    Request pathParameters(final List<Pair<String, String>> pairs);

    /**
     * Return full context to which this request belongs.
     *
     * @return Request context.
     */
    RequestContext context();

    /**
     * Get value of path parameter.
     *
     * @param name
     *        Parameter name
     * @return Result of parameter search wrapped into {@link Option}
     */
    Option<String> pathParameter(final String name);

    /**
     * Get parameter from query string. If multiple values with same name
     * provided, then first one is returned.
     *
     * @param name
     *        Parameter name
     * @return Result of parameter search wrapped into {@link Option}
     */
    Option<String> queryParameter(String name);

    /**
     * Retrieve all values for parameter provided in request query string. If no such parameter exists then empty list
     * is returned.
     *
     * @param name
     *        Parameter name
     * @return List of all values associated with this parameter name
     */
    List<String> queryParameters(String name);

    /**
     * Return all request parameters passed in query string.
     *
     * @return Map where keys are parameter names and values are lists of parameter values
     */
    Map<String, List<String>> queryParameters();

    /**
     * Get value of specified request header.
     *
     * @param name
     *        Header name
     * @return Header value wrapped into {@link Option}
     */
    Option<String> header(String name);

    /**
     * Get value retrieved from body (usually a form). Note that once this call is made, body may not be accessible
     * anymore, as this call triggers reading and parsing of full request body. The call to {@link #body()} will
     * return empty {@link Option} since body value is not available.
     * Retrieving other names parameters should be fine.
     *
     * @param name
     *        Parameter name
     * @return Body parameter value wrapped into {@link Option}
     * @see #body()
     */
    Option<String> bodyParameter(String name);

    /**
     * Retrieve full body of the request. Use with care, as this call will load whole request into memory. This might
     * be quite a lot if request, for example, contains uploaded file.
     *
     * @return Whole request body wrapped into {@link Option}
     * @see #bodyParameter(String)
     */
    Option<String> body();
}

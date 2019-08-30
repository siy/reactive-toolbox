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

import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.eventbus.RawParameters;

/**
 * Generalized HTTP request interface
 */
public interface Request {
    /**
     * Set parameter values retrieved from request path.
     *
     * @param parameters
     *        Parameters retrieved from request path
     * @return <code>this</code> for fluent call chaining
     */
    Request pathParameters(final RawParameters parameters);

    /**
     * Return full context to which this request belongs.
     *
     * @return Request context.
     */
    HttpProcessingContext context();

    /**
     * Get path parameters.
     *
     * @return instance of {@link RawParameters} corresponding to path parameters
     */
    RawParameters pathParameters();

    /**
     * Get query parameters.
     *
     * @return instance of {@link RawParameters} corresponding to query parameters
     */
    RawParameters queryParameters();

    /**
     * Get header parameters.
     *
     * @return instance of {@link RawParameters} corresponding to header parameters
     */
    RawParameters headerParameters();

    /**
     * Get parameters encoded in the body as form parameters.
     * Note that call to this parameter has side effect of reading request
     * body and parsing it.
     *
     * @return instance of {@link RawParameters} corresponding to header parameters
     * @see #body()
     */
    RawParameters bodyParameters();

    /**
     * Get cookies from this request
     *
     * @return instance of {@link Cookies} with all cookies provided with this request
     */
    Cookies cookies();

    /**
     * Retrieve full body of the request.
     * Note that call to this method triggers loading of entire request body
     * into memory.
     *
     * @return Whole request body wrapped into {@link Option}
     * @see #bodyParameters
     */
    Option<String> body();
}

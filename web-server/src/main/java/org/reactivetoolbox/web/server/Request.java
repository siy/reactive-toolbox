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

//TODO: extend it to make usable, add Javadoc
public interface Request {
    Request pathParameters(final List<Pair<String, String>> pairs);

    RequestContext context();

    Option<String> pathParameter(final String name);

    Option<String> queryParameter(String name);

    List<String> queryParameters(String name);

    Map<String, List<String>> queryParameters();

    Option<String> header(String name);

    Option<String> bodyParameter(String name);

    Option<String> body();
}

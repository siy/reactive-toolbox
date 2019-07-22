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
import org.reactivetoolbox.eventbus.Path;

import java.util.List;

/**
 * HTTP Request context abstraction
 */
public interface RequestContext {
    Request request();

    Response response();

    Path path();

    RequestContext pathParameters(final List<Pair<String, String>> pairs);

    Option<String> pathParameter(final String name);
}

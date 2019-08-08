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
import org.reactivetoolbox.eventbus.PathKey;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * HTTP Methods declaration suitable for building {@link org.reactivetoolbox.eventbus.Path} for HTTP routes.
 */
public enum HttpMethod implements PathKey {
    GET, PUT, PATCH, POST, HEADER;

    private static final Map<String, HttpMethod> NAME_TO_METHOD = new HashMap<>();

    static {
        Stream.of(values()).forEach(method -> NAME_TO_METHOD.put(method.key(), method));
    }

    @Override
    public String key() {
        return name();
    }

    public static Option<HttpMethod> fromString(final String name) {
        return Option.of(name)
                     .map(String::toUpperCase)
                     .flatMap(s -> Option.of(NAME_TO_METHOD.get(s)));
    }
}

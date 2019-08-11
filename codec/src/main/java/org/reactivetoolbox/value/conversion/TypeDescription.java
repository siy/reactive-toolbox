package org.reactivetoolbox.value.conversion;

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
 * Container for the documentation for the type. Used to generate human-readable documentation
 * for particular type while generating API docs (for example, <a href="https://swagger.io/docs/specification/about/">OpenAPI</a>).
 */
public class TypeDescription {
    private final String description;

    private TypeDescription(final String description) {
        this.description = description;
    }

    public static TypeDescription of(final String description) {
        return new TypeDescription(description);
    }

    public String description() {
        return description;
    }
}

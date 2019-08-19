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

import org.reactivetoolbox.core.functional.Option;

import java.util.List;

/**
 * Request processing context abstraction. The intent of this interface is to
 * concentrate all dependencies necessary to process specific request.
 */
public interface ProcessingContext {
    <T> ValueConverter<T> valueConverter(final Class<T> type);

    Option<String> first(String name);

    <T> MultiValueConverter<List<T>> multiValueConverter(final Class<T> elementType);

    Option<List<String>> all(String name);
}
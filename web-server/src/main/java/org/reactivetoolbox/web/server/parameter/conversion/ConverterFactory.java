package org.reactivetoolbox.web.server.parameter.conversion;

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
import org.reactivetoolbox.web.server.parameter.HeaderName;
import org.reactivetoolbox.web.server.parameter.auth.AuthHeader;

//TODO: Javadoc
public interface ConverterFactory {
    <T> Converter<Option<T>> getParameterConverter(final Class<T> type, final String name);

    <T> Converter<Option<T>> getHeaderConverter(final Class<T> type, final HeaderName name);

    <T> Converter<Option<T>> getHeaderConverter(final Class<T> type, final HeaderName name, final AuthHeader headerType);

    <T> Converter<Option<T>> getBodyValueConverter(final Class<T> type, final String name);

    <T> Converter<Option<T>> getContextConverter(final Class<T> type);
}

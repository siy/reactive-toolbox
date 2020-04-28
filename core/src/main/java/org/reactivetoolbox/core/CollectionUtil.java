package org.reactivetoolbox.core;

/*
 * Copyright (c) 2019, 2020 Sergiy Yevtushenko
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

import org.reactivetoolbox.core.lang.functional.Functions.FN1;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helper methods for transformation of List's and Set's.
 */
public interface CollectionUtil {
    static <R, T> List<R> map(final List<T> input, final FN1<R, T> mapper) {
        return input.stream().map(mapper::apply).collect(Collectors.toList());
    }

    static <R, T> Set<R> map(final Set<T> input, final FN1<R, T> mapper) {
        return input.stream().map(mapper::apply).collect(Collectors.toSet());
    }
}

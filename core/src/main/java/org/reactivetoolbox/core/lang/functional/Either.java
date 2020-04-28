package org.reactivetoolbox.core.lang.functional;

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

/**
 * The core interface for container which can hold either one value or other, but not both.
 *
 * @param <L>
 * @param <R>
 */
public interface Either<L, R> {
    <T> T fold(FN1<? extends T, ? super L> leftMapper, FN1<? extends T, ? super R> rightMapper);
}

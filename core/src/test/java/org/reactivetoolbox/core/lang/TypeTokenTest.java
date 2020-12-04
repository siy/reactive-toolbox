/*
 * Copyright (c) 2020 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.reactivetoolbox.core.lang;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.lang.Tuple.Tuple1;
import org.reactivetoolbox.core.lang.collection.List;
import org.reactivetoolbox.core.lang.functional.Result;

import java.lang.reflect.ParameterizedType;

import static org.junit.jupiter.api.Assertions.fail;
import static org.reactivetoolbox.core.lang.functional.When.when;

class TypeTokenTest {
    @Test
    void typeIsDetectedCorrectly() {
        final var token = new TypeToken<List<Pair<Result<String>, Tuple1<Integer>>>>() {};

        token.type()
             .onFailure(v -> fail("Type should be detected"))
             .onSuccess(type -> {
                 when(type)
                         .isA(ParameterizedType.class).then(parameterizedType -> {});
             });
    }
}
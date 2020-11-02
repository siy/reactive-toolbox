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
import org.reactivetoolbox.core.lang.functional.ThrowingFunctions.TFN1;
import org.reactivetoolbox.core.lang.support.WebFailureType;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.reactivetoolbox.core.lang.functional.ThrowingFunctions.lift;

class ThrowingFunctionsTest {

    @Test
    void lift1() {
        final var uriParser = lift((TFN1<URI, String>) URI::new);

        uriParser.apply("https://dev.to/")
                 .onFailure(failure -> fail())
                 .onSuccess(uri -> assertEquals("https://dev.to/", uri.toString()));

        uriParser.apply(":malformed/url")
                 .onFailure(failure -> assertEquals(failure.type(), WebFailureType.BAD_REQUEST))
                 .onSuccess(uri -> fail());
    }
}
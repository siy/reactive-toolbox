/*
 * Copyright (c) 2019 siy
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
 *
 */

package org.reactivetoolbox.async;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.reactivetoolbox.async.Tuples.with;

class TuplesTest {

    @Test
    void tuplesCanBeMapper() {
        assertEquals(">1",
                     with(1)
                           .map((v1) -> ">" + v1));

        assertEquals(">1a",
                     with(1, "a")
                           .map((v1, v2) -> ">" + v1 + v2));

        assertEquals(">1a3",
                     with(1, "a", 3)
                           .map((v1, v2, v3) -> ">" + v1 + v2 + v3));

        assertEquals(">1a3b",
                     with(1, "a", 3, "b")
                           .map((v1, v2, v3, v4) -> ">" + v1 + v2 + v3 + v4));

        assertEquals(">1a3b5",
                     with(1, "a", 3, "b", 5)
                           .map((v1, v2, v3, v4, v5) -> ">" + v1 + v2 + v3 + v4 + v5));

        assertEquals(">1a3b5c",
                     with(1, "a", 3, "b", 5, "c")
                           .map((v1, v2, v3, v4, v5, v6) -> ">" + v1 + v2 + v3 + v4 + v5 + v6));

        assertEquals(">1a3b5c7",
                     with(1, "a", 3, "b", 5, "c", 7)
                           .map((v1, v2, v3, v4, v5, v6, v7) -> ">" + v1 + v2 + v3 + v4 + v5 + v6 + v7));

        assertEquals(">1a3b5c7d",
                     with(1, "a", 3, "b", 5, "c", 7, "d")
                           .map((v1, v2, v3, v4, v5, v6, v7, v8) -> ">" + v1 + v2 + v3 + v4 + v5 + v6 + v7 + v8));

        assertEquals(">1a3b5c7d9",
                     with(1, "a", 3, "b", 5, "c", 7, "d", 9)
                           .map((v1, v2, v3, v4, v5, v6, v7, v8, v9) -> ">" + v1 + v2 + v3 + v4 + v5 + v6 + v7 + v8 + v9));
    }

    @Test
    void tuplesCanBeReturnedAndMappedAgain() {
        final Optional<String> result = with("1", 2, "3", 4, "5", 6, "7", 8)
                .map((v1, v2, v3, v4, v5, v6, v7, v8) -> with(v1 + v2, v3 + v4, v5 + v6, v7 + v8))
                .map((v1, v2, v3, v4) -> with(v4 + v3, v2 + v1))
                .map((v1, v2) -> with(v2 + v1))
                .map();

        assertTrue(result.isPresent());
        assertEquals("34127856", result.get());
    }
}
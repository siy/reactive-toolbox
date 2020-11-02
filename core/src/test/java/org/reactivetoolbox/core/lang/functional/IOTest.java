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

package org.reactivetoolbox.core.lang.functional;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IOTest {
    @Test
    void chainIsExecuted() {
        IO.io(() -> "test")
          .apply(value -> assertEquals("test", value))
          .run();
    }

    @Test
    void mapAppliesTransformation() {
        IO.io(() -> "1")
          .map(Integer::parseInt)
          .apply(value -> assertEquals(1, value))
          .run();
    }

    @Test
    void flatMapAppliesTransformation() {
        IO.io(() -> "1")
          .flatMap(value -> IO.io(() -> Integer.parseInt(value)))
          .apply(value -> assertEquals(1, value))
          .run();
    }

    @Test
    void executionIsDelayedUntilRunIsCalled() {
        final var holder = new AtomicInteger(0);

        final var delayed = IO.io(() -> "1")
          .flatMap(value -> IO.io(() -> Integer.parseInt(value)))
          .map(value -> {holder.set(value); return value;})
          .apply(value -> assertEquals(1, value));

        assertEquals(0, holder.get());
        delayed.run();
        assertEquals(1, holder.get());
    }
}
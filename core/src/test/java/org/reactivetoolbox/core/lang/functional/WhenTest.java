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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.reactivetoolbox.core.lang.functional.When.when;

class WhenTest {

    public static final Integer ONE = Integer.valueOf(1);

    @Test
    void classIsMatchedCorrectly() {
        when(createValue())
                .isA(Byte.class).then(v -> fail())
                .isA(Integer.class).then(this::assertIsOne)
                .isA(Short.class).then(v -> fail())
                .isA(Long.class).then(v -> fail())
                .otherwiseDo(Assertions::fail);
    }

    @Test
    void objectEqualityIsHandledCorrectly() {
        when(createValue())
                .equal(-1).then(v -> fail())
                .equal(5).then(v -> fail())
                .equal(1).then(this::assertIsOne)
                .otherwiseDo(Assertions::fail);
    }

    @Test
    void lessThanIsHandledCorrectly() {
        when(createIntegerValue())
                .lt(-1).then(v -> fail())
                .lt(1).then(v -> fail())
                .lt(2).then(this::assertIsOne)
                .otherwiseDo(Assertions::fail);
    }

    @Test
    void lessThanOrEqualIsHandledCorrectly() {
        when(createIntegerValue())
                .le(-1).then(v -> fail())
                .le(1).then(this::assertIsOne)
                .le(2).then(this::assertIsOne)
                .otherwiseDo(Assertions::fail);
    }

    @Test
    void greaterThanIsHandledCorrectly() {
        when(createIntegerValue())
                .gt(2).then(v -> fail())
                .gt(1).then(v -> fail())
                .gt(-1).then(this::assertIsOne)
                .otherwiseDo(Assertions::fail);
    }

    @Test
    void greaterThanOrEqualIsHandledCorrectly() {
        when(createIntegerValue())
                .ge(2).then(v -> fail())
                .ge(1).then(this::assertIsOne)
                .ge(-1).then(this::assertIsOne)
                .otherwiseDo(Assertions::fail);
    }

    @Test
    void equalIsHandledCorrectly() {
        when(createIntegerValue())
                .eq(2).then(v -> fail())
                .eq(1).then(this::assertIsOne)
                .eq(-1).then(v -> fail())
                .otherwiseDo(Assertions::fail);
    }

    @Test
    void notEqualIsHandledCorrectly() {
        when(createIntegerValue())
                .neq(2).then(this::assertIsOne)
                .neq(1).then(v -> fail())
                .neq(-1).then(this::assertIsOne)
                .otherwiseDo(Assertions::fail);
    }

    @Test
    void comparisonWorksCorrectlyForStringsToo() {
        when("abc")
                .lt("abc").then(v -> fail())
                .lt("abcd").then(v -> assertEquals("abc", v))
                .le("abcd").then(v -> assertEquals("abc", v))
                .le("abc").then(v -> assertEquals("abc", v))
                .eq("abc").then(v -> assertEquals("abc", v))
                .eq("abcd").then(v -> fail())
                .neq("abcd").then(v -> assertEquals("abc", v))
                .neq("abc").then(v -> fail())
                .ge("abc").then(v -> assertEquals("abc", v))
                .ge("ab").then(v -> assertEquals("abc", v))
                .gt("ab").then(v -> assertEquals("abc", v))
                .gt("abc").then(v -> fail())
                .otherwiseDo(Assertions::fail);
    }

    @Test
    void ifNoneMatchedThenOtherwiseClauseIsInvoked() {
        when("abc")
                .lt("abc").then(v -> fail())
                .eq("abcd").then(v -> fail())
                .neq("abc").then(v -> fail())
                .gt("abc").then(v -> fail())
                .otherwise(v -> assertEquals("abc", v));
    }

    private Number createValue() {
        return ONE;
    }

    private Integer createIntegerValue() {
        return ONE;
    }

    private void assertIsOne(final Number value) {
        assertEquals(ONE, value);
    }
}
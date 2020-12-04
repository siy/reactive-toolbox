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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class OptionTest {
    @Test
    void emptyOptionCanBeCreated() {
        Option.empty()
              .apply(() -> {}, v -> fail());
    }

    @Test
    void optionWithDataCanBeCreated() {
        Option.option("not empty")
              .apply(Assertions::fail, v -> assertEquals("not empty", v));
    }

    @Test
    void nonEmptyOptionCanBeMappedToOtherOption() {
        Option.option(123)
                .whenEmpty(Assertions::fail)
                .whenPresent(v -> assertEquals(123, v))
                .map(Object::toString)
                .whenEmpty(Assertions::fail)
                .whenPresent(v -> assertEquals("123", v));
    }

    @Test
    void emptyOptionRemainsEmptyAfterMapping() {
        Option.empty()
              .whenPresent(v -> fail())
              .map(Object::toString)
              .whenPresent(v -> fail());
    }

    @Test
    void nonEmptyOptionCanBeFlatMappedIntoOtherOption() {
        Option.option(345)
              .whenEmpty(Assertions::fail)
              .whenPresent(v -> assertEquals(345, v))
              .flatMap(val -> Option.option(val + 2))
              .whenEmpty(Assertions::fail)
              .whenPresent(v -> assertEquals(347, v));
    }

    @Test
    void emptyOptionRemainsEmptyAndNotFlatMapped() {
        Option.empty()
              .whenPresent(v -> fail())
              .flatMap(val -> Option.option("not empty"))
              .whenPresent(v -> fail());
    }

    @Test
    void logicalOrChoosesFirsNonEmptyOption1() {
        final var firstNonEmpty = Option.option("1");
        final var secondNonEmpty = Option.option("2");
        final var firstEmpty = Option.<String>empty();
        final var secondEmpty = Option.<String>empty();

        assertEquals(firstNonEmpty, firstNonEmpty.or(() -> secondNonEmpty));
        assertEquals(firstNonEmpty, firstEmpty.or(() -> firstNonEmpty));
        assertEquals(firstNonEmpty, firstEmpty.or(() -> firstNonEmpty).or(() -> secondNonEmpty));
        assertEquals(firstNonEmpty, firstEmpty.or(() -> secondEmpty).or(() -> firstNonEmpty));
    }

    @Test
    void logicalOrChoosesFirsNonEmptyOption2() {
        final var firstNonEmpty = Option.option("1");
        final var secondNonEmpty = Option.option("2");
        final var firstEmpty = Option.<String>empty();
        final var secondEmpty = Option.<String>empty();

        assertEquals(firstNonEmpty, firstNonEmpty.or(secondNonEmpty));
        assertEquals(firstNonEmpty, firstEmpty.or(firstNonEmpty));
        assertEquals(firstNonEmpty, firstEmpty.or(firstNonEmpty).or(secondNonEmpty));
        assertEquals(firstNonEmpty, firstEmpty.or(secondEmpty).or(firstNonEmpty));
    }

    @Test
    void otherwiseProvidesValueIfOptionIsEmpty() {
        assertEquals(123, Option.empty().otherwise(123));
        assertEquals(123, Option.empty().otherwiseGet(() -> 123));
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    void optionCanBeStreamed() {
        assertTrue(Option.empty().stream().findFirst().isEmpty());
        assertEquals(123, Option.option(123).stream().findFirst().get());
    }

    @Test
    void nonEmptyInstanceCanBeFiltered() {
        Option.option(123)
              .whenEmpty(Assertions::fail)
              .filter(val -> val > 1)
              .whenEmpty(Assertions::fail)
              .filter(val -> val < 100)
              .whenPresent(val -> fail());
    }

    @Test
    void emptyInstanceRemainsEmptyAfterFilteringAndPredicateIsNotInvoked() {
        Option.empty()
              .whenPresent(v -> fail())
              .filter(v -> true)
              .whenPresent(v -> fail());
    }

    @Test
    void emptyInstancesAreEqual() {
        assertFalse(Option.empty().equals(""));
        assertEquals(Option.empty(), Option.option(null));
    }

    @Test
    void nonEmptyInstancesAreEqual() {
        assertFalse(Option.option(1).equals(1));
        assertEquals(Option.option(1), Option.option(1));
    }

    @Test
    void optionCanBePutInMap() {
        final var map = Map.of(1, Option.option(1), 2, Option.option(2));

        assertEquals(Option.option(1), map.get(1));
        assertEquals(Option.option(2), map.get(2));
    }
}

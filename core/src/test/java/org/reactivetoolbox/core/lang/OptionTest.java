package org.reactivetoolbox.core.lang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.lang.functional.Option;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

//TODO: fix tests
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

package org.reactivetoolbox.core.functional;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class OptionTest {
    @Test
    void emptyOptionCanBeCreated() {
        assertNull(Option.empty().get());
    }

    @Test
    void optionWithDataCanBeCreated() {
        assertEquals("not empty", Option.of("not empty").get());
    }

    @Test
    void nonEmptyOptionCanBeMappedToOtherOption() {
        final var originalOption = Option.of(123);

        assertEquals(123, originalOption.get());
        assertEquals("123", originalOption.map(Object::toString).get());
    }

    @Test
    void emptyOptionRemainsEmptyAfterMapping() {
        assertNull(Option.empty().map(Object::toString).get());
    }

    @Test
    void emptyOptionIsNotPresent() {
        assertFalse(Option.of(null).isPresent());
        assertFalse(Option.empty().isPresent());
        assertTrue(Option.of(null).isEmpty());
        assertTrue(Option.empty().isEmpty());
    }

    @Test
    void nonEmptyOptionIsPresent() {
        assertTrue(Option.of(1).isPresent());
    }

    @Test
    void nonEmptyOptionCanBeConsumed() {
        Option.of(321L)
              .consume(val -> assertEquals(321L, val));
    }

    @Test
    void nonEmptyOptionCantBeConsumed() {
        Option.empty()
              .consume(val -> fail());
    }

    @Test
    void nonEmptyOptionCanBeFlatMappedIntoOtherOption() {
        assertEquals(347 , Option.of(345).flatMap(val -> Option.of(val + 2)).get());
    }

    @Test
    void emptyOptionRemainsEmptyAndNotFlatMapped() {
        assertTrue(Option.empty().flatMap(val -> Option.of(Objects.toString(val))).isEmpty());
    }

    @Test
    void logicalOrChoosesFirsNonEmptyOption1() {
        final var firstNonEmpty = Option.of("1");
        final var secondNonEmpty = Option.of("2");
        final var firstEmpty = Option.<String>empty();
        final var secondEmpty = Option.<String>empty();

        assertEquals(firstNonEmpty, firstNonEmpty.or(() -> secondNonEmpty));
        assertEquals(firstNonEmpty, firstEmpty.or(() -> firstNonEmpty));
        assertEquals(firstNonEmpty, firstEmpty.or(() -> firstNonEmpty).or(() -> secondNonEmpty));
        assertEquals(firstNonEmpty, firstEmpty.or(() -> secondEmpty).or(() -> firstNonEmpty));
    }

    @Test
    void logicalOrChoosesFirsNonEmptyOption2() {
        final var firstNonEmpty = Option.of("1");
        final var secondNonEmpty = Option.of("2");
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
        assertEquals(123, Option.of(123).stream().findFirst().get());
    }

    @Test
    void nonEmptyInstanceCanBeFiltered() {
        assertEquals(123, Option.of(123).filter(val -> val > 1).get());
    }

    @Test
    void emptyInstanceRemainsEmptyAfterFilteringAndPredicateIsNotInvoked() {
        assertTrue( Option.empty().filter(val -> {fail(); return true;}).isEmpty());
    }
}
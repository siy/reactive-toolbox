package org.reactivetoolbox.core.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.reactivetoolbox.core.lang.Pair.pair;

class PairTest {
    @Test
    void pairCanBeCreated() {
        final var pair = pair(1, "one");

        pair.applyLeft(l -> assertEquals(1, l));
        pair.applyRight(r -> assertEquals("one", r));
        assertFalse(pair.equals(null));
        assertTrue(pair.equals(pair));
        assertTrue(pair(1, "one").equals(pair));
        assertEquals(pair(1, "one").hashCode(), pair.hashCode());
        assertEquals(pair(1, "one").toString(), pair.toString());
    }

    @Test
    void pairCanBeMapped() {
        final var pair = pair("key", 10);
        assertEquals("key10", pair.fold((l, r) -> l + r));
        assertEquals(pair("-key-", 11), pair.map( l -> "-" + l + "-", r -> r + 1));
        assertEquals(pair("-key-", 10), pair.mapLeft( l -> "-" + l + "-"));
        assertEquals(pair("key", 11), pair.mapRight(r -> r + 1));
    }

    @Test
    void pairCanBeSwapped() {
        assertEquals(pair("value", "key"), pair("key", "value").swap());
    }

    @Test
    void canApplyFunctionToPair() {
        final var pair = pair("key", 15);
        pair.apply((l, r) -> {assertEquals("key", l); assertEquals(r, 15);});
        pair.applyLeft(l -> assertEquals("key", l));
        pair.applyRight(r -> assertEquals(r, 15));
    }
}
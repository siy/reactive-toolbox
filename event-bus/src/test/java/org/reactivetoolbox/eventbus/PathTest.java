package org.reactivetoolbox.eventbus;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.functional.Pair;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PathTest {

    @Test
    void properPathTypeIsCreated() {
        assertTrue(Path.of("/").isExact());
        assertTrue(Path.of("/one").isExact());
        assertTrue(Path.of("/one/two").isExact());
        assertTrue(Path.of("/one/{two}").hasParams());

        assertEquals("/one", Path.of("/one/{two}").prefix());
    }

    @Test
    void prefixIsNormalized() {
        assertEquals("/", Path.of("/").prefix());
        assertEquals("/one/", Path.of("/one").prefix());
        assertEquals("/one/", Path.of("/one/").prefix());
        assertEquals("/one/two/", Path.of("/one/two").prefix());
        assertEquals("/one/two/", Path.of("/one/two/").prefix());
    }

    @Test
    void prefixIsNormalizedIfKeyIsPresent() {
        assertEquals("/PUT/", Path.of("/", () -> "PUT").prefix());
        assertEquals("/GET/one/", Path.of("/one", () -> "GET").prefix());
        assertEquals("/GET/one/", Path.of("/one/", () -> "GET").prefix());
        assertEquals("/POST/one/two/", Path.of("/one/two", () -> "POST").prefix());
        assertEquals("/POST/one/two/", Path.of("/one/two/", () -> "POST").prefix());
    }

    @Test
    void parametersAreCollectedProperlyAtTheEndOfPath() {
        final var path = Path.of("/one/{param1}");

        assertTrue(path.hasParams());
        assertEquals(Arrays.asList("param1"), path.parameterNames());
        assertEquals(Arrays.asList(Pair.of("param1", "value1")), path.extractParameters("/one/value1"));
    }

    @Test
    void parametersAreCollectedProperlyInSubsequentSegments() {
        final var path = Path.of("/one/{param1}/{param2}/some_other_text");

        assertTrue(path.hasParams());
        assertEquals(Arrays.asList("param1", "param2"), path.parameterNames());
        assertEquals(Arrays.asList(Pair.of("param1", "value1"),
                                   Pair.of("param2", "value2")),
                     path.extractParameters("/one/value1/value2/some_other_text"));
    }
}
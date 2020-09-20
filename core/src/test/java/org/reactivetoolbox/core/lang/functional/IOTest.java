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
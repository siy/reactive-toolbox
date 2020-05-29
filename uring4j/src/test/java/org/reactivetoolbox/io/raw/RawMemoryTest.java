package org.reactivetoolbox.io.raw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RawMemoryTest {
    @Test
    void instanceCanBeObtained() {
        final long address = RawMemory.allocate(1024);
        RawMemory.release(address);

        assertTrue(address != 0);
    }
}
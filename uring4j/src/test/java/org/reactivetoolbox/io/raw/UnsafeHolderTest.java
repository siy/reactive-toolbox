package org.reactivetoolbox.io.raw;

import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

class UnsafeHolderTest {

    @Test
    void instanceCanBeObtained() {
        final Unsafe unsafe = UnsafeHolder.unsafe();

        assertNotNull(unsafe);
    }
}
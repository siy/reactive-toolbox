package org.reactivetoolbox.asyncio;

import org.junit.jupiter.api.Test;

class NativeIOTest {
    @Test
    void checkLoadLibrary() {
        final NativeIO ring = NativeIO.create(128);
        ring.close();
    }
}
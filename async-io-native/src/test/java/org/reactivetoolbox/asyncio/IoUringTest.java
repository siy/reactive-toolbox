package org.reactivetoolbox.asyncio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IoUringTest {
    @Test
    void checkLoadLibrary() {
        final IoUring ring = new IoUring(128);
        ring.close();
    }
}
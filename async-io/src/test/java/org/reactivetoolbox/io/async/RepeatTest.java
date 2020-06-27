package org.reactivetoolbox.io.async;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.io.async.lifecycle.Repeat;

import static org.junit.jupiter.api.Assertions.*;

public class RepeatTest {
    @Test
    void endIsEnd() {
        assertTrue(Repeat.END.isTerminal());
    }
}
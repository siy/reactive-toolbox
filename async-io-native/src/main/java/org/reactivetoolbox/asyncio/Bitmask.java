package org.reactivetoolbox.asyncio;

import java.util.Set;

public interface Bitmask {
    int mask();

    static int combine(final Set<? extends Bitmask> flags) {
        return flags.stream().mapToInt(Bitmask::mask).sum();
    }
}

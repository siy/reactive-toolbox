package org.reactivetoolbox.io.async.net;

import org.reactivetoolbox.io.Bitmask;

public enum SocketOption implements Bitmask {
    SO_KEEPALIVE(0x0001), // Enable keep-alive packets
    SO_REUSEADDR(0x0002), // Enable reuse address option
    SO_REUSEPORT(0x0004), // Enable reuse port option
    SO_LINGER   (0x0008); // Enable linger option and set linger time to 0

    private final int mask;

    SocketOption(final int mask) {
        this.mask = mask;
    }

    @Override
    public int mask() {
        return 0;
    }
}

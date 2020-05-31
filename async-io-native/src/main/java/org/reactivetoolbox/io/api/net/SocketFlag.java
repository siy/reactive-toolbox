package org.reactivetoolbox.io.api.net;

import org.reactivetoolbox.io.Bitmask;

/**
 * Socket open/accept flags.
 */
public enum SocketFlag implements Bitmask {
    SOCK_CLOEXEC(0x00080000),     /* Set close-on-exec flag for the descriptor */
    SOCK_NONBLOCK(0x00000800);    /* Mark descriptor as non-blocking */

    private final int mask;

    SocketFlag(final int mask) {
        this.mask = mask;
    }

    @Override
    public int mask() {
        return mask;
    }
}

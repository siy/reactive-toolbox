package org.reactivetoolbox.io.async.net;

import org.reactivetoolbox.io.Bitmask;

import java.util.EnumSet;

/**
 * Socket open/accept flags.
 */
public enum SocketFlag implements Bitmask {
    CLOEXEC(0x00080000),     /* Set close-on-exec flag for the descriptor */
    NONBLOCK(0x00000800);    /* Mark descriptor as non-blocking */

    private static final EnumSet<SocketFlag> NONE = EnumSet.noneOf(SocketFlag.class);
    private static final EnumSet<SocketFlag> CLOSE_ON_EXEC = EnumSet.of(CLOEXEC);

    private final int mask;

    SocketFlag(final int mask) {
        this.mask = mask;
    }

    @Override
    public int mask() {
        return mask;
    }

    public static EnumSet<SocketFlag> noFlags() {
        return NONE;
    }

    public static EnumSet<SocketFlag> closeOnExec() {
        return CLOSE_ON_EXEC;
    }
}

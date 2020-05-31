package org.reactivetoolbox.io.async;

import org.reactivetoolbox.io.Bitmask;

public enum SpliceFlags implements Bitmask {
    MOVE(1),        /* SPLICE_F_MOVE    : Move pages instead of copying.  */
    NONBLOCK(2),    /* SPLICE_F_NONBLOCK: Don't block on the pipe splicing */
    MORE(4);        /* SPLICE_F_MORE    : Expect more data.  */

    private final int mask;

    SpliceFlags(final int mask) {
        this.mask = mask;
    }

    @Override
    public int mask() {
        return mask;
    }
}

package org.reactivetoolbox.io.uring;

import org.reactivetoolbox.io.Bitmask;

import java.util.EnumSet;

public enum UringSetupFlags implements Bitmask {
    IOPOLL(1<<0),    /* io_context is polled */
    SQPOLL(1<<1),    /* SQ poll thread */
    SQ_AFF(1<<2),    /* sq_thread_cpu is valid */
    CQSIZE(1<<3),    /* app defines CQ size */
    CLAMP(1<<4),     /* clamp SQ/CQ ring sizes */
    ATTACH_WQ(1<<5); /* attach to existing wq */

    private static final EnumSet<UringSetupFlags> DEFAULT = EnumSet.noneOf(UringSetupFlags.class);
    private int mask;

    UringSetupFlags(final int mask) {
        this.mask = mask;
    }

    @Override
    public int mask() {
        return mask;
    }

    public static EnumSet<UringSetupFlags> defaultFlags() {
        return DEFAULT;
    }
}

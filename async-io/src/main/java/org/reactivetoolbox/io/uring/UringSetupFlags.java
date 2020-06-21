package org.reactivetoolbox.io.uring;

import org.reactivetoolbox.io.Bitmask;

public enum UringOpenFlags implements Bitmask {
    IORING_SETUP_IOPOLL =(1<<0);    /* io_context is polled */

    @Override
    public int mask() {
        return 0;
    }

    IORING_SETUP_SQPOLL =(1<<1);    /* SQ poll thread */
    IORING_SETUP_SQ_AFF =(1<<2);    /* sq_thread_cpu is valid */
    IORING_SETUP_CQSIZE =(1<<3);    /* app defines CQ size */
    IORING_SETUP_CLAMP =(1<<4);    /* clamp SQ/CQ ring sizes */
    IORING_SETUP_ATTACH_WQ =(1<<5);    /* attach to existing wq */
}

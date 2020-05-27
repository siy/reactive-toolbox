package org.reactivetoolbox.io.uring;

class Uring {
    /*
    extern int io_uring_queue_init(unsigned entries, struct io_uring *ring,	unsigned flags);
    extern int io_uring_ring_dontfork(struct io_uring *ring); //together with previous
    extern void io_uring_queue_exit(struct io_uring *ring);
    unsigned io_uring_peek_batch_cqe(struct io_uring *ring, struct io_uring_cqe **cqes, unsigned count);

    extern int io_uring_submit_and_wait(struct io_uring *ring, unsigned wait_nr);
    extern struct io_uring_sqe *io_uring_get_sqe(struct io_uring *ring);

    extern int io_uring_register_buffers(struct io_uring *ring,
                        const struct iovec *iovecs,
                        unsigned nr_iovecs);
    extern int io_uring_unregister_buffers(struct io_uring *ring);

    static inline void io_uring_cq_advance(struct io_uring *ring, unsigned nr);
    */

    //Start/Stop
    public static native int init(int numEntries, long baseAddress, long flags);
    public static native void close(long baseAddress);

    //Completion
    public static native int peekCQ(long baseAddress, long completionsAddress, long count);
    public static native int advanceCQ(long baseAddress, long count);
    public static native int readyCQ(long baseAddress);

    //Submissions
    public static native long spaceLeft(long baseAddress);
    public static native long nextSQEntry(long baseAddress);
    public static native long submitAndWait(long baseAddress, int waitNr);
}

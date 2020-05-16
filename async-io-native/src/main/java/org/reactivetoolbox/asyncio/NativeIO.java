package org.reactivetoolbox.asyncio;

import org.reactivetoolbox.asyncio.util.LibraryLoader;

class NativeIO {
    private static final int DEFAULT_QUEUE_SIZE = 4096;
    private static final int DEFAULT_FLAGS = 0;

    private final long ring;
    private final long[] completionData;

    // Startup/shutdown
    private native static int initApi();
    private native static long initRing(long entries, long flags);
    private native static void closeRing(long ring);

    // Completion Queue
    private native static int peekCQ(long ring, long[] data);
    private native static void advanceCQ(long ring, int count);

    // Submission Queue
    private native static long spaceLeft(long ring);
    private native static long submit(long ring);
    private native static long submitAndWait(long ring, long waitNum);

    // Preparations
    private native static int prepNop(long ring, long requestId);
    private native static int prepFsync(long ring, long requestId, int fd, long fsyncFlags);

    private native static int prepTimeout(long ring, long requestId, long seconds, long nanoseconds, long count, long flags);
    private native static int prepTimeoutRemove(long ring, long requestId, long flags);

    private native static int prepCancel(long ring, long requestId, int flags);
    //TODO: how to attach to previous request???
    private native static int prepLinkedTimeout(long ring, long requestId, long seconds, long nanoseconds, long flags);

    //sendfile/copy file
    private native static int prepSplice(long ring, long requestId, int fdIn, long offsetIn, int fdOut, long offsetOut, long numBytes, long spliceFlags);

    //r/w
    private native static int prepReadVector(long ring, long requestId, int fd, long[] bufs, int[] lens, long offset);
    private native static int prepWriteVector(long ring, long requestId, int fd, long[] bufs, int[] lens, long offset);
    private native static int prepReadFixed(long ring, long requestId, int fd, long buf, int len, long offset, int bufIndex);
    private native static int prepWriteFixed(long ring, long requestId, int fd, long buf, int len, long offset, int bufIndex);
    private native static int prepRead(long ring, long requestId, int fd, long buf, int len, long offset);
    private native static int prepWrite(long ring, long requestId, int fd, long buf, int len, long offset);
    // NOTE: dfd excluded and set to default
    // NOTE: path is zero-terminated
    private native static int prepOpen(long ring, long requestId, byte[] path, int flags, long mode);
    private native static int prepClose(long ring, long requestId, int fd);

    //TODO: add API's for socket opening/configuring

    /*
//sendfile/copy file
-- static inline void io_uring_prep_splice(struct io_uring_sqe *sqe, int fd_in, uint64_t off_in, int fd_out, uint64_t off_out, unsigned int nbytes, unsigned int splice_flags)

//scatter/gatter r/w
-- static inline void io_uring_prep_readv(struct io_uring_sqe *sqe, int fd, const struct iovec *iovecs, unsigned nr_vecs, off_t offset)
-- static inline void io_uring_prep_writev(struct io_uring_sqe *sqe, int fd, const struct iovec *iovecs, unsigned nr_vecs, off_t offset)

// r/w using fixed buffers
-- static inline void io_uring_prep_read_fixed(struct io_uring_sqe *sqe, int fd, void *buf, unsigned nbytes, off_t offset, int buf_index)
-- static inline void io_uring_prep_write_fixed(struct io_uring_sqe *sqe, int fd, const void *buf, unsigned nbytes, off_t offset, int buf_index)

// file descriptor polling for readiness
static inline void io_uring_prep_poll_add(struct io_uring_sqe *sqe, int fd, short poll_mask)
static inline void io_uring_prep_poll_remove(struct io_uring_sqe *sqe, void *user_data)

// update file at disk
-- static inline void io_uring_prep_fsync(struct io_uring_sqe *sqe, int fd, unsigned fsync_flags)

// NOP
-- static inline void io_uring_prep_nop(struct io_uring_sqe *sqe)

// Kernel-scheduled timeout
-- static inline void io_uring_prep_timeout(struct io_uring_sqe *sqe, struct __kernel_timespec *ts, unsigned count, unsigned flags)
-- static inline void io_uring_prep_timeout_remove(struct io_uring_sqe *sqe, __u64 user_data, unsigned flags)

// Network
-- static inline void io_uring_prep_connect(struct io_uring_sqe *sqe, int fd, struct sockaddr *addr, socklen_t addrlen)
-- static inline void io_uring_prep_accept(struct io_uring_sqe *sqe, int fd, struct sockaddr *addr, socklen_t *addrlen, int flags)
-- static inline void io_uring_prep_send(struct io_uring_sqe *sqe, int sockfd, const void *buf, size_t len, int flags)
-- static inline void io_uring_prep_recv(struct io_uring_sqe *sqe, int sockfd, void *buf, size_t len, int flags)
static inline void io_uring_prep_recvmsg(struct io_uring_sqe *sqe, int fd, struct msghdr *msg, unsigned flags)
static inline void io_uring_prep_sendmsg(struct io_uring_sqe *sqe, int fd, const struct msghdr *msg, unsigned flags)

// cancel request
-- static inline void io_uring_prep_cancel(struct io_uring_sqe *sqe, void *user_data, int flags)

// attach timeout to previous command (it should have IOSQE_IO_LINK flag set)
-- static inline void io_uring_prep_link_timeout(struct io_uring_sqe *sqe, struct __kernel_timespec *ts, unsigned flags)

// File I/O
-- static inline void io_uring_prep_openat(struct io_uring_sqe *sqe, int dfd, const char *path, int flags, mode_t mode)
static inline void io_uring_prep_openat2(struct io_uring_sqe *sqe, int dfd, const char *path, struct open_how *how)
-- static inline void io_uring_prep_read(struct io_uring_sqe *sqe, int fd, void *buf, unsigned nbytes, off_t offset)
-- static inline void io_uring_prep_write(struct io_uring_sqe *sqe, int fd, const void *buf, unsigned nbytes, off_t offset)
-- static inline void io_uring_prep_close(struct io_uring_sqe *sqe, int fd)

static inline void io_uring_prep_files_update(struct io_uring_sqe *sqe, int *fds, unsigned nr_fds, int offset)
static inline void io_uring_prep_fallocate(struct io_uring_sqe *sqe, int fd, int mode, off_t offset, off_t len)

static inline void io_uring_prep_statx(struct io_uring_sqe *sqe, int dfd, const char *path, int flags, unsigned mask, struct statx *statxbuf)
static inline void io_uring_prep_fadvise(struct io_uring_sqe *sqe, int fd, off_t offset, off_t len, int advice)
static inline void io_uring_prep_madvise(struct io_uring_sqe *sqe, void *addr, off_t length, int advice)
static inline void io_uring_prep_epoll_ctl(struct io_uring_sqe *sqe, int epfd, int fd, int op, struct epoll_event *ev)

// Advanced automated buffer management
static inline void io_uring_prep_provide_buffers(struct io_uring_sqe *sqe, void *addr, int len, int nr, int bgid, int bid)
static inline void io_uring_prep_remove_buffers(struct io_uring_sqe *sqe, int nr, int bgid)
 */
    static {
        //TODO: check handling of errors
        try {
            LibraryLoader.fromJar("/liburingnative.so");
            initApi(); //TODO: check if API's are available
        } catch (final Exception e) {
            System.err.println("Error while loading JNI library for NativeIO class: " + e);
        }
    }

    private static long calculateNumEntries(final int size) {
        if(size <= 0) {
            return DEFAULT_QUEUE_SIZE;
        }
        //Round up to nearest power of two
        return 1 << (32 - Integer.numberOfLeadingZeros(size - 1));
    }

    private NativeIO(final int queueSize) {
        final var count = calculateNumEntries(queueSize);
        // completion queue is 2x size of submission queue and each entry uses 2 longs to deliver completion
        completionData = new long[(int) (count * 4)];
        ring = initRing(count, DEFAULT_FLAGS);

        if (ring <= 0) {
            throw new RuntimeException("Unable to initialize uring");
        }
    }

    public static NativeIO create(final int queueSize) {
        return new NativeIO(queueSize);
    }

    public void close() {
        closeRing(ring);
    }
//
//    public void processCompletions(final ObjectHeap<CompletionHandler> heap) {
//        final int count = peekCQ(completionData);
//
//        if (count < 0) {
//            //TODO: add logging
//            return;
//        }
//
//        for (int i = 0, ndx = 0; i < count; i++) {
//            // Unpack data:
//            // first long is request ID
//            // second long holds two pieces: upper half is and result code (signed) and lower half is flags (unsigned)
//            final long userData = completionData[ndx++];
//            final long result = completionData[ndx++];
//
//            final CompletionHandler handler = heap.release((int) userData);
//            if (handler == null) {
//                //TODO: add logging: completion is submitted for non-existent request
//                continue;
//            }
//            handler.onCompletion((int) (result >> 32), (int) (result & 0x0FFFFFFFFL));
//        }
//        advanceCQ(count);
//    }
//
//    public void submitNop(final int key) {
//        //TODO: finish it
//    }
}

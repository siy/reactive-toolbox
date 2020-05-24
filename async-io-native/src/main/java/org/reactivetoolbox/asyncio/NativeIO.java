package org.reactivetoolbox.asyncio;

import org.reactivetoolbox.asyncio.util.LibraryLoader;
import org.reactivetoolbox.asyncio.util.ObjectHeap;

import java.util.function.BiConsumer;

/**
 * Low level API for IO_URING and other relevant functionality (opening/configuring socket).
 * <p>
 * NOTE: Do your best to keep code and comments consistent and up to date.
 */
//TODO: add probing of functionality?
class NativeIO {
    private static final int DEFAULT_QUEUE_SIZE = 4096;
    private static final int DEFAULT_QUEUE_FLAGS = 0;

    public static NativeIO create(final int queueSize) {
        return new NativeIO(queueSize);
    }

    public void close() {
        closeRing(ring);
    }

    //------------------------------------------------------------------------------------------------------
    // Low level internals
    //------------------------------------------------------------------------------------------------------
    private final long ring;
    private final long[] completionData;
    private final ObjectHeap<BiConsumer<Integer, Integer>> requests = ObjectHeap.objectHeap(DEFAULT_QUEUE_SIZE * 2);
    private final LocalSubmitter submitter;

    /**
     * Creates new instance of io_uring.
     * Note that queue size must be rounded to power of two.
     * Actual submission queue size is equal to requested entries number.
     * Actual completion queue size is twice as big as submission queue size.
     *
     * @param entries
     *         Number of entries in the queue
     * @param flags
     *         Flags to use while creating new instance
     *
     * @return reference to allocated internal structure.
     */
    private native static long initRing(long entries, long flags);

    /**
     * Close ring and release all resources.
     *
     * @param ring
     *         reference to internal structure
     */
    private native static void closeRing(long ring);

    /**
     * Peek entries from completion queue.
     * Retrieved entries are returned inside passed array. Each entry
     * occupies 2 elements in the array: first element contains user data,
     * second element holds operation result code (upper 32 bits) and
     * completion flags (lower 32 bits).
     * Note that function does not check passed array size and assumes
     * that is contains at least twice as much elements as completion queue
     * (which is twice as long as submission queue).
     *
     * @param ring
     *         reference to internal structure
     * @param data
     *         array to store completion queue entries
     *
     * @return number of retrieved completion events
     */
    private native static int peekCQ(long ring, long[] data);

    /**
     * Notify kernel that we've retrieved completions.
     *
     * @param ring
     *         reference to internal structure
     * @param count
     *         number of retrieved completions
     */
    private native static void advanceCQ(long ring, int count);

    /**
     * Check how many entries we can put into submission queue.
     *
     * @param ring
     *         reference to internal structure
     *
     * @return number of available entries.
     */
    private native static long spaceLeft(long ring);

    /**
     * Submit prepared entries into kernel and wait for specified number of completions.
     * If number of completion to wait is 0 then call finished without waiting for completions.
     *
     * @param ring
     *         reference to internal structure
     * @param waitNum
     *         number of completions to wait (0 for no wait)
     *
     * @return number of submitted entries.
     */
    private native static long submitAndWait(long ring, long waitNum);

    /**
     * Allocate and prepare a submission queue entry.
     *
     * @param ring
     *         reference to internal structure
     * @param operation
     *         operation code (see {@link Operation} for more details)
     * @param fd
     *         File descriptor
     * @param address
     *         Buffer/data address
     * @param len
     *         Buffer/data len
     * @param offset
     *         Buffer offset
     * @param opFlags
     *         Operation flags
     * @param sqFlags
     *         Submission entry flags (See {@link SQFlags} for more details)
     * @param requestId
     *         Request ID (user_data)
     *
     * @return -ENOSPC (-28) if no entries available in the submission queue, 0 if operation was successful
     */
    private native static int prepareIO(long ring, int operation,
                                        int fd, long address,
                                        long len, long offset,
                                        int opFlags, int sqFlags,
                                        long requestId);

    /**
     * Create new socket.
     *
     * @param flags
     *         socket open flags
     *
     * @return socket file descriptor (> 0) or error ( < 0)
     */
    private native static int createSocket(int flags);

    /**
     * Bind socket to specified port.
     * NOTE: current implementation always binds socket to INADDR_ANY
     *
     * @param socket
     *         socket file descriptor (returned by {@link #createSocket(int)}
     * @param port
     *         port to bind
     * @param addr
     *         address to bind, if {@code null} then INADDR_ANY is used, otherwise it should be a byte
     *         array from text representation of IP address to bind.
     *
     * @return 0 if success, != 0 - error code
     */
    private native static int bind(int socket, int port, byte[] addr);

    /**
     * Start listening for the incoming connections.
     *
     * @param socket
     *         socket file descriptor (returned by {@link #createSocket(int)}
     * @param backlog
     *         length of the backlog queue
     *
     * @return 0 if success, != 0 - error code
     */
    private native static int listen(int socket, int backlog);

    static {
        try {
            LibraryLoader.fromJar("/liburingnative.so");
        } catch (final Exception e) {
            System.err.println("Error while loading JNI library for NativeIO class: " + e);
        }
    }

    private static long calculateNumEntries(final int size) {
        if (size <= 0) {
            return DEFAULT_QUEUE_SIZE;
        }
        //Round up to nearest power of two
        return 1 << (32 - Integer.numberOfLeadingZeros(size - 1));
    }

    private NativeIO(final int queueSize) {
        final var count = calculateNumEntries(queueSize);
        // completion queue is 2x size of submission queue and each entry uses 2 longs to deliver completion
        completionData = new long[(int) (count * 4)];
        ring = initRing(count, DEFAULT_QUEUE_FLAGS);
        submitter = new LocalSubmitter();

        if (ring <= 0) {
            throw new RuntimeException("Unable to initialize io_uring interface");
        }
    }

    /**
     * WARNING: Always keep in sync with io_uring.h
     * <p>
     * Ordinals (by the means of .ordinal() call) of constants below must match
     * actual Linux API codes (as defined in io_uring.h).
     */
    private enum Operation {
        IORING_OP_NOP,
        IORING_OP_READV,
        IORING_OP_WRITEV,
        IORING_OP_FSYNC,
        IORING_OP_READ_FIXED,
        IORING_OP_WRITE_FIXED,
        IORING_OP_POLL_ADD,
        IORING_OP_POLL_REMOVE,
        IORING_OP_SYNC_FILE_RANGE,
        IORING_OP_SENDMSG,
        IORING_OP_RECVMSG,
        IORING_OP_TIMEOUT,
        IORING_OP_TIMEOUT_REMOVE,
        IORING_OP_ACCEPT,
        IORING_OP_ASYNC_CANCEL,
        IORING_OP_LINK_TIMEOUT,
        IORING_OP_CONNECT,
        IORING_OP_FALLOCATE,
        IORING_OP_OPENAT,
        IORING_OP_CLOSE,
        IORING_OP_FILES_UPDATE,
        IORING_OP_STATX,
        IORING_OP_READ,
        IORING_OP_WRITE,
        IORING_OP_FADVISE,
        IORING_OP_MADVISE,
        IORING_OP_SEND,
        IORING_OP_RECV,
        IORING_OP_OPENAT2,
        IORING_OP_EPOLL_CTL,
        IORING_OP_SPLICE,
        IORING_OP_PROVIDE_BUFFERS,
        IORING_OP_REMOVE_BUFFERS,
        IORING_OP_LAST
    }

    private enum SQFlags implements Bitmask {
        IOSQE_FIXED_FILE(1 << 0),      /* issue after inflight IO */
        IOSQE_IO_DRAIN(1 << 1),
        IOSQE_IO_LINK(1 << 2),         /* links next sqe */
        IOSQE_IO_HARDLINK(1 << 3),     /* like LINK, but stronger */
        IOSQE_ASYNC(1 << 4),           /* always go async */
        IOSQE_BUFFER_SELECT(1 << 5);   /* select buffer from sqe->buf_group */

        private final int mask;

        SQFlags(final int mask) {
            this.mask = mask;
        }

        @Override
        public int mask() {
            return mask;
        }
    }

    private enum RingFlags implements Bitmask {
        IORING_SETUP_IOPOLL(1 << 0),    /* io_context is polled */
        IORING_SETUP_SQPOLL(1 << 1),    /* SQ poll thread */
        IORING_SETUP_SQ_AFF(1 << 2),    /* sq_thread_cpu is valid */
        IORING_SETUP_CQSIZE(1 << 3),    /* app defines CQ size */
        IORING_SETUP_CLAMP(1 << 4),    /* clamp SQ/CQ ring sizes */
        IORING_SETUP_ATTACH_WQ(1 << 5);    /* attach to existing wq */

        private final int mask;

        RingFlags(final int mask) {
            this.mask = mask;
        }

        @Override
        public int mask() {
            return mask;
        }
    }

    private class LocalSubmitter /* implements Submitter */ {

    }
}

package org.reactivetoolbox.asyncio;

import org.reactivetoolbox.asyncio.util.LibraryLoader;

class NativeIO {
    private static final int DEFAULT_QUEUE_SIZE = 4096;
    private static final int DEFAULT_QUEUE_FLAGS = 0;
    private static final int SOCKET_OPEN_FLAG_STREAM = 0x00000001;
    private static final int SOCKET_OPEN_FLAG_NONBLOCK = 0x00000002;
    private static final int SOCKET_OPEN_FLAG_REUSEADDR = 0x00000004;

    private final long ring;
    private final long[] completionData;

    // Startup/shutdown
    private native static int initApi();                            //++
    private native static long initRing(long entries, long flags);  //++
    private native static void closeRing(long ring);                //++

    // Completion Queue
    private native static int peekCQ(long ring, long[] data);       //++
    private native static void advanceCQ(long ring, int count);     //++

    // Submission Queue
    private native static long spaceLeft(long ring);
    private native static long submitAndWait(long ring, long waitNum);

    private native static int prepareIO(long ring, long operation, int fd, long address, long len, long offset, int flags, long requestId);

    private native static int createSocket(int flags);
    private native static int bind(int socket, int port);
    private native static int listen(int socket, int backlog);

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
        ring = initRing(count, DEFAULT_QUEUE_FLAGS);

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

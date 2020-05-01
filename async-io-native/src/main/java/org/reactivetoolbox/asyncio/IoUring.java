package org.reactivetoolbox.asyncio;

public class IoUring {
    private static final int DEFAULT_QUEUE_SIZE = 4096;
    private static final int DEFAULT_FLAGS = 0;

    private long nativePtr;

    private native static void initIds(final Class<CompletionResult> clz);

    private native int init(long entries, long flags);
    private native void exit();

    static {
        try {
            NativeUtils.loadLibraryFromJar("/liburingnative.so");
        } catch (final Exception e) {
            System.err.println("Error while loading JNI library for IoUring class: " + e);
        }

        initIds(CompletionResult.class);
    }

    private static long calculateNumEntries(final int size) {
        //Round up to nearest power of two
        return 1 << (32 - Integer.numberOfLeadingZeros(size - 1));
    }

    public IoUring(final int queueSize) {
        init(calculateNumEntries((queueSize <= 0)
                                 ? DEFAULT_QUEUE_SIZE
                                 : queueSize),
             DEFAULT_FLAGS);
    }

    public void close() {
        exit();
    }
}

package org.reactivetoolbox.io.uring;

class Uring {
    //Start/Stop
    public static native int init(int numEntries, long baseAddress, long flags);
    public static native void close(long baseAddress);

    //Completion
    public static native int peekCQ(long baseAddress, long completionsAddress, long count);
    public static native void advanceCQ(long baseAddress, long count);
    public static native int readyCQ(long baseAddress);

    //Submissions
    public static native long spaceLeft(long baseAddress);
    public static native long nextSQEntry(long baseAddress);
    public static native long submitAndWait(long baseAddress, int waitNr);
}

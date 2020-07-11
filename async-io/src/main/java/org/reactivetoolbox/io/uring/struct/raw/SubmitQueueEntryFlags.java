package org.reactivetoolbox.io.uring.struct.raw;

//TODO: move to enum
public class SubmitQueueEntryFlags {
    public static final byte IOSQE_FIXED_FILE    = 0x001; /* use fixed fileset */
    public static final byte IOSQE_IO_DRAIN      = 0x002; /* issue after inflight IO */
    public static final byte IOSQE_IO_LINK       = 0x004; /* links next sqe */
    public static final byte IOSQE_IO_HARDLINK   = 0x008; /* like LINK, but stronger */
    public static final byte IOSQE_ASYNC         = 0x010; /* always go async */
    public static final byte IOSQE_BUFFER_SELECT = 0x020; /* select buffer from sqe->buf_group */
}

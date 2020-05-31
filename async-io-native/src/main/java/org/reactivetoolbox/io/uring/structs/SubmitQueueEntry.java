package org.reactivetoolbox.io.uring.structs;

import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.accept_flags;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.addr;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.addr2;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.buf_group;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.buf_index;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.cancel_flags;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.fadvise_advice;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.fd;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.flags;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.fsync_flags;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.ioprio;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.len;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.msg_flags;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.off;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.opcode;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.open_flags;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.personality;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.poll_events;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.rw_flags;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.splice_fd_in;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.splice_flags;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.splice_off_in;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.statx_flags;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.sync_range_flags;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.timeout_flags;
import static org.reactivetoolbox.io.uring.structs.SubmitQueueEntryOffsets.user_data;

public class SubmitQueueEntry extends AbstractExternalRawStructure<SubmitQueueEntry> {
    public static final int IORING_FSYNC_DATASYNC = 1;      /* sqe->fsync_flags */
    public static final int IORING_TIMEOUT_ABS = 1;         /* sqe->timeout_flags */
    public static final int SPLICE_F_FD_IN_FIXED = 1 << 31; /* sqe->splice_flags, extends splice(2) flags */

    //  SubmissionFlags
    public static final int IOSQE_FIXED_FILE = 1;       /* issue after inflight IO */
    public static final int IOSQE_IO_DRAIN = 2;
    public static final int IOSQE_IO_LINK = 4;          /* links next sqe */
    public static final int IOSQE_IO_HARDLINK = 8;      /* like LINK, but stronger */
    public static final int IOSQE_ASYNC = 16;           /* always go async */
    public static final int IOSQE_BUFFER_SELECT = 32;   /* select buffer from sqe->buf_group */

    private SubmitQueueEntry(final long address) {
        super(address, SubmitQueueEntryOffsets.SIZE);
    }

    public static SubmitQueueEntry at(final long address) {
        return new SubmitQueueEntry(address);
    }

    public SubmitQueueEntry opcode(final byte data) {
        return putByte(opcode, data);
    }

    public SubmitQueueEntry flags(final byte data) {
        return putByte(flags, data);
    }

    public SubmitQueueEntry ioprio(final short data) {
        return putShort(ioprio, data);
    }

    public SubmitQueueEntry pollEvents(final short data) {
        return putShort(poll_events, data);
    }

    public SubmitQueueEntry bufIndex(final short data) {
        return putShort(buf_index, data);
    }

    public SubmitQueueEntry bufGroup(final short data) {
        return putShort(buf_group, data);
    }

    public SubmitQueueEntry personality(final short data) {
        return putShort(personality, data);
    }

    public SubmitQueueEntry fd(final int data) {
        return putInt(fd, data);
    }

    public SubmitQueueEntry len(final int data) {
        return putInt(len, data);
    }

    public SubmitQueueEntry rwFlags(final int data) {
        return putInt(rw_flags, data);
    }

    public SubmitQueueEntry fsyncFlags(final int data) {
        return putInt(fsync_flags, data);
    }

    public SubmitQueueEntry syncRangeFlags(final int data) {
        return putInt(sync_range_flags, data);
    }

    public SubmitQueueEntry msgFlags(final int data) {
        return putInt(msg_flags, data);
    }

    public SubmitQueueEntry timeoutFlags(final int data) {
        return putInt(timeout_flags, data);
    }

    public SubmitQueueEntry acceptFlags(final int data) {
        return putInt(accept_flags, data);
    }

    public SubmitQueueEntry cancelFlags(final int data) {
        return putInt(cancel_flags, data);
    }

    public SubmitQueueEntry openFlags(final int data) {
        return putInt(open_flags, data);
    }

    public SubmitQueueEntry statxFlags(final int data) {
        return putInt(statx_flags, data);
    }

    public SubmitQueueEntry fadviseAdvice(final int data) {
        return putInt(fadvise_advice, data);
    }

    public SubmitQueueEntry spliceFlags(final int data) {
        return putInt(splice_flags, data);
    }

    public SubmitQueueEntry spliceFdIn(final int data) {
        return putInt(splice_fd_in, data);
    }

    public SubmitQueueEntry off(final long data) {
        return putLong(off, data);
    }

    public SubmitQueueEntry addr2(final long data) {
        return putLong(addr2, data);
    }

    public SubmitQueueEntry addr(final long data) {
        return putLong(addr, data);
    }

    public SubmitQueueEntry spliceOffIn(final long data) {
        return putLong(splice_off_in, data);
    }

    public SubmitQueueEntry userData(final long data) {
        return putLong(user_data, data);
    }
}
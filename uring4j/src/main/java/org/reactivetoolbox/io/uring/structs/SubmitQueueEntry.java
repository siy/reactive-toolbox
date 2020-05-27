package org.reactivetoolbox.io.uring.structs;

import static org.reactivetoolbox.io.raw.UnsafeHolder.unsafe;
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

public class SubmitQueueEntry {
    private final long address;

    private SubmitQueueEntry(final long address) {
        this.address = address;
    }

    public static SubmitQueueEntry at(final long address) {
        return new SubmitQueueEntry(address);
    }

    public SubmitQueueEntry opcode(final byte data) {
        unsafe().putByte(address + opcode.offset(), data);
        return this;
    }

    public SubmitQueueEntry flags(final byte data) {
        unsafe().putByte(address + flags.offset(), data);
        return this;
    }

    public SubmitQueueEntry ioprio(final short data) {
        unsafe().putShort(address + ioprio.offset(), data);
        return this;
    }

    public SubmitQueueEntry pollEvents(final short data) {
        unsafe().putShort(address + poll_events.offset(), data);
        return this;
    }

    public SubmitQueueEntry bufIndex(final short data) {
        unsafe().putShort(address + buf_index.offset(), data);
        return this;
    }

    public SubmitQueueEntry bufGroup(final short data) {
        unsafe().putShort(address + buf_group.offset(), data);
        return this;
    }

    public SubmitQueueEntry personality(final short data) {
        unsafe().putShort(address + personality.offset(), data);
        return this;
    }

    public SubmitQueueEntry fd(final int data) {
        unsafe().putInt(address + fd.offset(), data);
        return this;
    }

    public SubmitQueueEntry len(final int data) {
        unsafe().putInt(address + len.offset(), data);
        return this;
    }

    public SubmitQueueEntry rwFlags(final int data) {
        unsafe().putInt(address + rw_flags.offset(), data);
        return this;
    }

    public SubmitQueueEntry fsyncFlags(final int data) {
        unsafe().putInt(address + fsync_flags.offset(), data);
        return this;
    }

    public SubmitQueueEntry syncRangeFlags(final int data) {
        unsafe().putInt(address + sync_range_flags.offset(), data);
        return this;
    }

    public SubmitQueueEntry msgFlags(final int data) {
        unsafe().putInt(address + msg_flags.offset(), data);
        return this;
    }

    public SubmitQueueEntry timeoutFlags(final int data) {
        unsafe().putInt(address + timeout_flags.offset(), data);
        return this;
    }

    public SubmitQueueEntry acceptFlags(final int data) {
        unsafe().putInt(address + accept_flags.offset(), data);
        return this;
    }

    public SubmitQueueEntry cancelFlags(final int data) {
        unsafe().putInt(address + cancel_flags.offset(), data);
        return this;
    }

    public SubmitQueueEntry openFlags(final int data) {
        unsafe().putInt(address + open_flags.offset(), data);
        return this;
    }

    public SubmitQueueEntry statxFlags(final int data) {
        unsafe().putInt(address + statx_flags.offset(), data);
        return this;
    }

    public SubmitQueueEntry fadviseAdvice(final int data) {
        unsafe().putInt(address + fadvise_advice.offset(), data);
        return this;
    }

    public SubmitQueueEntry spliceFlags(final int data) {
        unsafe().putInt(address + splice_flags.offset(), data);
        return this;
    }

    public SubmitQueueEntry spliceFdIn(final int data) {
        unsafe().putInt(address + splice_fd_in.offset(), data);
        return this;
    }

    public SubmitQueueEntry off(final long data) {
        unsafe().putLong(address + off.offset(), data);
        return this;
    }

    public SubmitQueueEntry addr2(final long data) {
        unsafe().putLong(address + addr2.offset(), data);
        return this;
    }

    public SubmitQueueEntry addr(final long data) {
        unsafe().putLong(address + addr.offset(), data);
        return this;
    }

    public SubmitQueueEntry spliceOffIn(final long data) {
        unsafe().putLong(address + splice_off_in.offset(), data);
        return this;
    }

    public SubmitQueueEntry userData(final long data) {
        unsafe().putLong(address + user_data.offset(), data);
        return this;
    }
}
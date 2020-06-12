package org.reactivetoolbox.io.uring.struct.shape;

import org.reactivetoolbox.io.raw.RawProperty;

import static org.reactivetoolbox.io.raw.RawProperty.raw;

public interface SubmitQueueEntryOffsets {
    int SIZE = 64;
    RawProperty opcode = raw(0, 1);
    RawProperty flags = raw(1, 1);
    RawProperty ioprio = raw(2, 2);
    RawProperty fd = raw(4, 4);
    RawProperty off = raw(8, 8);
    RawProperty addr2 = raw(8, 8);
    RawProperty addr = raw(16, 8);
    RawProperty splice_off_in = raw(16, 8);
    RawProperty len = raw(24, 4);
    RawProperty rw_flags = raw(28, 4);
    RawProperty fsync_flags = raw(28, 4);
    RawProperty poll_events = raw(28, 4);   //TODO: watch until it will be included into liburing
    RawProperty sync_range_flags = raw(28, 4);
    RawProperty msg_flags = raw(28, 4);
    RawProperty timeout_flags = raw(28, 4);
    RawProperty accept_flags = raw(28, 4);
    RawProperty cancel_flags = raw(28, 4);
    RawProperty open_flags = raw(28, 4);
    RawProperty statx_flags = raw(28, 4);
    RawProperty fadvise_advice = raw(28, 4);
    RawProperty splice_flags = raw(28, 4);
    RawProperty user_data = raw(32, 8);
    RawProperty buf_index = raw(40, 2);
    RawProperty buf_group = raw(40, 2);
    RawProperty personality = raw(42, 2);
    RawProperty splice_fd_in = raw(44, 4);
}
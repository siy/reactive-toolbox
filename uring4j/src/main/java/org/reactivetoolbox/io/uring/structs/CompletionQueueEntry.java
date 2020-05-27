package org.reactivetoolbox.io.uring.structs;

import static org.reactivetoolbox.io.raw.UnsafeHolder.unsafe;
import static org.reactivetoolbox.io.uring.structs.CompletionQueueEntryOffsets.flags;
import static org.reactivetoolbox.io.uring.structs.CompletionQueueEntryOffsets.res;
import static org.reactivetoolbox.io.uring.structs.CompletionQueueEntryOffsets.user_data;

public class CompletionQueueEntry {
    private final long address;

    private CompletionQueueEntry(final long address) {
        this.address = address;
    }

    public static CompletionQueueEntry at(final long address) {
        return new CompletionQueueEntry(address);
    }

    public long userData() {
        return unsafe().getLong(address + user_data.offset());
    }

    public int result() {
        return unsafe().getInt(address + res.offset());
    }

    public int flags() {
        return unsafe().getInt(address + flags.offset());
    }
}

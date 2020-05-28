package org.reactivetoolbox.io.uring.structs;

import org.reactivetoolbox.io.raw.RawMemory;

import static org.reactivetoolbox.io.uring.structs.CompletionQueueEntryOffsets.flags;
import static org.reactivetoolbox.io.uring.structs.CompletionQueueEntryOffsets.res;
import static org.reactivetoolbox.io.uring.structs.CompletionQueueEntryOffsets.user_data;

//TODO: make common API for such structs?
//TODO: make it movable?
public class CompletionQueueEntry {
    private final long address;

    private CompletionQueueEntry(final long address) {
        this.address = address;
    }

    public static CompletionQueueEntry at(final long address) {
        return new CompletionQueueEntry(address);
    }

    public long userData() {
        return RawMemory.getLong(address + user_data.offset());
    }

    public int result() {
        return RawMemory.getInt(address + res.offset());
    }

    public int flags() {
        return RawMemory.getInt(address + flags.offset());
    }
}

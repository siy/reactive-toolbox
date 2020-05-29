package org.reactivetoolbox.io.uring.structs;

import static org.reactivetoolbox.io.uring.structs.CompletionQueueEntryOffsets.flags;
import static org.reactivetoolbox.io.uring.structs.CompletionQueueEntryOffsets.res;
import static org.reactivetoolbox.io.uring.structs.CompletionQueueEntryOffsets.user_data;

public class CompletionQueueEntry extends AbstractRawStructure<CompletionQueueEntry> {
    private CompletionQueueEntry(final long address) {
        super(address, CompletionQueueEntryOffsets.SIZE);
    }

    public static CompletionQueueEntry at(final long address) {
        return new CompletionQueueEntry(address);
    }

    public long userData() {
        return getLong(user_data);
    }

    public int result() {
        return getInt(res);
    }

    public int flags() {
        return getInt(flags);
    }
}

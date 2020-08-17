package org.reactivetoolbox.io.uring.struct.raw;

import org.reactivetoolbox.io.uring.struct.AbstractExternalRawStructure;
import org.reactivetoolbox.io.uring.struct.shape.CompletionQueueEntryOffsets;

import java.util.StringJoiner;

import static org.reactivetoolbox.io.uring.struct.shape.CompletionQueueEntryOffsets.flags;
import static org.reactivetoolbox.io.uring.struct.shape.CompletionQueueEntryOffsets.res;
import static org.reactivetoolbox.io.uring.struct.shape.CompletionQueueEntryOffsets.user_data;

public class CompletionQueueEntry extends AbstractExternalRawStructure<CompletionQueueEntry> {
    private CompletionQueueEntry(final long address) {
        super(address, CompletionQueueEntryOffsets.SIZE);
    }

    public static CompletionQueueEntry at(final long address) {
        return new CompletionQueueEntry(address);
    }

    public long userData() {
        return getLong(user_data);
    }

    public int res() {
        return getInt(res);
    }

    public int flags() {
        return getInt(flags);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "CompletionQueueEntry(", ")")
                .add("res: " + res())
                .add("flags: " + flags())
                .add("data: " + userData())
                .toString();
    }
}

package org.reactivetoolbox.io.uring.struct.raw;

import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.uring.struct.AbstractExternalRawStructure;
import org.reactivetoolbox.io.uring.struct.shape.CompletionQueueEntryOffsets;

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

    public <T> Result<T> result(final FN1<T, Integer> constructor) {
        return NativeError.result(res(), constructor);
    }
}

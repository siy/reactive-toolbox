package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.scheduler.Timeout;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.util.function.Consumer;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_CLOSE;
import static org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntryFlags.IOSQE_IO_LINK;

public class CloseExchangeEntry extends AbstractExchangeEntry<CloseExchangeEntry, Unit> {
    private int descriptor;
    private byte flags;

    protected CloseExchangeEntry(final PlainObjectPool<CloseExchangeEntry> pool) {
        super(IORING_OP_CLOSE, pool);
    }

    @Override
    protected void doAccept(final int res, final int flags) {
        completion.accept(res == 0 ? UNIT_RESULT : NativeError.result(res));
    }

    public CloseExchangeEntry prepare(final Consumer<Result<Unit>> completion,
                                      final FileDescriptor fd,
                                      final Option<Timeout> timeout) {
        descriptor = fd.descriptor();
        flags = timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK;
        return super.prepare(completion);
    }

    @Override
    public SubmitQueueEntry apply(final SubmitQueueEntry entry) {
        return super.apply(entry)
                    .flags(flags)
                    .fd(descriptor);
    }
}

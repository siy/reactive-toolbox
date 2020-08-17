package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.Bitmask;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.file.SpliceDescriptor;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.util.function.Consumer;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_SPLICE;

public class SpliceExchangeEntry extends AbstractExchangeEntry<SpliceExchangeEntry, SizeT> {
    private SpliceDescriptor descriptor;
    private byte flags;

    protected SpliceExchangeEntry(final PlainObjectPool<SpliceExchangeEntry> pool) {
        super(IORING_OP_SPLICE, pool);
    }

    @Override
    protected void doAccept(final int res, final int flags) {
        completion.accept(byteCountToResult(res));
    }

    @Override
    public SubmitQueueEntry apply(final SubmitQueueEntry entry) {
        return super.apply(entry)
                    .flags(flags)
                    .fd(descriptor.toDescriptor().descriptor())
                    .len((int) descriptor.bytesToCopy().value())
                    .off(descriptor.toOffset().value())
                    .spliceFdIn(descriptor.fromDescriptor().descriptor())
                    .spliceOffIn(descriptor.fromOffset().value())
                    .spliceFlags(Bitmask.combine(descriptor.flags()));
    }

    public SpliceExchangeEntry prepare(final Consumer<Result<SizeT>> completion,
                                       final SpliceDescriptor descriptor,
                                       final byte flags) {
        this.flags = flags;
        this.descriptor = descriptor;
        return super.prepare(completion);
    }

    private Result<SizeT> byteCountToResult(final int res) {
        return res > 0
               ? sizeResult(res)
               : NativeError.result(res);
    }
}

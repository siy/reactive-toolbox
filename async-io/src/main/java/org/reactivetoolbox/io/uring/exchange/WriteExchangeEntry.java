package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.util.function.BiConsumer;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_WRITE;

public class WriteExchangeEntry extends AbstractExchangeEntry<WriteExchangeEntry, SizeT> {
    private int descriptor;
    private byte flags;
    private OffHeapBuffer buffer;
    private long offset;

    protected WriteExchangeEntry(final PlainObjectPool<WriteExchangeEntry> pool) {
        super(IORING_OP_WRITE, pool);
    }

    @Override
    protected void doAccept(final int res, final int flags, final Submitter submitter) {
        completion.accept(byteCountToResult(res), submitter);
    }

    @Override
    public SubmitQueueEntry apply(final SubmitQueueEntry entry) {
        return super.apply(entry)
                    .fd(descriptor)
                    .flags(flags)
                    .addr(buffer.address())
                    .len(buffer.used())
                    .off(offset);
    }

    public WriteExchangeEntry prepare(final BiConsumer<Result<SizeT>, Submitter> completion,
                                      final int descriptor,
                                      final OffHeapBuffer buffer,
                                      final long offset,
                                      final byte flags) {
        this.descriptor = descriptor;
        this.flags = flags;
        this.buffer = buffer;
        this.offset = offset;
        return super.prepare(completion);
    }

    private Result<SizeT> byteCountToResult(final int res) {
        return res > 0
               ? sizeResult(res)
               : NativeError.result(res);
    }
}

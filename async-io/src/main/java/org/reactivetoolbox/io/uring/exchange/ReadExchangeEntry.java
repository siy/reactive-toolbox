package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.common.OffsetT;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;
import org.reactivetoolbox.io.scheduler.Timeout;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.util.function.Consumer;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_READ;
import static org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntryFlags.IOSQE_IO_LINK;

public class ReadExchangeEntry extends AbstractExchangeEntry<ReadExchangeEntry, SizeT> {
    private static final Result<SizeT> EOF_RESULT = Result.fail(NativeError.ENODATA.asFailure());

    private int descriptor;
    private byte flags;
    private OffHeapBuffer buffer;
    private OffsetT offset;

    protected ReadExchangeEntry(final PlainObjectPool<ReadExchangeEntry> pool) {
        super(IORING_OP_READ, pool);
    }

    @Override
    protected void doAccept(final int res, final int flags) {
        if (res > 0) {
            buffer.used(res);
        }
        completion.accept(bytesReadToResult(res));
    }

    @Override
    public SubmitQueueEntry apply(final SubmitQueueEntry entry) {
        return super.apply(entry)
                    .fd(descriptor)
                    .flags(flags)
                    .addr(buffer.address())
                    .len(buffer.size())
                    .off(offset.value());
    }

    public ReadExchangeEntry prepare(final Consumer<Result<SizeT>> completion,
                                     final FileDescriptor fd,
                                     final OffHeapBuffer buffer,
                                     final OffsetT offset,
                                     final Option<Timeout> timeout) {
        descriptor = fd.descriptor();
        flags = timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK;
        this.buffer = buffer;
        this.offset = offset;
        return super.prepare(completion);
    }

    private Result<SizeT> bytesReadToResult(final int res) {
        return res == 0 ? EOF_RESULT
                        : res > 0 ? sizeResult(res)
                                  : NativeError.result(res);
    }
}

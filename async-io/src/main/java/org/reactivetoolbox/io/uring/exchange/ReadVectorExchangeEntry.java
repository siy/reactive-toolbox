package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.common.OffsetT;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.scheduler.Timeout;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapIoVector;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.util.function.Consumer;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_READV;
import static org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntryFlags.IOSQE_IO_LINK;

public class ReadVectorExchangeEntry extends AbstractExchangeEntry<ReadVectorExchangeEntry, SizeT> {
    private static final Result<SizeT> EOF_RESULT = Result.fail(NativeError.ENODATA.asFailure());

    private OffHeapIoVector ioVector;
    private byte flags;
    private int descriptor;
    private long offset;

    protected ReadVectorExchangeEntry(final PlainObjectPool<ReadVectorExchangeEntry> pool) {
        super(IORING_OP_READV, pool);
    }

    @Override
    protected void doAccept(final int res, final int flags) {
        completion.accept(bytesReadToResult(res));
        ioVector.dispose();
        ioVector = null;
    }

    @Override
    public SubmitQueueEntry apply(final SubmitQueueEntry entry) {
        return super.apply(entry)
                    .flags(flags)
                    .fd(descriptor)
                    .addr(ioVector.address())
                    .len(ioVector.length())
                    .off(offset);
    }

    public ReadVectorExchangeEntry prepare(final Consumer<Result<SizeT>> completion,
                                           final FileDescriptor fileDescriptor,
                                           final OffsetT offset,
                                           final Option<Timeout> timeout,
                                           final OffHeapIoVector ioVector) {
        descriptor = fileDescriptor.descriptor();
        this.offset = offset.value();
        flags = timeout.equals(Option.empty()) ? 0 : IOSQE_IO_LINK;
        this.ioVector = ioVector;
        return super.prepare(completion);
    }

    private Result<SizeT> bytesReadToResult(final int res) {
        return res == 0 ? EOF_RESULT
                        : res > 0 ? sizeResult(res)
                                  : NativeError.result(res);
    }
}

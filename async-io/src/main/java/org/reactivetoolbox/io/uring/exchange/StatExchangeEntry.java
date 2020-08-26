package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.file.stat.FileStat;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapCString;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapFileStat;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.util.function.BiConsumer;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_STATX;

public class StatExchangeEntry extends AbstractExchangeEntry<StatExchangeEntry, FileStat> {
    private final OffHeapFileStat fileStat = OffHeapFileStat.fileStat();
    private OffHeapCString rawPath;
    private int descriptor;
    private int statFlags;
    private int statMask;

    protected StatExchangeEntry(final PlainObjectPool<StatExchangeEntry> pool) {
        super(IORING_OP_STATX, pool);
    }

    @Override
    protected void doAccept(final int res, final int flags, final Submitter submitter) {
        completion.accept(res < 0
                          ? NativeError.result(res)
                          : Result.ok(fileStat.extract()),
                          submitter);
        fileStat.dispose();
        rawPath.dispose();
        rawPath = null;
    }

    @Override
    public void close() {
        fileStat.dispose();

        if (rawPath != null) {
            rawPath.dispose();
        }
    }

    @Override
    public SubmitQueueEntry apply(final SubmitQueueEntry entry) {
        return super.apply(entry)
                    .fd(descriptor)
                    .addr(rawPath.address())
                    .len(statMask)
                    .off(fileStat.address())
                    .statxFlags(statFlags);
    }

    public StatExchangeEntry prepare(final BiConsumer<Result<FileStat>, Submitter> completion,
                                     final int descriptor,
                                     final int statFlags,
                                     final int statMask,
                                     final OffHeapCString rawPath) {
        this.descriptor = descriptor;
        this.statFlags = statFlags;
        this.statMask = statMask;
        this.rawPath = rawPath;

        fileStat.clear();

        return super.prepare(completion);
    }
}

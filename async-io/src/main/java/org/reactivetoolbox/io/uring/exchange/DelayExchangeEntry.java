package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.scheduler.Timeout;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapTimeSpec;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.time.Duration;
import java.util.function.BiConsumer;

import static org.reactivetoolbox.io.scheduler.Timeout.timeout;
import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_TIMEOUT;

public class DelayExchangeEntry extends AbstractExchangeEntry<DelayExchangeEntry, Duration> {
    private final OffHeapTimeSpec timeSpec = OffHeapTimeSpec.uninitialized();
    private long startNanos;

    protected DelayExchangeEntry(final PlainObjectPool<DelayExchangeEntry> pool) {
        super(IORING_OP_TIMEOUT, pool);
    }

    @Override
    public void close() {
        timeSpec.dispose();
    }

    @Override
    protected void doAccept(final int res, final int flags, final Submitter submitter) {
        final var totalNanos = System.nanoTime() - startNanos;

        final var result = Math.abs(res) != NativeError.ETIME.typeCode()
                           ? NativeError.<Duration>result(res)
                           : Result.ok(timeout(totalNanos).nanos().asDuration());

        completion.accept(result, submitter);
    }

    public DelayExchangeEntry prepare(final BiConsumer<Result<Duration>, Submitter> completion, final Timeout timeout) {
        startNanos = System.nanoTime();

        timeout.asSecondsAndNanos()
               .map(timeSpec::setSecondsNanos);

        return super.prepare(completion);
    }

    @Override
    public SubmitQueueEntry apply(final SubmitQueueEntry entry) {
        return super.apply(entry)
                    .addr(timeSpec.address())
                    .fd(-1)
                    .len(1)
                    .off(1);
    }
}

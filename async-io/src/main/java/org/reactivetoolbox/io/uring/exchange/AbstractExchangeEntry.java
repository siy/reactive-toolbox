package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.CompletionHandler;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.uring.AsyncOperation;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.ObjectHeap;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.util.function.Consumer;

import static org.reactivetoolbox.core.lang.functional.Result.ok;
import static org.reactivetoolbox.core.lang.functional.Unit.unit;
import static org.reactivetoolbox.io.async.common.SizeT.sizeT;

@SuppressWarnings("rawtypes")
public abstract class AbstractExchangeEntry<T extends AbstractExchangeEntry<T, R>, R> implements ExchangeEntry<T> {
    public static final Result<Unit> UNIT_RESULT = ok(unit());

    private static final int RESULT_SIZET_POOL_SIZE = 65536;
    @SuppressWarnings("rawtypes")
    private static final Result[] RESULT_SIZET_POOL;

    static {
        RESULT_SIZET_POOL = new Result[RESULT_SIZET_POOL_SIZE + 1];

        for (int i = 0; i < RESULT_SIZET_POOL.length; i++) {
            RESULT_SIZET_POOL[i] = Result.ok(sizeT(i));
        }
    }

    private final PlainObjectPool pool;
    private final AsyncOperation operation;
    private T next;
    private int key;
    protected Consumer<Result<R>> completion;

    protected AbstractExchangeEntry(final AsyncOperation operation, final PlainObjectPool pool) {
        this.operation = operation;
        this.pool = pool;
    }

    @SuppressWarnings("unchecked")
    public void release() {
        cleanup();
        pool.release(this);
    }

    protected void cleanup() {
        completion = null;
    }

    @Override
    public final void accept(final int result, final int flags) {
        doAccept(result, flags);
        release();
    }

    protected abstract void doAccept(final int result, final int flags);

    @Override
    public void close() {
    }

    @Override
    public T next() {
        return next;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T next(final T next) {
        this.next = next;
        return (T) this;
    }

    @Override
    public T register(final ObjectHeap<CompletionHandler> heap) {
        key = heap.allocKey(this);
        return (T) this;
    }

    @Override
    public SubmitQueueEntry apply(final SubmitQueueEntry entry) {
        return entry.userData(key)
                    .opcode(operation.opcode());
    }

    public T prepare(final Consumer<Result<R>> completion) {
        this.completion = completion;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    protected static Result<SizeT> sizeResult(final int res) {
        return res < RESULT_SIZET_POOL.length
               ? RESULT_SIZET_POOL[res]
               : Result.ok(sizeT(res));
    }
}
package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_NOP;

public class NopExchangeEntry extends AbstractExchangeEntry<NopExchangeEntry, Unit> {
    protected NopExchangeEntry(final PlainObjectPool<NopExchangeEntry> pool) {
        super(IORING_OP_NOP, pool);
    }

    @Override
    protected void doAccept(final int result, final int flags) {
        completion.accept(UNIT_RESULT);
    }
}

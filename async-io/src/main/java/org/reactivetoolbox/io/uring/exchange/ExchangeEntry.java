package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.io.CompletionHandler;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.ObjectHeap;
import org.reactivetoolbox.io.uring.utils.Poolable;

public interface ExchangeEntry<T extends ExchangeEntry<T> & Poolable<T>> extends Poolable<T>, CompletionHandler {
    /**
     * Finally close the instance and release associated resources
     */
    void close();

    SubmitQueueEntry apply(final SubmitQueueEntry entry);

    T register(final ObjectHeap<CompletionHandler> heap);
}

package org.reactivetoolbox.core.scheduler.impl;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.scheduler.SchedulerError;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.IntStream.range;

public class RingBuffer<T> {
    private final AtomicLong sequence = new AtomicLong(0);
    private final ArrayList<BufferEntry<T>> entries;

    private RingBuffer(final int size) {
        entries = new ArrayList<>(size);
        range(0, size).forEach((ignored) -> entries.add(new BufferEntry<>()));
    }

    public static <T> RingBuffer<T> with(final int size) {
        return new RingBuffer<>(size);
    }

    public Either<? extends BaseError, BufferEntry<T>> request() {
        for (int i = 0; i < entries.size(); i++) {
            final int index = (int) (sequence.getAndIncrement() % entries.size());

            if (entries.get(index).lock()) {
                return Either.success(entries.get(index));
            }
        }
        return Either.failure(SchedulerError.NO_FREE_SLOTS);
    }
}

package org.reactivetoolbox.core.async;

import org.reactivetoolbox.core.functional.Tuples.Tuple1;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.reactivetoolbox.core.functional.Tuples.Tuple;
import static org.reactivetoolbox.core.functional.Tuples.Tuple2;
import static org.reactivetoolbox.core.functional.Tuples.Tuple3;
import static org.reactivetoolbox.core.functional.Tuples.Tuple4;
import static org.reactivetoolbox.core.functional.Tuples.Tuple5;
import static org.reactivetoolbox.core.functional.Tuples.Tuple6;
import static org.reactivetoolbox.core.functional.Tuples.Tuple7;
import static org.reactivetoolbox.core.functional.Tuples.Tuple8;
import static org.reactivetoolbox.core.functional.Tuples.Tuple9;
import static org.reactivetoolbox.core.functional.Tuples.of;

public class SimplePromise<T> {
    private final AtomicMarkableReference<T> value = new AtomicMarkableReference<>(null, false);
    private final BlockingQueue<Consumer<T>> thenActions = new LinkedBlockingQueue<>();

    private SimplePromise() {
    }

    public static <T> SimplePromise<T> give() {
        return new SimplePromise<>();
    }

    public static <T> SimplePromise<T> fulfilled(final T value) {
        final SimplePromise<T> promise = give();
        promise.resolve(value);
        return promise;
    }

    @SafeVarargs
    public static <T> SimplePromise<T> any(final SimplePromise<T>... promises) {
        final SimplePromise<T> result = give();

        for (var promise : promises) {
            promise.then(value -> result.resolve(value));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T1> SimplePromise<Tuple1<T1>> all(final SimplePromise<T1> promise) {
        return zipper(values -> of((T1) values[0]), promise);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2> SimplePromise<Tuple2<T1, T2>> all(final SimplePromise<T1> promise1,
                                                             final SimplePromise<T2> promise2) {
        return zipper(values -> of((T1) values[0], (T2) values[1]), promise1, promise2);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3> SimplePromise<Tuple3<T1, T2, T3>> all(final SimplePromise<T1> promise1,
                                                                     final SimplePromise<T2> promise2,
                                                                     final SimplePromise<T3> promise3) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2]), promise1,
                      promise2, promise3);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4> SimplePromise<Tuple4<T1, T2, T3, T4>> all(final SimplePromise<T1> promise1,
                                                                             final SimplePromise<T2> promise2,
                                                                             final SimplePromise<T3> promise3,
                                                                             final SimplePromise<T4> promise4) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3]),
                      promise1, promise2, promise3, promise4);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5> SimplePromise<Tuple5<T1, T2, T3, T4, T5>> all(final SimplePromise<T1> promise1,
                                                                                     final SimplePromise<T2> promise2,
                                                                                     final SimplePromise<T3> promise3,
                                                                                     final SimplePromise<T4> promise4,
                                                                                     final SimplePromise<T5> promise5) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3],
                                   (T5) values[4]), promise1, promise2, promise3, promise4,
                      promise5);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6> SimplePromise<Tuple6<T1, T2, T3, T4, T5, T6>> all(final SimplePromise<T1> promise1,
                                                                                             final SimplePromise<T2> promise2,
                                                                                             final SimplePromise<T3> promise3,
                                                                                             final SimplePromise<T4> promise4,
                                                                                             final SimplePromise<T5> promise5,
                                                                                             final SimplePromise<T6> promise6) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3],
                                   (T5) values[4], (T6) values[5]), promise1, promise2, promise3,
                      promise4, promise5, promise6);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7> SimplePromise<Tuple7<T1, T2, T3, T4, T5, T6, T7>> all(
            final SimplePromise<T1> promise1,
            final SimplePromise<T2> promise2,
            final SimplePromise<T3> promise3,
            final SimplePromise<T4> promise4,
            final SimplePromise<T5> promise5,
            final SimplePromise<T6> promise6,
            final SimplePromise<T7> promise7) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3],
                                   (T5) values[4], (T6) values[5], (T7) values[6]), promise1,
                      promise2, promise3, promise4, promise5, promise6, promise7);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8> SimplePromise<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> all(
            final SimplePromise<T1> promise1,
            final SimplePromise<T2> promise2,
            final SimplePromise<T3> promise3,
            final SimplePromise<T4> promise4,
            final SimplePromise<T5> promise5,
            final SimplePromise<T6> promise6,
            final SimplePromise<T7> promise7,
            final SimplePromise<T8> promise8) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3],
                                   (T5) values[4], (T6) values[5], (T7) values[6], (T8) values[7]),
                      promise1, promise2, promise3, promise4, promise5, promise6, promise7,
                      promise8);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> SimplePromise<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> all(
            final SimplePromise<T1> promise1,
            final SimplePromise<T2> promise2,
            final SimplePromise<T3> promise3,
            final SimplePromise<T4> promise4,
            final SimplePromise<T5> promise5,
            final SimplePromise<T6> promise6,
            final SimplePromise<T7> promise7,
            final SimplePromise<T8> promise8,
            final SimplePromise<T9> promise9) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3],
                                   (T5) values[4], (T6) values[5], (T7) values[6], (T8) values[7],
                                   (T9) values[8]), promise1, promise2, promise3, promise4,
                      promise5, promise6, promise7, promise8, promise9);
    }


    public Optional<T> value() {
        return Optional.ofNullable(value.getReference());
    }

    public boolean ready() {
        return value().isPresent();
    }

    public SimplePromise<T> resolve(final T result) {
        if (value.compareAndSet(null, result, false, true)) {
            thenActions.forEach(action -> action.accept(value.getReference()));
        }
        return this;
    }

    public SimplePromise<T> then(final Consumer<T> action) {
        if (value.isMarked()) {
            action.accept(value.getReference());
        } else {
            thenActions.offer(action);
        }
        return this;
    }

    public SimplePromise<T> syncWait() {
        final CountDownLatch latch = new CountDownLatch(1);
        then(value -> latch.countDown());

        try {
            latch.await();
        } catch (final InterruptedException e) {
            // Ignore exception
        }
        return this;
    }

    public SimplePromise<T> syncWait(final long timeout, final TimeUnit unit) {
        final CountDownLatch latch = new CountDownLatch(1);
        then(value -> latch.countDown());

        try {
            latch.await(timeout, unit);
        } catch (final InterruptedException e) {
            // Ignore exception
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Tuple> SimplePromise<T> zipper(final Function<Object[], T> valueTransformer,
                                                             final SimplePromise<?>... promises) {
        final Object[] values = new Object[promises.length];
        final SimplePromise<T> result = give();
        final ThresholdAction thresholdAction = ThresholdAction.of(promises.length,
                                                                   () -> result.resolve(
                                                                           valueTransformer.apply(
                                                                                   values)));

        int i = 0;
        for (var promise : promises) {
            promise.then(nextAction(values, i, thresholdAction));
            i++;
        }
        return result;
    }

    private static <T> Consumer<T> nextAction(final Object[] values,
                                              final int index,
                                              final ThresholdAction thresholdAction) {
        return value -> {
            values[index] = value;
            thresholdAction.registerEvent();
        };
    }

    private static class ThresholdAction {
        private final AtomicInteger counter;
        private final Runnable action;

        private ThresholdAction(final int count, final Runnable action) {
            counter = new AtomicInteger(count);
            this.action = action;
        }

        public static ThresholdAction of(final int count, final Runnable action) {
            return new ThresholdAction(count, action);
        }

        void registerEvent() {
            if (counter.get() <= 0) {
                return;
            }

            if (counter.decrementAndGet() == 0) {
                action.run();
            }
        }
    }
}

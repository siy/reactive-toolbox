package org.reactivetoolbox.core.async;

/*
 * Copyright (c) 2017-2019 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Tuples.Tuple;
import org.reactivetoolbox.core.functional.Tuples.Tuple1;
import org.reactivetoolbox.core.functional.Tuples.Tuple2;
import org.reactivetoolbox.core.functional.Tuples.Tuple3;
import org.reactivetoolbox.core.functional.Tuples.Tuple4;
import org.reactivetoolbox.core.functional.Tuples.Tuple5;
import org.reactivetoolbox.core.functional.Tuples.Tuple6;
import org.reactivetoolbox.core.functional.Tuples.Tuple7;
import org.reactivetoolbox.core.functional.Tuples.Tuple8;
import org.reactivetoolbox.core.functional.Tuples.Tuple9;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.reactivetoolbox.core.functional.Tuples.of;

/**
 * Representation of the future result of asynchronous operation. The result of operation will be accepted only once.
 * Subsequent notifications do not change the result.
 */
public class Promises {
    public static <E, T> Promise<E, T> create() {
        return new Promise<>();
    }

    public static <E, T> Promise<E, T> success(final T value) {
        return new Promise<E, T>().resolve(Either.right(value));
    }

    public static <E, T> Promise<E, T> failed(final E value) {
        return new Promise<E, T>().resolve(Either.left(value));
    }

    @SafeVarargs
    public static <E, T> Promise<E, T> any(final Promise<E, T>... promises) {
        final Promise<E, T> result = create();

        for (final Promise<E, T> promise : promises) {
            promise.then(value -> result.resolve(Either.right(value)))
                   .otherwise(error -> result.resolve(Either.left(error)));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public static <E, T1> Promise<E, Tuple1<T1>> all(final Promise<E, T1> promise) {
        return zipper(values -> of((T1) values[0]), promise);
    }

    @SuppressWarnings("unchecked")
    public static <E, T1, T2> Promise<E, Tuple2<T1, T2>> all(final Promise<E, T1> promise1,
                                                             final Promise<E, T2> promise2) {
        return zipper(values -> of((T1) values[0], (T2) values[1]), promise1, promise2);
    }

    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3> Promise<E, Tuple3<T1, T2, T3>>
           all(final Promise<E, T1> promise1, final Promise<E, T2> promise2, final Promise<E, T3> promise3) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2]), promise1, promise2, promise3);
    }

    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4> Promise<E, Tuple4<T1, T2, T3, T4>>
           all(final Promise<E, T1> promise1, final Promise<E, T2> promise2, final Promise<E, T3> promise3,
               final Promise<E, T4> promise4) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3]), promise1, promise2,
                      promise3, promise4);
    }

    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5> Promise<E, Tuple5<T1, T2, T3, T4, T5>>
           all(final Promise<E, T1> promise1, final Promise<E, T2> promise2, final Promise<E, T3> promise3,
               final Promise<E, T4> promise4, final Promise<E, T5> promise5) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4]),
                      promise1, promise2, promise3, promise4, promise5);
    }

    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6> Promise<E, Tuple6<T1, T2, T3, T4, T5, T6>>
           all(final Promise<E, T1> promise1, final Promise<E, T2> promise2, final Promise<E, T3> promise3,
               final Promise<E, T4> promise4, final Promise<E, T5> promise5, final Promise<E, T6> promise6) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4],
                                   (T6) values[5]),
                      promise1, promise2, promise3, promise4, promise5, promise6);
    }

    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7> Promise<E, Tuple7<T1, T2, T3, T4, T5, T6, T7>>
           all(final Promise<E, T1> promise1, final Promise<E, T2> promise2, final Promise<E, T3> promise3,
               final Promise<E, T4> promise4, final Promise<E, T5> promise5, final Promise<E, T6> promise6,
               final Promise<E, T7> promise7) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4],
                                   (T6) values[5], (T7) values[6]),
                      promise1, promise2, promise3, promise4, promise5, promise6, promise7);
    }

    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8> Promise<E, Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>>
           all(final Promise<E, T1> promise1, final Promise<E, T2> promise2, final Promise<E, T3> promise3,
               final Promise<E, T4> promise4, final Promise<E, T5> promise5, final Promise<E, T6> promise6,
               final Promise<E, T7> promise7, final Promise<E, T8> promise8) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4],
                                   (T6) values[5], (T7) values[6], (T8) values[7]),
                      promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8);
    }

    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9> Promise<E, Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>
           all(final Promise<E, T1> promise1, final Promise<E, T2> promise2, final Promise<E, T3> promise3,
               final Promise<E, T4> promise4, final Promise<E, T5> promise5, final Promise<E, T6> promise6,
               final Promise<E, T7> promise7, final Promise<E, T8> promise8, final Promise<E, T9> promise9) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4],
                                   (T6) values[5], (T7) values[6], (T8) values[7], (T9) values[8]),
                      promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8, promise9);
    }

    @SuppressWarnings("unchecked")
    private static <E, T extends Tuple> Promise<E, T> zipper(final Function<Object[], T> valueTransformer,
                                                             final Promise<E, ?>... promises) {
        final Object[] values = new Object[promises.length];
        final Promise<E, T> result = new Promise<>();
        final ThresholdAction thresholdAction =
                                              ThresholdAction.of(promises.length,
                                                                 () -> result.resolve(Either.right(valueTransformer.apply(values))));

        int i = 0;
        for (final Promise<?, ?> promise : promises) {
            promise.then(nextAction(values, i, thresholdAction))
                   .otherwise(error -> result.resolve(Either.left((E) error)));
            i++;
        }
        return result;
    }

    private static <T> Consumer<T> nextAction(final Object[] values, final int index,
                                              final ThresholdAction thresholdAction) {
        return value -> {
            values[index] = value;
            thresholdAction.event();
        };
    }

    // TODO: add support for timeouts?
    public static class Promise<E, T> {
        private final AtomicMarkableReference<Either<E, T>> value = new AtomicMarkableReference<>(null, false);
        private final BlockingQueue<Consumer<T>> thenActions = new LinkedBlockingQueue<>();
        private final BlockingQueue<Consumer<E>> otherwiseActions = new LinkedBlockingQueue<>();

        private Promise() {
        }

        public Optional<Either<E, T>> value() {
            return Optional.ofNullable(value.getReference());
        }

        public boolean ready() {
            return value().isPresent();
        }

        public Promise<E, T> resolve(final Either<E, T> result) {
            if (value.compareAndSet(null, result, false, true)) {
                if (result.isRight()) {
                    thenActions.forEach(action -> action.accept(value.getReference().right().get()));
                } else {
                    otherwiseActions.forEach(action -> action.accept(value.getReference().left().get()));
                }

            }
            return this;
        }

        public Promise<E, T> then(final Consumer<T> action) {
            if (value.isMarked()) {
                final Either<E, T> reference = value.getReference();
                if (reference.isRight()) {
                    action.accept(reference.right().get());
                }
            } else {
                thenActions.offer(action);
            }
            return this;
        }

        public Promise<E, T> otherwise(final Consumer<E> action) {
            if (value.isMarked()) {
                final Either<E, T> reference = value.getReference();
                if (reference.isLeft()) {
                    action.accept(reference.left().get());
                }
            } else {
                otherwiseActions.offer(action);
            }
            return this;
        }

        public Promise<E, T> syncWait() {
            final CountDownLatch latch = new CountDownLatch(1);
            then(value -> latch.countDown());

            try {
                latch.await();
            } catch (final InterruptedException e) {
                // Ignore exception
            }
            return this;
        }

        public Promise<E, T> syncWait(final long timeout, final TimeUnit unit) {
            final CountDownLatch latch = new CountDownLatch(1);
            then(value -> latch.countDown());

            try {
                latch.await(timeout, unit);
            } catch (final InterruptedException e) {
                // Ignore exception
            }
            return this;
        }
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

        void event() {
            if (counter.get() <= 0) {
                return;
            }

            if (counter.decrementAndGet() == 0) {
                action.run();
            }
        }
    }
}

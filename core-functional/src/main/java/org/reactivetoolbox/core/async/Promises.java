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

import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.core.functional.Tuples.Tuple1;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.function.Consumer;

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

//TODO: Javadoc
public class Promises {
    public static <T> Promise<T> give() {
        return new Promise<>();
    }

    public static <T> Promise<T> fulfilled(final T value) {
        return Promises.<T>give().resolve(value);
    }

    @SafeVarargs
    public static <T> Promise<T> any(final Promise<T>... promises) {
        final var result = Promises.<T>give();
        Arrays.asList(promises).forEach(promise -> promise.then(result::resolve));
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T1> Promise<Tuple1<T1>> all(final Promise<T1> promise) {
        return zipper(values -> of((T1) values[0]),
                      promise);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2> Promise<Tuple2<T1, T2>> all(final Promise<T1> promise1,
                                                       final Promise<T2> promise2) {
        return zipper(values -> of((T1) values[0],
                                   (T2) values[1]),
                      promise1,
                      promise2);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3> Promise<Tuple3<T1, T2, T3>> all(final Promise<T1> promise1,
                                                               final Promise<T2> promise2,
                                                               final Promise<T3> promise3) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2]), promise1, promise2, promise3);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4> Promise<Tuple4<T1, T2, T3, T4>> all(final Promise<T1> promise1,
                                                                       final Promise<T2> promise2,
                                                                       final Promise<T3> promise3,
                                                                       final Promise<T4> promise4) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3]), promise1, promise2, promise3, promise4);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5> Promise<Tuple5<T1, T2, T3, T4, T5>> all(final Promise<T1> promise1,
                                                                               final Promise<T2> promise2,
                                                                               final Promise<T3> promise3,
                                                                               final Promise<T4> promise4,
                                                                               final Promise<T5> promise5) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3],
                                   (T5) values[4]), promise1, promise2, promise3, promise4,
                      promise5);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6> Promise<Tuple6<T1, T2, T3, T4, T5, T6>> all(final Promise<T1> promise1,
                                                                                       final Promise<T2> promise2,
                                                                                       final Promise<T3> promise3,
                                                                                       final Promise<T4> promise4,
                                                                                       final Promise<T5> promise5,
                                                                                       final Promise<T6> promise6) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3],
                                   (T5) values[4], (T6) values[5]), promise1, promise2, promise3,
                      promise4, promise5, promise6);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7> Promise<Tuple7<T1, T2, T3, T4, T5, T6, T7>> all(final Promise<T1> promise1,
                                                                                               final Promise<T2> promise2,
                                                                                               final Promise<T3> promise3,
                                                                                               final Promise<T4> promise4,
                                                                                               final Promise<T5> promise5,
                                                                                               final Promise<T6> promise6,
                                                                                               final Promise<T7> promise7) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3],
                                   (T5) values[4], (T6) values[5], (T7) values[6]), promise1,
                      promise2, promise3, promise4, promise5, promise6, promise7);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8> Promise<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> all(
            final Promise<T1> promise1,
            final Promise<T2> promise2,
            final Promise<T3> promise3,
            final Promise<T4> promise4,
            final Promise<T5> promise5,
            final Promise<T6> promise6,
            final Promise<T7> promise7,
            final Promise<T8> promise8) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3],
                                   (T5) values[4], (T6) values[5], (T7) values[6], (T8) values[7]),
                      promise1, promise2, promise3, promise4, promise5, promise6, promise7,
                      promise8);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Promise<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> all(
            final Promise<T1> promise1,
            final Promise<T2> promise2,
            final Promise<T3> promise3,
            final Promise<T4> promise4,
            final Promise<T5> promise5,
            final Promise<T6> promise6,
            final Promise<T7> promise7,
            final Promise<T8> promise8,
            final Promise<T9> promise9) {
        return zipper(values -> of((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3],
                                   (T5) values[4], (T6) values[5], (T7) values[6], (T8) values[7],
                                   (T9) values[8]), promise1, promise2, promise3, promise4,
                      promise5, promise6, promise7, promise8, promise9);
    }

    public static class Promise<T> {
        private final AtomicMarkableReference<T> value = new AtomicMarkableReference<>(null, false);
        private final BlockingQueue<Consumer<T>> thenActions = new LinkedBlockingQueue<>();

        private Promise() {
        }

        public Option<T> value() {
            return Option.of(value.getReference());
        }

        public boolean ready() {
            return value().isPresent();
        }

        public Promise<T> resolve(final T result) {
            if (value.compareAndSet(null, result, false, true)) {
                thenActions.forEach(action -> action.accept(value.getReference()));
            }
            return this;
        }

        public Promise<T> then(final Consumer<T> action) {
            if (value.isMarked()) {
                action.accept(value.getReference());
            } else {
                thenActions.offer(action);
            }
            return this;
        }

        public Promise<T> syncWait() {
            final var latch = new CountDownLatch(1);
            then(value -> latch.countDown());

            try {
                latch.await();
            } catch (final InterruptedException e) {
                // Ignore exception
            }
            return this;
        }

        public Promise<T> syncWait(final long timeout, final TimeUnit unit) {
            final var latch = new CountDownLatch(1);
            then(value -> latch.countDown());

            try {
                latch.await(timeout, unit);
            } catch (final InterruptedException e) {
                // Ignore exception
            }
            return this;
        }
    }

    private static <T extends Tuple> Promise<T> zipper(final FN1<T, Object[]> valueTransformer,
                                                       final Promise<?>... promises) {
        final var values = new Object[promises.length];
        final var result = Promises.<T>give();
        final var thresholdAction = ThresholdAction.of(promises.length,
                                                       () -> result.resolve(valueTransformer.apply(values)));

        int i = 0;
        for (final var promise : promises) {
            final var index = i;

            promise.then(value -> {
                values[index] = value;
                thresholdAction.registerEvent();
            });
            i++;
        }
        return result;
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

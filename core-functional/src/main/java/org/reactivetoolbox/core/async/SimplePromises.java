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
 *
 *
 */

import org.reactivetoolbox.core.async.Tuples.Tuple;
import org.reactivetoolbox.core.async.Tuples.Tuple1;
import org.reactivetoolbox.core.async.Tuples.Tuple2;
import org.reactivetoolbox.core.async.Tuples.Tuple3;
import org.reactivetoolbox.core.async.Tuples.Tuple4;
import org.reactivetoolbox.core.async.Tuples.Tuple5;
import org.reactivetoolbox.core.async.Tuples.Tuple6;
import org.reactivetoolbox.core.async.Tuples.Tuple7;
import org.reactivetoolbox.core.async.Tuples.Tuple8;
import org.reactivetoolbox.core.async.Tuples.Tuple9;
import org.reactivetoolbox.core.functional.Either;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.reactivetoolbox.core.async.Tuples.with;

/**
 * Representation of the future result of asynchronous operation.
 * The result of operation will be accepted only once. Subsequent notifications do not change the result.
 */
public class SimplePromises {
    public static <T> Promise<T> empty() {
        return new Promise<>();
    }

    public static <T> Promise<T> ready(final T value) {
        return new Promise<T>().notify(value);
    }

    @SuppressWarnings("unchecked")
    public static <T1> Promise<Tuple1<T1>> zip(final Promise<T1> promise) {
        return zipper(values -> with((T1) values[0]), promise);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2> Promise<Tuple2<T1, T2>> zip(final Promise<T1> promise1, final Promise<T2> promise2) {
        return zipper(values -> with((T1) values[0], (T2) values[1]), promise1, promise2);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3> Promise<Tuple3<T1, T2, T3>> zip(final Promise<T1> promise1, final Promise<T2> promise2,
                                                               final Promise<T3> promise3) {
        return zipper(values -> with((T1) values[0], (T2) values[1], (T3) values[2]),
                promise1, promise2, promise3);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4> Promise<Tuple4<T1, T2, T3, T4>> zip(final Promise<T1> promise1, final Promise<T2> promise2,
                                                                       final Promise<T3> promise3, final Promise<T4> promise4) {
        return zipper(values -> with((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3]),
                promise1, promise2, promise3, promise4);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5> Promise<Tuple5<T1, T2, T3, T4, T5>> zip(final Promise<T1> promise1, final Promise<T2> promise2,
                                                                               final Promise<T3> promise3, final Promise<T4> promise4,
                                                                               final Promise<T5> promise5) {
        return zipper(values -> with((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4]),
                promise1, promise2, promise3, promise4, promise5);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6> Promise<Tuple6<T1, T2, T3, T4, T5, T6>> zip(final Promise<T1> promise1, final Promise<T2> promise2,
                                                                                       final Promise<T3> promise3, final Promise<T4> promise4,
                                                                                       final Promise<T5> promise5, final Promise<T6> promise6) {
        return zipper(values -> with((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4],
                (T6) values[5]),
                promise1, promise2, promise3, promise4, promise5, promise6);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7> Promise<Tuple7<T1, T2, T3, T4, T5, T6, T7>> zip(final Promise<T1> promise1, final Promise<T2> promise2,
                                                                                               final Promise<T3> promise3, final Promise<T4> promise4,
                                                                                               final Promise<T5> promise5, final Promise<T6> promise6,
                                                                                               final Promise<T7> promise7) {
        return zipper(values -> with((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4],
                (T6) values[5], (T7) values[6]),
                promise1, promise2, promise3, promise4,
                promise5, promise6, promise7);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8> Promise<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> zip(final Promise<T1> promise1, final Promise<T2> promise2,
                                                                                                       final Promise<T3> promise3, final Promise<T4> promise4,
                                                                                                       final Promise<T5> promise5, final Promise<T6> promise6,
                                                                                                       final Promise<T7> promise7, final Promise<T8> promise8) {
        return zipper(values -> with((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4],
                (T6) values[5], (T7) values[6], (T8) values[7]),
                promise1, promise2, promise3, promise4,
                promise5, promise6, promise7, promise8);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Promise<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> zip(final Promise<T1> promise1, final Promise<T2> promise2,
                                                                                                               final Promise<T3> promise3, final Promise<T4> promise4,
                                                                                                               final Promise<T5> promise5, final Promise<T6> promise6,
                                                                                                               final Promise<T7> promise7, final Promise<T8> promise8,
                                                                                                               final Promise<T9> promise9) {
        return zipper(values -> with((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4],
                (T6) values[5], (T7) values[6], (T8) values[7], (T9) values[8]),
                promise1, promise2, promise3, promise4, promise5,
                promise6, promise7, promise8, promise9);
    }

    private static <T extends Tuple> Promise<T> zipper(Function<Object[], T> transformer, Promise<?>... promises) {
        final Object[] values = new Object[promises.length];
        final Promise<T> result = new Promise<>();
        final ThresholdAction action = ThresholdAction.of(promises.length, () -> result.notify(transformer.apply(values)));

        int i = 0;
        for(Promise promise : promises) {
            promise.then(value -> { values[i] = value; action.event();});
            i++;
        }
        return result;
    }

    public static class Promise<T> {
        private final AtomicMarkableReference<T> value = new AtomicMarkableReference<>(null, false);
        private final BlockingQueue<Consumer<T>> actions = new LinkedBlockingQueue<>();

        private Promise() {
        }

        public Promise<T> notify(T result) {
            if (value.compareAndSet(null, result, false, true)) {
                actions.forEach(action -> action.accept(value.getReference()));
            }
            return this;
        }

        public Promise<T> then(Consumer<T> action) {
            if (value.isMarked()) {
                action.accept(value.getReference());
            } else {
                actions.offer(action);
            }
            return this;
        }
    }

    private static class ThresholdAction {
        private final AtomicInteger counter;
        private final Runnable action;

        private ThresholdAction(int count, Runnable action) {
            counter = new AtomicInteger(count);
            this.action = action;
        }

        public static ThresholdAction of(int count, Runnable action) {
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

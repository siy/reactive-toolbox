package org.reactivetoolbox.async.promise;

/*
 * Copyright (c) 2017 Sergiy Yevtushenko
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

import org.reactivetoolbox.core.async.Tuples;
import org.reactivetoolbox.core.async.Tuples.Tuple;
import org.reactivetoolbox.core.async.Tuples.Tuple1;
import org.reactivetoolbox.core.async.Tuples.Tuple2;
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
 * The result of operation will be accepted only once, when {@link Promise#notify(Either)} method is called
 * first time. Subsequent notifications will not change the result.
 */
public class Promises {
    public static <E,T> Promise<E, T> ready(final T value) {
        return new Promise<E,T>().notify(Either.right(value));
    }

    public static <E,T> Promise<E, T> errorReady(final E errValue) {
        return new Promise<E,T>().notify(Either.left(errValue));
    }

    public static <E, T1> Promise<E, Tuple1<T1>> zip(final Promise<E, T1> promise) {
        return zipper(values -> with(values[0]), promise);
    }

    private static <E, T extends Tuple> Promise<E, T> zipper(Function<Either[], Either<E, T>> transformer, Promise<E, ?>... promises) {
        final Either[] values = new Either[promises.length];
        final Promise<E, T> result = new Promise<>();
        final ThresholdAction action = ThresholdAction.of(promises.length, () -> result.notify(transformer.apply(values)));

        int i = 0;
        for(Promise promise : promises) {
            promise.either(value -> { values[i++] = (Either) value; action.event();});
        }
        return result;
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

    public static class Promise<E, T> {
        private final AtomicMarkableReference<Either<E, T>> value = new AtomicMarkableReference<>(null, false);
        private final BlockingQueue<Consumer<T>> actions = new LinkedBlockingQueue<>();
        private final BlockingQueue<Consumer<E>> errorActions = new LinkedBlockingQueue<>();
        private final BlockingQueue<Consumer<Either<E, T>>> eitherActions = new LinkedBlockingQueue<>();

        private Promise() {
        }

        public Promise<E, T> notify(Either<E, T> result) {
            if (value.compareAndSet(null, result, false, true)) {
                if (value.getReference().isRight()) {
                    actions.forEach(action -> action.accept(value.getReference().right().get()));
                } else {
                    errorActions.forEach(action -> action.accept(value.getReference().left().get()));
                }
                eitherActions.forEach(action -> action.accept(action));
            }
            return this;
        }

        public Promise<E, T> either(Consumer<Either<E, T>> action) {
            if (value.isMarked()) {
                action.accept(value.getReference());
            } else {
                eitherActions.offer(action);
            }
            return this;
        }

        public Promise<E, T> then(Consumer<T> action) {
            if (value.isMarked()) {
                value.getReference().right().ifPresent(action);
            } else {
                actions.offer(action);
            }
            return this;
        }

        public Promise<E, T> onError(Consumer<E> action) {
            if (value.isMarked()) {
                value.getReference().left().ifPresent(action);
            } else {
                errorActions.offer(action);
            }
            return this;
        }
    }
}

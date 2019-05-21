package org.reactivetoolbox.core.async;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.async.Tuples.Tuple2;
import org.reactivetoolbox.core.functional.Either;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PromisesTest {
    @Test
    void multipleAssignmentsAreIgnored() {
        final Promise<Throwable, Integer> promise = Promises.create();

        promise.resolve(Either.right(1));
        promise.resolve(Either.right(2));
        promise.resolve(Either.right(3));
        promise.resolve(Either.right(4));

        assertTrue(promise.value().isPresent());
        assertTrue(promise.value().get().isRight());
        assertEquals(Integer.valueOf(1), promise.value().get().right().get());
    }

    @Test
    void thenActionsAreExecuted() {
        final Promise<Throwable, Integer> promise = Promises.create();
        final AtomicInteger holder = new AtomicInteger(-1);

        promise.then(value -> holder.set(value));
        promise.resolve(Either.right(1));

        assertEquals(Integer.valueOf(1), holder.get());
    }

    @Test
    void thenActionsAreExecutedEvenIfAddedAfterPromiseResolution() {
        final Promise<Throwable, Integer> promise = Promises.create();
        final AtomicInteger holder = new AtomicInteger(-1);

        promise.resolve(Either.right(1));
        promise.then(value -> holder.set(value));

        assertEquals(Integer.valueOf(1), holder.get());
    }

    @Test
    void otherwiseActionsAreExecuted() {
        final Promise<Integer, Integer> promise = Promises.create();
        final AtomicInteger holder = new AtomicInteger(-1);

        promise.otherwise(value -> holder.set(value));
        promise.resolve(Either.left(1));

        assertEquals(Integer.valueOf(1), holder.get());
    }

    @Test
    void otherwiseActionsAreExecutedEvenIfAddedAfterPromiseResolution() {
        final Promise<Integer, Integer> promise = Promises.create();
        final AtomicInteger holder = new AtomicInteger(-1);

        promise.resolve(Either.left(1));
        promise.otherwise(value -> holder.set(value));

        assertEquals(Integer.valueOf(1), holder.get());
    }

    @Test
    void anyResolvedPromiseResolvesResult1() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> anyPromise = Promises.any(promise1, promise2);

        assertFalse(anyPromise.value().isPresent());

        promise1.resolve(Either.right(1));

        assertTrue(anyPromise.value().isPresent());
        assertTrue(anyPromise.value().get().isRight());
        assertEquals(Integer.valueOf(1), anyPromise.value().get().right().get());
    }

    @Test
    void anyResolvedPromiseResolvesResult2() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> anyPromise = Promises.any(promise1, promise2);

        assertFalse(anyPromise.value().isPresent());

        promise2.resolve(Either.left(1));

        assertTrue(anyPromise.value().isPresent());
        assertTrue(anyPromise.value().get().isLeft());
        assertEquals(Integer.valueOf(1), anyPromise.value().get().left().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Tuple2<Integer, Integer>> allPromise = Promises.all(promise1, promise2);

        assertFalse(allPromise.value().isPresent());

        promise1.resolve(Either.right(1));

        assertFalse(allPromise.value().isPresent());

        promise2.resolve(Either.right(2));

        assertTrue(allPromise.value().isPresent());

        assertEquals(Tuples.of(1, 2), allPromise.value().get().right().get());
    }
//TODO: finish tests
}

package org.reactivetoolbox.core.async;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.async.Promises.Promise;
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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PromisesTest {
    private final Executor executor = Executors.newSingleThreadExecutor();
    @Test
    void multipleAssignmentsAreIgnored() {
        final Promise<Throwable, Integer> promise = Promises.create();

        promise.resolve(Either.right(1));
        promise.resolve(Either.right(2));
        promise.resolve(Either.right(3));
        promise.resolve(Either.right(4));

        assertTrue(promise.ready());
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

        assertFalse(anyPromise.ready());

        promise1.resolve(Either.right(1));

        assertTrue(anyPromise.ready());
        assertTrue(anyPromise.value().get().isRight());
        assertEquals(Integer.valueOf(1), anyPromise.value().get().right().get());
    }

    @Test
    void anyResolvedPromiseResolvesResult2() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> anyPromise = Promises.any(promise1, promise2);

        assertFalse(anyPromise.ready());

        promise2.resolve(Either.left(1));

        assertTrue(anyPromise.ready());
        assertTrue(anyPromise.value().get().isLeft());
        assertEquals(Integer.valueOf(1), anyPromise.value().get().left().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved1() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Tuple1<Integer>> allPromise = Promises.all(promise1);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.right(1));

        assertTrue(allPromise.ready());

        assertEquals(Tuples.of(1), allPromise.value().get().right().get());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft1() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Tuple1<Integer>> allPromise = Promises.all(promise1);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.left(1));

        assertTrue(allPromise.ready());

        assertEquals(1, allPromise.value().get().left().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved2() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Tuple2<Integer, Integer>> allPromise = Promises.all(promise1, promise2);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.right(1));

        assertFalse(allPromise.ready());

        promise2.resolve(Either.right(2));

        assertTrue(allPromise.ready());

        assertEquals(Tuples.of(1, 2), allPromise.value().get().right().get());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft2() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Tuple2<Integer, Integer>> allPromise = Promises.all(promise1, promise2);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.left(1));

        assertTrue(allPromise.ready());

        assertEquals(1, allPromise.value().get().left().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved3() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> promise3 = Promises.create();
        final Promise<Integer, Tuple3<Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.right(1));

        assertFalse(allPromise.ready());

        promise2.resolve(Either.right(2));

        assertFalse(allPromise.ready());

        promise3.resolve(Either.right(3));

        assertTrue(allPromise.ready());

        assertEquals(Tuples.of(1, 2, 3), allPromise.value().get().right().get());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft3() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> promise3 = Promises.create();
        final Promise<Integer, Tuple3<Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3);

        assertFalse(allPromise.ready());

        promise2.resolve(Either.left(1));

        assertTrue(allPromise.ready());

        assertEquals(1, allPromise.value().get().left().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved4() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> promise3 = Promises.create();
        final Promise<Integer, Integer> promise4 = Promises.create();
        final Promise<Integer, Tuple4<Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.right(1));

        assertFalse(allPromise.ready());

        promise2.resolve(Either.right(2));

        assertFalse(allPromise.ready());

        promise3.resolve(Either.right(3));

        assertFalse(allPromise.ready());

        promise4.resolve(Either.right(4));

        assertTrue(allPromise.ready());

        assertEquals(Tuples.of(1, 2, 3, 4), allPromise.value().get().right().get());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft4() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> promise3 = Promises.create();
        final Promise<Integer, Integer> promise4 = Promises.create();
        final Promise<Integer, Tuple4<Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4);

        assertFalse(allPromise.ready());

        promise3.resolve(Either.left(1));

        assertTrue(allPromise.ready());

        assertEquals(1, allPromise.value().get().left().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved5() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> promise3 = Promises.create();
        final Promise<Integer, Integer> promise4 = Promises.create();
        final Promise<Integer, Integer> promise5 = Promises.create();
        final Promise<Integer, Tuple5<Integer, Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4, promise5);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.right(1));

        assertFalse(allPromise.ready());

        promise2.resolve(Either.right(2));

        assertFalse(allPromise.ready());

        promise3.resolve(Either.right(3));

        assertFalse(allPromise.ready());

        promise4.resolve(Either.right(4));

        assertFalse(allPromise.ready());

        promise5.resolve(Either.right(5));

        assertTrue(allPromise.ready());

        assertEquals(Tuples.of(1, 2, 3, 4, 5), allPromise.value().get().right().get());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft5() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> promise3 = Promises.create();
        final Promise<Integer, Integer> promise4 = Promises.create();
        final Promise<Integer, Integer> promise5 = Promises.create();
        final Promise<Integer, Tuple5<Integer, Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4, promise5);

        assertFalse(allPromise.ready());

        promise4.resolve(Either.left(1));

        assertTrue(allPromise.ready());

        assertEquals(1, allPromise.value().get().left().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved6() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> promise3 = Promises.create();
        final Promise<Integer, Integer> promise4 = Promises.create();
        final Promise<Integer, Integer> promise5 = Promises.create();
        final Promise<Integer, Integer> promise6 = Promises.create();
        final Promise<Integer, Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4, promise5, promise6);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.right(1));

        assertFalse(allPromise.ready());

        promise2.resolve(Either.right(2));

        assertFalse(allPromise.ready());

        promise3.resolve(Either.right(3));

        assertFalse(allPromise.ready());

        promise4.resolve(Either.right(4));

        assertFalse(allPromise.ready());

        promise5.resolve(Either.right(5));

        assertFalse(allPromise.ready());

        promise6.resolve(Either.right(6));

        assertTrue(allPromise.ready());

        assertEquals(Tuples.of(1, 2, 3, 4, 5, 6), allPromise.value().get().right().get());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft6() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> promise3 = Promises.create();
        final Promise<Integer, Integer> promise4 = Promises.create();
        final Promise<Integer, Integer> promise5 = Promises.create();
        final Promise<Integer, Integer> promise6 = Promises.create();
        final Promise<Integer, Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4, promise5, promise6);

        assertFalse(allPromise.ready());

        promise5.resolve(Either.left(1));

        assertTrue(allPromise.ready());

        assertEquals(1, allPromise.value().get().left().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved7() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> promise3 = Promises.create();
        final Promise<Integer, Integer> promise4 = Promises.create();
        final Promise<Integer, Integer> promise5 = Promises.create();
        final Promise<Integer, Integer> promise6 = Promises.create();
        final Promise<Integer, Integer> promise7 = Promises.create();
        final Promise<Integer, Tuple7<Integer, Integer, Integer, Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4, promise5, promise6, promise7);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.right(1));

        assertFalse(allPromise.ready());

        promise2.resolve(Either.right(2));

        assertFalse(allPromise.ready());

        promise3.resolve(Either.right(3));

        assertFalse(allPromise.ready());

        promise4.resolve(Either.right(4));

        assertFalse(allPromise.ready());

        promise5.resolve(Either.right(5));

        assertFalse(allPromise.ready());

        promise6.resolve(Either.right(6));

        assertFalse(allPromise.ready());

        promise7.resolve(Either.right(7));

        assertTrue(allPromise.ready());

        assertEquals(Tuples.of(1, 2, 3, 4, 5, 6, 7), allPromise.value().get().right().get());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft7() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> promise3 = Promises.create();
        final Promise<Integer, Integer> promise4 = Promises.create();
        final Promise<Integer, Integer> promise5 = Promises.create();
        final Promise<Integer, Integer> promise6 = Promises.create();
        final Promise<Integer, Integer> promise7 = Promises.create();
        final Promise<Integer, Tuple7<Integer, Integer, Integer, Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4, promise5, promise6, promise7);

        assertFalse(allPromise.ready());

        promise6.resolve(Either.left(1));

        assertTrue(allPromise.ready());

        assertEquals(1, allPromise.value().get().left().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved8() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> promise3 = Promises.create();
        final Promise<Integer, Integer> promise4 = Promises.create();
        final Promise<Integer, Integer> promise5 = Promises.create();
        final Promise<Integer, Integer> promise6 = Promises.create();
        final Promise<Integer, Integer> promise7 = Promises.create();
        final Promise<Integer, Integer> promise8 = Promises.create();
        final Promise<Integer, Tuple8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.right(1));

        assertFalse(allPromise.ready());

        promise2.resolve(Either.right(2));

        assertFalse(allPromise.ready());

        promise3.resolve(Either.right(3));

        assertFalse(allPromise.ready());

        promise4.resolve(Either.right(4));

        assertFalse(allPromise.ready());

        promise5.resolve(Either.right(5));

        assertFalse(allPromise.ready());

        promise6.resolve(Either.right(6));

        assertFalse(allPromise.ready());

        promise7.resolve(Either.right(7));

        assertFalse(allPromise.ready());

        promise8.resolve(Either.right(8));

        assertTrue(allPromise.ready());

        assertEquals(Tuples.of(1, 2, 3, 4, 5, 6, 7, 8), allPromise.value().get().right().get());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft8() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> promise3 = Promises.create();
        final Promise<Integer, Integer> promise4 = Promises.create();
        final Promise<Integer, Integer> promise5 = Promises.create();
        final Promise<Integer, Integer> promise6 = Promises.create();
        final Promise<Integer, Integer> promise7 = Promises.create();
        final Promise<Integer, Integer> promise8 = Promises.create();
        final Promise<Integer, Tuple8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8);

        assertFalse(allPromise.ready());

        promise7.resolve(Either.left(1));

        assertTrue(allPromise.ready());

        assertEquals(1, allPromise.value().get().left().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved9() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> promise3 = Promises.create();
        final Promise<Integer, Integer> promise4 = Promises.create();
        final Promise<Integer, Integer> promise5 = Promises.create();
        final Promise<Integer, Integer> promise6 = Promises.create();
        final Promise<Integer, Integer> promise7 = Promises.create();
        final Promise<Integer, Integer> promise8 = Promises.create();
        final Promise<Integer, Integer> promise9 = Promises.create();
        final Promise<Integer, Tuple9<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8, promise9);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.right(1));

        assertFalse(allPromise.ready());

        promise2.resolve(Either.right(2));

        assertFalse(allPromise.ready());

        promise3.resolve(Either.right(3));

        assertFalse(allPromise.ready());

        promise4.resolve(Either.right(4));

        assertFalse(allPromise.ready());

        promise5.resolve(Either.right(5));

        assertFalse(allPromise.ready());

        promise6.resolve(Either.right(6));

        assertFalse(allPromise.ready());

        promise7.resolve(Either.right(7));

        assertFalse(allPromise.ready());

        promise8.resolve(Either.right(8));

        assertFalse(allPromise.ready());

        promise9.resolve(Either.right(9));

        assertTrue(allPromise.ready());

        assertEquals(Tuples.of(1, 2, 3, 4, 5, 6, 7, 8, 9), allPromise.value().get().right().get());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft9() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Integer> promise3 = Promises.create();
        final Promise<Integer, Integer> promise4 = Promises.create();
        final Promise<Integer, Integer> promise5 = Promises.create();
        final Promise<Integer, Integer> promise6 = Promises.create();
        final Promise<Integer, Integer> promise7 = Promises.create();
        final Promise<Integer, Integer> promise8 = Promises.create();
        final Promise<Integer, Integer> promise9 = Promises.create();
        final Promise<Integer, Tuple9<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8, promise9);

        assertFalse(allPromise.ready());

        promise8.resolve(Either.left(1));

        assertTrue(allPromise.ready());

        assertEquals(1, allPromise.value().get().left().get());
    }

    @Test
    void subsequentResolutionsAreIgnoreByAll() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Tuple2<Integer, Integer>> allPromise = Promises.all(promise1, promise2);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.right(1));
        promise2.resolve(Either.right(2));

        assertTrue(allPromise.ready());

        promise1.resolve(Either.right(3));
        promise2.resolve(Either.right(4));

        assertEquals(Tuples.of(1, 2), allPromise.value().get().right().get());
    }

    @Test
    void subsequentSuccessResolutionsAreIgnoreIfErrorAlreadyResolvedByAll() {
        final Promise<Integer, Integer> promise1 = Promises.create();
        final Promise<Integer, Integer> promise2 = Promises.create();
        final Promise<Integer, Tuple2<Integer, Integer>> allPromise = Promises.all(promise1, promise2);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.left(1));

        assertTrue(allPromise.ready());

        promise1.resolve(Either.right(3));
        promise2.resolve(Either.right(4));

        assertEquals(1, allPromise.value().get().left().get());
    }

    @Test
    void syncWaitIsWaitingForResolution() {
        final Promise<Integer, Integer> promise = Promises.create();

        assertFalse(promise.ready());

        executor.execute(() -> {safeSleep(20); promise.resolve(Either.right(1));});

        promise.syncWait();

        assertTrue(promise.ready());
        assertEquals(1, promise.value().get().right().get());
    }

    @Test
    void syncWaitDoesNotWaitForAlreadyResolved() {
        final Promise<Integer, Integer> promise = Promises.create();

        assertFalse(promise.ready());

        promise.resolve(Either.right(1));

        promise.syncWait();

        assertTrue(promise.ready());
        assertEquals(1, promise.value().get().right().get());
    }

    private static void safeSleep(final long delay) {
        try {
            Thread.sleep(delay);
        } catch (final InterruptedException e) {
            //Ignore
        }
    }
}

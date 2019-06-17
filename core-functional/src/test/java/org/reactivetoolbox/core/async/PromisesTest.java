package org.reactivetoolbox.core.async;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Tuples;
import org.reactivetoolbox.core.functional.Tuples.*;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;


public class PromisesTest {
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Test
    void multipleAssignmentsAreIgnored() {
        final var promise = Promises.<Integer>give();

        promise.resolve(1);
        promise.resolve(2);
        promise.resolve(3);
        promise.resolve(4);

        assertTrue(promise.ready());
        assertEquals(1, promise.value().get());
    }

    @Test
    void thenActionsAreExecuted() {
        final var promise = Promises.<Integer>give();
        final var holder = new AtomicInteger(-1);

        promise.then(value -> holder.set(value));
        promise.resolve(1);

        assertEquals(1, holder.get());
    }

    @Test
    void thenActionsAreExecutedEvenIfAddedAfterPromiseResolution() {
        final var promise = Promises.<Integer>give();
        final var holder = new AtomicInteger(-1);

        promise.resolve(1);
        promise.then(value -> holder.set(value));

        assertEquals(1, holder.get());
    }

    @Test
    void anyResolvedPromiseResolvesResultForFirstPromise() {
        final var promise1 = Promises.<Integer>give();
        final var promise2 = Promises.<Integer>give();
        final var anyPromise = Promises.any(promise1, promise2);

        assertFalse(anyPromise.ready());

        promise1.resolve(1);

        assertTrue(anyPromise.ready());
        assertEquals(1, anyPromise.value().get());
    }

    @Test
    void anyResolvedPromiseResolvesResultForSecondPromise() {
        final var promise1 = Promises.<Integer>give();
        final var promise2 = Promises.<Integer>give();
        final Promise<Integer> anyPromise = Promises.any(promise1, promise2);

        assertFalse(anyPromise.ready());

        promise2.resolve(1);

        assertTrue(anyPromise.ready());
        assertEquals(1, anyPromise.value().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedForOnePromise() {
        final var promise1 = Promises.<Integer>give();
        final var allPromise = Promises.all(promise1);

        assertFalse(allPromise.ready());

        promise1.resolve(1);

        assertTrue(allPromise.ready());
        assertEquals(Tuples.of(1), allPromise.value().get());
    }


    @Test
    void allResolvesWhenAllPromisesAreResolvedFor2Promises() {
        final var promise1 = Promises.<Integer>give();
        final var promise2 = Promises.<Integer>give();
        final var allPromise = Promises.all(promise1, promise2);

        assertFalse(allPromise.ready());

        promise1.resolve(1);

        assertFalse(allPromise.ready());

        promise2.resolve(2);

        assertTrue(allPromise.ready());
        assertEquals(Tuples.of(1, 2), allPromise.value().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedFor3Promises() {
        final var promise1 = Promises.<Integer>give();
        final var promise2 = Promises.<Integer>give();
        final var promise3 = Promises.<Integer>give();
        final var allPromise = Promises.all(promise1, promise2, promise3);

        assertFalse(allPromise.ready());

        promise1.resolve(1);

        assertFalse(allPromise.ready());

        promise2.resolve(2);

        assertFalse(allPromise.ready());

        promise3.resolve(3);

        assertTrue(allPromise.ready());

        assertEquals(Tuples.of(1, 2, 3), allPromise.value().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedFor4Promises() {
        final var promise1 = Promises.<Integer>give();
        final var promise2 = Promises.<Integer>give();
        final var promise3 = Promises.<Integer>give();
        final var promise4 = Promises.<Integer>give();
        final var allPromise = Promises.all(promise1, promise2, promise3, promise4);

        assertFalse(allPromise.ready());

        promise1.resolve(1);

        assertFalse(allPromise.ready());

        promise2.resolve(2);

        assertFalse(allPromise.ready());

        promise3.resolve(3);

        assertFalse(allPromise.ready());

        promise4.resolve(4);

        assertTrue(allPromise.ready());

        assertEquals(Tuples.of(1, 2, 3, 4), allPromise.value().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedFor5Promises() {
        final var promise1 = Promises.<Integer>give();
        final var promise2 = Promises.<Integer>give();
        final var promise3 = Promises.<Integer>give();
        final var promise4 = Promises.<Integer>give();
        final var promise5 = Promises.<Integer>give();
        final var allPromise = Promises.all(promise1, promise2, promise3, promise4, promise5);

        assertFalse(allPromise.ready());

        promise1.resolve(1);

        assertFalse(allPromise.ready());

        promise2.resolve(2);

        assertFalse(allPromise.ready());

        promise3.resolve(3);

        assertFalse(allPromise.ready());

        promise4.resolve(4);

        assertFalse(allPromise.ready());

        promise5.resolve(5);

        assertTrue(allPromise.ready());

        assertEquals(Tuples.of(1, 2, 3, 4, 5), allPromise.value().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedFor6Promises() {
        final var promise1 = Promises.<Integer>give();
        final var promise2 = Promises.<Integer>give();
        final var promise3 = Promises.<Integer>give();
        final var promise4 = Promises.<Integer>give();
        final var promise5 = Promises.<Integer>give();
        final var promise6 = Promises.<Integer>give();
        final var allPromise = Promises.all(promise1, promise2, promise3, promise4, promise5, promise6);

        assertFalse(allPromise.ready());

        promise1.resolve(1);

        assertFalse(allPromise.ready());

        promise2.resolve(2);

        assertFalse(allPromise.ready());

        promise3.resolve(3);

        assertFalse(allPromise.ready());

        promise4.resolve(4);

        assertFalse(allPromise.ready());

        promise5.resolve(5);

        assertFalse(allPromise.ready());

        promise6.resolve(6);

        assertTrue(allPromise.ready());

        assertEquals(Tuples.of(1, 2, 3, 4, 5, 6), allPromise.value().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedFor7Promises() {
        final var promise1 = Promises.<Integer>give();
        final var promise2 = Promises.<Integer>give();
        final var promise3 = Promises.<Integer>give();
        final var promise4 = Promises.<Integer>give();
        final var promise5 = Promises.<Integer>give();
        final var promise6 = Promises.<Integer>give();
        final var promise7 = Promises.<Integer>give();
        final var allPromise = Promises.all(promise1, promise2, promise3, promise4, promise5, promise6, promise7);

        assertFalse(allPromise.ready());

        promise1.resolve(1);

        assertFalse(allPromise.ready());

        promise2.resolve(2);

        assertFalse(allPromise.ready());

        promise3.resolve(3);

        assertFalse(allPromise.ready());

        promise4.resolve(4);

        assertFalse(allPromise.ready());

        promise5.resolve(5);

        assertFalse(allPromise.ready());

        promise6.resolve(6);

        assertFalse(allPromise.ready());

        promise7.resolve(7);

        assertTrue(allPromise.ready());

        assertEquals(Tuples.of(1, 2, 3, 4, 5, 6, 7), allPromise.value().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedFor8Promises() {
        final var promise1 = Promises.<Integer>give();
        final var promise2 = Promises.<Integer>give();
        final var promise3 = Promises.<Integer>give();
        final var promise4 = Promises.<Integer>give();
        final var promise5 = Promises.<Integer>give();
        final var promise6 = Promises.<Integer>give();
        final var promise7 = Promises.<Integer>give();
        final var promise8 = Promises.<Integer>give();
        final var allPromise = Promises.all(promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8);

        assertFalse(allPromise.ready());

        promise1.resolve(1);

        assertFalse(allPromise.ready());

        promise2.resolve(2);

        assertFalse(allPromise.ready());

        promise3.resolve(3);

        assertFalse(allPromise.ready());

        promise4.resolve(4);

        assertFalse(allPromise.ready());

        promise5.resolve(5);

        assertFalse(allPromise.ready());

        promise6.resolve(6);

        assertFalse(allPromise.ready());

        promise7.resolve(7);

        assertFalse(allPromise.ready());

        promise8.resolve(8);

        assertTrue(allPromise.ready());

        assertEquals(Tuples.of(1, 2, 3, 4, 5, 6, 7, 8), allPromise.value().get());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedFor9Promises() {
        final var promise1 = Promises.<Integer>give();
        final var promise2 = Promises.<Integer>give();
        final var promise3 = Promises.<Integer>give();
        final var promise4 = Promises.<Integer>give();
        final var promise5 = Promises.<Integer>give();
        final var promise6 = Promises.<Integer>give();
        final var promise7 = Promises.<Integer>give();
        final var promise8 = Promises.<Integer>give();
        final var promise9 = Promises.<Integer>give();
        final var  allPromise = Promises.all(promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8, promise9);

        assertFalse(allPromise.ready());

        promise1.resolve(1);

        assertFalse(allPromise.ready());

        promise2.resolve(2);

        assertFalse(allPromise.ready());

        promise3.resolve(3);

        assertFalse(allPromise.ready());

        promise4.resolve(4);

        assertFalse(allPromise.ready());

        promise5.resolve(5);

        assertFalse(allPromise.ready());

        promise6.resolve(6);

        assertFalse(allPromise.ready());

        promise7.resolve(7);

        assertFalse(allPromise.ready());

        promise8.resolve(8);

        assertFalse(allPromise.ready());

        promise9.resolve(9);

        assertTrue(allPromise.ready());

        assertEquals(Tuples.of(1, 2, 3, 4, 5, 6, 7, 8, 9), allPromise.value().get());
    }

    @Test
    void subsequentResolutionsAreIgnoreByAll() {
        final var promise1 = Promises.<Integer>give();
        final var promise2 = Promises.<Integer>give();
        final var allPromise = Promises.all(promise1, promise2);

        assertFalse(allPromise.ready());

        promise1.resolve(1);
        promise2.resolve(2);

        assertTrue(allPromise.ready());

        promise1.resolve(3);
        promise2.resolve(4);

        assertEquals(Tuples.of(1, 2), allPromise.value().get());
    }

    @Test
    void syncWaitIsWaitingForResolution() {
        final var promise = Promises.<Integer>give();

        assertFalse(promise.ready());

        executor.execute(() -> {safeSleep(20); promise.resolve(1);});

        promise.syncWait();

        assertTrue(promise.ready());

        assertEquals(1, promise.value().get());
    }

    @Test
    void syncWaitDoesNotWaitForAlreadyResolved() {
        final var promise = Promises.<Integer>give();

        assertFalse(promise.ready());

        promise.resolve(1);

        promise.syncWait();

        assertTrue(promise.ready());

        assertEquals(1, promise.value().get());
    }

    private static void safeSleep(final long delay) {
        try {
            Thread.sleep(delay);
        } catch (final InterruptedException e) {
            //Ignore
        }
    }
}

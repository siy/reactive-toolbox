package org.reactivetoolbox.core.async;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Tuples;
import org.reactivetoolbox.core.functional.Tuples.*;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;


public class PromisesTest {
    private final Executor executor = Executors.newSingleThreadExecutor();
    @Test
    void multipleAssignmentsAreIgnored() {
        final Promise<ErrorHolder, Integer> promise = Promises.create();

        promise.resolve(Either.right(1));
        promise.resolve(Either.right(2));
        promise.resolve(Either.right(3));
        promise.resolve(Either.right(4));

        assertTrue(promise.ready());
        assertTrue(promise.value().get().isRight());

        promise
            .then(val -> assertEquals(1, val))
            .otherwise(val -> fail());
    }

    @Test
    void thenActionsAreExecuted() {
        final Promise<ErrorHolder, Integer> promise = Promises.create();
        final AtomicInteger holder = new AtomicInteger(-1);

        promise.then(value -> holder.set(value));
        promise.resolve(Either.right(1));

        assertEquals(1, holder.get());
    }

    @Test
    void thenActionsAreExecutedEvenIfAddedAfterPromiseResolution() {
        final Promise<ErrorHolder, Integer> promise = Promises.create();
        final AtomicInteger holder = new AtomicInteger(-1);

        promise.resolve(Either.right(1));
        promise.then(value -> holder.set(value));

        assertEquals(1, holder.get());
    }

    @Test
    void otherwiseActionsAreExecuted() {
        final Promise<ErrorHolder, Integer> promise = Promises.create();
        final AtomicInteger holder = new AtomicInteger(-1);

        promise.otherwise(value -> holder.set(value.code()));
        promise.resolve(Either.left(ErrorHolder.of(1)));

        assertEquals(1, holder.get());
    }

    @Test
    void otherwiseActionsAreExecutedEvenIfAddedAfterPromiseResolution() {
        final Promise<ErrorHolder, Integer> promise = Promises.create();
        final AtomicInteger holder = new AtomicInteger(-1);

        promise.resolve(Either.left(ErrorHolder.of(1)));
        promise.otherwise(value -> holder.set(value.code()));

        assertEquals(1, holder.get());
    }

    @Test
    void anyResolvedPromiseResolvesResult1() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Integer> anyPromise = Promises.any(promise1, promise2);

        assertFalse(anyPromise.ready());

        promise1.resolve(Either.right(1));

        assertTrue(anyPromise.ready());

        anyPromise
            .then(val -> assertEquals(1, val))
            .otherwise(val -> fail());
    }

    @Test
    void anyResolvedPromiseResolvesResult2() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Integer> anyPromise = Promises.any(promise1, promise2);

        assertFalse(anyPromise.ready());

        promise2.resolve(Either.left(ErrorHolder.of(1)));

        assertTrue(anyPromise.ready());

        anyPromise
            .otherwise(val -> assertEquals(ErrorHolder.of(1), val))
            .then(val -> fail());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved1() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Tuple1<Integer>> allPromise = Promises.all(promise1);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.right(1));

        assertTrue(allPromise.ready());

        allPromise
            .then(val -> assertEquals(Tuples.of(1), val))
            .otherwise(val -> fail());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft1() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Tuple1<Integer>> allPromise = Promises.all(promise1);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.left(ErrorHolder.of(1)));

        assertTrue(allPromise.ready());

        allPromise
            .otherwise(val -> assertEquals(ErrorHolder.of(1), val))
            .then(val -> fail());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved2() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Tuple2<Integer, Integer>> allPromise = Promises.all(promise1, promise2);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.right(1));

        assertFalse(allPromise.ready());

        promise2.resolve(Either.right(2));

        assertTrue(allPromise.ready());

        allPromise
            .then(val -> assertEquals(Tuples.of(1, 2), val))
            .otherwise(val -> fail());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft2() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Tuple2<Integer, Integer>> allPromise = Promises.all(promise1, promise2);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.left(ErrorHolder.of(1)));

        assertTrue(allPromise.ready());

        allPromise
            .otherwise(val -> assertEquals(ErrorHolder.of(1), val))
            .then(val -> fail());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved3() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Integer> promise3 = Promises.create();
        final Promise<ErrorHolder, Tuple3<Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.right(1));

        assertFalse(allPromise.ready());

        promise2.resolve(Either.right(2));

        assertFalse(allPromise.ready());

        promise3.resolve(Either.right(3));

        assertTrue(allPromise.ready());

        allPromise
            .then(val -> assertEquals(Tuples.of(1, 2, 3), val))
            .otherwise(val -> fail());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft3() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Integer> promise3 = Promises.create();
        final Promise<ErrorHolder, Tuple3<Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3);

        assertFalse(allPromise.ready());

        promise2.resolve(Either.left(ErrorHolder.of(1)));

        assertTrue(allPromise.ready());

        allPromise
            .otherwise(val -> assertEquals(ErrorHolder.of(1), val))
            .then(val -> fail());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved4() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Integer> promise3 = Promises.create();
        final Promise<ErrorHolder, Integer> promise4 = Promises.create();
        final Promise<ErrorHolder, Tuple4<Integer, Integer, Integer, Integer>>
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

        allPromise
            .then(val -> assertEquals(Tuples.of(1, 2, 3, 4), val))
            .otherwise(val -> fail());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft4() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Integer> promise3 = Promises.create();
        final Promise<ErrorHolder, Integer> promise4 = Promises.create();
        final Promise<ErrorHolder, Tuple4<Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4);

        assertFalse(allPromise.ready());

        promise3.resolve(Either.left(ErrorHolder.of(1)));

        assertTrue(allPromise.ready());

        allPromise
            .otherwise(val -> assertEquals(ErrorHolder.of(1), val))
            .then(val -> fail());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved5() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Integer> promise3 = Promises.create();
        final Promise<ErrorHolder, Integer> promise4 = Promises.create();
        final Promise<ErrorHolder, Integer> promise5 = Promises.create();
        final Promise<ErrorHolder, Tuple5<Integer, Integer, Integer, Integer, Integer>>
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

        allPromise
            .then(val -> assertEquals(Tuples.of(1, 2, 3, 4, 5), val))
            .otherwise(val -> fail());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft5() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Integer> promise3 = Promises.create();
        final Promise<ErrorHolder, Integer> promise4 = Promises.create();
        final Promise<ErrorHolder, Integer> promise5 = Promises.create();
        final Promise<ErrorHolder, Tuple5<Integer, Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4, promise5);

        assertFalse(allPromise.ready());

        promise4.resolve(Either.left(ErrorHolder.of(1)));

        assertTrue(allPromise.ready());

        allPromise
            .otherwise(val -> assertEquals(ErrorHolder.of(1), val))
            .then(val -> fail());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved6() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Integer> promise3 = Promises.create();
        final Promise<ErrorHolder, Integer> promise4 = Promises.create();
        final Promise<ErrorHolder, Integer> promise5 = Promises.create();
        final Promise<ErrorHolder, Integer> promise6 = Promises.create();
        final Promise<ErrorHolder, Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>>
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

        allPromise
            .then(val -> assertEquals(Tuples.of(1, 2, 3, 4, 5, 6), val))
            .otherwise(val -> fail());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft6() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Integer> promise3 = Promises.create();
        final Promise<ErrorHolder, Integer> promise4 = Promises.create();
        final Promise<ErrorHolder, Integer> promise5 = Promises.create();
        final Promise<ErrorHolder, Integer> promise6 = Promises.create();
        final Promise<ErrorHolder, Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4, promise5, promise6);

        assertFalse(allPromise.ready());

        promise5.resolve(Either.left(ErrorHolder.of(1)));

        assertTrue(allPromise.ready());

        allPromise
            .otherwise(val -> assertEquals(ErrorHolder.of(1), val))
            .then(val -> fail());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved7() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Integer> promise3 = Promises.create();
        final Promise<ErrorHolder, Integer> promise4 = Promises.create();
        final Promise<ErrorHolder, Integer> promise5 = Promises.create();
        final Promise<ErrorHolder, Integer> promise6 = Promises.create();
        final Promise<ErrorHolder, Integer> promise7 = Promises.create();
        final Promise<ErrorHolder, Tuple7<Integer, Integer, Integer, Integer, Integer, Integer, Integer>>
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

        allPromise
            .then(val -> assertEquals(Tuples.of(1, 2, 3, 4, 5, 6, 7), val))
            .otherwise(val -> fail());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft7() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Integer> promise3 = Promises.create();
        final Promise<ErrorHolder, Integer> promise4 = Promises.create();
        final Promise<ErrorHolder, Integer> promise5 = Promises.create();
        final Promise<ErrorHolder, Integer> promise6 = Promises.create();
        final Promise<ErrorHolder, Integer> promise7 = Promises.create();
        final Promise<ErrorHolder, Tuple7<Integer, Integer, Integer, Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4, promise5, promise6, promise7);

        assertFalse(allPromise.ready());

        promise6.resolve(Either.left(ErrorHolder.of(1)));

        assertTrue(allPromise.ready());

        allPromise
            .otherwise(val -> assertEquals(ErrorHolder.of(1), val))
            .then(val -> fail());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved8() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Integer> promise3 = Promises.create();
        final Promise<ErrorHolder, Integer> promise4 = Promises.create();
        final Promise<ErrorHolder, Integer> promise5 = Promises.create();
        final Promise<ErrorHolder, Integer> promise6 = Promises.create();
        final Promise<ErrorHolder, Integer> promise7 = Promises.create();
        final Promise<ErrorHolder, Integer> promise8 = Promises.create();
        final Promise<ErrorHolder, Tuple8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>
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

        allPromise
            .then(val -> assertEquals(Tuples.of(1, 2, 3, 4, 5, 6, 7, 8), val))
            .otherwise(val -> fail());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft8() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Integer> promise3 = Promises.create();
        final Promise<ErrorHolder, Integer> promise4 = Promises.create();
        final Promise<ErrorHolder, Integer> promise5 = Promises.create();
        final Promise<ErrorHolder, Integer> promise6 = Promises.create();
        final Promise<ErrorHolder, Integer> promise7 = Promises.create();
        final Promise<ErrorHolder, Integer> promise8 = Promises.create();
        final Promise<ErrorHolder, Tuple8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8);

        assertFalse(allPromise.ready());

        promise7.resolve(Either.left(ErrorHolder.of(1)));

        assertTrue(allPromise.ready());

        allPromise
            .otherwise(val -> assertEquals(ErrorHolder.of(1), val))
            .then(val -> fail());
    }

    @Test
    void allResolvesWhenAllPromisesAreResolved9() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Integer> promise3 = Promises.create();
        final Promise<ErrorHolder, Integer> promise4 = Promises.create();
        final Promise<ErrorHolder, Integer> promise5 = Promises.create();
        final Promise<ErrorHolder, Integer> promise6 = Promises.create();
        final Promise<ErrorHolder, Integer> promise7 = Promises.create();
        final Promise<ErrorHolder, Integer> promise8 = Promises.create();
        final Promise<ErrorHolder, Integer> promise9 = Promises.create();
        final Promise<ErrorHolder, Tuple9<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>
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

        allPromise
            .then(val -> assertEquals(Tuples.of(1, 2, 3, 4, 5, 6, 7, 8, 9), val))
            .otherwise(val -> fail());
    }

    @Test
    void allResolvesWhenAnyPromiseResolvedWithLeft9() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Integer> promise3 = Promises.create();
        final Promise<ErrorHolder, Integer> promise4 = Promises.create();
        final Promise<ErrorHolder, Integer> promise5 = Promises.create();
        final Promise<ErrorHolder, Integer> promise6 = Promises.create();
        final Promise<ErrorHolder, Integer> promise7 = Promises.create();
        final Promise<ErrorHolder, Integer> promise8 = Promises.create();
        final Promise<ErrorHolder, Integer> promise9 = Promises.create();
        final Promise<ErrorHolder, Tuple9<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>
                allPromise =
                Promises.all(promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8, promise9);

        assertFalse(allPromise.ready());

        promise8.resolve(Either.left(ErrorHolder.of(1)));

        assertTrue(allPromise.ready());

        allPromise
            .otherwise(val -> assertEquals(ErrorHolder.of(1), val))
            .then(val -> fail());
    }

    @Test
    void subsequentResolutionsAreIgnoreByAll() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Tuple2<Integer, Integer>> allPromise = Promises.all(promise1, promise2);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.right(1));
        promise2.resolve(Either.right(2));

        assertTrue(allPromise.ready());

        promise1.resolve(Either.right(3));
        promise2.resolve(Either.right(4));

        allPromise
            .then(val -> assertEquals(Tuples.of(1, 2), val))
            .otherwise(val -> fail());
    }

    @Test
    void subsequentSuccessResolutionsAreIgnoreIfErrorAlreadyResolvedByAll() {
        final Promise<ErrorHolder, Integer> promise1 = Promises.create();
        final Promise<ErrorHolder, Integer> promise2 = Promises.create();
        final Promise<ErrorHolder, Tuple2<Integer, Integer>> allPromise = Promises.all(promise1, promise2);

        assertFalse(allPromise.ready());

        promise1.resolve(Either.left(ErrorHolder.of(1)));

        assertTrue(allPromise.ready());

        promise1.resolve(Either.right(3));
        promise2.resolve(Either.right(4));

        allPromise
            .otherwise(val -> assertEquals(ErrorHolder.of(1), val))
            .then(val -> fail());
    }

    @Test
    void syncWaitIsWaitingForResolution() {
        final Promise<ErrorHolder, Integer> promise = Promises.create();

        assertFalse(promise.ready());

        executor.execute(() -> {safeSleep(20); promise.resolve(Either.right(1));});

        promise.syncWait();

        assertTrue(promise.ready());

        promise
            .then(val -> assertEquals(1, val))
            .otherwise(val -> fail());
    }

    @Test
    void syncWaitDoesNotWaitForAlreadyResolved() {
        final Promise<ErrorHolder, Integer> promise = Promises.create();

        assertFalse(promise.ready());

        promise.resolve(Either.right(1));

        promise.syncWait();

        assertTrue(promise.ready());

        promise
            .then(val -> assertEquals(1, val))
            .otherwise(val -> fail());
    }

    private static void safeSleep(final long delay) {
        try {
            Thread.sleep(delay);
        } catch (final InterruptedException e) {
            //Ignore
        }
    }

    public static class ErrorHolder implements BaseError {
        private final Integer code;

        private ErrorHolder(final int i) {
            code = i;
        }

        public static ErrorHolder of(final int i) {
            return new ErrorHolder(i);
        }

        public Integer code() {
            return code;
        }

        @Override
        public int hashCode() {
            return Objects.hash(code);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ErrorHolder other = (ErrorHolder) obj;
            return Objects.equals(code, other.code);
        }
    }
}

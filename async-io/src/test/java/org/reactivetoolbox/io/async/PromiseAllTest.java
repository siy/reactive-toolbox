package org.reactivetoolbox.io.async;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.lang.Tuple.Tuple1;
import org.reactivetoolbox.core.lang.Tuple.Tuple2;
import org.reactivetoolbox.core.lang.Tuple.Tuple3;
import org.reactivetoolbox.core.lang.Tuple.Tuple4;
import org.reactivetoolbox.core.lang.Tuple.Tuple5;
import org.reactivetoolbox.core.lang.Tuple.Tuple6;
import org.reactivetoolbox.core.lang.Tuple.Tuple7;
import org.reactivetoolbox.core.lang.Tuple.Tuple8;
import org.reactivetoolbox.core.lang.Tuple.Tuple9;
import org.reactivetoolbox.core.lang.functional.Result;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.reactivetoolbox.core.Errors.CANCELLED;
import static org.reactivetoolbox.core.Errors.TIMEOUT;
import static org.reactivetoolbox.core.lang.Tuple.tuple;
import static org.reactivetoolbox.core.lang.functional.Option.option;
import static org.reactivetoolbox.core.lang.functional.Result.ok;
import static org.reactivetoolbox.io.async.Promises.all;
import static org.reactivetoolbox.io.scheduler.Timeout.timeout;

class PromiseAllTest {
    @BeforeClass
    public static void setupExceptionConsumer() {
        Promise.exceptionConsumer(throwable -> System.out.println("Exception inside Promise: " + throwable));
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedFor1Promise() {
        final var value = new AtomicReference<Result<Tuple1<Integer>>>();
        final var promise1 = Promise.<Integer>promise();

        final var allPromise =
                all(promise1)
                        .onResult(value::set)
                        .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise1.resolve(ok(1));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onSuccess(v -> assertEquals(tuple(1), v))
                                             .onFailure(f -> fail()));
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedFor2Promises() {
        final var value = new AtomicReference<Result<Tuple2<Integer, Integer>>>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();

        final var allPromise =
        all(promise1, promise2)
                .onResult(value::set)
                .when(timeout(100).millis(), TIMEOUT::asResult);

        promise1.resolve(ok(1));
        promise2.resolve(ok(2));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onSuccess(v -> assertEquals(tuple(1, 2), v))
                                             .onFailure(f -> fail()));
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedFor3Promises() {
        final var value = new AtomicReference<Result<Tuple3<Integer, Integer, Integer>>>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        final var promise3 = Promise.<Integer>promise();

        final var allPromise =
        all(promise1, promise2, promise3)
                .onResult(value::set)
                .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise1.resolve(ok(1));
        promise2.resolve(ok(2));
        promise3.resolve(ok(3));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onSuccess(v -> assertEquals(tuple(1, 2, 3), v))
                                             .onFailure(f -> fail()));
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedFor4Promises() {
        final var value = new AtomicReference<Result<Tuple4<Integer, Integer, Integer, Integer>>>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        final var promise3 = Promise.<Integer>promise();
        final var promise4 = Promise.<Integer>promise();

        final var allPromise =
        all(promise1, promise2, promise3, promise4)
                .onResult(value::set)
                .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise1.resolve(ok(1));
        promise2.resolve(ok(2));
        promise3.resolve(ok(3));
        promise4.resolve(ok(4));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onSuccess(v -> assertEquals(tuple(1, 2, 3, 4), v))
                                             .onFailure(f -> fail()));
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedFor5Promises() {
        final var value = new AtomicReference<Result<Tuple5<Integer, Integer, Integer, Integer, Integer>>>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        final var promise3 = Promise.<Integer>promise();
        final var promise4 = Promise.<Integer>promise();
        final var promise5 = Promise.<Integer>promise();

        final var allPromise =
        all(promise1, promise2, promise3, promise4, promise5)
                .onResult(value::set)
                .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise1.resolve(ok(1));
        promise2.resolve(ok(2));
        promise3.resolve(ok(3));
        promise4.resolve(ok(4));
        promise5.resolve(ok(5));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onSuccess(v -> assertEquals(tuple(1, 2, 3, 4, 5), v))
                                             .onFailure(f -> fail()));
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedFor6Promises() {
        final var value = new AtomicReference<Result<Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>>>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        final var promise3 = Promise.<Integer>promise();
        final var promise4 = Promise.<Integer>promise();
        final var promise5 = Promise.<Integer>promise();
        final var promise6 = Promise.<Integer>promise();

        final var allPromise =
        all(promise1, promise2, promise3, promise4, promise5, promise6)
                .onResult(value::set)
                .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise1.resolve(ok(1));
        promise2.resolve(ok(2));
        promise3.resolve(ok(3));
        promise4.resolve(ok(4));
        promise5.resolve(ok(5));
        promise6.resolve(ok(6));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onSuccess(v -> assertEquals(tuple(1, 2, 3, 4, 5, 6), v))
                                             .onFailure(f -> fail()));
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedFor7Promises() {
        final var value = new AtomicReference<Result<Tuple7<Integer, Integer, Integer, Integer, Integer, Integer, Integer>>>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        final var promise3 = Promise.<Integer>promise();
        final var promise4 = Promise.<Integer>promise();
        final var promise5 = Promise.<Integer>promise();
        final var promise6 = Promise.<Integer>promise();
        final var promise7 = Promise.<Integer>promise();

        final var allPromise =
        all(promise1, promise2, promise3, promise4, promise5, promise6, promise7)
                .onResult(value::set)
                .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise1.resolve(ok(1));
        promise2.resolve(ok(2));
        promise3.resolve(ok(3));
        promise4.resolve(ok(4));
        promise5.resolve(ok(5));
        promise6.resolve(ok(6));
        promise7.resolve(ok(7));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onSuccess(v -> assertEquals(tuple(1, 2, 3, 4, 5, 6, 7), v))
                                             .onFailure(f -> fail()));
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedFor8Promises() {
        final var value = new AtomicReference<Result<Tuple8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        final var promise3 = Promise.<Integer>promise();
        final var promise4 = Promise.<Integer>promise();
        final var promise5 = Promise.<Integer>promise();
        final var promise6 = Promise.<Integer>promise();
        final var promise7 = Promise.<Integer>promise();
        final var promise8 = Promise.<Integer>promise();

        final var allPromise =
        all(promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8)
                .onResult(value::set)
                .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise1.resolve(ok(1));
        promise2.resolve(ok(2));
        promise3.resolve(ok(3));
        promise4.resolve(ok(4));
        promise5.resolve(ok(5));
        promise6.resolve(ok(6));
        promise7.resolve(ok(7));
        promise8.resolve(ok(8));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onSuccess(v -> assertEquals(tuple(1, 2, 3, 4, 5, 6, 7, 8), v))
                                             .onFailure(f -> fail()));
    }

    @Test
    void allResolvesWhenAllPromisesAreResolvedFor9Promises() {
        final var value = new AtomicReference<Result<Tuple9<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        final var promise3 = Promise.<Integer>promise();
        final var promise4 = Promise.<Integer>promise();
        final var promise5 = Promise.<Integer>promise();
        final var promise6 = Promise.<Integer>promise();
        final var promise7 = Promise.<Integer>promise();
        final var promise8 = Promise.<Integer>promise();
        final var promise9 = Promise.<Integer>promise();

        final var allPromise =
        all(promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8, promise9)
                .onResult(value::set)
                .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise1.resolve(ok(1));
        promise2.resolve(ok(2));
        promise3.resolve(ok(3));
        promise4.resolve(ok(4));
        promise5.resolve(ok(5));
        promise6.resolve(ok(6));
        promise7.resolve(ok(7));
        promise8.resolve(ok(8));
        promise9.resolve(ok(9));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onSuccess(v -> assertEquals(tuple(1, 2, 3, 4, 5, 6, 7, 8, 9), v))
                                             .onFailure(f -> fail()));
    }

    @Test
    void anyErrorResolvesToFailureImmediatelyFor1Promise() {
        final var value = new AtomicReference<Result<Tuple1<Integer>>>();
        final var promise1 = Promise.<Integer>promise();

        final var allPromise =
                all(promise1).onResult(value::set)
                             .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise1.resolve(Result.fail(CANCELLED));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onFailure(f -> assertEquals(CANCELLED, f))
                                             .onSuccess(v -> fail()));
    }

    @Test
    void anyErrorResolvesToFailureImmediatelyFor2Promises() {
        final var value = new AtomicReference<Result<Tuple2<Integer, Integer>>>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();

        final var allPromise =
                all(promise1, promise2)
                        .onResult(value::set)
                        .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise2.resolve(Result.fail(CANCELLED));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onFailure(f -> assertEquals(CANCELLED, f))
                                             .onSuccess(v -> fail()));
    }

    @Test
    void anyErrorResolvesToFailureImmediatelyFor3Promises() {
        final var value = new AtomicReference<Result<Tuple3<Integer, Integer, Integer>>>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        final var promise3 = Promise.<Integer>promise();

        final var allPromise =
                all(promise1, promise2, promise3)
                        .onResult(value::set)
                        .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise3.resolve(Result.fail(CANCELLED));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onFailure(f -> assertEquals(CANCELLED, f))
                                             .onSuccess(v -> fail()));
    }

    @Test
    void anyErrorResolvesToFailureImmediatelyFor4Promises() {
        final var value = new AtomicReference<Result<Tuple4<Integer, Integer, Integer, Integer>>>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        final var promise3 = Promise.<Integer>promise();
        final var promise4 = Promise.<Integer>promise();

        final var allPromise =
                all(promise1, promise2, promise3, promise4)
                        .onResult(value::set)
                        .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise4.resolve(Result.fail(CANCELLED));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onFailure(f -> assertEquals(CANCELLED, f))
                                             .onSuccess(v -> fail()));
    }

    @Test
    void anyErrorResolvesToFailureImmediatelyFor5Promises() {
        final var value = new AtomicReference<Result<Tuple5<Integer, Integer, Integer, Integer, Integer>>>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        final var promise3 = Promise.<Integer>promise();
        final var promise4 = Promise.<Integer>promise();
        final var promise5 = Promise.<Integer>promise();

        final var allPromise =
                all(promise1, promise2, promise3, promise4, promise5)
                        .onResult(value::set)
                        .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise5.resolve(Result.fail(CANCELLED));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onFailure(f -> assertEquals(CANCELLED, f))
                                             .onSuccess(v -> fail()));
    }

    @Test
    void anyErrorResolvesToFailureImmediatelyFor6Promises() {
        final var value = new AtomicReference<Result<Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>>>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        final var promise3 = Promise.<Integer>promise();
        final var promise4 = Promise.<Integer>promise();
        final var promise5 = Promise.<Integer>promise();
        final var promise6 = Promise.<Integer>promise();

        final var allPromise =
                all(promise1, promise2, promise3, promise4, promise5, promise6)
                        .onResult(value::set)
                        .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise6.resolve(Result.fail(CANCELLED));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onFailure(f -> assertEquals(CANCELLED, f))
                                             .onSuccess(v -> fail()));
    }

    @Test
    void anyErrorResolvesToFailureImmediatelyFor7Promises() {
        final var value = new AtomicReference<Result<Tuple7<Integer, Integer, Integer, Integer, Integer, Integer, Integer>>>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        final var promise3 = Promise.<Integer>promise();
        final var promise4 = Promise.<Integer>promise();
        final var promise5 = Promise.<Integer>promise();
        final var promise6 = Promise.<Integer>promise();
        final var promise7 = Promise.<Integer>promise();

        final var allPromise =
                all(promise1, promise2, promise3, promise4, promise5, promise6, promise7)
                        .onResult(value::set)
                        .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise7.resolve(Result.fail(CANCELLED));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onFailure(f -> assertEquals(CANCELLED, f))
                                             .onSuccess(v -> fail()));
    }

    @Test
    void anyErrorResolvesToFailureImmediatelyFor8Promises() {
        final var value = new AtomicReference<Result<Tuple8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        final var promise3 = Promise.<Integer>promise();
        final var promise4 = Promise.<Integer>promise();
        final var promise5 = Promise.<Integer>promise();
        final var promise6 = Promise.<Integer>promise();
        final var promise7 = Promise.<Integer>promise();
        final var promise8 = Promise.<Integer>promise();

        final var allPromise =
                all(promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8)
                        .onResult(value::set)
                        .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise8.resolve(Result.fail(CANCELLED));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onFailure(f -> assertEquals(CANCELLED, f))
                                             .onSuccess(v -> fail()));
    }

    @Test
    void anyErrorResolvesToFailureImmediatelyFor9Promises() {
        final var value = new AtomicReference<Result<Tuple9<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        final var promise3 = Promise.<Integer>promise();
        final var promise4 = Promise.<Integer>promise();
        final var promise5 = Promise.<Integer>promise();
        final var promise6 = Promise.<Integer>promise();
        final var promise7 = Promise.<Integer>promise();
        final var promise8 = Promise.<Integer>promise();
        final var promise9 = Promise.<Integer>promise();

        final var allPromise =
                all(promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8, promise9)
                        .onResult(value::set)
                        .when(timeout(100).millis(), Result.fail(TIMEOUT));

        promise9.resolve(Result.fail(CANCELLED));

        allPromise.syncWait();

        option(value.get())
                .whenEmpty(() -> fail("Value is empty"))
                .whenPresent(result -> result.onFailure(f -> assertEquals(CANCELLED, f))
                                             .onSuccess(v -> fail()));
    }
}
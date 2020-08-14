package org.reactivetoolbox.io.async;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.reactivetoolbox.core.Errors;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.async.impl.PromiseImpl;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.reactivetoolbox.core.Errors.TIMEOUT;
import static org.reactivetoolbox.core.lang.functional.Result.ok;
import static org.reactivetoolbox.io.scheduler.Timeout.timeout;

class PromiseTest {
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Test
    void checkInstanceSize() {
        System.out.println(ClassLayout.parseClass(PromiseImpl.class).toPrintable());
        System.out.println(ClassLayout.parseClass(CompletableFuture.class).toPrintable());
    }

    @Test
    void multipleResolutionsAreIgnored() {
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>promise().onSuccess(holder::set);

        promise.syncOk(1);
        promise.syncOk(2);
        promise.syncOk(3);
        promise.syncOk(4);

        assertEquals(1, holder.get());
    }

    @Test
    void fulfilledPromiseIsAlreadyResolved() {
        final var holder = new AtomicInteger(-1);
        Promise.readyOk(123).onSuccess(holder::set);

        assertEquals(123, holder.get());
    }

    @Test
    void thenActionsAreExecuted() {
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>promise().onSuccess(holder::set);

        promise.syncOk(1);

        assertEquals(1, holder.get());
    }

    @Test
    void thenActionsAreExecutedEvenIfAddedAfterPromiseResolution() {
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>promise();

        promise.syncOk(1);
        promise.onSuccess(holder::set);

        assertEquals(1, holder.get());
    }

    @Test
    void syncMapTransformsValue() {
        final var holder = new AtomicReference<String>();
        final var promise = Promise.<Integer>promise();

        promise.syncMap(Objects::toString)
               .onSuccess(holder::set);

        promise.syncOk(1234);

        assertEquals("1234", holder.get());
    }

    @Test
    void mapTransformsValue() {
        final var holder = new AtomicReference<String>();
        final var promise = Promise.<Integer>promise();
        final var waitable = Promise.<String>waitablePromise();

        final var mappedPromise = promise.map(Objects::toString)
                                         .onSuccess(holder::set)
                                         .chainTo(waitable);

        promise.ok(1234);

        waitable.syncWait();

        assertEquals("1234", holder.get());
    }

    @Test
    void syncMapResultTransformsValue() {
        final var holder = new AtomicReference<String>();
        final var promise = Promise.<Integer>promise();

        promise.syncMapResult((Integer o) -> Result.ok(Objects.toString(o)))
               .onSuccess(holder::set);

        promise.syncOk(1234);

        assertEquals("1234", holder.get());
    }

    @Test
    void asyncMapResultTransformsValue() {
        final var holder = new AtomicReference<String>();
        final var promise = Promise.<Integer>promise();

        final var mappedPromise = promise.mapResult((Integer o) -> Result.ok(Objects.toString(o)))
                                         .onSuccess(holder::set);

        promise.syncOk(1234);

        Promise.<String>waitablePromise(mappedPromise::syncChainTo)
                .syncWait();

        assertEquals("1234", holder.get());
    }

    @Test
    void promiseCanBeResolvedAsynchronouslyWithSuccess() {
        final var currentTid = Thread.currentThread().getId();
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>waitablePromise()
                .onSuccess(val -> assertNotEquals(currentTid, Thread.currentThread().getId()))
                .onSuccess(holder::set);

        promise.ok(1).syncWait(timeout(1).seconds());

        assertEquals(1, holder.get());
    }

    @Test
    void promiseCanBeResolvedAsynchronouslyWithFailure() {
        final var currentTid = Thread.currentThread().getId();
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>waitablePromise()
                .onFailure(f -> assertNotEquals(currentTid, Thread.currentThread().getId()))
                .onFailure(f -> holder.set(1));

        promise.fail(TIMEOUT).syncWait(timeout(1).seconds());

        assertEquals(1, holder.get());
    }

    @Test
    void syncWaitIsWaitingForResolution() {
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>waitablePromise().onSuccess(holder::set);

        executor.execute(() -> {
            safeSleep(20);
            promise.syncOk(1);
        });

        promise.syncWait();

        assertEquals(1, holder.get());
    }

    @Test
    void syncWaitDoesNotWaitForAlreadyResolved() {
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>promise().onSuccess(holder::set);

        assertEquals(-1, holder.get());

        promise.syncOk(1);

        promise.syncWait();

        assertEquals(1, holder.get());
    }

    @Test
    void syncWaitWithTimeoutIsWaitingForResolution() {
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>waitablePromise().onSuccess(holder::set);

        assertEquals(-1, holder.get());

        executor.execute(() -> {
            safeSleep(20);
            promise.syncOk(1);
        });

        assertEquals(-1, holder.get());

        promise.syncWait(timeout(100).millis());

        assertEquals(1, holder.get());
    }

    @Test
    void syncWaitWithTimeoutIsWaitingForTimeout() {
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>promise().onSuccess(holder::set);

        assertEquals(-1, holder.get());

        executor.execute(() -> {
            safeSleep(200);
            promise.syncOk(1);
        });

        promise.syncWait(timeout(10).millis());

        assertEquals(-1, holder.get());
    }

    @Test
    void promiseIsResolvedWhenTimeoutExpires() {
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>waitablePromise().onSuccess(holder::set)
                                                              .async(timeout(100).millis(), p -> p.syncOk(123));

        assertEquals(-1, holder.get());

        promise.syncWait();

        assertEquals(123, holder.get());
    }

    @Test
    void taskCanBeExecuted() {
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>waitablePromise()
                .onSuccess(holder::set)
                .when(timeout(100).millis(), ok(123));

        assertEquals(-1, holder.get());

        promise.async(p -> p.syncOk(345)).syncWait();

        assertEquals(345, holder.get());
    }

    @Test
    void anyResolvedPromiseResolvesResultForFirstPromise() {
        final var holder = new AtomicInteger(-1);
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();

        Promise.any(promise1, promise2).onSuccess(holder::set);

        assertEquals(-1, holder.get());

        promise1.syncOk(1);

        assertEquals(1, holder.get());
    }

    @Test
    void anyResolvedPromiseResolvesResultForSecondPromise() {
        final var holder = new AtomicInteger(-1);
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        Promise.any(promise1, promise2).onSuccess(holder::set);

        assertEquals(-1, holder.get());

        promise2.syncOk(1);

        assertEquals(1, holder.get());
    }

    @Test
    void onlySuccessResolvesAnySuccess() {
        final var holder = new AtomicInteger(-1);
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        Promise.anySuccess(promise1, promise2).onSuccess(holder::set);

        assertEquals(-1, holder.get());

        promise1.syncFail(TIMEOUT);

        assertEquals(-1, holder.get());

        promise2.syncOk(1);

        assertEquals(1, holder.get());
    }

    @Test
    void allFailuresResolvesAnySuccessToFailure() {
        final var holder = new AtomicInteger(-1);
        final var errHolder = new AtomicReference<>();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        Promise.anySuccess(promise1, promise2).onSuccess(holder::set).onFailure(errHolder::set);

        assertEquals(-1, holder.get());

        promise1.syncFail(TIMEOUT);

        assertEquals(-1, holder.get());

        promise2.syncFail(TIMEOUT);

        assertEquals(-1, holder.get());

        assertEquals(Errors.CANCELLED, errHolder.get());
    }

    @Test
    void chainMapResolvesToFailureIfBasePromiseIsResolvedToFailure() {
        final var holder = new AtomicInteger(-1);
        final var stringHolder = new AtomicReference<String>();
        final var promise = Promise.<Integer>promise().onSuccess(s -> holder.set(1))
                                                      .onFailure(f -> holder.set(2));

        final var chain = promise.syncFlatMap(val -> Promise.readyOk(val.toString()))
                                 .onSuccess(s -> stringHolder.set("success"))
                                 .onFailure(f -> stringHolder.set("failure"));

        promise.syncFail(TIMEOUT);

        assertEquals(2, holder.get());
        assertEquals("failure", stringHolder.get());
    }

    @Test
    void chainMapResolvesToSuccessIfBasePromiseIsResolvedToSuccess() {
        final var holder = new AtomicInteger(-1);
        final var stringHolder = new AtomicReference<String>();
        final var promise = Promise.<Integer>promise().onSuccess(s -> holder.set(1))
                                                      .onFailure(f -> holder.set(2));

        final var chain = promise.syncFlatMap(val -> Promise.readyOk(val.toString()))
                                 .onSuccess(s -> stringHolder.set("success"))
                                 .onFailure(f -> stringHolder.set("failure"));

        promise.syncOk(123);

        assertEquals(1, holder.get());
        assertEquals("success", stringHolder.get());
    }

    //TODO: create separate set of tests for I/O
    @Test
    void ioTaskCanBeSubmitted() {
        final var promise = Promise.<String>promise()
                .async((p, io) -> io.nop(Promise.promise())
                                    .thenDo(() -> p.syncOk("success")));

        promise.syncWait(timeout(1).seconds())
               .onSuccess(success -> assertEquals("success", success))
               .onFailure(failure -> fail());

    }

    private static void safeSleep(final long delay) {
        try {
            Thread.sleep(delay);
        } catch (final InterruptedException e) {
            //Ignore
        }
    }
}

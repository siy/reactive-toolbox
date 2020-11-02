/*
 * Copyright (c) 2020 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.reactivetoolbox.io.async;

import org.junit.jupiter.api.Assertions;
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
import static org.reactivetoolbox.core.lang.functional.Option.option;
import static org.reactivetoolbox.core.lang.functional.Result.ok;
import static org.reactivetoolbox.io.scheduler.Timeout.timeout;

class PromiseTest {
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Test
    void checkInstanceSize() {
        assertEquals(ClassLayout.parseClass(CompletableFuture.class).instanceSize(),
                     ClassLayout.parseClass(PromiseImpl.class).instanceSize());
    }

    @Test
    void multipleResolutionsAreIgnored() {
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>promise().onSuccess(holder::set);

        promise.ok(1);
        promise.ok(2);
        promise.ok(3);
        promise.ok(4);

        promise.syncWait();

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

        promise.ok(1).syncWait();

        assertEquals(1, holder.get());
    }

    @Test
    void thenActionsAreExecutedEvenIfAddedAfterPromiseResolution() {
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>promise();

        promise.ok(1).syncWait();
        promise.onSuccess(holder::set);

        assertEquals(1, holder.get());
    }

    @Test
    void syncMapTransformsValue() {
        final var holder = new AtomicReference<String>();
        final var promise = Promise.<Integer>promise();

        promise.syncMap(Objects::toString, null)
               .onSuccess(holder::set);

        promise.syncOk(1234, null);

        assertEquals("1234", holder.get());
    }

    @Test
    void mapTransformsValue() {
        final var holder = new AtomicReference<String>();
        final var promise = Promise.<Integer>promise();

        final var mappedPromise = promise.map(Objects::toString)
                                         .onSuccess(holder::set);

        promise.ok(1234);

        mappedPromise.syncWait();

        assertEquals("1234", holder.get());
    }

    @Test
    void syncMapResultTransformsValue() {
        final var holder = new AtomicReference<String>();
        final var promise = Promise.<Integer>promise();

        promise.syncMapResult((Integer o) -> Result.ok(Objects.toString(o)), null)
               .onSuccess(holder::set);

        promise.syncOk(1234, null);

        assertEquals("1234", holder.get());
    }

    @Test
    void asyncMapResultTransformsValue() {
        final var holder = new AtomicReference<String>();
        final var threshold = Promise.<String>promise();
        final var promise = Promise.<Integer>promise();

        promise.mapResult((Integer o) -> Result.ok(Objects.toString(o)))
               .onSuccess(holder::set)
               .chainTo(threshold);

        promise.ok(1234);

        threshold.syncWait();

        assertEquals("1234", holder.get());
    }

    @Test
    void promiseCanBeResolvedAsynchronouslyWithSuccess() {
        final var currentTid = Thread.currentThread().getId();
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>promise()
                .onSuccess(val -> assertNotEquals(currentTid, Thread.currentThread().getId()))
                .onSuccess(holder::set);

        promise.ok(1).syncWait(timeout(1).seconds());

        assertEquals(1, holder.get());
    }

    @Test
    void promiseCanBeResolvedAsynchronouslyWithFailure() {
        final var currentTid = Thread.currentThread().getId();
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>promise()
                .onFailure(f -> assertNotEquals(currentTid, Thread.currentThread().getId()))
                .onFailure(f -> holder.set(1));

        promise.fail(TIMEOUT).syncWait(timeout(1).seconds());

        assertEquals(1, holder.get());
    }

    @Test
    void syncWaitIsWaitingForResolution() {
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>promise().onSuccess(holder::set);

        executor.execute(() -> {
            safeSleep(20);
            promise.ok(1);
        });

        promise.syncWait();

        assertEquals(1, holder.get());
    }

    @Test
    void syncWaitDoesNotWaitForAlreadyResolved() {
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>promise().onSuccess(holder::set);

        assertEquals(-1, holder.get());

        promise.ok(1);

        promise.syncWait();

        assertEquals(1, holder.get());
    }

    @Test
    void syncWaitWithTimeoutIsWaitingForResolution() {
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>promise().onSuccess(holder::set);

        assertEquals(-1, holder.get());

        executor.execute(() -> {
            safeSleep(20);
            promise.ok(1);
        });

        assertEquals(-1, holder.get());

        promise.syncWait(timeout(100).millis());

        assertEquals(1, holder.get());
    }

    @Test
    void syncWaitWithTimeoutIsWaitingForTimeout() {
        final var holder = new AtomicInteger(-1);
        final var resultHolder = new AtomicReference<Result<Integer>>();
        final var promise = Promise.<Integer>promise().onSuccess(holder::set);

        assertEquals(-1, holder.get());

        executor.execute(() -> {
            safeSleep(200);
            promise.ok(1);
        });

        promise.syncWait(timeout(100).millis(), resultHolder::set);

        option(resultHolder.get())
                .whenEmpty(Assertions::fail)
                .whenPresent(result -> result.onSuccess(v -> fail())
                                             .onFailure(f -> assertEquals(Errors.TIMEOUT, f)));

        assertEquals(-1, holder.get());
    }

    @Test
    void promiseIsResolvedWhenTimeoutExpires() {
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>promise().onSuccess(holder::set)
                                                      .async(timeout(100).millis(),
                                                             p -> p.ok(123));

        assertEquals(-1, holder.get());

        promise.syncWait();

        assertEquals(123, holder.get());
    }

    @Test
    void taskCanBeExecuted() {
        final var holder = new AtomicInteger(-1);
        final var promise = Promise.<Integer>promise()
                .onSuccess(holder::set)
                .when(timeout(100).millis(), ok(123));

        assertEquals(-1, holder.get());

        promise.async(p -> p.ok(345)).syncWait();

        assertEquals(345, holder.get());
    }

    @Test
    void anyResolvedPromiseResolvesResultForFirstPromise() {
        final var holder = new AtomicInteger(-1);
        final var threshold = Promise.<Integer>promise();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();

        Promises.any(promise1, promise2)
                .onSuccess(holder::set)
                .chainTo(threshold);

        assertEquals(-1, holder.get());

        promise1.ok(1);
        threshold.syncWait();

        assertEquals(1, holder.get());
    }

    @Test
    void anyResolvedPromiseResolvesResultForSecondPromise() {
        final var holder = new AtomicInteger(-1);
        final var threshold = Promise.<Integer>promise();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();

        Promises.any(promise1, promise2)
                .onSuccess(holder::set).chainTo(threshold);

        assertEquals(-1, holder.get());

        promise2.ok(1);

        threshold.syncWait();

        assertEquals(1, holder.get());
    }

    @Test
    void onlySuccessResolvesAnySuccess() {
        final var holder = new AtomicInteger(-1);
        final var threshold = Promise.<Integer>promise();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();
        Promises.anySuccess(promise1, promise2)
                .onSuccess(holder::set)
                .chainTo(threshold);

        assertEquals(-1, holder.get());

        promise1.fail(TIMEOUT);

        assertEquals(-1, holder.get());

        promise2.ok(1);

        threshold.syncWait();

        assertEquals(1, holder.get());
    }

    @Test
    void allFailuresResolvesAnySuccessToFailure() {
        final var holder = new AtomicInteger(-1);
        final var errHolder = new AtomicReference<>();
        final var threshold = Promise.<Integer>promise();
        final var promise1 = Promise.<Integer>promise();
        final var promise2 = Promise.<Integer>promise();

        Promises.anySuccess(promise1, promise2)
                .onSuccess(holder::set)
                .onFailure(errHolder::set)
                .chainTo(threshold);

        assertEquals(-1, holder.get());

        promise1.fail(TIMEOUT);

        assertEquals(-1, holder.get());

        promise2.fail(TIMEOUT);

        threshold.syncWait();

        assertEquals(-1, holder.get());

        assertEquals(Errors.CANCELLED, errHolder.get());
    }

    @Test
    void chainMapResolvesToFailureIfBasePromiseIsResolvedToFailure() {
        final var holder = new AtomicInteger(-1);
        final var stringHolder = new AtomicReference<String>();
        final var promise = Promise.<Integer>promise().onSuccess(s -> holder.set(1))
                                                      .onFailure(f -> holder.set(2));

        final var chain = promise.syncFlatMap(val -> Promise.readyOk(val.toString()), null)
                                 .onSuccess(s -> stringHolder.set("success"))
                                 .onFailure(f -> stringHolder.set("failure"));

        promise.syncFail(TIMEOUT, null);

        assertEquals(2, holder.get());
        assertEquals("failure", stringHolder.get());
    }

    @Test
    void chainMapResolvesToSuccessIfBasePromiseIsResolvedToSuccess() {
        final var holder = new AtomicInteger(-1);
        final var stringHolder = new AtomicReference<String>();
        final var promise = Promise.<Integer>promise().onSuccess(s -> holder.set(1))
                                                      .onFailure(f -> holder.set(2));

        final var chain = promise.syncFlatMap(val -> Promise.readyOk(val.toString()), null)
                                 .onSuccess(s -> stringHolder.set("success"))
                                 .onFailure(f -> stringHolder.set("failure"));

        promise.syncOk(123, null);

        assertEquals(1, holder.get());
        assertEquals("success", stringHolder.get());
    }

    //TODO: create separate set of tests for I/O
    @Test
    void ioTaskCanBeSubmitted() {
        final var promise = Promise.<String>promise()
                .async((p, io) -> io.nop(Promise.promise())
                                    .thenDo(() -> p.syncOk("success", null)));

        promise.onSuccess(success -> assertEquals("success", success))
               .onFailure(failure -> fail())
               .syncWait(timeout(1).seconds());

    }

    private static void safeSleep(final long delay) {
        try {
            Thread.sleep(delay);
        } catch (final InterruptedException e) {
            //Ignore
        }
    }
}

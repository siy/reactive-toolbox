package org.reactivetoolbox.io.async;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.lang.Tuple.Tuple2;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.scheduler.Timeout;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.reactivetoolbox.core.lang.Tuple.tuple;
import static org.reactivetoolbox.io.async.Promise.waitablePromise;
import static org.reactivetoolbox.io.scheduler.Timeout.timeout;

//@Disabled
public class PromisePerfTest {
    void testRunner(final String name,
                    final FN1<Long, Integer> promiseBenchmark,
                    final FN1<Long, Integer> futureBenchmark,
                    final int numIterations,
                    final int batchLen) {
        long futureTotal = 0;
        long promiseTotal = 0;

        System.out.println("Preheating " + name);
        for (int i = 0; i < numIterations; i++) {
            promiseBenchmark.apply(batchLen);
            futureBenchmark.apply(batchLen);
        }

        System.out.println("Benchmarking " + name);

        for (int i = 0; i < numIterations; i++) {
            promiseTotal += promiseBenchmark.apply(batchLen);
            futureTotal += futureBenchmark.apply(batchLen);
        }

        print("Future", futureTotal / numIterations, batchLen);
        print("Promise", promiseTotal / numIterations, batchLen);
        System.out.println();
    }

    @Test
    void testSequential() {
        for (int i = 2; i <= 20; i += 2) {
            testRunner("Sequential " + i,
                       PromisePerfTest::sequentialPromiseRun,
                       PromisePerfTest::sequentialFutureRun,
                       50_000, i);
        }
    }

    @Test
    void testSequentialSynchronous() {
        for (int i = 2; i <= 20; i += 2) {
            testRunner("Sequential Synchronous " + i,
                       PromisePerfTest::sequentialSynchronousPromiseRun,
                       PromisePerfTest::sequentialSynchronousFutureRun,
                       2_000_000, i);
        }
    }

    @Test
    void testParallel() {
        for (int i = 2; i <= 20; i += 2) {
            testRunner("Parallel " + i,
                       PromisePerfTest::parallelPromiseRun,
                       PromisePerfTest::parallelFutureRun,
                       50_000, i);
        }
    }

    private static void print(final String prefix, final long finish, final int numIterations) {
        final long ms = Timeout.timeout(finish)
                               .nanos()
                               .asMicros();

        System.out.printf("%s: %dus (%dns) total, %dns per resolve\n", prefix, ms, finish, finish / numIterations);
    }

    private static long parallelPromiseRun(final int count) {
        final var origin = Promise.<Integer>promise();
        final var latch = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {
            origin.map(v -> v)
                  .thenDo(latch::countDown);
        }

        final long start = System.nanoTime();
        origin.ok(1);
        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        final long result = System.nanoTime() - start;

        if (latch.getCount() > 0) {
            System.out.println("Promise Latch:" + latch.getCount());
        }
        return result;
    }

    private static long parallelFutureRun(final int count) {
        final var origin = new CompletableFuture<Result<Integer>>();
        final var latch = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {
            origin.thenApplyAsync(v -> v)
                  .thenAccept(v -> latch.countDown());
        }

        final long start = System.nanoTime();
        origin.completeAsync(() -> Result.ok(1));
        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        final long result = System.nanoTime() - start;

        if (latch.getCount() > 0) {
            System.out.println("Future Latch:" + latch.getCount());
        }
        return result;
    }

    private static long sequentialPromiseRun(final int count) {
        final var holder = new AtomicInteger(-1);
        final var promises = configurePromises(count);
        final long start = System.nanoTime();

        final long finish = promises.map((origin, last) -> {
            origin.ok(1);
            last.syncWait(timeout(10).seconds())
                .onSuccess(holder::set);
            return System.nanoTime() - start;
        });

        assertEquals(count + 1, holder.get());
        return finish;
    }

    private static long sequentialFutureRun(final int count) {
        final var holder = new AtomicInteger(-1);
        final var promises = configureCompletableFutures(count);
        final long start = System.nanoTime();

        final long finish = promises.map((origin, last) -> {
            origin.completeAsync(() -> Result.ok(1));
            last.join().onSuccess(holder::set);
            return System.nanoTime() - start;
        });

        assertEquals(count + 1, holder.get());
        return finish;
    }

    private static long sequentialSynchronousFutureRun(final int count) {
        final var holder = new AtomicInteger(-1);
        final var promises = configureSynchronousCompletableFutures(count);
        final long start = System.nanoTime();

        final long finish = promises.map((origin, last) -> {
            origin.complete(Result.ok(1));
            last.join().onSuccess(holder::set);
            return System.nanoTime() - start;
        });

        assertEquals(count + 1, holder.get());
        return finish;
    }

    private static long sequentialSynchronousPromiseRun(final int count) {
        final var holder = new AtomicInteger(-1);
        final var promises = configureSynchronousPromises(count);
        final long start = System.nanoTime();

        final long finish = promises.map((origin, last) -> {
            origin.syncOk(1);
            last.syncWait(timeout(10).seconds())
                .onSuccess(holder::set);
            return System.nanoTime() - start;
        });

        assertEquals(count + 1, holder.get());
        return finish;
    }

    static private Tuple2<Promise<Integer>, Promise<Integer>> configurePromises(final int count) {
        final var origin = Promise.<Integer>promise();
        var last = origin;

        for (int i = 0; i < count; i++) {
            last = last.map(v -> v + 1);
        }

        return tuple(origin, waitablePromise(last::syncChainTo));
    }

    static private Tuple2<CompletableFuture<Result<Integer>>, CompletableFuture<Result<Integer>>> configureCompletableFutures(final int count) {
        final var origin = new CompletableFuture<Result<Integer>>();
        var last = origin;

        for (int i = 0; i < count; i++) {
            final var next = new CompletableFuture<Result<Integer>>();
            last.thenAcceptAsync(v -> next.complete(v.map(vv -> vv + 1)));
            last = next;
        }

        return tuple(origin, last);
    }

    static private Tuple2<CompletableFuture<Result<Integer>>, CompletableFuture<Result<Integer>>> configureSynchronousCompletableFutures(final int count) {
        final var origin = new CompletableFuture<Result<Integer>>();
        var last = origin;

        for (int i = 0; i < count; i++) {
            final var next = new CompletableFuture<Result<Integer>>();
            last.thenAccept(v -> next.complete(v.map(vv -> vv + 1)));
            last = next;
        }

        return tuple(origin, last);
    }

    static private Tuple2<Promise<Integer>, Promise<Integer>> configureSynchronousPromises(final int count) {
        final var origin = Promise.<Integer>promise();
        var last = origin;

        for (int i = 0; i < count; i++) {
            last = last.syncMap(v -> v + 1);
        }

        return tuple(origin, waitablePromise(last::syncChainTo));
    }
}

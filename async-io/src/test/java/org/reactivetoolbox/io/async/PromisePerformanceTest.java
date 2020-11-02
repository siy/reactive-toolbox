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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.reactivetoolbox.core.lang.Tuple.Tuple2;
import org.reactivetoolbox.core.lang.functional.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.reactivetoolbox.core.lang.Tuple.tuple;
import static org.reactivetoolbox.io.scheduler.Timeout.timeout;

/* Test results as of Jul 10, 2020 (reordered to show pairs)
 * Benchmark                                    (batchLenght)   Mode  Cnt       Score       Error  Units
 * PromisePerformanceTest.parallelFutureRun                 1  thrpt    5  103645.226 ±   766.387  ops/s
 * PromisePerformanceTest.parallelPromiseRun                1  thrpt    5  152505.300 ±  2832.612  ops/s
 *
 * PromisePerformanceTest.parallelFutureRun                 3  thrpt    5   98872.902 ±   362.354  ops/s
 * PromisePerformanceTest.parallelPromiseRun                3  thrpt    5  143747.248 ±  9473.385  ops/s
 *
 * PromisePerformanceTest.parallelFutureRun                 5  thrpt    5   96779.873 ±  5897.543  ops/s
 * PromisePerformanceTest.parallelPromiseRun                5  thrpt    5  136348.630 ±  6092.202  ops/s
 *
 * PromisePerformanceTest.parallelFutureRun                10  thrpt    5   91092.523 ±  1161.285  ops/s
 * PromisePerformanceTest.parallelPromiseRun               10  thrpt    5  116935.357 ± 10001.950  ops/s
 *
 *
 * PromisePerformanceTest.sequentialFutureRun               1  thrpt    5  103922.220 ±   786.688  ops/s
 * PromisePerformanceTest.sequentialPromiseRun              1  thrpt    5  171665.230 ±  6710.966  ops/s
 *
 * PromisePerformanceTest.sequentialFutureRun               3  thrpt    5   76175.409 ±   562.426  ops/s
 * PromisePerformanceTest.sequentialPromiseRun              3  thrpt    5  139860.696 ± 17561.052  ops/s
 *
 * PromisePerformanceTest.sequentialFutureRun               5  thrpt    5   62790.490 ±  9925.255  ops/s
 * PromisePerformanceTest.sequentialPromiseRun              5  thrpt    5  127013.838 ±  9612.598  ops/s
 *
 * PromisePerformanceTest.sequentialFutureRun              10  thrpt    5   58385.537 ±   149.676  ops/s
 * PromisePerformanceTest.sequentialPromiseRun             10  thrpt    5   97655.647 ±  4193.467  ops/s
 */
@Disabled
public class PromisePerformanceTest {
    @Test
    void testPerformance() throws Exception {
        org.openjdk.jmh.Main.main(new String[]{});
    }

    @State(Scope.Benchmark)
    public static class ExecutionPlan {
        @Param({"1", "3", "5", "10"})
        public int batchLenght;
    }

    @Fork(1)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void sequentialPromiseRun(final ExecutionPlan plan) {
        configurePromises(plan.batchLenght).map((origin, last) -> {
            origin.ok(1);
            return last;
        }).syncWait(timeout(10).seconds());
    }

    @Fork(1)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void sequentialFutureRun(final ExecutionPlan plan) {
        configureCompletableFutures(plan.batchLenght).map((origin, last) -> {
            origin.completeAsync(() -> Result.ok(1));
            return last;
        }).join();
    }

    @Fork(1)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void parallelPromiseRun(final ExecutionPlan plan) throws Exception {
        final var origin = Promise.<Integer>promise();
        final CountDownLatch latch = new CountDownLatch(plan.batchLenght);

        for (int i = 0; i < plan.batchLenght; i++) {
            origin.map(v -> v)
                  .onResult(v -> latch.countDown());
        }

        origin.ok(1);
        latch.await(5, TimeUnit.SECONDS);
    }

    @Fork(1)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void parallelFutureRun(final ExecutionPlan plan) throws Exception {
        final var origin = new CompletableFuture<Result<Integer>>();
        final CountDownLatch latch = new CountDownLatch(plan.batchLenght);

        for (int i = 0; i < plan.batchLenght; i++) {
            origin.thenApplyAsync(v -> v)
                  .thenAccept(v -> latch.countDown());
        }

        origin.completeAsync(() -> Result.ok(1));
        latch.await(5, TimeUnit.SECONDS);
    }

    static private Tuple2<Promise<Integer>, Promise<Integer>> configurePromises(final int count) {
        final var origin = Promise.<Integer>promise();
        var last = origin;

        for (int i = 0; i < count; i++) {
            last = last.map(v -> v + 1);
        }

        return tuple(origin, last);
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
}

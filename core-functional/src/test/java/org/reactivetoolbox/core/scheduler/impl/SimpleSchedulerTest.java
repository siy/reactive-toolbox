package org.reactivetoolbox.core.scheduler.impl;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.scheduler.SchedulerError;
import org.reactivetoolbox.core.scheduler.Timeout;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class SimpleSchedulerTest {
    private final Random random = new Random();

    //Kinda performance/load test, 50_000_000 items are processed in this case
//    private static final int N_ITEMS_PER_TASK = 500;
//    private static final int N_TASKS = 100_000;
//    private static final int N_PROCESSING_THREADS = 8;
    private static final int N_ITEMS_PER_TASK = 10;
    private static final int N_TASKS = 1_000;
    private static final int N_PROCESSING_THREADS = 8;
    private static final int SINGLE_TASK_DELAY_MAX = 95;
    private static final int SINGLE_TASK_DELAY_MIN = 5;
    private static final int SCHEDULER_CAPACITY = N_PROCESSING_THREADS * 4;

    @Test
    void submittedTasksAreProcessed() throws InterruptedException {
        final var scheduler = SimpleScheduler.with(SCHEDULER_CAPACITY);
        final var executor = Executors.newFixedThreadPool(N_PROCESSING_THREADS, SimpleScheduler::newDaemonThread);
        final var counters = new AtomicLong[N_TASKS];

        executor.submit(() -> IntStream.range(0, N_TASKS)
                                       .forEach((n) -> {
                                           counters[n] = new AtomicLong();
                                           singleTask(n, scheduler, counters[n]);
                                       }));

        executor.shutdown();
        assertTrue(executor.awaitTermination(60, TimeUnit.SECONDS));

        Thread.sleep(200);

        assertEquals(N_TASKS * N_ITEMS_PER_TASK, List.of(counters).stream().mapToLong(AtomicLong::get).sum());
    }

    private void singleTask(final int n, final SimpleScheduler scheduler, final AtomicLong counter) {
        scheduler.request()
                 .onFailure(error -> fail())
                 .onSuccess(timeoutScheduler -> submitTimeoutTask(timeoutScheduler, counter));
    }

    private void submitTimeoutTask(final SimpleScheduler.TimeoutScheduler timeoutScheduler,
                                   final AtomicLong counter) {
        for (int i = 0; i < N_ITEMS_PER_TASK; i++) {
            final var promise = Promise.<Either<? extends BaseError, String>>give().then(v -> counter.incrementAndGet());

            timeoutScheduler.submit(() -> promise.resolve(Either.failure(SchedulerError.TIMEOUT)),
                                    Timeout.of(nextTaskDelay()).millis());
        }
        timeoutScheduler.release();
    }

    private int nextTaskDelay() {
        return random.nextInt(SINGLE_TASK_DELAY_MAX - SINGLE_TASK_DELAY_MIN) + SINGLE_TASK_DELAY_MIN;
    }

    private void countDown(final CountDownLatch gate) {
        gate.countDown();
    }
}
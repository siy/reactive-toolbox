package org.reactivetoolbox.io.scheduler.impl;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.scheduler.Timeout;

import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.reactivetoolbox.core.Errors.TIMEOUT;

class TimeoutSchedulerTest {
    private final Random random = new Random();

    private static final int N_ITEMS_PER_TASK = 100_000;
    private static final int N_TASKS = 4;
    private static final int N_PROCESSING_THREADS = N_TASKS/2;
    private static final int SINGLE_TASK_DELAY_MAX = 95;
    private static final int SINGLE_TASK_DELAY_MIN = 5;

    @Test
    void timeoutsAreProcessed() throws InterruptedException {
        final var executor = new ThreadPoolExecutor(N_PROCESSING_THREADS, N_PROCESSING_THREADS,
                                                    0L, TimeUnit.MILLISECONDS,
                                                    new LinkedTransferQueue<>(),
                                                    DaemonThreadFactory.of("Test Task Thread #%d"));

        final var counters = new AtomicLong[N_TASKS];

        range(0, counters.length)
                .forEach((n) -> executor.execute(() -> {
                    counters[n] = new AtomicLong();
                    range(0, N_ITEMS_PER_TASK).forEach((k) -> {
                        Promise.promise()
                               .when(Timeout.timeout(nextTaskDelay()).millis(), TIMEOUT.asResult())
                               .onResult(v -> counters[n].incrementAndGet());
                    });
                }));

        executor.shutdown();
        assertTrue(executor.awaitTermination(15, TimeUnit.SECONDS));

        Thread.sleep(100);

        assertEquals(N_TASKS * N_ITEMS_PER_TASK, List.of(counters).stream().mapToLong(AtomicLong::get).sum());
    }

    private int nextTaskDelay() {
        return random.nextInt(SINGLE_TASK_DELAY_MAX - SINGLE_TASK_DELAY_MIN) + SINGLE_TASK_DELAY_MIN;
    }
}
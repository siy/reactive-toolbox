package org.reactivetoolbox.core.scheduler.impl;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.scheduler.Timeout;

import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.reactivetoolbox.core.scheduler.SchedulerError.TIMEOUT;

class TimeoutSchedulerTest {
    private final Random random = new Random();

    private static final int N_ITEMS_PER_TASK = 1_000_000;
    private static final int N_TASKS = 6;
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
                        Promise.give()
                               .with(Timeout.of(nextTaskDelay()).millis(), TIMEOUT.asFailure())
                               .then(v -> counters[n].incrementAndGet());
                    });
                }));

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        // Wait for 2 x max task timeout
        Thread.sleep(200);

        assertEquals(N_TASKS * N_ITEMS_PER_TASK, List.of(counters).stream().mapToLong(AtomicLong::get).sum());
    }

    private int nextTaskDelay() {
        return random.nextInt(SINGLE_TASK_DELAY_MAX - SINGLE_TASK_DELAY_MIN) + SINGLE_TASK_DELAY_MIN;
    }
}
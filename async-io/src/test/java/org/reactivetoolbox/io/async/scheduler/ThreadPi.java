package org.reactivetoolbox.io.async.scheduler;

import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.io.scheduler.TaskScheduler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPi {

    public interface ExecutorWithShutdown extends Executor {
        void shutdown();
    }

    static double calculatePiFor(int slice, int nrOfIterations) {
        double acc = 0.0;
        for (int i = slice * nrOfIterations; i <= ((slice + 1) * nrOfIterations - 1); i++) {
            acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1);
        }
        return acc;
    }

    private static long executorTest(final int numTasks, final FN1<Runnable, Integer> taskFactory, final ExecutorWithShutdown executor) throws InterruptedException {
        //final CountDownLatch latch = new CountDownLatch(numTasks);

        final long start = System.nanoTime();
        final CountDownLatch[] latches = new CountDownLatch[numTasks];

        for (int i = 0; i < numTasks; i++) {
            var task = taskFactory.apply(i);
            var latch = new CountDownLatch(1);
            latches[i] = latch;
            final var cnt = i;

            executor.execute(() -> {
                task.run();
                latch.countDown();
            });
        }

        for(int i = 0; i < numTasks; i++) {
            latches[i].await();
        }
        final long result = System.nanoTime() - start;
        executor.shutdown();

        return result;
    }

    public static void main(String arg[]) throws Exception {
        runTest("FixedThreadPool", ThreadPi::newTraditionalExecutor);
        runTest("FixedThreadPool with LTQ", ThreadPi::newLTQExecutor);
        runTest("ForkJoinPool", ThreadPi::newForkJoinExecutor);
        runTest("PipelinedTaskScheduler", ThreadPi::newPipelinedTaskScheduler);
    }

    private static void runTest(final String message, final FN1<ExecutorWithShutdown, Integer> executorFactory) throws InterruptedException {
        final int NUM_ITERATIONS_PER_TASK = 1;
        final int NUM_TASKS = 500_000;
        final int MAX_ACT = 10;
        final int START_ACT = 4;
        String[] results = new String[MAX_ACT];
        System.out.println(message);

        for (int numActors = START_ACT; numActors <= MAX_ACT; numActors++) {
            long sum = 0;
            for (int ii = 0; ii < 30; ii++) {
                long res = executorTest(NUM_TASKS,
                                        (i -> () -> calculatePiFor(i, NUM_ITERATIONS_PER_TASK)),
                                        executorFactory.apply(numActors));

                if (ii >= 20) {
                    sum += res;
                }
            }
            results[numActors - 1] = "average " + numActors + " threads : " + (sum / 10) / 1_000_000 + "ms";
        }

        for (int i = (START_ACT - 1); i < results.length; i++) {
            String result = results[i];
            System.out.println(result);
        }
        System.out.println();
    }

    private static ExecutorWithShutdown wrap(final ExecutorService service) {
        return new ExecutorWithShutdown() {
            @Override
            public void shutdown() {
                service.shutdown();
            }

            @Override
            public void execute(final Runnable command) {
                service.execute(command);
            }
        };
    }

    static ExecutorWithShutdown newTraditionalExecutor(final int nThreads) {
        return wrap(new ThreadPoolExecutor(nThreads, nThreads,
                                           0L, TimeUnit.MILLISECONDS,
                                           new LinkedBlockingQueue<>()));
    }

    static ExecutorWithShutdown newLTQExecutor(final int nThreads) {
        return wrap(new ThreadPoolExecutor(nThreads, nThreads,
                                           0L, TimeUnit.MILLISECONDS,
                                           new LinkedTransferQueue<>()));
    }

    static ExecutorWithShutdown newForkJoinExecutor(final int nThreads) {
        return wrap(new ForkJoinPool(nThreads));
    }

    static ExecutorWithShutdown newPipelinedTaskScheduler(final int nThreads) {
        return new ExecutorWithShutdown() {
            private final TaskScheduler scheduler = TaskScheduler.with(nThreads);

            @Override
            public void shutdown() {
                scheduler.shutdown();
            }

            @Override
            public void execute(final Runnable command) {
                scheduler.execute(command);
            }
        };
    }
}
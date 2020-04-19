package org.reactivetoolbox.core.scheduler;

/*
 * Copyright (c) 2019 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.reactivetoolbox.core.log.CoreLogger;
import org.reactivetoolbox.core.scheduler.impl.DoubleQueueTaskScheduler;

import java.util.concurrent.Executor;

/**
 * General purpose task scheduler for executing arbitrary functions.
 * <br>
 * Note that this scheduler is tailored for short living, non-blocking tasks. For long living and blocking tasks
 * other schedulers should be used.
 *
 * @see RunnablePredicate
 */
public interface TaskScheduler extends Executor {
    /**
     * Low-level method which accepts {@link RunnablePredicate} and processes it as many times, as {@link RunnablePredicate#isDone(long)}
     * returns false.
     *
     * @param predicate
     *        Runnable predicate to execute
     * @return this instance for fluent call chaining.
     */
    TaskScheduler submit(final RunnablePredicate predicate);

    /**
     * Get internal logger instance.
     *
     * @return logger instance used to log {@link TaskScheduler} internal events
     */
    CoreLogger logger();

    /**
     * Submit task which will be executed exactly once and as soon as possible.
     *
     * @param runnable
     *        Task to execute
     * @return this instance for fluent call chaining.
     */
    default TaskScheduler submit(final Runnable runnable) {
        return submit((nanoTime) -> { runnable.run(); return true;});
    }

    /**
     * Implementation of {@link Executor} interface
     *
     * @param task
     *        Task to run
     */
    @Override
    default void execute(final Runnable task) {
        submit(() -> {
            try {
                task.run();
            } catch (final Throwable t) {
                logger().debug("Error while running task submitted via Executor.execute() interface", t);
            }
        });
    }

    /**
     * Submit task which will be executed once specified timeout is expired.
     *
     * @param timeout
     *        Timeout after which task will be executed
     * @param runnable
     *        Code to execute
     * @return this instance fo fluent call chaining.
     */
    default TaskScheduler submit(final Timeout timeout, final Runnable runnable) {
        final long threshOld = System.nanoTime() + timeout.nanos();

        return submit(nanoTime -> {
            if (nanoTime >= threshOld) {
                runnable.run();
                return true;
            }
            return false;
        });
    }

    /**
     * Shutdown scheduler. Once scheduler is shut down, remaining tasks will be processed, but no new tasks
     * will be accepted.
     */
    void shutdown();

    /**
     * Create instance of scheduler with specified execution pool size.
     *
     * @param size
     *        Execution pool size
     * @return created scheduler
     */
    static TaskScheduler with(final int size) {
        return DoubleQueueTaskScheduler.with(size);
    }
}

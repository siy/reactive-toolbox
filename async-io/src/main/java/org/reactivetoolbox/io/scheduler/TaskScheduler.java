package org.reactivetoolbox.io.scheduler;

/*
 * Copyright (c) 2019, 2020 Sergiy Yevtushenko
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
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.scheduler.impl.PipelinedTaskScheduler;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * General purpose task scheduler for executing arbitrary functions.
 * <br>
 * Note that this scheduler is tailored for short living, non-blocking tasks.
 *
 * @see Action
 */
public interface TaskScheduler extends Executor {
    /**
     * Low-level method which accepts {@link Action} and processes it as many times, as {@link Action#perform(long, Submitter)}
     * returns false.
     *
     * @param action
     *         Runnable predicate to execute
     *
     * @return this instance for fluent call chaining.
     */
    default TaskScheduler submit(final Action action) {
        return submit(() -> {
            if (!action.perform(System.nanoTime(), localSubmitter())) {
                TaskScheduler.this.submit(action);
            }
        });
    }

    /**
     * Submit an I/O task.
     *
     * @param ioAction
     *          The I/O task
     *
     * @return this instance for fluent call chaining.
     */
    default TaskScheduler submit(final Consumer<Submitter> ioAction) {
        return submit(() -> ioAction.accept(localSubmitter()));
    }

    /**
     * Submit task which will be executed exactly once and as soon as possible.
     *
     * @param runnable
     *         Task to execute
     *
     * @return this instance for fluent call chaining.
     */
    TaskScheduler submit(final Runnable runnable);

    /**
     * Submit task which will be executed once specified timeout is expired.
     *
     * @param timeout
     *         Timeout after which task will be executed
     * @param runnable
     *         Code to execute
     *
     * @return this instance fo fluent call chaining.
     */
    default TaskScheduler submit(final Timeout timeout, final Runnable runnable) {
        final long threshOld = System.nanoTime() + timeout.asNanos();

        return submit((nanoTime, $$) -> {
            if (nanoTime >= threshOld) {
                runnable.run();
                return true;
            }
            return false;
        });
    }

    /**
     * Implementation of {@link Executor} interface
     *
     * @param task
     *         Task to run
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
     * Get internal logger instance.
     *
     * @return logger instance used to log {@link TaskScheduler} internal events
     */
    CoreLogger logger();

    /**
     * Return {@link Submitter} instance local for current thread.
     * <p>
     * Note that {@link Submitter} instances are present only for threads which belong to this instance of {@link TaskScheduler}.
     * Calling this method from other threads will return {@code null}. The returned instance is not thread safe and should not
     * be passed to other threads.
     *
     * @return instance of {@link Submitter} suitable for use in current thread.
     */
    Submitter localSubmitter();

    /**
     * Shutdown scheduler. Once scheduler is shut down, remaining tasks will be processed, but no new tasks
     * will be accepted.
     */
    void shutdown();

    /**
     * Create instance of scheduler with specified execution pool size.
     *
     * @param size
     *         Execution pool size
     *
     * @return created scheduler
     */
    static TaskScheduler with(final int size) {
        return PipelinedTaskScheduler.with(size);
    }
}

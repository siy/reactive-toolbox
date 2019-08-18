package org.reactivetoolbox.core.scheduler;

/*
 * Copyright (c) 2017-2019 Sergiy Yevtushenko
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

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.scheduler.impl.DoubleQueueTaskScheduler;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.reactivetoolbox.core.scheduler.SchedulerError.TIMEOUT;

/**
 * General purpose task scheduler for executing arbitrary short functions.
 * The API is designed to make submitting tasks which deal with {@link Promise} convenient,
 * although general purpose methods like {@link #submit(Timeout, Runnable)} and {@link #submit(RunnablePredicate)}
 * are also provided.
 * <br>
 * Note that this scheduler is tailored for short living, non-blocking tasks. For long living and blocking tasks
 * other schedulers should be used.
 *
 * @see RunnablePredicate
 */
public interface TaskScheduler {
    Timeout DEFAULT_TIMEOUT = Timeout.of(30).sec();

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
     * Submit task which will be executed once specified timeout is expired. It is guaranteed that timeout will be at least as long
     * as specified.
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
     * Submit task which will be executed by passing specified instance of {@link Promise} as a parameter.
     * Note that this method does not configure timeout for the task execution.
     *
     * @param promise
     *        {@link Promise} instance to pass as a parameter
     * @param task
     *        Task to execute
     * @return input promise for call chaining
     */
    default <T> Promise<T> submit(final Promise<T> promise, final Consumer<Promise<T>> task) {
        submit((nanoTime) -> { task.accept(promise); return true;});
        return promise;
    }

    /**
     * Start specified task with new instance of {@link Promise} as a parameter. Convenient in cases when there is
     * no ready to use {@link Promise} instance. Calling code then can use {@link Promise#then(Consumer)} method to
     * add processing of the execution result. Task also will be scheduled for timeout processing with default timeout
     * configured for this {@link TaskScheduler}. If timeout expires, then {@link Promise} will be resolved
     * with {@link SchedulerError#TIMEOUT} error.
     *
     * @param task
     *        Task to execute.
     * @return created instance of {@link Promise}
     */
    default <T> Promise<Either<? extends BaseError, T>> submit(final Consumer<Promise<Either<? extends BaseError, T>>> task) {
        return submit(defaultTimeout(), TIMEOUT::asFailure, task);
    }

    /**
     * Start specified task with new instance of {@link Promise} as a parameter. Convenient in cases when there is
     * no ready to use {@link Promise} instance. Calling code then can use {@link Promise#then(Consumer)} method to
     * add processing of the execution result. Task also will be scheduled for timeout processing with default timeout
     * configured for this {@link TaskScheduler}. If timeout expires, then {@link Promise} will be resolved
     * with result returned by provided <code>timeoutResultSupplier</code>.
     *
     * @param timeoutResultSupplier
     *        Result supplier for the timeout
     * @param task
     *        Task to execute.
     * @return created instance of {@link Promise}
     */
    default <T> Promise<T> submit(final Supplier<T> timeoutResultSupplier, final Consumer<Promise<T>> task) {
        return submit(defaultTimeout(), timeoutResultSupplier, task);
    }

    /**
     * Start specified task with new instance of {@link Promise} as a parameter. Convenient in cases when there is
     * no ready to use {@link Promise} instance. Calling code then can use {@link Promise#then(Consumer)} method to
     * add processing of the execution result. Task also will be scheduled for timeout processing with specified timeout.
     * If timeout expires, then {@link Promise} will be resolved with result returned by provided <code>timeoutResultSupplier</code>.
     *
     * @param timeout
     *        Task timeout
     * @param timeoutResultSupplier
     *        Result supplier for the timeout
     * @param task
     *        Task to execute.
     * @return created instance of {@link Promise}
     */
    default <T> Promise<T> submit(final Timeout timeout,
                                  final Supplier<T> timeoutResultSupplier, final Consumer<Promise<T>> task) {
        return submit(Promise.<T>give().with(timeout, timeoutResultSupplier), task);
    }

    default Timeout defaultTimeout() {
        return DEFAULT_TIMEOUT;
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
        return DoubleQueueTaskScheduler.with(size, DEFAULT_TIMEOUT);
    }

    /**
     * Create instance of scheduler with specified execution pool size and
     * default timeout.
     *
     * @param size
     *        Execution pool size
     * @param timeout
     *        Default task timeout
     * @return created scheduler
     */
    static TaskScheduler with(final int size, final Timeout timeout) {
        return DoubleQueueTaskScheduler.with(size, timeout);
    }
}

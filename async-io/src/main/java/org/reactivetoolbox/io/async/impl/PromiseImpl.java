package org.reactivetoolbox.io.async.impl;

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

import org.reactivetoolbox.core.Errors;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.log.CoreLogger;
import org.reactivetoolbox.core.meta.AppMetaRepository;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.util.StackingCollector;
import org.reactivetoolbox.io.scheduler.TaskScheduler;
import org.reactivetoolbox.io.scheduler.Timeout;

import java.util.StringJoiner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Implementation of {@link Promise}
 */
public class PromiseImpl<T> implements Promise<T> {
    private final AtomicReference<Result<T>> value = new AtomicReference<>(null);
    private final StackingCollector<Consumer<Result<T>>> actions = StackingCollector.stackingCollector();
    private final CountDownLatch actionsHandled = new CountDownLatch(1);

    private static final AtomicReference<Consumer<Throwable>> exceptionConsumer =
            new AtomicReference<>(e -> SingletonHolder.logger().debug("Exception while applying handlers", e));

    private PromiseImpl() {
    }

    public static void exceptionConsumer(final Consumer<Throwable> consumer) {
        exceptionConsumer.set(consumer);
    }

    public static <T> Promise<T> promise() {
        return new PromiseImpl<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> syncResolve(final Result<T> result) {
        if (value.compareAndSet(null, result)) {
            handleActions();
        }

        return this;
    }

    @Override
    public Promise<T> resolve(final Result<T> result) {
        if (value.compareAndSet(null, result)) {
            handleActionsAsync();
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> onResult(final Consumer<Result<T>> action) {
        final Consumer<Result<T>> safeAction = result -> {
            try {
                action.accept(result);
            } catch (final Throwable t) {
                exceptionConsumer.get().accept(t);
            }
        };

        if (value.get() != null) {
            safeAction.accept(value.get());
            return this;
        }

        actions.push(safeAction);

        if (value.get() != null) {
            handleActions();
        }
        return this;
    }

    private void handleActionsAsync() {
        SingletonHolder.scheduler()
                       .submit(this::handleActions);
    }

    private void handleActions() {
        final var result = this.value.get();

        while (true) {
            if (!actions.swapAndApply(action -> action.accept(result))) {
                break;
            }
        }
        actionsHandled.countDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> syncWait() {
        try {
            actionsHandled.await();
        } catch (final Exception e) {
            logger().debug("Exception in syncWait()", e);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> syncWait(final Timeout timeout) {
        try {
            if (!actionsHandled.await(timeout.asMillis(), TimeUnit.MILLISECONDS)) {
                syncFail(Errors.TIMEOUT);
            }
        } catch (final Exception e) {
            logger().debug("Exception in syncWait(timeout)", e);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> async(final Consumer<Promise<T>> task) {
        SingletonHolder.scheduler()
                       .submit(() -> task.accept(this));
        return this;
    }

    @Override
    public Promise<T> async(final Timeout timeout, final Consumer<Promise<T>> task) {
        SingletonHolder.scheduler()
                       .submit(timeout, () -> task.accept(this));
        return this;
    }

    @Override
    public Promise<T> async(final BiConsumer<Promise<T>, Submitter> task) {
        SingletonHolder.scheduler()
                       .submit(submitter -> task.accept(this, submitter));
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Promise(", ")")
                .add(value.get() == null ? "<pending>" : value.get().toString())
                .toString();
    }

    @Override
    public CoreLogger logger() {
        return SingletonHolder.logger();
    }

    private static final class SingletonHolder {
        private static final int WORKER_SCHEDULER_SIZE = Math.max(Runtime.getRuntime().availableProcessors() - 1, 2);

        private static final TaskScheduler SCHEDULER = AppMetaRepository.instance()
                                                                        .put(TaskScheduler.class,
                                                                             TaskScheduler.with(WORKER_SCHEDULER_SIZE))
                                                                        .get(TaskScheduler.class);

        private SingletonHolder() {
        }

        public static TaskScheduler scheduler() {
            return SCHEDULER;
        }

        public static CoreLogger logger() {
            return SCHEDULER.logger();
        }
    }
}

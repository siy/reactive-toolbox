package org.reactivetoolbox.core.async.impl;

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

import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.log.CoreLogger;
import org.reactivetoolbox.core.meta.AppMetaRepository;
import org.reactivetoolbox.core.scheduler.TaskScheduler;
import org.reactivetoolbox.core.scheduler.Timeout;

import java.util.StringJoiner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Implementation of {@link Promise}
 */
public class PromiseImpl<T> implements Promise<T> {
    private final AtomicMarkableReference<Result<T>> value = new AtomicMarkableReference<>(null, false);
    private final BlockingQueue<Consumer<Result<T>>> thenActions = new LinkedBlockingQueue<>();
    private final CountDownLatch actionsHandled = new CountDownLatch(1);
    private final AtomicReference<Consumer<Throwable>> exceptionLogger = new AtomicReference<>(e -> logger().debug("Exception while applying handlers", e));

    public PromiseImpl() {
    }

    @Override
    public Promise<T> exceptionCollector(final Consumer<Throwable> consumer) {
        exceptionLogger.set(consumer);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> resolve(final Result<T> result) {
        if (value.compareAndSet(null, result, false, true)) {
            thenActions.forEach(action -> {
                try {
                    action.accept(value.getReference());
                } catch (final Throwable t) {
                    exceptionLogger.get().accept(t);
                }
            });
            actionsHandled.countDown();
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> onResult(final Consumer<Result<T>> action) {
        if (value.isMarked()) {
            try {
                action.accept(value.getReference());
            } catch (final Throwable t) {
                exceptionLogger.get().accept(t);
            }
        } else {
            thenActions.offer(action);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> syncWait() {
        try {
            actionsHandled.await();
        } catch (final InterruptedException e) {
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
            actionsHandled.await(timeout.timeout(), TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            logger().debug("Exception in syncWait(timeout)", e);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> async(final Consumer<Promise<T>> task) {
        SingletonHolder.scheduler().submit(() -> task.accept(this));
        return this;
    }

    @Override
    public Promise<T> async(final Timeout timeout, final Consumer<Promise<T>> task) {
        SingletonHolder.scheduler().submit(timeout, () -> task.accept(this));
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Promise(", ")")
                .add(value.isMarked() ? value.getReference().toString() : "<pending>")
                .toString();
    }

    @Override
    public CoreLogger logger() {
        return SingletonHolder.logger();
    }

    private static final class SingletonHolder {
        private static final TaskScheduler SCHEDULER = AppMetaRepository.instance().get(TaskScheduler.class);

        static TaskScheduler scheduler() {
            return SCHEDULER;
        }

        static CoreLogger logger() {
            return SCHEDULER.logger();
        }
    }
}

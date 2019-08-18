package org.reactivetoolbox.core.async.impl;

import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.core.meta.AppMetaRepository;
import org.reactivetoolbox.core.scheduler.TaskScheduler;
import org.reactivetoolbox.core.scheduler.Timeout;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Implementation of {@link Promise}
 */
public final class PromiseImpl<T> implements Promise<T> {
    private final AtomicMarkableReference<T> value = new AtomicMarkableReference<>(null, false);
    private final BlockingQueue<Consumer<T>> thenActions = new LinkedBlockingQueue<>();

    public PromiseImpl() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Option<T> value() {
        return Option.of(value.getReference());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean ready() {
        return value().isPresent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> resolve(final T result) {
        if (value.compareAndSet(null, result, false, true)) {
            thenActions.forEach(action -> action.accept(value.getReference()));
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> then(final Consumer<T> action) {
        if (value.isMarked()) {
            action.accept(value.getReference());
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
        final var latch = new CountDownLatch(1);
        then(value -> latch.countDown());

        try {
            latch.await();
        } catch (final InterruptedException e) {
            // Ignore exception
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> syncWait(final Timeout timeout) {
        final var latch = new CountDownLatch(1);
        then(value -> latch.countDown());

        try {
            latch.await(timeout.timeout(), TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            // Ignore exception
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> with(final Timeout timeout, final T timeoutResult) {
        TaskSchedulerHolder.instance().submit(timeout, () -> resolve(timeoutResult));

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> with(final Timeout timeout, final Supplier<T> timeoutResultSupplier) {
        TaskSchedulerHolder.instance().submit(timeout, () -> resolve(timeoutResultSupplier.get()));

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> perform(final Consumer<Promise<T>> task) {
        return TaskSchedulerHolder.instance().submit(this, task);
    }

    private static final class TaskSchedulerHolder {
        private static final TaskScheduler taskScheduler = AppMetaRepository.instance().seal().get(TaskScheduler.class);

        static TaskScheduler instance() {
            return taskScheduler;
        }
    }
}

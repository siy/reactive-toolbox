package org.reactivetoolbox.core.async.impl;

import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.core.scheduler.TaskScheduler;
import org.reactivetoolbox.core.scheduler.Timeout;
import org.reactivetoolbox.core.scheduler.TimeoutScheduler;

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
    //TODO: provide some way to configure these values
    private static final int TIMEOUT_SCHEDULER_SIZE = Runtime.getRuntime().availableProcessors() * 16;
    private static final int WORKER_SCHEDULER_SIZE = Runtime.getRuntime().availableProcessors() * 16;
    private static final TimeoutScheduler timeoutScheduler = TimeoutScheduler.with(TIMEOUT_SCHEDULER_SIZE);
    private static final TaskScheduler taskScheduler = TaskScheduler.with(WORKER_SCHEDULER_SIZE);

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
        timeoutScheduler.request()
                        .onSuccess(timeoutScheduler -> timeoutScheduler.submit(timeout,
                                                                        () -> resolve(timeoutResult))
                                                                .release());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> with(final Timeout timeout, final Supplier<T> timeoutResultSupplier) {
        timeoutScheduler.request()
                        .onSuccess(timeoutScheduler -> timeoutScheduler.submit(timeout,
                                                                        () -> resolve(timeoutResultSupplier.get()))
                                                                .release());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> perform(final Consumer<Promise<T>> task) {
        return taskScheduler.submit(this, task);
    }
}

package org.reactivetoolbox.core.async.impl;

import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.core.scheduler.Timeout;
import org.reactivetoolbox.core.scheduler.impl.SimpleScheduler;

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
    //TODO: provide some way to configure it
    private static final int schedulerSize = Runtime.getRuntime().availableProcessors() * 16;
    private static final SimpleScheduler scheduler = SimpleScheduler.with(schedulerSize);

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
        //TODO: consider some way to report failure
        scheduler.request()
                 .onSuccess(timeoutScheduler -> {
                     timeoutScheduler.submit(() -> resolve(timeoutResult), timeout);
                     timeoutScheduler.release();
                 });
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> with(final Timeout timeout, final Supplier<T> timeoutResultSupplier) {
        //TODO: consider some way to report failure
        scheduler.request()
                 .onSuccess(timeoutScheduler -> {
                     timeoutScheduler.submit(() -> resolve(timeoutResultSupplier.get()), timeout);
                     timeoutScheduler.release();
                 });
        return this;
    }
}

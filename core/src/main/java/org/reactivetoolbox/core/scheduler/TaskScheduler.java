package org.reactivetoolbox.core.scheduler;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.scheduler.impl.ExcutorTaskScheduler;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.reactivetoolbox.core.scheduler.SchedulerError.TIMEOUT;

public interface TaskScheduler {
    Timeout DEFAULT_TIMEOUT = Timeout.of(30).sec();

    default <T> Promise<Either<? extends BaseError, T>> submit(final Consumer<Promise<Either<? extends BaseError, T>>> task) {
        return submit(task, TIMEOUT::asFailure, DEFAULT_TIMEOUT);
    }

    default <T> Promise<T> submit(final Consumer<Promise<T>> task, final Supplier<T> timeoutResultSupplier) {
        return submit(task, timeoutResultSupplier, DEFAULT_TIMEOUT);
    }

    default <T> Promise<T> submit(final Consumer<Promise<T>> task, final Supplier<T> timeoutResultSupplier, final Timeout timeout) {
        return submit(Promise.<T>give().with(timeout, timeoutResultSupplier), task);
    }

    <T> Promise<T> submit(final Promise<T> promise, final Consumer<Promise<T>> task);

    static TaskScheduler with(final int size) {
        return ExcutorTaskScheduler.with(size);
    }
}

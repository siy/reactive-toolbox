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
import org.reactivetoolbox.core.scheduler.impl.ExcutorTaskScheduler;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.reactivetoolbox.core.scheduler.SchedulerError.TIMEOUT;

//TODO: JavaDoc
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

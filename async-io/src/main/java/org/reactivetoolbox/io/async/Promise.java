package org.reactivetoolbox.io.async;

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
import org.reactivetoolbox.core.lang.Tuple.Tuple5;
import org.reactivetoolbox.core.lang.Tuple.Tuple6;
import org.reactivetoolbox.core.lang.Tuple.Tuple7;
import org.reactivetoolbox.core.lang.Tuple.Tuple8;
import org.reactivetoolbox.core.lang.Tuple.Tuple9;
import org.reactivetoolbox.core.lang.collection.List;
import org.reactivetoolbox.core.lang.functional.Failure;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.log.CoreLogger;
import org.reactivetoolbox.io.async.impl.PromiseImpl;
import org.reactivetoolbox.io.scheduler.Timeout;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.reactivetoolbox.core.lang.Tuple.Tuple1;
import static org.reactivetoolbox.core.lang.Tuple.Tuple2;
import static org.reactivetoolbox.core.lang.Tuple.Tuple3;
import static org.reactivetoolbox.core.lang.Tuple.Tuple4;
import static org.reactivetoolbox.core.lang.Tuple.tuple;
import static org.reactivetoolbox.core.lang.collection.List.list;
import static org.reactivetoolbox.io.async.util.ActionableThreshold.threshold;

/**
 * Extended {@link Promise} implementation which works with {@link Result} values.
 */
public interface Promise<T> {
    /**
     * Perform user-provided action once this instance will be resolved. Action will be executed once regardless if instance is already resolved or not. User may add as many
     * actions as necessary.
     *
     * @param action
     *         Action to perform
     * @return Current instance
     */
    Promise<T> onResult(final Consumer<Result<T>> action);

    default Promise<T> thenDo(final Runnable action) {
        return onResult(ignored -> action.run());
    }

    default <R> Promise<R> chainTo(final Promise<R> promise, final FN1<R, T> mapper) {
        onResult(result -> promise.resolve(result.map(mapper)));
        return promise;
    }

    default Promise<T> chainTo(final Promise<T> promise) {
        return chainTo(promise, FN1.id());
    }

    /**
     * Run specified task asynchronously. Current instance of {@link Promise} is passed to the task as a parameter.
     *
     * @param task
     *         Task to execute with this promise
     * @return Current instance
     */
    Promise<T> async(final Consumer<Promise<T>> task);

    /**
     * Run specified task asynchronously and for I/O operations. Current instance of {@link Promise} and I/O {@link Submitter} are passed as a parameters.
     *
     * @param task
     *         Task to execute
     * @return Current instance
     */
    Promise<T> async(final BiConsumer<Promise<T>, Submitter> task);

    /**
     * Run specified task asynchronously after specified timeout expires. Current instance of {@link Promise} is passed to the task as a parameter.
     *
     * @param task
     *         Task to execute with this promise
     * @return Current instance
     */
    Promise<T> async(final Timeout timeout, final Consumer<Promise<T>> task);

    //Promise<T> async(final Timeout timeout, final BiConsumer<Promise<T>, Submitter> task);

    /**
     * Synchronously wait for this instance resolution. <br/> This method is provided only for testing purposes, it is not recommended to use it in production code.
     *
     * @return Current instance
     */
    Promise<T> syncWait();

    /**
     * Synchronously wait for this instance resolution or timeout.
     * <p>
     * If timeout expires and instance is not resolved, then it is resolved with {@link Errors#TIMEOUT}.
     * <p>
     * This method is provided only for testing purposes, it is not recommended to use it in production code.
     *
     * @param timeout
     *         Timeout amount
     * @return Current instance
     */
    Promise<T> syncWait(final Timeout timeout);

    /**
     * Resolve the promise with specified result. All actions already waiting for resolution will be scheduled for synchronous execution.
     *
     * @param result
     *         The value which will be stored in this instance and make it resolved
     * @return Current instance
     */
    Promise<T> syncResolve(final Result<T> result);

    /**
     * Resolve the promise with specified result. All actions already waiting for resolution will be scheduled for asynchronous execution.
     *
     * @param result
     *         The value which will be stored in this instance and make it resolved
     * @return Current instance
     */
    Promise<T> resolve(final Result<T> result);

    /**
     * Resolve current instance with successful result.
     *
     * @param result
     *         Successful result value
     * @return Current instance
     */
    default Promise<T> syncOk(final T result) {
        return syncResolve(Result.ok(result));
    }

    /**
     * Resolve current instance with successful result asynchronously.
     *
     * @param result
     *         Successful result value
     * @return Current instance
     */
    default Promise<T> ok(final T result) {
        return resolve(Result.ok(result));
    }

    /**
     * Resolve current instance with failure result.
     *
     * @param failure
     *         Failure result value
     * @return Current instance
     */
    default Promise<T> syncFail(final Failure failure) {
        return syncResolve(Result.fail(failure));
    }

    /**
     * Resolve current instance with failure result asynchronously.
     *
     * @param failure
     *         Failure result value
     * @return Current instance
     */
    default Promise<T> fail(final Failure failure) {
        return resolve(Result.fail(failure));
    }

    /**
     * Set timeout for instance resolution. When timeout expires, instance will be resolved with specified timeout result.
     *
     * @param timeout
     *         Timeout amount
     * @param timeoutResult
     *         Resolution value in case of timeout
     * @return Current instance
     */
    default Promise<T> when(final Timeout timeout, final Result<T> timeoutResult) {
        return when(timeout, () -> timeoutResult);
    }

    default Promise<T> when(final Option<Timeout> timeout, final Result<T> timeoutResult) {
        return timeout.fold($ -> this, timeoutValue -> when(timeoutValue, () -> timeoutResult));
    }

    /**
     * Set timeout for instance resolution. When timeout expires, instance will be resolved with value returned by provided supplier. Resolution value is lazily evaluated.
     *
     * @param timeout
     *         Timeout amount
     * @param timeoutResultSupplier
     *         Supplier of resolution value in case of timeout
     * @return Current instance
     */
    default Promise<T> when(final Timeout timeout, final Supplier<Result<T>> timeoutResultSupplier) {
        return async(timeout, promise -> promise.syncResolve(timeoutResultSupplier.get()));
    }

    default Promise<T> onSuccess(final Consumer<T> consumer) {
        onResult(result -> result.onSuccess(consumer));
        return this;
    }

    default Promise<T> onFailure(final Consumer<? super Failure> consumer) {
        onResult(result -> result.onFailure(consumer));
        return this;
    }

    /**
     * Resolve instance with {@link Errors#CANCELLED}. Often necessary if pending request need to be resolved prematurely, without waiting for response or timeout.
     *
     * @return Current instance
     */
    default Promise<T> syncCancel() {
        syncFail(Errors.CANCELLED);
        return this;
    }

    default Promise<T> cancel() {
        fail(Errors.CANCELLED);
        return this;
    }

    /**
     * Invoke provided mapping function once successful result will be available. If current instance is resolved to failure, no invocation is performed and error value is returned
     * instead.
     *
     * @param mapper
     *         Function to call if current instance is resolved with success.
     * @return Created instance which represents result of resolution of current instance (if current instance is resolved to failure) or result of invocation of provided function
     *         (if current instance is resolved to success).
     */
    default <R> Promise<R> flatMap(final FN1<Promise<R>, T> mapper) {
        return promise(promise -> onResult(result -> result.fold(error -> promise.resolve(Result.fail(error)),
                                                                 success -> mapper.apply(success)
                                                                                  .onResult(promise::resolve))));
    }

    /**
     * Same as {@link Promise#flatMap(FN1)} except that synchronous versions of resolution methods are used.
     *
     * @param mapper
     *         Function to call if current instance is resolved with success.
     * @return Created instance which represents result of resolution of current instance (if current instance is resolved to failure) or result of invocation of provided function
     *         (if current instance is resolved to success).
     */
    default <R> Promise<R> syncFlatMap(final FN1<Promise<R>, T> mapper) {
        return promise(promise -> onResult(result -> result.fold(error -> promise.syncResolve(Result.fail(error)),
                                                                 success -> mapper.apply(success)
                                                                                  .onResult(promise::syncResolve))));
    }

    /**
     * Convenience method which provides access to inner value of successful result. If current instance contains failure, then mapping function is not called and created instance
     * is resolved with same error as current instance.
     *
     * @param mapper
     *         Function to transform successful result value if current instance is resolved with success
     * @return Created instance
     */
    default <R> Promise<R> syncMap(final FN1<R, T> mapper) {
        final var result = Promise.<R>promise();
        onResult(value -> result.syncResolve(value.map(mapper)));
        return result;
    }

    default <R> Promise<R> map(final FN1<R, T> mapper) {
        final var result = Promise.<R>promise();
        onResult(value -> result.resolve(value.map(mapper)));
        return result;
    }

    default <R> Promise<R> syncMapResult(final FN1<Result<R>, T> mapper) {
        final var result = Promise.<R>promise();
        onResult(value -> result.syncResolve(value.flatMap(mapper)));
        return result;
    }

    default <R> Promise<R> mapResult(final FN1<Result<R>, T> mapper) {
        final var result = Promise.<R>promise();
        onResult(value -> result.resolve(value.flatMap(mapper)));
        return result;
    }

    /**
     * Get internal logger instance.
     *
     * @return logger instance used to log {@link Promise} internal events
     */
    CoreLogger logger();

    /**
     * Configure consumer for exceptions which may happen during {@link Promise} resolution, in particular during invocation of attached actions triggered by resolution. By default
     * these exceptions are logged, but user may define custom exception processing.
     *
     * @param consumer
     *         Consumer for intercepted exceptions.
     */
    static void exceptionConsumer(final Consumer<Throwable> consumer) {
        PromiseImpl.exceptionConsumer(consumer);
    }

    /**
     * Create new unresolved instance.om
     *
     * @return Created instance
     */
    static <T> Promise<T> promise() {
        return PromiseImpl.promise();
    }

    /**
     * Create instance and immediately invoke provided function with created instance. Usually this function is used to configure actions on created instance.
     *
     * @param setup
     *         Function to invoke with created instance
     * @return Created instance
     */
    static <T> Promise<T> promise(final Consumer<Promise<T>> setup) {
        final var result = Promise.<T>promise();

        setup.accept(result);

        return result;
    }

    /**
     * Create instance and asynchronously start specified task with created instance.
     *
     * @param task
     *         Function to invoke with created instance
     * @return Created instance
     */
    static <T> Promise<T> asyncPromise(final Consumer<Promise<T>> task) {
        return Promise.<T>promise().async(task);
    }

    /**
     * Create instance and asynchronously start specified I/O task with created instance.
     * <p>
     * This method is similar to {@link Promise#asyncPromise(Consumer)} except task can perform I/O operations using {@link Submitter} passed as a parameter.
     *
     * @param task
     *         Function to invoke with created instance
     * @return Created instance
     */
    static <T> Promise<T> asyncPromise(final BiConsumer<Promise<T>, Submitter> task) {
        return Promise.<T>promise().async(task);
    }

    /**
     * Create new resolved instance.
     *
     * @return Created instance
     */
    static <T> Promise<T> ready(final Result<T> result) {
        return Promise.<T>promise().syncResolve(result);
    }

    /**
     * Create new resolved instance.
     *
     * @return Created instance
     */
    static <T> Promise<T> readyOk(final T result) {
        return ready(Result.ok(result));
    }

    /**
     * Create new resolved instance.
     *
     * @return Created instance
     */
    static <T> Promise<T> readyFail(final Failure failure) {
        return ready(Result.fail(failure));
    }

    /**
     * Create instance for synchronization point of type {@code ANY}.
     * <p>
     * The returned instance will be resolved once any of the promises provided as a parameters will be resolved. Remaining promises are cancelled.
     *
     * @param promises
     *         Input promises
     * @return created instance
     */
    @SafeVarargs
    static <T> Promise<T> any(final Promise<T>... promises) {
        return promise(result -> list(promises).apply(promise -> promise.onResult(v -> {
            result.syncResolve(v);
            cancelAll(promises);
        })));
    }

    /**
     * Create instance for synchronization point of type {@code ANY}.
     * <p>
     * The returned instance which will be resolved once any of the promises provided as a parameters will be resolved with success. If none of the promises will be resolved with
     * success, then created instance will be resolved with {@link Errors#CANCELLED}.
     *
     * @param promises
     *         Input promises
     * @return Created instance
     */
    @SafeVarargs
    static <T> Promise<T> anySuccess(final Promise<T>... promises) {
        return anySuccess(Result.fail(Errors.CANCELLED), promises);
    }

    /**
     * Create instance for synchronization point of type {@code ANY}.
     * <p>
     * The returned instance will be resolved once any of the promises provided as a parameters will be resolved with success. If none of the promises will be resolved with
     * success, then created instance will be resolved with provided {@code failureResult}.
     *
     * @param failureResult
     *         Result in case if no instances were resolved with success
     * @param promises
     *         Input promises
     * @return Created instance
     */
    static <T> Promise<T> anySuccess(final Result<T> failureResult, final Promise<T>... promises) {
        return Promise.promise(anySuccess -> threshold(promises.length,
                                                       at -> list(promises)
                                                               .apply(promise -> promise.onResult(result -> {
                                                                   result.onSuccess(succ -> {
                                                                       anySuccess.syncOk(succ);
                                                                       cancelAll(promises);
                                                                   });
                                                                   at.registerEvent();
                                                               })),
                                                       () -> anySuccess.syncResolve(failureResult)));
    }

    /**
     * Cancel several promises at once.
     *
     * @param promises
     *         Promises to cancel.
     */
    static <T> void cancelAll(final Promise<T>... promises) {
        list(promises).apply(Promise::syncCancel);
    }

    /**
     * Resolve several promises at once with the same result.
     *
     * @param result
     *         Resolution result
     * @param promises
     *         Promises to resolve
     */
    static <T> void resolveAll(final Result<T> result, final Promise<T>... promises) {
        list(promises)
                .apply(promise -> promise.syncResolve(result));
    }

    static <T> Promise<List<Result<T>>> allOf(final List<Promise<T>> promises) {
        final var results = new Result[promises.size()];

        return promise(result -> threshold(Tuple1.size(),
                                           at -> promises.applyN((ndx, element) -> element.onResult(value -> {
                                               results[ndx] = value;
                                               at.registerEvent();
                                           })),
                                           () -> result.ok(list(results))));
    }

    @SafeVarargs
    static <T> Promise<List<Result<T>>> allOf(final Promise<T>... promises) {
        return allOf(List.list(promises));
    }

    static <T> Promise<List<T>> flatAllOf(final List<Promise<T>> promises) {
        return allOf(promises).syncMapResult(Result::flatten);
    }

    static <T> Promise<List<T>> flatAllOf(final Promise<T>... promises) {
        return flatAllOf(List.list(promises));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     *
     * @param promise1
     *         Input promise
     * @return Created instance
     */
    static <T1> Promise<Tuple1<T1>> all(final Promise<T1> promise1) {
        return promise(promise -> threshold(Tuple1.size(),
                                            at -> promise1.thenDo(at::registerEvent),
                                            () -> promise1.onResult(
                                                    v1 -> promise.syncResolve(tuple(v1).map(Result::flatten)))));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     *
     * @param promise1
     *         Input promise
     * @param promise2
     *         Input promise
     * @return Created instance
     */
    static <T1, T2> Promise<Tuple2<T1, T2>> all(final Promise<T1> promise1,
                                                final Promise<T2> promise2) {
        return promise(promise -> threshold(Tuple2.size(),
                                            at -> {
                                                promise1.thenDo(at::registerEvent);
                                                promise2.thenDo(at::registerEvent);
                                            },
                                            () -> promise1.onResult(
                                                    v1 -> promise2.onResult(
                                                            v2 -> promise.syncResolve(
                                                                    tuple(v1, v2).map(Result::flatten))))));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     *
     * @param promise1
     *         Input promise
     * @param promise2
     *         Input promise
     * @param promise3
     *         Input promise
     * @return Created instance
     */
    static <T1, T2, T3> Promise<Tuple3<T1, T2, T3>> all(final Promise<T1> promise1,
                                                        final Promise<T2> promise2,
                                                        final Promise<T3> promise3) {
        return promise(promise -> threshold(Tuple3.size(),
                                            at -> {
                                                promise1.thenDo(at::registerEvent);
                                                promise2.thenDo(at::registerEvent);
                                                promise3.thenDo(at::registerEvent);
                                            },
                                            () -> promise1.onResult(
                                                    v1 -> promise2.onResult(
                                                            v2 -> promise3.onResult(
                                                                    v3 -> promise.syncResolve(
                                                                            tuple(v1, v2, v3).map(Result::flatten)))))));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     *
     * @param promise1
     *         Input promise
     * @param promise2
     *         Input promise
     * @param promise3
     *         Input promise
     * @param promise4
     *         Input promise
     * @return Created instance
     */
    static <T1, T2, T3, T4> Promise<Tuple4<T1, T2, T3, T4>> all(final Promise<T1> promise1,
                                                                final Promise<T2> promise2,
                                                                final Promise<T3> promise3,
                                                                final Promise<T4> promise4) {
        return promise(promise -> threshold(Tuple4.size(),
                                            at -> {
                                                promise1.thenDo(at::registerEvent);
                                                promise2.thenDo(at::registerEvent);
                                                promise3.thenDo(at::registerEvent);
                                                promise4.thenDo(at::registerEvent);
                                            },
                                            () -> promise1.onResult(
                                                    v1 -> promise2.onResult(
                                                            v2 -> promise3.onResult(
                                                                    v3 -> promise4.onResult(
                                                                            v4 -> promise.syncResolve(
                                                                                    tuple(v1, v2, v3, v4).map(Result::flatten))))))));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     *
     * @param promise1
     *         Input promise
     * @param promise2
     *         Input promise
     * @param promise3
     *         Input promise
     * @param promise4
     *         Input promise
     * @param promise5
     *         Input promise
     * @return Created instance
     */
    static <T1, T2, T3, T4, T5> Promise<Tuple5<T1, T2, T3, T4, T5>> all(final Promise<T1> promise1,
                                                                        final Promise<T2> promise2,
                                                                        final Promise<T3> promise3,
                                                                        final Promise<T4> promise4,
                                                                        final Promise<T5> promise5) {
        return promise(promise -> threshold(Tuple5.size(),
                                            at -> {
                                                promise1.thenDo(at::registerEvent);
                                                promise2.thenDo(at::registerEvent);
                                                promise3.thenDo(at::registerEvent);
                                                promise4.thenDo(at::registerEvent);
                                                promise5.thenDo(at::registerEvent);
                                            },
                                            () -> promise1.onResult(
                                                    v1 -> promise2.onResult(
                                                            v2 -> promise3.onResult(
                                                                    v3 -> promise4.onResult(
                                                                            v4 -> promise5.onResult(
                                                                                    v5 -> promise.syncResolve(
                                                                                            tuple(v1, v2, v3, v4, v5)
                                                                                                    .map(Result::flatten)))))))));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     *
     * @param promise1
     *         Input promise
     * @param promise2
     *         Input promise
     * @param promise3
     *         Input promise
     * @param promise4
     *         Input promise
     * @param promise5
     *         Input promise
     * @param promise6
     *         Input promise
     * @return Created instance
     */
    static <T1, T2, T3, T4, T5, T6> Promise<Tuple6<T1, T2, T3, T4, T5, T6>> all(final Promise<T1> promise1,
                                                                                final Promise<T2> promise2,
                                                                                final Promise<T3> promise3,
                                                                                final Promise<T4> promise4,
                                                                                final Promise<T5> promise5,
                                                                                final Promise<T6> promise6) {
        return promise(promise -> threshold(Tuple6.size(),
                                            at -> {
                                                promise1.thenDo(at::registerEvent);
                                                promise2.thenDo(at::registerEvent);
                                                promise3.thenDo(at::registerEvent);
                                                promise4.thenDo(at::registerEvent);
                                                promise5.thenDo(at::registerEvent);
                                                promise6.thenDo(at::registerEvent);
                                            },
                                            () -> promise1.onResult(
                                                    v1 -> promise2.onResult(
                                                            v2 -> promise3.onResult(
                                                                    v3 -> promise4.onResult(
                                                                            v4 -> promise5.onResult(
                                                                                    v5 -> promise6.onResult(
                                                                                            v6 -> promise.syncResolve(
                                                                                                    tuple(v1, v2, v3, v4, v5, v6)
                                                                                                            .map(Result::flatten))))))))));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     *
     * @param promise1
     *         Input promise
     * @param promise2
     *         Input promise
     * @param promise3
     *         Input promise
     * @param promise4
     *         Input promise
     * @param promise5
     *         Input promise
     * @param promise6
     *         Input promise
     * @param promise7
     *         Input promise
     * @return Created instance
     */
    static <T1, T2, T3, T4, T5, T6, T7> Promise<Tuple7<T1, T2, T3, T4, T5, T6, T7>> all(final Promise<T1> promise1,
                                                                                        final Promise<T2> promise2,
                                                                                        final Promise<T3> promise3,
                                                                                        final Promise<T4> promise4,
                                                                                        final Promise<T5> promise5,
                                                                                        final Promise<T6> promise6,
                                                                                        final Promise<T7> promise7) {
        return promise(promise -> threshold(Tuple7.size(),
                                            at -> {
                                                promise1.thenDo(at::registerEvent);
                                                promise2.thenDo(at::registerEvent);
                                                promise3.thenDo(at::registerEvent);
                                                promise4.thenDo(at::registerEvent);
                                                promise5.thenDo(at::registerEvent);
                                                promise6.thenDo(at::registerEvent);
                                                promise7.thenDo(at::registerEvent);
                                            },
                                            () -> promise1.onResult(
                                                    v1 -> promise2.onResult(
                                                            v2 -> promise3.onResult(
                                                                    v3 -> promise4.onResult(
                                                                            v4 -> promise5.onResult(
                                                                                    v5 -> promise6.onResult(
                                                                                            v6 -> promise7.onResult(
                                                                                                    v7 -> promise.syncResolve(
                                                                                                            tuple(v1, v2, v3, v4, v5, v6, v7)
                                                                                                                    .map(Result::flatten)))))))))));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     *
     * @param promise1
     *         Input promise
     * @param promise2
     *         Input promise
     * @param promise3
     *         Input promise
     * @param promise4
     *         Input promise
     * @param promise5
     *         Input promise
     * @param promise6
     *         Input promise
     * @param promise7
     *         Input promise
     * @param promise8
     *         Input promise
     * @return Created instance
     */
    static <T1, T2, T3, T4, T5, T6, T7, T8> Promise<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> all(final Promise<T1> promise1,
                                                                                                final Promise<T2> promise2,
                                                                                                final Promise<T3> promise3,
                                                                                                final Promise<T4> promise4,
                                                                                                final Promise<T5> promise5,
                                                                                                final Promise<T6> promise6,
                                                                                                final Promise<T7> promise7,
                                                                                                final Promise<T8> promise8) {
        return promise(promise -> threshold(Tuple8.size(),
                                            at -> {
                                                promise1.thenDo(at::registerEvent);
                                                promise2.thenDo(at::registerEvent);
                                                promise3.thenDo(at::registerEvent);
                                                promise4.thenDo(at::registerEvent);
                                                promise5.thenDo(at::registerEvent);
                                                promise6.thenDo(at::registerEvent);
                                                promise7.thenDo(at::registerEvent);
                                                promise8.thenDo(at::registerEvent);
                                            },
                                            () -> promise1.onResult(
                                                    v1 -> promise2.onResult(
                                                            v2 -> promise3.onResult(
                                                                    v3 -> promise4.onResult(
                                                                            v4 -> promise5.onResult(
                                                                                    v5 -> promise6.onResult(
                                                                                            v6 -> promise7.onResult(
                                                                                                    v7 -> promise8.onResult(
                                                                                                            v8 -> promise.syncResolve(
                                                                                                                    tuple(v1, v2, v3, v4, v5, v6, v7, v8)
                                                                                                                            .map(Result::flatten))))))))))));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     *
     * @param promise1
     *         Input promise
     * @param promise2
     *         Input promise
     * @param promise3
     *         Input promise
     * @param promise4
     *         Input promise
     * @param promise5
     *         Input promise
     * @param promise6
     *         Input promise
     * @param promise7
     *         Input promise
     * @param promise8
     *         Input promise
     * @param promise9
     *         Input promise
     * @return Created instance
     */
    static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Promise<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> all(final Promise<T1> promise1,
                                                                                                        final Promise<T2> promise2,
                                                                                                        final Promise<T3> promise3,
                                                                                                        final Promise<T4> promise4,
                                                                                                        final Promise<T5> promise5,
                                                                                                        final Promise<T6> promise6,
                                                                                                        final Promise<T7> promise7,
                                                                                                        final Promise<T8> promise8,
                                                                                                        final Promise<T9> promise9) {
        return promise(promise -> threshold(Tuple9.size(),
                                            at -> {
                                                promise1.thenDo(at::registerEvent);
                                                promise2.thenDo(at::registerEvent);
                                                promise3.thenDo(at::registerEvent);
                                                promise4.thenDo(at::registerEvent);
                                                promise5.thenDo(at::registerEvent);
                                                promise6.thenDo(at::registerEvent);
                                                promise7.thenDo(at::registerEvent);
                                                promise8.thenDo(at::registerEvent);
                                                promise9.thenDo(at::registerEvent);
                                            },
                                            () -> promise1.onResult(
                                                    v1 -> promise2.onResult(
                                                            v2 -> promise3.onResult(
                                                                    v3 -> promise4.onResult(
                                                                            v4 -> promise5.onResult(
                                                                                    v5 -> promise6.onResult(
                                                                                            v6 -> promise7.onResult(
                                                                                                    v7 -> promise8.onResult(
                                                                                                            v8 -> promise9.onResult(
                                                                                                                    v9 -> promise.syncResolve(
                                                                                                                            tuple(v1, v2, v3, v4, v5, v6, v7, v8, v9)
                                                                                                                                    .map(Result::flatten)))))))))))));
    }
}

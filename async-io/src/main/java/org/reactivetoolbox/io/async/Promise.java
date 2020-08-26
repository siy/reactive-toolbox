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
import org.reactivetoolbox.core.lang.Tuple;
import org.reactivetoolbox.core.lang.Tuple.Tuple5;
import org.reactivetoolbox.core.lang.Tuple.Tuple6;
import org.reactivetoolbox.core.lang.Tuple.Tuple7;
import org.reactivetoolbox.core.lang.Tuple.Tuple8;
import org.reactivetoolbox.core.lang.Tuple.Tuple9;
import org.reactivetoolbox.core.lang.collection.List;
import org.reactivetoolbox.core.lang.functional.Failure;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.log.CoreLogger;
import org.reactivetoolbox.io.async.impl.PromiseImpl;
import org.reactivetoolbox.io.async.util.BooleanLatch;
import org.reactivetoolbox.io.scheduler.TaskScheduler;
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
 * Promise represents a result of the asynchronous operation which might not be yet available. Since result might be not yet
 * available, instead of providing access to result, Promise allows to register actions which will be executed when result became
 * available. Every action is executed only once. If result is already available, then action is executed at the moment of its
 * registration in the context of current thread.
 * <p>
 * Every action should be as short as possible and should not block execution. Each action is guaranteed to be executed by
 * exactly one thread so there is no need to perform any additional synchronization.
 * <p>
 * Most Promise API's provided in two versions - synchronous and asynchronous. Synchronous version handles request in context of
 * current thread. Asynchronous version submits asynchronous task where request will be handled. The chosen method naming assumes
 * that by default operations should be performed asynchronously.
 * <p>
 * Beside basic operation on single Promise instance this interface also provides access operations combining several Promises:
 * {@link #all}, {@link #allOf}, {@link #any} and {@link #anySuccess}. These operations allow to synchronise execution of several
 * independent asynchronous operations in a non-blocking fashion.
 * <p>
 * The Promise API also provides access to underlying task scheduler via {@link #async(Consumer)} API. The {@link #async(BiConsumer)}
 * method provides access to asynchronous I/O API via {@link Submitter} interface.
 */
public interface Promise<T> {
    /**
     * Run specified task asynchronously. Current instance of {@link Promise} is passed to the task as a parameter.
     *
     * @param task
     *         Task to execute with this promise
     * @return Current instance for fluent call chaining
     */
    Promise<T> async(final Consumer<Promise<T>> task);

    /**
     * Run specified task asynchronously and for I/O operations. Current instance of {@link Promise} and I/O {@link Submitter}
     * are passed as a parameters to provided {@link BiConsumer}.
     *
     * @param task
     *         Task to execute
     * @return Current instance for fluent call chaining
     */
    Promise<T> async(final BiConsumer<Promise<T>, Submitter> task);

    /**
     * Run specified task asynchronously when specified timeout expires. Current instance of {@link Promise} is passed to the task as a parameter.
     *
     * @param task
     *         Task to execute with this promise
     * @return Current instance for fluent call chaining
     */
    Promise<T> async(final Timeout timeout, final Consumer<Promise<T>> task);

    /**
     * Synchronously wait for this instance resolution.
     * <p>
     * WARNING: This method is a no-op unless Promise instance is specifically obtained via {@link #waitablePromise()} or {@link #waitablePromise(Consumer)}
     * methods.
     *
     * @return Current instance for fluent call chaining
     */
    Promise<T> syncWait();

    /**
     * Synchronously wait for this instance resolution or timeout.
     * <p>
     * If timeout expires and instance is not resolved, then it is resolved with {@link Errors#TIMEOUT}.
     * <p>
     * WARNING: This method is a no-op unless Promise instance is specifically obtained via {@link #waitablePromise()} or {@link #waitablePromise(Consumer)}
     * methods.
     *
     * @param timeout
     *         Timeout amount
     * @return Current instance
     */
    Promise<T> syncWait(final Timeout timeout);

    /**
     * Resolve the promise with specified result. All actions already waiting for resolution will be scheduled for asynchronous execution.
     * <p>
     * Note that resolution may happen only once. All subsequent resolutions will be ignored.
     *
     * @param result
     *         The value which will be stored in this instance and make it resolved
     * @return Current instance
     */
    Promise<T> resolve(final Result<T> result);

    /**
     * Resolve the promise with specified result. All actions already waiting for resolution will be executed in context of current thread.
     * <p>
     * Note that resolution may happen only once. All subsequent resolutions will be ignored.
     *
     * @param result
     *         The value which will be stored in this instance and make it resolved
     * @return Current instance
     */
    Promise<T> syncResolve(final Result<T> result, final Submitter submitter);

    /**
     * Asynchronously resolve current instance with successful result.
     * <p>
     * Note that resolution may happen only once. All subsequent resolutions will be ignored.
     *
     * @param result
     *         Successful result value
     * @return Current instance
     */
    default Promise<T> ok(final T result) {
        return resolve(Result.ok(result));
    }

    /**
     * Synchronously resolve current instance with successful result.
     * <p>
     * Note that resolution may happen only once. All subsequent resolutions will be ignored.
     *
     * @param result
     *         Successful result value
     * @return Current instance
     */
    default Promise<T> syncOk(final T result, final Submitter submitter) {
        return syncResolve(Result.ok(result), submitter);
    }

    /**
     * Asynchronously resolve current instance with failure result.
     * <p>
     * Note that resolution may happen only once. All subsequent resolutions will be ignored.
     *
     * @param failure
     *         Failure result value
     * @return Current instance
     */
    default Promise<T> fail(final Failure failure) {
        return resolve(Result.fail(failure));
    }

    /**
     * Synchronously resolve current instance with failure result.
     * <p>
     * Note that resolution may happen only once. All subsequent resolutions will be ignored.
     *
     * @param failure
     *         Failure result value
     * @return Current instance
     */
    default Promise<T> syncFail(final Failure failure, final Submitter submitter) {
        return syncResolve(Result.fail(failure), submitter);
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

    /**
     * Set timeout for instance resolution. When timeout expires, instance will be resolved with value returned by provided supplier.
     *
     * @param timeout
     *         Timeout amount
     * @param timeoutResultSupplier
     *         Supplier of resolution value in case of timeout
     * @return Current instance
     */
    default Promise<T> when(final Timeout timeout, final Supplier<Result<T>> timeoutResultSupplier) {
        return async(timeout, promise -> promise.resolve(timeoutResultSupplier.get()));
    }

    /**
     * Perform action provided by user when this instance will be resolved. Action will be executed exactly once regardless if instance is already resolved or not. User may add as
     * many actions as necessary. Note that provided {@link Consumer} action will be invoked regardless from the result success or failure.
     *
     * @param action
     *         Action to perform on received result
     * @return Current instance for fluent call chaining
     */
    Promise<T> onResult(final Consumer<Result<T>> action);

    Promise<T> onResult(final Submitter submitter, final BiConsumer<Result<T>, Submitter> action);

    /**
     * Perform action provided by user when this instance will be resolved. Action will be executed exactly once regardless if instance is already resolved or not. User may add as
     * many actions as necessary. Note that provided {@link Runnable} action will be invoked regardless from the result success or failure.
     *
     * @param action
     *         Action to perform on received result
     * @return Current instance for fluent call chaining
     */
    default Promise<T> thenDo(final Runnable action) {
        return onResult(ignored -> action.run());
    }

    /**
     * Perform special action once this instance will be resolved. The action will transform Result from current Promise instance and then resolve input Promise instance with
     * transformed result.
     *
     * @param promise
     *         Input instance to resolve.
     * @param mapper
     *         Transformation function
     * @return Current instance for fluent call chaining
     */
    default <R> Promise<R> chainTo(final Promise<R> promise, final FN1<R, T> mapper) {
        onResult(result -> promise.resolve(result.map(mapper)));
        return promise;
    }

    /**
     * Perform special action once this instance will be resolved. The action will take Result from current Promise instance and then resolve input Promise with the same result.
     *
     * @param promise
     *         Input instance to resolve.
     * @return Current instance for fluent call chaining
     */
    default Promise<T> chainTo(final Promise<T> promise) {
        onResult(promise::resolve);
        return promise;
    }

    /**
     * Same as {@link Promise#chainTo(Promise, FN1)} except resolution of input promise is done synchronously.
     *
     * @param promise
     *         Input instance to resolve.
     * @param mapper
     *         Transformation function
     * @return Current instance for fluent call chaining
     */
    default <R> Promise<R> syncChainTo(final Promise<R> promise, final FN1<R, T> mapper, final Submitter submitter) {
        onResult(submitter, (result, asyncSubmitter) -> promise.syncResolve(result.map(mapper), asyncSubmitter));
        return promise;
    }

    /**
     * Same as {@link Promise#chainTo(Promise)} except resolution of input promise is done synchronously.
     *
     * @param promise
     *         Input instance to resolve.
     * @return Current instance for fluent call chaining
     */
    default Promise<T> syncChainTo(final Promise<T> promise, final Submitter submitter) {
        onResult(submitter, promise::syncResolve);
        return promise;
    }

    /**
     * Perform action provided by user when this instance will be resolved with success. If instance will be resolved with failure result then
     * action will be ignored.
     *
     * @param action
     *         Action to perform on success result
     * @return Current instance for fluent call chaining
     */
    Promise<T> onSuccess(final Consumer<T> action);

    /**
     * Perform action provided by user when this instance will be resolved with failure. If instance will be resolved with success result then
     * action will be ignored.
     *
     * @param action
     *         Action to perform on failure result
     * @return Current instance for fluent call chaining
     */
    Promise<T> onFailure(final Consumer<? super Failure> action);

    /**
     * Convenience method to asynchronously resolve instance with {@link Errors#CANCELLED}.
     * <p>
     * Useful in cases when pending request need to be resolved prematurely,
     * without waiting for response or timeout.
     *
     * @return Current instance for fluent call chaining
     */
    default Promise<T> cancel() {
        fail(Errors.CANCELLED);
        return this;
    }

//    /**
//     * Convenience method to synchronously resolve instance with {@link Errors#CANCELLED}.
//     * <p>
//     * Useful in cases when pending request need to be resolved prematurely,
//     * without waiting for response or timeout.
//     *
//     * @return Current instance for fluent call chaining
//     */
//    default Promise<T> syncCancel() {
//        syncFail(Errors.CANCELLED);
//        return this;
//    }

    /**
     * Compose current Promise instance with subsequent call which also returns Promise and requires data from current instance
     * to be available. The provided mapping function will be called once successful result available.
     * If current instance is resolved to failure, no invocation is performed and error value is returned instead.
     *
     * @param mapper
     *         Function to call if current instance is resolved with success.
     * @return New Promise instance which represents result of resolution of current instance (if current instance is resolved to failure) or result of invocation of provided function
     *         (if current instance is resolved to success).
     */
    default <R> Promise<R> flatMap(final FN1<Promise<R>, T> mapper) {
        final var resultPromise = PromiseImpl.<R>promise();

        onResult(result -> resultPromise.async((mapResult, submitter) -> result.fold(failure -> mapResult.syncResolve((Result<R>) result, submitter),
                                                                        success -> mapper.apply(success)
                                                                                         .onResult(submitter, mapResult::syncResolve))));
        return resultPromise;
    }

    /**
     * Same as {@link Promise#flatMap(FN1)} except that synchronous versions of resolution methods are used.
     *
     * @param mapper
     *         Function to call if current instance is resolved with success.
     * @return Created instance which represents result of resolution of current instance (if current instance is resolved to failure) or result of invocation of provided function
     *         (if current instance is resolved to success).
     */
    default <R> Promise<R> syncFlatMap(final FN1<Promise<R>, T> mapper, final Submitter immediate) {
        final var mapResult = PromiseImpl.<R>promise();

        onResult(immediate, (result, submitter) -> result.fold(failure -> mapResult.syncResolve((Result<R>) result, submitter),
                                       success -> mapper.apply(success)
                                                        .onResult(submitter, mapResult::syncResolve)));
        return mapResult;
    }

    /**
     * Compose current instance with the transformation function. Once current instance will be resolved with success result,
     * the provided mapping function will be used to transform result and resolve Promise instance returned by this method.
     * If current instance resolved with failure result, then mapping function is not called and failure result is propagated
     * to Promise instance returned by this method.
     *
     * @param mapper
     *         Function to transform successful result value if current instance is resolved with success
     * @return Created instance
     */
    default <R> Promise<R> map(final FN1<R, T> mapper) {
        final var result = PromiseImpl.<R>promise();

        onResult(value -> result.resolve(value.map(mapper)));
        return result;
    }

    /**
     * Synchronous version of {@link #map(FN1)} method.
     *
     * @param mapper
     *         Function to transform successful result value if current instance is resolved with success
     * @return Created instance
     */
    default <R> Promise<R> syncMap(final FN1<R, T> mapper, final Submitter immediate) {
        final var result = PromiseImpl.<R>promise();

        onResult(immediate, (value, submitter) -> result.syncResolve(value.map(mapper), submitter));
        return result;
    }

    /**
     * Compose current instance with the transformation function. Once current instance will be resolved with success result,
     * the provided mapping function will be used to transform result and resolve Promise instance returned by this method.
     * If current instance resolved with failure result, then mapping function is not called and failure result is propagated
     * to Promise instance returned by this method.
     * <p>
     * The method is similar to {@link #map(FN1)} except it can transform success result into both, success and failure results.
     *
     * @param mapper
     *         Function to transform successful result value if current instance is resolved with success
     * @return Created instance
     */
    default <R> Promise<R> mapResult(final FN1<Result<R>, T> mapper) {
        final var result = PromiseImpl.<R>promise();

        onResult(value -> result.resolve(value.flatMap(mapper)));
        return result;
    }

    /**
     * Synchronous version of {@link #mapResult(FN1)} method.
     *
     * @param mapper
     *         Function to transform successful result value if current instance is resolved with success
     * @return Created instance
     */
    default <R> Promise<R> syncMapResult(final FN1<Result<R>, T> mapper, final Submitter immediate) {
        final var result = PromiseImpl.<R>promise();

        onResult(immediate, (value, submitter) -> result.syncResolve(value.flatMap(mapper), submitter));
        return result;
    }

    /**
     * Get access to internal logger instance.
     *
     * @return logger instance used to log {@link Promise} internal events
     */
    CoreLogger logger();

    /**
     * Configure consumer for exceptions which may happen during {@link Promise} resolution, in particular during invocation of attached actions triggered by resolution.
     * By default these exceptions are logged, but user may define custom exception processing.
     *
     * @param consumer
     *         Consumer for intercepted exceptions.
     */
    static void exceptionConsumer(final Consumer<Throwable> consumer) {
        PromiseImpl.exceptionConsumer(consumer);
    }

    /**
     * Get information about current level of parallelism set for underlying {@link TaskScheduler}.
     *
     * @return number of threads executing Promise asynchronous tasks.
     */
    static int parallelism() {
        return PromiseImpl.schedulerParallelism();
    }

    /**
     * Create new unresolved Promise instance.
     *
     * @return Created instance
     */
    static <T> Promise<T> promise() {
        return PromiseImpl.promise();
    }

    /**
     * Create instance and synchronously invoke provided function with created instance.
     * Usually this function is used to configure actions on created instance.
     *
     * @param setup
     *         Function to invoke with created instance
     * @return Created instance
     */
    static <T> Promise<T> promise(final Consumer<Promise<T>> setup) {
        final var result = PromiseImpl.<T>promise();

        setup.accept(result);

        return result;
    }

    /**
     * Create a Promise instance with synchronous waiting functionality enabled.
     *
     * @see #syncWait()
     * @see #syncWait(Timeout)
     *
     * @return Created instance.
     */
    static <T> Promise<T> waitablePromise() {
        return new PromiseImpl<>(new BooleanLatch());
    }

    /**
     * Create a Promise instance with synchronous waiting functionality enabled and immediately call provided setup function.
     *
     * @see #syncWait()
     * @see #syncWait(Timeout)
     *
     * @return Created instance.
     */
    static <T> Promise<T> waitablePromise(final Consumer<Promise<T>> setup) {
        final var result = new PromiseImpl<T>(new BooleanLatch());

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
        return PromiseImpl.<T>promise().async(task);
    }

    /**
     * Create instance and asynchronously start specified task with created instance.
     * <p>
     * This method is similar to {@link Promise#asyncPromise(Consumer)} except task can perform I/O operations using {@link Submitter} passed as
     * a second parameter.
     *
     * @param task
     *         Function to invoke with created instance
     * @return Created instance
     */
    static <T> Promise<T> asyncPromise(final BiConsumer<Promise<T>, Submitter> task) {
        return PromiseImpl.<T>promise().async(task);
    }

    /**
     * Create new resolved instance.
     *
     * @return Created instance
     */
    static <T> Promise<T> ready(final Result<T> result) {
        return PromiseImpl.promise(result);
    }

    /**
     * Create new resolved instance. The instance is resolved with successful result and provided value.
     *
     * @return Created instance
     */
    static <T> Promise<T> readyOk(final T result) {
        return ready(Result.ok(result));
    }

    /**
     * Create new resolved instance. The instance is resolved with failure result and provided failure value.
     *
     * @return Created instance
     */
    static <T> Promise<T> readyFail(final Failure failure) {
        return ready(Result.fail(failure));
    }

    /**
     * Create instance for synchronization point of type {@code ANY}.
     * <p>
     * The returned instance will be resolved once any of the promises provided as a parameters will be resolved.
     * Remaining promises are automatically cancelled.
     *
     * @param promises
     *         Input promises
     * @return created instance
     */
    @SafeVarargs
    static <T> Promise<T> any(final Promise<T>... promises) {
        return promise(result -> list(promises).apply(promise -> promise.onResult(v -> {
            result.resolve(v);
            cancelAll(promises);
        })));
    }

    /**
     * Create instance for synchronization point of type {@code ANY}.
     * <p>
     * The returned instance will be resolved once any of the promises provided as a parameters will be resolved with success.
     * If none of the promises will be resolved with success, then created instance will be resolved with provided {@code failureResult}.
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
                                                                   result.onSuccess(anySuccess::ok)
                                                                         .onSuccessDo(() -> cancelAll(promises));
                                                                   at.registerEvent();
                                                               })),
                                                       () -> anySuccess.resolve(failureResult)));
    }

    /**
     * Create instance for synchronization point of type {@code ANY}.
     * <p>
     * The returned instance which will be resolved once any of the promises provided as a parameters will be resolved with success.
     * If none of the promises will be resolved with success, then created instance will be resolved with {@link Errors#CANCELLED}.
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
     * Cancel several promises at once.
     *
     * @param promises
     *         Promises to cancel.
     */
    static <T> void cancelAll(final Promise<T>... promises) {
        list(promises).apply(Promise::cancel);
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
                .apply(promise -> promise.resolve(result));
    }

    /**
     * Wait for several promises of same type. The returned instance resolved with the list of results returned by all Promise
     * instances contained in the input list.
     *
     * @param promises
     *      List of promises to wait.
     *
     * @return Promise which will be resolved with list of results from Promises passes in the input list.
     */
    static <T> Promise<List<Result<T>>> allOf(final List<Promise<T>> promises) {
        final var results = new Result[promises.size()];

        return promise(result -> threshold(Tuple1.size(),
                                           at -> promises.applyN((ndx, element) -> element.onResult(value -> {
                                               results[ndx] = value;
                                               at.registerEvent();
                                           })),
                                           () -> result.ok(list(results))));
    }

    /**
     * Wait for several promises of same type. The returned instance resolved with the list of results returned by all Promise
     * instances passed as a parameter.
     *
     * @param promises
     *      Promises to wait.
     *
     * @return Promise which will be resolved with list of results from Promises passes as a parameters.
     */
    @SafeVarargs
    static <T> Promise<List<Result<T>>> allOf(final Promise<T>... promises) {
        return allOf(List.list(promises));
    }

    /**
     * Same as {@link #allOf(List)} except returned list is "flattened". If all results in the list were successes, then returned
     * Promise instance is resolved with list of values contained in the results. If any result is failure then returned Promise
     * instance is resolved to failure.
     *
     * @param promises
     *      Promises to wait.
     * @return Promise with list of success values
     */
    static <T> Promise<List<T>> flatAllOf(final List<Promise<T>> promises) {
        return allOf(promises).mapResult(Result::flatten);
    }

    /**
     * Same as {@link #allOf(Promise[])} except returned list is "flattened". If all results in the list were successes, then returned
     * Promise instance is resolved with list of values contained in the results. If any result is failure then returned Promise
     * instance is resolved to failure.
     *
     * @param promises
     *      Promises to wait.
     * @return Promise with list of success values
     */
    static <T> Promise<List<T>> flatAllOf(final Promise<T>... promises) {
        return flatAllOf(List.list(promises));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with value from passed Promise instance if passed Promise instance
     * is resolved to success result. If passed Promise instance resolved to failure then returned Promise instance resolved with
     * same failure result.
     *
     * @param promise1
     *         Input promise
     * @return Created instance
     */
    static <T1> Promise<Tuple1<T1>> all(final Promise<T1> promise1) {
        return promise(promise -> threshold(Tuple1.size(),
                                            at -> promise1.thenDo(at::registerEvent),
                                            () -> promise1.onResult(
                                                    v1 -> promise.resolve(
                                                            tuple(v1).map(Result::flatten)))));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with values from passed Promise instances if passed Promise instances
     * are resolved to success result. If any passed Promise instance resolved to failure then returned Promise instance resolved
     * with first found failure result.
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
                                                            v2 -> promise.resolve(
                                                                    tuple(v1, v2).map(Result::flatten))))));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with values from passed Promise instances if passed Promise instances
     * are resolved to success result. If any passed Promise instance resolved to failure then returned Promise instance resolved
     * with first found failure result.
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
                                                                    v3 -> promise.resolve(
                                                                            tuple(v1, v2, v3).map(Result::flatten)))))));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with values from passed Promise instances if passed Promise instances
     * are resolved to success result. If any passed Promise instance resolved to failure then returned Promise instance resolved
     * with first found failure result.
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
                                                                            v4 -> promise.resolve(
                                                                                    tuple(v1, v2, v3, v4).map(Result::flatten))))))));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with values from passed Promise instances if passed Promise instances
     * are resolved to success result. If any passed Promise instance resolved to failure then returned Promise instance resolved
     * with first found failure result.
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
                                                                                    v5 -> promise.resolve(
                                                                                            tuple(v1, v2, v3, v4, v5)
                                                                                                    .map(Result::flatten)))))))));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with values from passed Promise instances if passed Promise instances
     * are resolved to success result. If any passed Promise instance resolved to failure then returned Promise instance resolved
     * with first found failure result.
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
                                                                                            v6 -> promise.resolve(
                                                                                                    tuple(v1, v2, v3, v4, v5, v6)
                                                                                                            .map(Result::flatten))))))))));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with values from passed Promise instances if passed Promise instances
     * are resolved to success result. If any passed Promise instance resolved to failure then returned Promise instance resolved
     * with first found failure result.
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
                                                                                                    v7 -> promise.resolve(
                                                                                                            tuple(v1, v2, v3, v4, v5, v6, v7)
                                                                                                                    .map(Result::flatten)))))))))));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with values from passed Promise instances if passed Promise instances
     * are resolved to success result. If any passed Promise instance resolved to failure then returned Promise instance resolved
     * with first found failure result.
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
                                                                                                            v8 -> promise.resolve(
                                                                                                                    tuple(v1, v2, v3, v4, v5, v6, v7, v8)
                                                                                                                            .map(Result::flatten))))))))))));
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with values from passed Promise instances if passed Promise instances
     * are resolved to success result. If any passed Promise instance resolved to failure then returned Promise instance resolved
     * with first found failure result.
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
                                                                                                                    v9 -> promise.resolve(
                                                                                                                            tuple(v1, v2, v3, v4, v5, v6, v7, v8, v9)
                                                                                                                                    .map(Result::flatten)))))))))))));
    }
}

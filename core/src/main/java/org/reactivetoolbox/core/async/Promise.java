package org.reactivetoolbox.core.async;

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
import org.reactivetoolbox.core.async.impl.PromiseImpl;
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
import org.reactivetoolbox.core.scheduler.Timeout;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.reactivetoolbox.core.async.ActionableThreshold.threshold;
import static org.reactivetoolbox.core.lang.Tuple.Tuple1;
import static org.reactivetoolbox.core.lang.Tuple.Tuple2;
import static org.reactivetoolbox.core.lang.Tuple.Tuple3;
import static org.reactivetoolbox.core.lang.Tuple.Tuple4;
import static org.reactivetoolbox.core.lang.Tuple.tuple;

/**
 * Extended {@link Promise} implementation which works with {@link Result} values.
 */
public interface Promise<T> {
    /**
     * Perform user-provided action once this instance will be resolved. Action will be executed once regardless
     * if instance is already resolved or not. User may add as many actions as necessary.
     *
     * @param action Action to perform
     * @return Current instance
     */
    Promise<T> onResult(final Consumer<Result<T>> action);

    /**
     * Run specified task asynchronously. Current instance of {@link Promise} is passed to the task as a parameter.
     *
     * @param task Task to execute with this promise
     * @return Current instance
     */
    Promise<T> async(final Consumer<Promise<T>> task);

    /**
     * Run specified task asynchronously after specified timeout expires. Current instance of {@link Promise} is passed
     * to the task as a parameter.
     *
     * @param task Task to execute with this promise
     * @return Current instance
     */
    Promise<T> async(final Timeout timeout, final Consumer<Promise<T>> task);

    /**
     * Synchronously wait for this instance resolution.
     * <br/>
     * This method is provided only for testing purposes, it is not recommended to use it in production code.
     *
     * @return Current instance
     */
    Promise<T> syncWait();

    /**
     * Synchronously wait for this instance resolution or timeout.
     * <br/>
     * This method is provided only for testing purposes, it is not recommended to use it in production code.
     *
     * @param timeout Timeout amount
     * @return Current instance
     */
    Promise<T> syncWait(final Timeout timeout);

    /**
     * Resolve the promise with specified result. All actions already
     * waiting for resolution will be scheduled for synchronous execution.
     *
     * @param result The value which will be stored in this instance and make it resolved
     * @return Current instance
     */
    Promise<T> resolve(final Result<T> result);

    /**
     * Resolve the promise with specified result. All actions already
     * waiting for resolution will be scheduled for asynchronous execution.
     *
     * @param result The value which will be stored in this instance and make it resolved
     * @return Current instance
     */
    default Promise<T> asyncResolve(final Result<T> result) {
        return async(promise -> promise.resolve(result));
    }

    /**
     * Resolve current instance with successful result.
     *
     * @param result Successful result value
     * @return Current instance
     */
    default Promise<T> ok(final T result) {
        return resolve(Result.ok(result));
    }

    /**
     * Resolve current instance with successful result asynchronously.
     *
     * @param result Successful result value
     * @return Current instance
     */
    default Promise<T> asyncOk(final T result) {
        return asyncResolve(Result.ok(result));
    }

    /**
     * Resolve current instance with failure result.
     *
     * @param failure Failure result value
     * @return Current instance
     */
    default Promise<T> fail(final Failure failure) {
        return resolve(Result.fail(failure));
    }

    /**
     * Resolve current instance with failure result asynchronously.
     *
     * @param failure Failure result value
     * @return Current instance
     */
    default Promise<T> asyncFail(final Failure failure) {
        return asyncResolve(Result.fail(failure));
    }

    /**
     * Convenience method for performing some actions with current promise instance.
     *
     * @param consumer Action to perform on current instance.
     * @return Current instance
     */
    default Promise<T> apply(final Consumer<Promise<T>> consumer) {
        consumer.accept(this);
        return this;
    }

    /**
     * Set timeout for instance resolution. When timeout expires, instance will be resolved with specified timeout
     * result.
     *
     * @param timeout       Timeout amount
     * @param timeoutResult Resolution value in case of timeout
     * @return Current instance
     */
    default Promise<T> when(final Timeout timeout, final Result<T> timeoutResult) {
        return when(timeout, () -> timeoutResult);
    }

    /**
     * Set timeout for instance resolution. When timeout expires, instance will be resolved with value returned by
     * provided supplier. Resolution value is lazily evaluated.
     *
     * @param timeout               Timeout amount
     * @param timeoutResultSupplier Supplier of resolution value in case of timeout
     * @return Current instance
     */
    default Promise<T> when(final Timeout timeout, final Supplier<Result<T>> timeoutResultSupplier) {
        return async(timeout, promise -> promise.resolve(timeoutResultSupplier.get()));
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
     * Resolve instance with {@link Errors#CANCELLED}. Often necessary if pending request need to be resolved prematurely,
     * without waiting for response or timeout.
     *
     * @return Current instance
     */
    default Promise<T> cancel() {
        fail(Errors.CANCELLED);
        return this;
    }

    /**
     * This method enables chaining of calls to functions which return {@link Promise} and require unwrapped
     * results of successful previous calls. If current instance is resolved to {@link Result#fail(Failure)}, then
     * function passes as parameter is not invoked and resolved instance of {@link Promise} is returned instead.
     *
     * @param mapper Function to call if current instance is resolved with success.
     * @return Created instance which is either already resolved with same error as current instance or
     * waiting for resolving by provided mapping function.
     */
    default <R> Promise<R> chainMap(final FN1<Promise<R>, T> mapper) {
        return promise(promise -> onResult(result -> result.fold(error -> promise.resolve(Result.fail(error)),
                                                                 success -> mapper.apply(success)
                                                                                  .onResult(promise::resolve))));
    }

    /**
     * Convenience method which provides access to inner value of successful result. If current instance
     * contains failure, then mapping function is not called and created instance is resolved with same error
     * as current instance.
     *
     * @param mapper Function to transform successful result value if current instance is resolved with success
     * @return Created instance
     */
    default <R> Promise<R> map(final FN1<R, T> mapper) {
        return promise(promise -> onResult(result -> promise.resolve(result.map(mapper))));
    }

    default <R> Promise<R> mapAsync(final FN1<R, T> mapper) {
        return Promise.<R>promise().async(promise -> onResult(result -> promise.resolve(result.map(mapper))));
    }

    default <R> Promise<R> flatMap(final FN1<Result<R>, T> mapper) {
        return promise(promise -> onResult(result -> promise.resolve(result.flatMap(mapper))));
    }

    default <R> Promise<R> flatMapAsync(final FN1<Result<R>, T> mapper) {
        return Promise.<R>promise().async(promise -> onResult(result -> promise.resolve(result.flatMap(mapper))));
    }

    /**
     * Get internal logger instance.
     *
     * @return logger instance used to log {@link Promise} internal events
     */
    CoreLogger logger();

    /**
     * Set exception logger.
     *
     * @param consumer Consumer for intercepted exceptions.
     * @return current {@link Promise} instance
     */
    Promise<T> exceptionCollector(final Consumer<Throwable> consumer);

    /**
     * Create new unresolved instance.om
     *
     * @return Created instance
     */
    static <T> Promise<T> promise() {
        return new PromiseImpl<>();
    }

    /**
     * Create new resolved instance.
     *
     * @return Created instance
     */
    static <T> Promise<T> ready(final Result<T> result) {
        return Promise.<T>promise().resolve(result);
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
     * Create instance and immediately invoke provided function with created instance.
     * Usually this function is used to configure actions on created instance.
     *
     * @param setup Function to invoke with created instance
     * @return Created instance
     */
    static <T> Promise<T> promise(final Consumer<Promise<T>> setup) {
        return Promise.<T>promise().apply(setup);
    }

    /**
     * Create instance which will be resolved once any of the promises provided as a parameters will be resolved.
     * Remaining promises are cancelled upon resolution of any promises.
     *
     * @param promises Input promises
     * @return created instance
     */
    @SafeVarargs
    static <T> Promise<T> any(final Promise<T>... promises) {
        return promise(result -> List.list(promises).apply(promise -> promise.onResult(result::resolve)
                                                                             .onResult(v -> cancelAll(promises))));
    }

    /**
     * Create instance which will be resolved once any of the promises provided as a parameters will be resolved
     * with successful result. If none of the promises will be resolved with successful result, then created
     * instance will be resolved with {@link Errors#CANCELLED}.
     *
     * @param promises Input promises
     * @return Created instance
     */
    @SafeVarargs
    static <T> Promise<T> anySuccess(final Promise<T>... promises) {
        return anySuccess(Result.fail(Errors.CANCELLED), promises);
    }

    /**
     * Create instance which will be resolved once any of the promises provided as a parameters will be resolved
     * with successful result. If none of the promises will be resolved with successful result, then created
     * instance will be resolved with provided {@code failureResult}.
     *
     * @param failureResult Result in case if no instances were resolved with success
     * @param promises      Input promises
     * @return Created instance
     */
    static <T> Promise<T> anySuccess(final Result<T> failureResult, final Promise<T>... promises) {
        return Promise.<T>promise(anySuccess -> threshold(promises.length,
                                                          (at) -> List.list(promises)
                                                                      .apply(promise -> promise.onResult($ -> at.registerEvent())
                                                                                               .onResult(res -> res.onSuccess(t -> {
                                                                                                   anySuccess.resolve(res);
                                                                                                   resolveAll(failureResult, promises);
                                                                                               }))),
                                                          () -> resolveAll(failureResult, promises)));
    }

    /**
     * Cancel several promises at once.
     *
     * @param promises Promises to cancel.
     */
    static <T> void cancelAll(final Promise<T>... promises) {
        List.list(promises).apply(Promise::cancel);
    }

    /**
     * Resolve several promises at once.
     *
     * @param result   Resolution result
     * @param promises Promises to resolve
     */
    static <T> void resolveAll(final Result<T> result, final Promise<T>... promises) {
        List.list(promises)
            .apply(promise -> promise.resolve(result));
    }

    class RethrowingCollector implements Consumer<Throwable> {
        private final Queue<Throwable> list = new ConcurrentLinkedQueue<>();

        private RethrowingCollector() {}

        @Override
        public void accept(final Throwable throwable) {
            list.add(throwable);
        }

        public List<Throwable> collected() {
            return List.from(list);
        }

        public void rethrow() {
            if (list.isEmpty()) {
                return;
            }

            final Throwable throwable = list.iterator().next();

            if (throwable instanceof RuntimeException rt) {
                throw rt;
            }

            throw new RuntimeException(throwable);
        }

        public static RethrowingCollector collector() {
            return new RethrowingCollector();
        }
    }

    static <T1> Promise<Tuple1<T1>> all(final Promise<T1> promise1) {
        return promise(promise -> threshold(Tuple1.size(),
                                            (at) -> promise1.onResult($ -> at.registerEvent()),
                                            () -> promise1.onResult(v1 -> promise.resolve(Tuple.tuple(v1).map(Result::flatten)))));
    }

    static <T1, T2> Promise<Tuple2<T1, T2>> all(final Promise<T1> promise1,
                                                final Promise<T2> promise2) {
        return promise(promise -> threshold(Tuple2.size(),
                                            (at) -> {
                                                promise1.onResult($ -> at.registerEvent());
                                                promise2.onResult($ -> at.registerEvent());
                                            },
                                            () -> promise1.onResult(v1 ->
                                                                            promise2.onResult(v2 -> promise.resolve(Tuple.tuple(v1, v2)
                                                                                                                         .map(Result::flatten))))));
    }

    static <T1, T2, T3> Promise<Tuple3<T1, T2, T3>> all(final Promise<T1> promise1,
                                                        final Promise<T2> promise2,
                                                        final Promise<T3> promise3) {
        return promise(promise -> threshold(Tuple3.size(),
                                            (at) -> {
                                                promise1.onResult($ -> at.registerEvent());
                                                promise2.onResult($ -> at.registerEvent());
                                                promise3.onResult($ -> at.registerEvent());
                                            },
                                            () -> promise1.onResult(v1 ->
                                                                            promise2.onResult(v2 ->
                                                                                                      promise3.onResult(v3 -> promise.resolve(Tuple.tuple(
                                                                                                              v1,
                                                                                                              v2,
                                                                                                              v3).map(Result::flatten)))))));
    }

    static <T1, T2, T3, T4> Promise<Tuple4<T1, T2, T3, T4>> all(final Promise<T1> promise1,
                                                                final Promise<T2> promise2,
                                                                final Promise<T3> promise3,
                                                                final Promise<T4> promise4) {
        return promise(promise -> threshold(Tuple4.size(),
                                            (at) -> {
                                                promise1.onResult($ -> at.registerEvent());
                                                promise2.onResult($ -> at.registerEvent());
                                                promise3.onResult($ -> at.registerEvent());
                                                promise4.onResult($ -> at.registerEvent());
                                            },
                                            () -> promise1.onResult(v1 ->
                                                                            promise2.onResult(v2 ->
                                                                                                      promise3.onResult(v3 ->
                                                                                                                                promise4.onResult(v4 -> promise.resolve(
                                                                                                                                        Tuple.tuple(v1,
                                                                                                                                                    v2,
                                                                                                                                                    v3,
                                                                                                                                                    v4)
                                                                                                                                             .map(Result::flatten))))))));
    }

    static <T1, T2, T3, T4, T5> Promise<Tuple5<T1, T2, T3, T4, T5>> all(final Promise<T1> promise1,
                                                                        final Promise<T2> promise2,
                                                                        final Promise<T3> promise3,
                                                                        final Promise<T4> promise4,
                                                                        final Promise<T5> promise5) {
        return promise(promise -> threshold(Tuple5.size(),
                                            (at) -> {
                                                promise1.onResult($ -> at.registerEvent());
                                                promise2.onResult($ -> at.registerEvent());
                                                promise3.onResult($ -> at.registerEvent());
                                                promise4.onResult($ -> at.registerEvent());
                                                promise5.onResult($ -> at.registerEvent());
                                            },
                                            () -> promise1.onResult(v1 ->
                                                                            promise2.onResult(v2 ->
                                                                                                      promise3.onResult(v3 ->
                                                                                                                                promise4.onResult(v4 ->
                                                                                                                                                          promise5.onResult(
                                                                                                                                                                  v5 -> promise.resolve(
                                                                                                                                                                          Tuple.tuple(
                                                                                                                                                                                  v1,
                                                                                                                                                                                  v2,
                                                                                                                                                                                  v3,
                                                                                                                                                                                  v4,
                                                                                                                                                                                  v5)
                                                                                                                                                                               .map(Result::flatten)))))))));
    }

    static <T1, T2, T3, T4, T5, T6> Promise<Tuple6<T1, T2, T3, T4, T5, T6>> all(final Promise<T1> promise1,
                                                                                final Promise<T2> promise2,
                                                                                final Promise<T3> promise3,
                                                                                final Promise<T4> promise4,
                                                                                final Promise<T5> promise5,
                                                                                final Promise<T6> promise6) {
        return promise(promise -> threshold(Tuple6.size(),
                                            (at) -> {
                                                promise1.onResult($ -> at.registerEvent());
                                                promise2.onResult($ -> at.registerEvent());
                                                promise3.onResult($ -> at.registerEvent());
                                                promise4.onResult($ -> at.registerEvent());
                                                promise5.onResult($ -> at.registerEvent());
                                                promise6.onResult($ -> at.registerEvent());
                                            },
                                            () -> promise1.onResult(v1 ->
                                                                            promise2.onResult(v2 ->
                                                                                                      promise3.onResult(v3 ->
                                                                                                                                promise4.onResult(v4 ->
                                                                                                                                                          promise5.onResult(
                                                                                                                                                                  v5 ->
                                                                                                                                                                          promise6.onResult(
                                                                                                                                                                                  v6 -> promise.resolve(
                                                                                                                                                                                          Tuple.tuple(
                                                                                                                                                                                                  v1,
                                                                                                                                                                                                  v2,
                                                                                                                                                                                                  v3,
                                                                                                                                                                                                  v4,
                                                                                                                                                                                                  v5,
                                                                                                                                                                                                  v6)
                                                                                                                                                                                               .map(Result::flatten))))))))));
    }

    static <T1, T2, T3, T4, T5, T6, T7> Promise<Tuple7<T1, T2, T3, T4, T5, T6, T7>> all(final Promise<T1> promise1,
                                                                                        final Promise<T2> promise2,
                                                                                        final Promise<T3> promise3,
                                                                                        final Promise<T4> promise4,
                                                                                        final Promise<T5> promise5,
                                                                                        final Promise<T6> promise6,
                                                                                        final Promise<T7> promise7) {
        return promise(promise -> threshold(Tuple7.size(),
                                            (at) -> {
                                                promise1.onResult($ -> at.registerEvent());
                                                promise2.onResult($ -> at.registerEvent());
                                                promise3.onResult($ -> at.registerEvent());
                                                promise4.onResult($ -> at.registerEvent());
                                                promise5.onResult($ -> at.registerEvent());
                                                promise6.onResult($ -> at.registerEvent());
                                                promise7.onResult($ -> at.registerEvent());
                                            },
                                            () -> promise1.onResult(v1 ->
                                                                            promise2.onResult(v2 ->
                                                                                                      promise3.onResult(v3 ->
                                                                                                                                promise4.onResult(v4 ->
                                                                                                                                                          promise5.onResult(
                                                                                                                                                                  v5 ->
                                                                                                                                                                          promise6.onResult(
                                                                                                                                                                                  v6 ->
                                                                                                                                                                                          promise7.onResult(
                                                                                                                                                                                                  v7 -> promise.resolve(
                                                                                                                                                                                                          Tuple.tuple(
                                                                                                                                                                                                                  v1,
                                                                                                                                                                                                                  v2,
                                                                                                                                                                                                                  v3,
                                                                                                                                                                                                                  v4,
                                                                                                                                                                                                                  v5,
                                                                                                                                                                                                                  v6,
                                                                                                                                                                                                                  v7)
                                                                                                                                                                                                               .map(Result::flatten)))))))))));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8> Promise<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> all(final Promise<T1> promise1,
                                                                                                final Promise<T2> promise2,
                                                                                                final Promise<T3> promise3,
                                                                                                final Promise<T4> promise4,
                                                                                                final Promise<T5> promise5,
                                                                                                final Promise<T6> promise6,
                                                                                                final Promise<T7> promise7,
                                                                                                final Promise<T8> promise8) {
        return promise(promise -> threshold(Tuple8.size(),
                                            (at) -> {
                                                promise1.onResult($ -> at.registerEvent());
                                                promise2.onResult($ -> at.registerEvent());
                                                promise3.onResult($ -> at.registerEvent());
                                                promise4.onResult($ -> at.registerEvent());
                                                promise5.onResult($ -> at.registerEvent());
                                                promise6.onResult($ -> at.registerEvent());
                                                promise7.onResult($ -> at.registerEvent());
                                                promise8.onResult($ -> at.registerEvent());
                                            },
                                            () -> promise1.onResult(v1 ->
                                                                            promise2.onResult(v2 ->
                                                                                                      promise3.onResult(v3 ->
                                                                                                                                promise4.onResult(v4 ->
                                                                                                                                                          promise5.onResult(
                                                                                                                                                                  v5 ->
                                                                                                                                                                          promise6.onResult(
                                                                                                                                                                                  v6 ->
                                                                                                                                                                                          promise7.onResult(
                                                                                                                                                                                                  v7 ->
                                                                                                                                                                                                          promise8.onResult(
                                                                                                                                                                                                                  v8 -> promise.resolve(
                                                                                                                                                                                                                          Tuple.tuple(
                                                                                                                                                                                                                                  v1,
                                                                                                                                                                                                                                  v2,
                                                                                                                                                                                                                                  v3,
                                                                                                                                                                                                                                  v4,
                                                                                                                                                                                                                                  v5,
                                                                                                                                                                                                                                  v6,
                                                                                                                                                                                                                                  v7,
                                                                                                                                                                                                                                  v8)
                                                                                                                                                                                                                               .map(Result::flatten))))))))))));
    }

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
                                            (at) -> {
                                                promise1.onResult($ -> at.registerEvent());
                                                promise2.onResult($ -> at.registerEvent());
                                                promise3.onResult($ -> at.registerEvent());
                                                promise4.onResult($ -> at.registerEvent());
                                                promise5.onResult($ -> at.registerEvent());
                                                promise6.onResult($ -> at.registerEvent());
                                                promise7.onResult($ -> at.registerEvent());
                                                promise8.onResult($ -> at.registerEvent());
                                                promise9.onResult($ -> at.registerEvent());
                                            },
                                            () -> promise1.onResult(v1 ->
                                                                            promise2.onResult(v2 ->
                                                                                                      promise3.onResult(v3 ->
                                                                                                                                promise4.onResult(v4 ->
                                                                                                                                                          promise5.onResult(
                                                                                                                                                                  v5 ->
                                                                                                                                                                          promise6.onResult(
                                                                                                                                                                                  v6 ->
                                                                                                                                                                                          promise7.onResult(
                                                                                                                                                                                                  v7 ->
                                                                                                                                                                                                          promise8.onResult(
                                                                                                                                                                                                                  v8 ->
                                                                                                                                                                                                                          promise9.onResult(
                                                                                                                                                                                                                                  v9 -> promise.resolve(
                                                                                                                                                                                                                                          tuple(v1,
                                                                                                                                                                                                                                                v2,
                                                                                                                                                                                                                                                v3,
                                                                                                                                                                                                                                                v4,
                                                                                                                                                                                                                                                v5,
                                                                                                                                                                                                                                                v6,
                                                                                                                                                                                                                                                v7,
                                                                                                                                                                                                                                                v8,
                                                                                                                                                                                                                                                v9)
                                                                                                                                                                                                                                                  .map(Result::flatten)))))))))))));
    }
}

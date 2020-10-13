package org.reactivetoolbox.io.async;

import org.reactivetoolbox.core.Errors;
import org.reactivetoolbox.core.lang.Tuple;
import org.reactivetoolbox.core.lang.Tuple.Tuple1;
import org.reactivetoolbox.core.lang.Tuple.Tuple2;
import org.reactivetoolbox.core.lang.Tuple.Tuple3;
import org.reactivetoolbox.core.lang.Tuple.Tuple4;
import org.reactivetoolbox.core.lang.Tuple.Tuple5;
import org.reactivetoolbox.core.lang.Tuple.Tuple6;
import org.reactivetoolbox.core.lang.Tuple.Tuple7;
import org.reactivetoolbox.core.lang.Tuple.Tuple8;
import org.reactivetoolbox.core.lang.Tuple.Tuple9;
import org.reactivetoolbox.core.lang.collection.List;
import org.reactivetoolbox.core.lang.functional.Failure;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.async.PromiseMappers.Mapper1;
import org.reactivetoolbox.io.async.PromiseMappers.Mapper2;
import org.reactivetoolbox.io.async.PromiseMappers.Mapper3;
import org.reactivetoolbox.io.async.PromiseMappers.Mapper4;
import org.reactivetoolbox.io.async.PromiseMappers.Mapper5;
import org.reactivetoolbox.io.async.PromiseMappers.Mapper6;
import org.reactivetoolbox.io.async.PromiseMappers.Mapper7;
import org.reactivetoolbox.io.async.PromiseMappers.Mapper8;
import org.reactivetoolbox.io.async.PromiseMappers.Mapper9;
import org.reactivetoolbox.io.async.util.ActionableThreshold;

import static org.reactivetoolbox.core.lang.Tuple.tuple;
import static org.reactivetoolbox.core.lang.collection.List.list;
import static org.reactivetoolbox.io.async.util.ActionableThreshold.threshold;

public interface Promises {

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
     * Create instance for synchronization point of type {@code ANY}.
     * <p>
     * The returned instance will be resolved once any of the promises provided as a parameters will be resolved. Remaining promises are automatically cancelled.
     *
     * @param promises
     *         Input promises
     * @return created instance
     */
    @SafeVarargs
    static <T> Promise<T> any(final Promise<T>... promises) {
        return Promise.promise(result -> list(promises).apply(promise -> promise.onResult(v -> {
            result.resolve(v);
            cancelAll(promises);
        })));
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
                                                                   result.onSuccess(anySuccess::ok)
                                                                         .onSuccessDo(() -> cancelAll(promises));
                                                                   at.registerEvent();
                                                               })),
                                                       () -> anySuccess.resolve(failureResult)));
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
     * Wait for several promises of same type. The returned instance resolved with the list of results returned by all Promise instances contained in the input list.
     *
     * @param promises
     *         List of promises to wait.
     * @return Promise which will be resolved with list of results from Promises passes in the input list.
     */
    static <T> Promise<List<Result<T>>> allOf(final List<Promise<T>> promises) {
        final var results = new Result[promises.size()];

        return Promise.promise(result -> threshold(Tuple1.size(),
                                                   at -> promises.applyN((ndx, element) -> element.onResult(value -> {
                                                       results[ndx] = value;
                                                       at.registerEvent();
                                                   })),
                                                   () -> result.ok(list(results))));
    }

    /**
     * Wait for several promises of same type. The returned instance resolved with the list of results returned by all Promise instances passed as a parameter.
     *
     * @param promises
     *         Promises to wait.
     * @return Promise which will be resolved with list of results from Promises passes as a parameters.
     */
    @SafeVarargs
    static <T> Promise<List<Result<T>>> allOf(final Promise<T>... promises) {
        return allOf(List.list(promises));
    }

    /**
     * Same as {@link #allOf(List)} except returned list is "flattened". If all results in the list were successes, then returned Promise instance is resolved with list of values
     * contained in the results. If any result is failure then returned Promise instance is resolved to failure.
     *
     * @param promises
     *         Promises to wait.
     * @return Promise with list of success values
     */
    static <T> Promise<List<T>> flatAllOf(final List<Promise<T>> promises) {
        return allOf(promises).mapResult(Result::flatten);
    }

    /**
     * Same as {@link #allOf(Promise[])} except returned list is "flattened". If all results in the list were successes, then returned Promise instance is resolved with list of
     * values contained in the results. If any result is failure then returned Promise instance is resolved to failure.
     *
     * @param promises
     *         Promises to wait.
     * @return Promise with list of success values
     */
    static <T> Promise<List<T>> flatAllOf(final Promise<T>... promises) {
        return flatAllOf(List.list(promises));
    }

    //TODO: fix Javadocs

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with value from passed Promise instance if passed Promise instance is resolved to success result. If passed Promise
     * instance resolved to failure then returned Promise instance resolved with same failure result.
     *
     * @param promise1
     *         Input promise
     * @return Created instance
     */
    static <T1> Promise<Tuple1<T1>> all(final Promise<T1> promise1) {
        return Promise.promise(promise -> threshold(Tuple1.size(),
                                                    at -> setup(at, promise, promise1),
                                                    () -> promise1.onFailure(promise::fail)
                                                                  .onSuccess(v1 -> promise.ok(tuple(v1)))));
    }

    static <T1> Mapper1<T1> doWithAll(final Promise<T1> promise1) {
        return () -> all(promise1);
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with values from passed Promise instances if passed Promise instances are resolved to success result. If any passed
     * Promise instance resolved to failure then returned Promise instance resolved with first found failure result.
     *
     * @param promise1
     *         Input promise
     * @param promise2
     *         Input promise
     * @return Created instance
     */
    static <T1, T2> Promise<Tuple2<T1, T2>> all(final Promise<T1> promise1,
                                                final Promise<T2> promise2) {
        return Promise.promise(promise -> threshold(Tuple2.size(),
                                                    at -> setup(at, promise, promise1, promise2),
                                                    () -> promise1.onSuccess(
                                                            v1 -> promise2.onSuccess(
                                                                    v2 -> promise.ok(tuple(v1, v2))))));
    }

    static <T1, T2> Mapper2<T1, T2> doWithAll(final Promise<T1> promise1,
                                              final Promise<T2> promise2) {
        return () -> all(promise1, promise2);
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with values from passed Promise instances if passed Promise instances are resolved to success result. If any passed
     * Promise instance resolved to failure then returned Promise instance resolved with first found failure result.
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
        return Promise.promise(promise -> threshold(Tuple3.size(),
                                                    at -> setup(at, promise, promise1, promise2, promise3),
                                                    () -> promise1.onSuccess(
                                                            v1 -> promise2.onSuccess(
                                                                    v2 -> promise3.onSuccess(
                                                                            v3 -> promise.ok(tuple(v1, v2, v3)))))));
    }

    static <T1, T2, T3> Mapper3<T1, T2, T3> doWithAll(final Promise<T1> promise1,
                                                      final Promise<T2> promise2,
                                                      final Promise<T3> promise3) {
        return () -> all(promise1, promise2, promise3);
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with values from passed Promise instances if passed Promise instances are resolved to success result. If any passed
     * Promise instance resolved to failure then returned Promise instance resolved with first found failure result.
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
        return Promise.promise(promise -> threshold(Tuple4.size(),
                                                    at -> setup(at, promise, promise1, promise2, promise3, promise4),
                                                    () -> promise1.onSuccess(
                                                            v1 -> promise2.onSuccess(
                                                                    v2 -> promise3.onSuccess(
                                                                            v3 -> promise4.onSuccess(
                                                                                    v4 -> promise.ok(tuple(v1,
                                                                                                           v2,
                                                                                                           v3,
                                                                                                           v4))))))));
    }

    static <T1, T2, T3, T4> Mapper4<T1, T2, T3, T4> doWithAll(final Promise<T1> promise1,
                                                              final Promise<T2> promise2,
                                                              final Promise<T3> promise3,
                                                              final Promise<T4> promise4) {
        return () -> all(promise1, promise2, promise3, promise4);
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with values from passed Promise instances if passed Promise instances are resolved to success result. If any passed
     * Promise instance resolved to failure then returned Promise instance resolved with first found failure result.
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
        return Promise.promise(promise -> threshold(Tuple5.size(),
                                                    at -> setup(at, promise, promise1, promise2, promise3, promise4, promise5),
                                                    () -> promise1.onSuccess(
                                                            v1 -> promise2.onSuccess(
                                                                    v2 -> promise3.onSuccess(
                                                                            v3 -> promise4.onSuccess(
                                                                                    v4 -> promise5.onSuccess(
                                                                                            v5 -> promise.ok(tuple(v1,
                                                                                                                   v2,
                                                                                                                   v3,
                                                                                                                   v4,
                                                                                                                   v5)))))))));
    }

    static <T1, T2, T3, T4, T5> Mapper5<T1, T2, T3, T4, T5> doWithAll(final Promise<T1> promise1,
                                                                      final Promise<T2> promise2,
                                                                      final Promise<T3> promise3,
                                                                      final Promise<T4> promise4,
                                                                      final Promise<T5> promise5) {
        return () -> all(promise1, promise2, promise3, promise4, promise5);
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with values from passed Promise instances if passed Promise instances are resolved to success result. If any passed
     * Promise instance resolved to failure then returned Promise instance resolved with first found failure result.
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
        return Promise.promise(promise -> threshold(Tuple6.size(),
                                                    at -> setup(at, promise, promise1, promise2, promise3, promise4, promise5, promise6),
                                                    () -> promise1.onSuccess(
                                                            v1 -> promise2.onSuccess(
                                                                    v2 -> promise3.onSuccess(
                                                                            v3 -> promise4.onSuccess(
                                                                                    v4 -> promise5.onSuccess(
                                                                                            v5 -> promise6.onSuccess(
                                                                                                    v6 -> promise.ok(tuple(v1, v2, v3, v4, v5, v6))))))))));
    }

    static <T1, T2, T3, T4, T5, T6> Mapper6<T1, T2, T3, T4, T5, T6> doWithAll(final Promise<T1> promise1,
                                                                              final Promise<T2> promise2,
                                                                              final Promise<T3> promise3,
                                                                              final Promise<T4> promise4,
                                                                              final Promise<T5> promise5,
                                                                              final Promise<T6> promise6) {
        return () -> all(promise1, promise2, promise3, promise4, promise5, promise6);
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with values from passed Promise instances if passed Promise instances are resolved to success result. If any passed
     * Promise instance resolved to failure then returned Promise instance resolved with first found failure result.
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
        return Promise.promise(promise -> threshold(Tuple7.size(),
                                                    at -> setup(at, promise, promise1, promise2, promise3, promise4, promise5, promise6, promise7),
                                                    () -> promise1.onSuccess(
                                                            v1 -> promise2.onSuccess(
                                                                    v2 -> promise3.onSuccess(
                                                                            v3 -> promise4.onSuccess(
                                                                                    v4 -> promise5.onSuccess(
                                                                                            v5 -> promise6.onSuccess(
                                                                                                    v6 -> promise7.onSuccess(
                                                                                                            v7 -> promise.ok(tuple(v1, v2, v3, v4, v5, v6, v7)))))))))));
    }

    static <T1, T2, T3, T4, T5, T6, T7> Mapper7<T1, T2, T3, T4, T5, T6, T7> doWithAll(final Promise<T1> promise1,
                                                                                      final Promise<T2> promise2,
                                                                                      final Promise<T3> promise3,
                                                                                      final Promise<T4> promise4,
                                                                                      final Promise<T5> promise5,
                                                                                      final Promise<T6> promise6,
                                                                                      final Promise<T7> promise7) {
        return () -> all(promise1, promise2, promise3, promise4, promise5, promise6, promise7);
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with values from passed Promise instances if passed Promise instances are resolved to success result. If any passed
     * Promise instance resolved to failure then returned Promise instance resolved with first found failure result.
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
        return Promise.promise(promise -> threshold(Tuple8.size(),
                                                    at -> setup(at, promise, promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8),
                                                    () -> promise1.onSuccess(
                                                            v1 -> promise2.onSuccess(
                                                                    v2 -> promise3.onSuccess(
                                                                            v3 -> promise4.onSuccess(
                                                                                    v4 -> promise5.onSuccess(
                                                                                            v5 -> promise6.onSuccess(
                                                                                                    v6 -> promise7.onSuccess(
                                                                                                            v7 -> promise8.onSuccess(
                                                                                                                    v8 -> promise.ok(tuple(v1,
                                                                                                                                           v2,
                                                                                                                                           v3,
                                                                                                                                           v4,
                                                                                                                                           v5,
                                                                                                                                           v6,
                                                                                                                                           v7,
                                                                                                                                           v8))))))))))));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8> Mapper8<T1, T2, T3, T4, T5, T6, T7, T8> doWithAll(final Promise<T1> promise1,
                                                                                              final Promise<T2> promise2,
                                                                                              final Promise<T3> promise3,
                                                                                              final Promise<T4> promise4,
                                                                                              final Promise<T5> promise5,
                                                                                              final Promise<T6> promise6,
                                                                                              final Promise<T7> promise7,
                                                                                              final Promise<T8> promise8) {
        return () -> all(promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8);
    }

    /**
     * Create instance for synchronization point of type {@code ALL}.
     * <p>
     * Create instance which will be resolved when all promises provided as parameters will be resolved.
     * <p>
     * Returned Promise instance will contain {@link Tuple} with values from passed Promise instances if passed Promise instances are resolved to success result. If any passed
     * Promise instance resolved to failure then returned Promise instance resolved with first found failure result.
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
        return Promise.promise(promise -> threshold(Tuple9.size(),
                                                    at -> setup(at, promise, promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8, promise9),
                                                    () -> promise1.onSuccess(
                                                            v1 -> promise2.onSuccess(
                                                                    v2 -> promise3.onSuccess(
                                                                            v3 -> promise4.onSuccess(
                                                                                    v4 -> promise5.onSuccess(
                                                                                            v5 -> promise6.onSuccess(
                                                                                                    v6 -> promise7.onSuccess(
                                                                                                            v7 -> promise8.onSuccess(
                                                                                                                    v8 -> promise9.onSuccess(
                                                                                                                            v9 -> promise.ok(tuple(v1,
                                                                                                                                                   v2,
                                                                                                                                                   v3,
                                                                                                                                                   v4,
                                                                                                                                                   v5,
                                                                                                                                                   v6,
                                                                                                                                                   v7,
                                                                                                                                                   v8,
                                                                                                                                                   v9)))))))))))));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Mapper9<T1, T2, T3, T4, T5, T6, T7, T8, T9> doWithAll(final Promise<T1> promise1,
                                                                                                      final Promise<T2> promise2,
                                                                                                      final Promise<T3> promise3,
                                                                                                      final Promise<T4> promise4,
                                                                                                      final Promise<T5> promise5,
                                                                                                      final Promise<T6> promise6,
                                                                                                      final Promise<T7> promise7,
                                                                                                      final Promise<T8> promise8,
                                                                                                      final Promise<T9> promise9) {
        return () -> all(promise1, promise2, promise3, promise4, promise5, promise6, promise7, promise8, promise9);
    }

    @SuppressWarnings("rawtypes")
    private static void setup(final ActionableThreshold at, final Promise result, final Promise... promises) {
        for (final var promise : promises) {
            promise.thenDo(at::registerEvent).onFailure(f -> result.fail((Failure) f));
        }
    }
}

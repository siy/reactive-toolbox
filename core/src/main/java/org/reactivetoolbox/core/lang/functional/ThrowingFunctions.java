/*
 * Copyright (c) 2019, 2020 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.reactivetoolbox.core.lang.functional;

import org.reactivetoolbox.core.lang.functional.Functions.FN0;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Functions.FN2;
import org.reactivetoolbox.core.lang.functional.Functions.FN3;
import org.reactivetoolbox.core.lang.functional.Functions.FN4;
import org.reactivetoolbox.core.lang.functional.Functions.FN5;
import org.reactivetoolbox.core.lang.functional.Functions.FN6;
import org.reactivetoolbox.core.lang.functional.Functions.FN7;
import org.reactivetoolbox.core.lang.functional.Functions.FN8;
import org.reactivetoolbox.core.lang.functional.Functions.FN9;
import org.reactivetoolbox.core.lang.support.DefaultExceptionTranslator;

import static org.reactivetoolbox.core.lang.functional.Result.fail;
import static org.reactivetoolbox.core.lang.functional.Result.ok;

/**
 * This set of interfaces represents functions which accept different number of arguments and can throw exceptions.
 */
public interface ThrowingFunctions {
    @FunctionalInterface
    interface TFN0<R> {
        R apply() throws Throwable;
    }

    @FunctionalInterface
    interface TFN1<R, T1> {
        R apply(T1 param1) throws Throwable;

        default TFN0<R> bind(final T1 param) {
            return () -> apply(param);
        }
    }

    @FunctionalInterface
    interface TFN2<R, T1, T2> {
        R apply(T1 param1, T2 param2) throws Throwable;

        default TFN1<R, T2> bind(final T1 param) {
            return (v2) -> apply(param, v2);
        }
    }

    @FunctionalInterface
    interface TFN3<R, T1, T2, T3> {
        R apply(T1 param1, T2 param2, T3 param3) throws Throwable;

        default TFN2<R, T2, T3> bind(final T1 param) {
            return (v2, v3) -> apply(param, v2, v3);
        }
    }

    @FunctionalInterface
    interface TFN4<R, T1, T2, T3, T4> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4) throws Throwable;

        default TFN3<R, T2, T3, T4> bind(final T1 param) {
            return (v2, v3, v4) -> apply(param, v2, v3, v4);
        }
    }

    @FunctionalInterface
    interface TFN5<R, T1, T2, T3, T4, T5> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4, T5 param5) throws Throwable;

        default TFN4<R, T2, T3, T4, T5> bind(final T1 param) {
            return (v2, v3, v4, v5) -> apply(param, v2, v3, v4, v5);
        }
    }

    @FunctionalInterface
    interface TFN6<R, T1, T2, T3, T4, T5, T6> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4, T5 param5, T6 param6) throws Throwable;

        default TFN5<R, T2, T3, T4, T5, T6> bind(final T1 param) {
            return (v2, v3, v4, v5, v6) -> apply(param, v2, v3, v4, v5, v6);
        }
    }

    @FunctionalInterface
    interface TFN7<R, T1, T2, T3, T4, T5, T6, T7> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4, T5 param5, T6 param6, T7 param7) throws Throwable;

        default TFN6<R, T2, T3, T4, T5, T6, T7> bind(final T1 param) {
            return (v2, v3, v4, v5, v6, v7) -> apply(param, v2, v3, v4, v5, v6, v7);
        }
    }

    @FunctionalInterface
    interface TFN8<R, T1, T2, T3, T4, T5, T6, T7, T8> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4, T5 param5, T6 param6, T7 param7, T8 param8) throws Throwable;

        default TFN7<R, T2, T3, T4, T5, T6, T7, T8> bind(final T1 param) {
            return (v2, v3, v4, v5, v6, v7, v8) -> apply(param, v2, v3, v4, v5, v6, v7, v8);
        }
    }

    @FunctionalInterface
    interface TFN9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4, T5 param5, T6 param6, T7 param7, T8 param8, T9 param9) throws Throwable;

        default TFN8<R, T2, T3, T4, T5, T6, T7, T8, T9> bind(final T1 param) {
            return (v2, v3, v4, v5, v6, v7, v8, v9) -> apply(param, v2, v3, v4, v5, v6, v7, v8, v9);
        }
    }

    /**
     * Helper method to call function which can throw an exception and wrap function return value into {@link Result}.
     * Additional conversion function is used for transformation of {@link Throwable} into {@link Failure}.
     *
     * @param function
     * @param translator
     *
     * @return Function return value wrapped into {@link Result#ok(Object)} or {@link Result#fail(Failure)} if function throws an exception.
     */
    static <R> Result<R> wrap(final TFN0<R> function, final FN1<Failure, Throwable> translator) {
        try {
            return ok(function.apply());
        } catch (final Throwable t) {
            return fail(translator.apply(t));
        }
    }

    static <R> Result<R> wrap(final TFN0<R> function) {
        return wrap(function, DefaultExceptionTranslator::translate);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by default conversion method from {@link DefaultExceptionTranslator}.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R> FN0<Result<R>> lift(final TFN0<R> function) {
        return lift(function, DefaultExceptionTranslator::translate);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by default conversion method from {@link DefaultExceptionTranslator}.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1> FN1<Result<R>, T1> lift(final TFN1<R, T1> function) {
        return lift(function, DefaultExceptionTranslator::translate);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by default conversion method from {@link DefaultExceptionTranslator}.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1, T2> FN2<Result<R>, T1, T2> lift(final TFN2<R, T1, T2> function) {
        return lift(function, DefaultExceptionTranslator::translate);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by default conversion method from {@link DefaultExceptionTranslator}.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1, T2, T3> FN3<Result<R>, T1, T2, T3> lift(final TFN3<R, T1, T2, T3> function) {
        return lift(function, DefaultExceptionTranslator::translate);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by default conversion method from {@link DefaultExceptionTranslator}.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1, T2, T3, T4> FN4<Result<R>, T1, T2, T3, T4> lift(final TFN4<R, T1, T2, T3, T4> function) {
        return lift(function, DefaultExceptionTranslator::translate);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by default conversion method from {@link DefaultExceptionTranslator}.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1, T2, T3, T4, T5> FN5<Result<R>, T1, T2, T3, T4, T5> lift(final TFN5<R, T1, T2, T3, T4, T5> function) {
        return lift(function, DefaultExceptionTranslator::translate);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by default conversion method from {@link DefaultExceptionTranslator}.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1, T2, T3, T4, T5, T6> FN6<Result<R>, T1, T2, T3, T4, T5, T6> lift(final TFN6<R, T1, T2, T3, T4, T5, T6> function) {
        return lift(function, DefaultExceptionTranslator::translate);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by default conversion method from {@link DefaultExceptionTranslator}.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1, T2, T3, T4, T5, T6, T7> FN7<Result<R>, T1, T2, T3, T4, T5, T6, T7> lift(final TFN7<R, T1, T2, T3, T4, T5, T6, T7> function) {
        return lift(function, DefaultExceptionTranslator::translate);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by default conversion method from {@link DefaultExceptionTranslator}.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1, T2, T3, T4, T5, T6, T7, T8> FN8<Result<R>, T1, T2, T3, T4, T5, T6, T7, T8> lift(final TFN8<R, T1, T2, T3, T4, T5, T6, T7, T8> function) {
        return lift(function, DefaultExceptionTranslator::translate);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by default conversion method from {@link DefaultExceptionTranslator}.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1, T2, T3, T4, T5, T6, T7, T8, T9> FN9<Result<R>, T1, T2, T3, T4, T5, T6, T7, T8, T9> lift(final TFN9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> function) {
        return lift(function, DefaultExceptionTranslator::translate);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by function passed as parameter.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R> FN0<Result<R>> lift(final TFN0<R> function, final FN1<Failure, Throwable> translator) {
        return () -> wrap(function, translator);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by function passed as parameter.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1> FN1<Result<R>, T1> lift(final TFN1<R, T1> function, final FN1<Failure, Throwable> translator) {
        return (param1) -> wrap(function.bind(param1), translator);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by function passed as parameter.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1, T2> FN2<Result<R>, T1, T2> lift(final TFN2<R, T1, T2> function, final FN1<Failure, Throwable> translator) {
        return (param1, param2) -> wrap(function.bind(param1).bind(param2), translator);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by function passed as parameter.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1, T2, T3> FN3<Result<R>, T1, T2, T3> lift(final TFN3<R, T1, T2, T3> function, final FN1<Failure, Throwable> translator) {
        return (param1, param2, param3) -> wrap(function.bind(param1).bind(param2).bind(param3), translator);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by function passed as parameter.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1, T2, T3, T4> FN4<Result<R>, T1, T2, T3, T4> lift(final TFN4<R, T1, T2, T3, T4> function, final FN1<Failure, Throwable> translator) {
        return (param1, param2, param3, param4) -> wrap(function.bind(param1).bind(param2).bind(param3).bind(param4), translator);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by function passed as parameter.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1, T2, T3, T4, T5> FN5<Result<R>, T1, T2, T3, T4, T5> lift(final TFN5<R, T1, T2, T3, T4, T5> function, final FN1<Failure, Throwable> translator) {
        return (param1, param2, param3, param4, param5) -> wrap(function.bind(param1).bind(param2).bind(param3)
                                                                        .bind(param4).bind(param5), translator);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by function passed as parameter.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1, T2, T3, T4, T5, T6> FN6<Result<R>, T1, T2, T3, T4, T5, T6> lift(final TFN6<R, T1, T2, T3, T4, T5, T6> function,
                                                                                   final FN1<Failure, Throwable> translator) {
        return (param1, param2, param3, param4, param5, param6) -> wrap(function.bind(param1).bind(param2).bind(param3)
                                                                                .bind(param4).bind(param5).bind(param6), translator);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by function passed as parameter.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1, T2, T3, T4, T5, T6, T7> FN7<Result<R>, T1, T2, T3, T4, T5, T6, T7> lift(final TFN7<R, T1, T2, T3, T4, T5, T6, T7> function,
                                                                                           final FN1<Failure, Throwable> translator) {
        return (param1, param2, param3, param4, param5, param6, param7) -> wrap(function.bind(param1).bind(param2).bind(param3).bind(param4)
                                                                                        .bind(param5).bind(param6).bind(param7), translator);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by function passed as parameter.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1, T2, T3, T4, T5, T6, T7, T8> FN8<Result<R>, T1, T2, T3, T4, T5, T6, T7, T8> lift(final TFN8<R, T1, T2, T3, T4, T5, T6, T7, T8> function,
                                                                                                   final FN1<Failure, Throwable> translator) {
        return (param1, param2, param3, param4, param5, param6, param7, param8) -> wrap(function.bind(param1).bind(param2).bind(param3).bind(param4)
                                                                                                .bind(param5).bind(param6).bind(param7).bind(param8),
                                                                                        translator);
    }

    /**
     * Convert function which can throw exceptions into function which returns {@link Result} instead.
     * {@link Throwable} into {@link Failure} converted by function passed as parameter.
     *
     * @param function
     *        Function to convert
     * @return Converted function
     */
    static <R, T1, T2, T3, T4, T5, T6, T7, T8, T9> FN9<Result<R>, T1, T2, T3, T4, T5, T6, T7, T8, T9> lift(final TFN9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> function,
                                                                                                           final FN1<Failure, Throwable> translator) {
        return (param1, param2, param3, param4, param5, param6, param7, param8, param9) -> wrap(function.bind(param1).bind(param2).bind(param3)
                                                                                                        .bind(param4).bind(param5).bind(param6)
                                                                                                        .bind(param7).bind(param8).bind(param9),
                                                                                                translator);
    }
}

package org.reactivetoolbox.core.functional;

/*
 * Copyright (c) 2019 Sergiy Yevtushenko
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

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a function that accepts one argument and produces a result. Unlike similar {@link Function},
 * {@link #apply(Object)} method can throw exceptions. This interface is intentionally created be similar to (and
 * interoperable with) {@link Function}.
 *
 * @param <T>
 *        the type of the input to the function
 * @param <R>
 *        the type of the result of the function
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t
     *        the function argument
     * @return the function result
     */
    R apply(T t) throws Throwable;

    /**
     * Returns a composed function that first applies the {@code before} function to its input, and then applies this
     * function to the result.
     *
     * @param <V>
     *        the type of input to the {@code before} function, and to the composed function
     * @param before
     *        the function to apply before this function is applied
     * @return a composed function that first applies the {@code before} function and then applies this function
     * @throws NullPointerException
     *         if before is null
     * @see #andThen(CheckedFunction)
     */
    default <V> CheckedFunction<V, R> compose(CheckedFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    /**
     * Returns a composed function that first applies the {@code before} function to its input, and then applies this
     * function to the result.
     *
     * @param <V>
     *        the type of input to the {@code before} function, and to the composed function
     * @param before
     *        the function to apply before this function is applied
     * @return a composed function that first applies the {@code before} function and then applies this function
     * @throws NullPointerException
     *         if before is null
     * @see #andThen(CheckedFunction)
     */
    default <V> CheckedFunction<V, R> compose(Function<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    /**
     * Returns a composed function that first applies this function to its input, and then applies the {@code after}
     * function to the result.
     *
     * @param <V>
     *        the type of output of the {@code after} function, and of the composed function
     * @param after
     *        the function to apply after this function is applied
     * @return a composed function that first applies this function and then applies the {@code after} function
     * @throws NullPointerException
     *         if after is null
     * @see #compose(CheckedFunction)
     */
    default <V> CheckedFunction<T, V> andThen(CheckedFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }
}

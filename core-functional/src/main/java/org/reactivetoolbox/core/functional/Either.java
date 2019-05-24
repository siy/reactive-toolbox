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

import java.util.Optional;
import java.util.function.Function;

/**
 * Convenience type for use in cases when function may return different types. For example, when either execution result
 * or error value can be returned. By convention <code>right</code> value holds results of successful execution, while
 * <code>left</code> holds exception or error value.
 *
 * @param <L>
 *        left value type
 * @param <R>
 *        right value type
 */
public final class Either<L, R> {
    private final L left;
    private final R right;

    private Either(final L left, final R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Build an instance of {@link Either} for given <code>left</code> value.
     *
     * @param value
     *        left value
     * @param <L>
     *        type of left value
     * @param <R>
     *        type of right value
     * @return built instance
     */
    public static <L, R> Either<L, R> left(final L value) {
        return new Either<>(value, null);
    }

    /**
     * Build an instance of {@link Either} for given <code>right</code> value.
     *
     * @param value
     *        right value
     * @param <L>
     *        type of left value
     * @param <R>
     *        type of right value
     * @return built instance
     */
    public static <L, R> Either<L, R> right(final R value) {
        return new Either<>(null, value);
    }

    /**
     * Similar to {@link #left()}, but enforces convention and returns an instance of {@link Either} for given
     * <code>left</code> value.
     *
     * @param value
     *        right value
     * @param <L>
     *        type of left value
     * @param <R>
     *        type of right value
     * @return built instance
     */
    public static <L, R> Either<L, R> failure(final L value) {
        return left(value);
    }

    /**
     * Similar to {@link #right()}, but enforces convention and returns an instance of {@link Either} for given
     * <code>right</code> value.
     *
     * @param value
     *        right value
     * @param <L>
     *        type of left value
     * @param <R>
     *        type of right value
     * @return built instance
     */
    public static <L, R> Either<L, R> success(final R value) {
        return right(value);
    }

    /**
     * Apply transformation and create new instance.
     *
     * @param mapper
     *        Function to apply
     * @return transformed instance
     */
    public <L1, R1> Either<L1, R1> map(final Functions.FN2<Either<L1, R1>, L, R> mapper) {
        return mapper.apply(left, right);
    }

    /**
     * Get access to left value wrapped into {@link Optional}.
     */
    public Optional<L> left() {
        return Optional.ofNullable(left);
    }

    /**
     * Get access to right value wrapped into {@link Optional}.
     */
    public Optional<R> right() {
        return Optional.ofNullable(right);
    }

    /**
     * Return <code>true</code> if instance holds left value.
     */
    public boolean isLeft() {
        return left != null;
    }

    /**
     * Return <code>true</code> if instance holds right value.
     */
    public boolean isRight() {
        return right != null;
    }

    /**
     * Wrap checked function into {@link Function} which returns {@link Either}.
     *
     * @param function
     *        function to wrap
     * @param <T>
     *        input value type
     * @param <R>
     *        result value type
     * @return {@link Function} which returns {@link Either}
     */
    public static <T, R> Function<T, Either<Exception, R>> lift(final CheckedFunction<T, R> function) {
        return t -> {
            try {
                return Either.success(function.apply(t));
            } catch (final Exception ex) {
                return Either.failure(ex);
            }
        };
    }

    /**
     * Wrap checked function into {@link Function} which returns {@link Either}. This method enables convenient
     * transformation of the exception into user defined type.
     *
     * @param function
     *        function to wrap
     * @param errorMapper
     *        function which transforms exception into user defined type
     * @param <T>
     *        input value type
     * @param <R>
     *        result value type
     * @return {@link Function} which returns {@link Either}
     */
    public static <T, R, N> Function<T, Either<N, R>> lift(final CheckedFunction<T, R> function,
                                                           final Function<Exception, N> errorMapper) {
        return t -> {
            try {
                return Either.success(function.apply(t));
            } catch (final Exception ex) {
                return Either.failure(errorMapper.apply(ex));
            }
        };
    }

    /**
     * Wrap checked function into {@link Function} which returns {@link Either}. In contrast to
     * {@link #lift(CheckedFunction)}, this function returns {@link Pair} which holds exception and initial input value
     * in case if exception was thrown. This is suitable for cases when operation might need to be retried with initial
     * input value.
     *
     * @param function
     *        function to wrap
     * @param <T>
     *        input value type
     * @param <R>
     *        output value type
     * @return {@link Function} which returns {@link Either}
     */
    public static <T, R> Function<T, Either<Pair<Exception, T>, R>>
           liftWithValue(final CheckedFunction<T, R> function) {
        return t -> {
            try {
                return Either.success(function.apply(t));
            } catch (final Exception ex) {
                return Either.failure(Pair.of(ex, t));
            }
        };
    }

    @Override
    public String toString() {
        if (isLeft()) {
            return "Left(" + left + ")";
        }
        return "Right(" + right + ")";
    }
}

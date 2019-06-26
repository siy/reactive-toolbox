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

import org.reactivetoolbox.core.functional.Functions.FN1;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Convenience type for use in cases when function may return either result of successful execution or failure.
 *
 * @param <F>
 *        failure type
 * @param <S>
 *        success type
 */
//TODO: rework into interface + 2 implementations
public final class Either<F, S> {
    private final F failure;
    private final S success;

    private Either(final F failure, final S success) {
        this.failure = failure;
        this.success = success;
    }

    /**
     * Return <code>true</code> if instance holds failure value.
     */
    public boolean isFailure() {
        return failure != null;
    }

    /**
     * Return <code>true</code> if instance holds success value.
     */
    public boolean isSuccess() {
        return success != null;
    }

    /**
     * Create instance which holds success.
     *
     * @param success
     *        success value
     * @param <F>
     *        failure type
     * @param <S>
     *        success type
     * @return built instance
     */
    public static <F, S> Either<F, S> success(final S success) {
        return new Either<>(null, success);
    }

    /**
     * Get access to success value wrapped into {@link Optional}.
     */
    public Optional<S> success() {
        return Optional.ofNullable(success);
    }

    /**
     * Create instance which holds failure.
     *
     * @param failure
     *        failure value
     * @param <F>
     *        failure type
     * @param <S>
     *        success type
     * @return built instance
     */
    public static <F, S> Either<F, S> failure(final F failure) {
        return new Either<>(failure, null);
    }

    /**
     * Get access to failure wrapped into {@link Optional}.
     */
    public Optional<F> failure() {
        return Optional.ofNullable(failure);
    }

    /**
     * Transform given instance into another, using provided mapper.
     * Transformation takes place only if current instance contains success.
     *
     * @param mapper
     *        Transformation to apply
     * @param <NS>
     *        New type for success
     * @return transformed instance
     */
    //TODO: this is flatMap, actually
    public <NS> Either<F, NS> map(final FN1<Either<F, NS>, S> mapper) {
        return isSuccess() ? mapper.apply(success) : failure(failure);
    }

    /**
     * Transform given instance into another one which has same success type
     * but new failure type. Convenient for transformation of instance containing
     * {@link Throwable} into some more convenient type
     *
     * @param mapper
     *        Transformation
     * @param <NF>
     *        New type for failure get
     * @return transformed instance
     */
    //TODO: rename method
    public <NF> Either<NF, S> mapLeft(final FN1<NF, F> mapper) {
        return isFailure() ? failure(mapper.apply(failure)) : success(success);
    }

    /**
     * Expose success value or a replacement provided by caller if instance
     * contains failure
     *
     * @param replacement
     *        Replacement value used in case of failure
     * @return contained success value if there is one, otherwise replacement value
     */
    public S otherwise(final S replacement) {
        if (isFailure()) {
            return replacement;
        }
        return success;
    }

    /**
     * Expose success value or a replacement obtained from provided
     * supplier if instance contains failure
     *
     * @param supplier
     *        Supplier for replacement. Invoked only if instance contains a failure
     * @return contained success get if there is one ond replacement get otherwise
     */
    public S otherwiseGet(final Supplier<S> supplier) {
        if (isFailure()) {
            return supplier.get();
        }
        return success;
    }

    /**
     * Expose success or throw an {@link IllegalStateException} if instance
     * contains failure
     *
     * @return contained success value
     * @throws IllegalStateException if current instance contains failure
     */
    @Deprecated
    public S otherwiseThrow() {
        if (isFailure()) {
            throw (failure instanceof Throwable)
                    ? new IllegalStateException((Throwable) failure)
                    : new IllegalStateException("'Either' does not contain success get");
        }
        return success;
    }

    /**
     * Expose success value or throw user-provided exception
     *
     * @param error
     *        Exception supplier
     * @param <E>
     *        Type of thrown exception
     * @return contained success get
     */
    @Deprecated
    public <E extends RuntimeException> S otherwiseThrow(final Supplier<E> error) {
        if (isFailure()) {
            throw error.get();
        }
        return success;
    }

    /**
     * Convenience method to get access to success value without affecting current instance
     *
     * @param consumer
     *        Receiver for successful result
     * @return same instance
     */
    //TODO: use 'on' naming
    public Either<F, S> ifSuccess(Consumer<S> consumer) {
        if (isSuccess()) {
            consumer.accept(success);
        }
        return this;
    }

    /**
     * Convenience method to get access to failure value without changing current instance
     *
     * @param consumer
     *        Receiver for failure result
     * @return same instance
     */
    public Either<F, S> ifFailure(Consumer<F> consumer) {
        if (isFailure()) {
            consumer.accept(failure);
        }
        return this;
    }

    /**
     * Wrap function which throws checked exception into {@link Function} which returns {@link Either}.
     *
     * @param function
     *        function to wrap
     * @param <T>
     *        input get type
     * @param <R>
     *        result get type
     * @return {@link Function} which returns {@link Either}
     */
    public static <T, R> Function<T, Either<Throwable, R>> lift(final CheckedFunction<T, R> function) {
        return t -> {
            try {
                return Either.success(function.apply(t));
            } catch (final Throwable ex) {
                return Either.failure(ex);
            }
        };
    }

    /**
     * Wrap checked function into {@link Function} which returns {@link Either}. In contrast to
     * {@link #lift(CheckedFunction)}, this function returns {@link Pair} which holds exception and initial input get
     * in case if exception was thrown. This is suitable for cases when operation might need to be retried with initial
     * input get.
     *
     * @param function
     *        function to wrap
     * @param <T>
     *        input get type
     * @param <R>
     *        output get type
     * @return {@link Function} which returns {@link Either}
     */
    public static <T, R> Function<T, Either<Pair<Throwable, T>, R>> liftWithValue(final CheckedFunction<T, R> function) {
        return t -> {
            try {
                return Either.success(function.apply(t));
            } catch (final Throwable ex) {
                return Either.failure(Pair.of(ex, t));
            }
        };
    }

    @Override
    public String toString() {
        if (isFailure()) {
            return "Left(" + failure + ")";
        }
        return "Right(" + success + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Either<?, ?> either = (Either<?, ?>) o;

        return Objects.equals(failure, either.failure)
            && Objects.equals(success, either.success);
    }

    @Override
    public int hashCode() {
        return Objects.hash(failure, success);
    }
}

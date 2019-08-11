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
import java.util.StringJoiner;
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
public interface Either<F, S> {
    /**
     * Return <code>true</code> if instance holds failure value.
     */
    boolean isFailure();

    /**
     * Return <code>true</code> if instance holds success value.
     */
    boolean isSuccess();

    /**
     * Get access to success value wrapped into {@link Option}.
     */
    Option<S> success();

    /**
     * Get access to failure wrapped into {@link Option}.
     */
    Option<F> failure();

    /**
     * Transform given instance into another, using provided mapper.
     * Transformation takes place only if current instance contains success.
     *
     * @param mapper
     *        Transformation to apply
     * @param <NF>
     *        New type for failure
     * @param <NS>
     *        New type for success
     * @return transformed instance
     */
    <NF, NS> Either<NF, NS> flatMap(final FN1<? extends Either<NF, NS>, ? super S> mapper);

    /**
     * Transform given instance into another one which has same success type
     * but new failure type. Convenient for transformation of instance containing
     * {@link Throwable} into some more convenient type
     *
     * @param mapper
     *        Transformation function
     * @param <NF>
     *        New type for failure
     * @return transformed instance
     */
    <NF> Either<NF, S> mapFailure(final FN1<? extends NF, ? super F> mapper);

    /**
     * Transform given instance into another one which has same failure type
     * but new success type. Convenient for "happy day scenario" processing
     *
     * @param mapper
     *        Transformation function
     * @param <NS>
     *        New type for success
     * @return transformed instance
     */
    <NS> Either<F, NS> mapSuccess(final FN1<? extends NS, ? super S> mapper);

    /**
     * Expose success value or a replacement provided by caller if instance
     * contains failure
     *
     * @param replacement
     *        Replacement value used in case of failure
     * @return contained success value if there is one, otherwise replacement value
     */
    S otherwise(final S replacement);

    /**
     * Expose success value or a replacement obtained from provided
     * supplier if instance contains failure
     *
     * @param supplier
     *        Supplier for replacement. Invoked only if instance contains a failure
     * @return contained success get if there is one ond replacement get otherwise
     */
    S otherwise(final Supplier<? extends S> supplier);

    /**
     * Convenience method to get access to success value without affecting current instance
     *
     * @param consumer
     *        Receiver for successful result
     * @return same instance
     */
    Either<F, S> onSuccess(Consumer<S> consumer);

    /**
     * Convenience method to get access to failure value without changing current instance
     *
     * @param consumer
     *        Receiver for failure result
     * @return same instance
     */
    Either<F, S> onFailure(Consumer<F> consumer);

    //----------------------------------------------------------------------------------------------

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
    static <F, S> Either<F, S> success(final S success) {
        return new Success<>(success);
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
    static <F, S> Either<F, S> failure(final F failure) {
        return new Failure<>(failure);
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
    static <T, R> Function<T, Either<Throwable, R>> lift(final CheckedFunction<T, R> function) {
        return t -> {
            try {
                return new Success<>(function.apply(t));
            } catch (final Throwable ex) {
                return new Failure<>(ex);
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
    static <T, R> Function<T, Either<Pair<Throwable, T>, R>> liftWithValue(final CheckedFunction<T, R> function) {
        return t -> {
            try {
                return new Success<>(function.apply(t));
            } catch (final Throwable ex) {
                return new Failure<>(Pair.of(ex, t));
            }
        };
    }

    final class Success<F, S> implements Either<F, S> {
        private final S success;

        private Success(final S success) {
            this.success = success;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public Option<S> success() {
            return Option.of(success);
        }

        @Override
        public Option<F> failure() {
            return Option.empty();
        }

        @Override
        public <NF, NS> Either<NF, NS> flatMap(final FN1<? extends Either<NF, NS>, ? super S> mapper) {
            return mapper.apply(success);
        }

        @Override
        public <NF> Either<NF, S> mapFailure(final FN1<? extends NF, ? super F> mapper) {
            return new Success<>(success);
        }

        @Override
        public <NS> Either<F, NS> mapSuccess(final FN1<? extends NS, ? super S> mapper) {
            return new Success<>(mapper.apply(success));
        }

        @Override
        public S otherwise(final S replacement) {
            return success;
        }

        @Override
        public S otherwise(final Supplier<? extends S> supplier) {
            return success;
        }

        @Override
        public Either<F, S> onSuccess(final Consumer<S> consumer) {
            consumer.accept(success);
            return this;
        }

        @Override
        public Either<F, S> onFailure(final Consumer<F> consumer) {
            return this;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            return Objects.equals(success, ((Success<?, ?>) o).success);
        }

        @Override
        public int hashCode() {
            return Objects.hash(success);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Success.class.getSimpleName() + "[", "]")
                    .add(success.toString())
                    .toString();
        }
    }

    final class Failure<F, S> implements Either<F, S> {
        private final F failure;

        private Failure(final F failure) {
            this.failure = failure;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public Option<S> success() {
            return Option.empty();
        }

        @Override
        public Option<F> failure() {
            return Option.of(failure);
        }

        //Note that for failure we can't actually transform the value, so error types of both instances should be compatible
        //otherwise we'll get runtime class cast exception
        @Override
        @SuppressWarnings("unchecked")
        public <NF, NS> Either<NF, NS> flatMap(final FN1<? extends Either<NF, NS>, ? super S> mapper) {
            return new Failure<>((NF) failure);
        }

        @Override
        public <NF> Either<NF, S> mapFailure(final FN1<? extends NF, ? super F> mapper) {
            return new Failure<>(mapper.apply(failure));
        }

        @Override
        public <NS> Either<F, NS> mapSuccess(final FN1<? extends NS, ? super S> mapper) {
            return new Failure<>(failure);
        }

        @Override
        public S otherwise(final S replacement) {
            return replacement;
        }

        @Override
        public S otherwise(final Supplier<? extends S> supplier) {
            return supplier.get();
        }

        @Override
        public Either<F, S> onSuccess(final Consumer<S> consumer) {
            return this;
        }

        @Override
        public Either<F, S> onFailure(final Consumer<F> consumer) {
            consumer.accept(failure);
            return this;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            return Objects.equals(failure, ((Failure<?, ?>) o).failure);
        }

        @Override
        public int hashCode() {
            return Objects.hash(failure);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Failure.class.getSimpleName() + "[", "]")
                    .add(failure.toString())
                    .toString();
        }
    }
}

package org.reactivetoolbox.core.lang.functional;

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
import org.reactivetoolbox.core.lang.functional.Functions.FN1;

import java.util.ArrayList;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.reactivetoolbox.core.lang.Tuple.tuple;

/**
 * Representation of the operation result. The result can be either success or failure.
 * In case of success it holds value returned by the operation. In case of failure it
 * holds a failure description.
 *
 * @param <T>
 *     Type of value in case of success.
 */
public interface Result<T> extends Either<Failure, T> {
    /**
     * Transform operation result value into value of other type and wrap new
     * value into {@link Result}. Transformation takes place if current instance
     * (this) contains successful result, otherwise current instance remains
     * unchanged and transformation function is not invoked.
     *
     * @param mapper
     *        Function to transform successful value
     *
     * @return transformed value (in case of success) or current instance (in case of failure)
     */
    default <R> Result<R> map(final FN1<R, ? super T> mapper) {
        return fold(l -> (Result<R>) this, r -> ok(mapper.apply(r)));
    }

    /**
     * Transform operation result into another operation result. In case if current
     * instance (this) is an error, transformation function is not invoked
     * and value remains the same.
     *
     * @param mapper
     *        Function to apply to result
     *
     * @return transformed value (in case of success) or current instance (in case of failure)
     */
    default <R> Result<R> flatMap(final FN1<Result<R>, ? super T> mapper) {
        return fold(t -> (Result<R>) this, mapper);
    }

    /**
     * Apply consumers to result value. Note that depending on the result (success or failure) only one consumer will be
     * applied at a time.
     *
     * @param failureConsumer
     *        Consumer for failure result
     * @param successConsumer
     *        Consumer for success result
     *
     * @return current instance
     */
    default Result<T> apply(final Consumer<? super Failure> failureConsumer, final Consumer<? super T> successConsumer) {
        return fold(t -> {failureConsumer.accept(t); return this;}, t -> {successConsumer.accept(t); return this;});
    }

    /**
     * Combine current instance with another result. If current instance holds
     * success then result is equivalent to current instance, otherwise other
     * instance (passed as {@code replacement} parameter) is returned.
     *
     * @param replacement
     *        Value to return if current instance contains failure operation result
     *
     * @return current instance in case of success or replacement instance in case of failure.
     */
    default Result<T> or(final Result<T> replacement) {
        return fold(t -> replacement, t -> this);
    }

    /**
     * Combine current instance with another result. If current instance holds
     * success then result is equivalent to current instance, otherwise instance provided by
     * specified supplier is returned.
     *
     * @param supplier
     *        Supplier for replacement instance if current instance contains failure operation result
     *
     * @return current instance in case of success or result returned by supplier in case of failure.
     */
    default Result<T> or(final Supplier<Result<T>> supplier) {
        return fold(t -> supplier.get(), t -> this);
    }

    /**
     * Pass successful operation result value into provided consumer.
     *
     * @param consumer
     *        Consumer to pass value to
     *
     * @return current instance for fluent call chaining
     */
    Result<T> onSuccess(final Consumer<T> consumer);

    Result<T> onSuccessDo(final Runnable action);

    /**
     * Pass failure operation result value into provided consumer.
     *
     * @param consumer
     *        Consumer to pass value to
     *
     * @return current instance for fluent call chaining
     */
    Result<T> onFailure(final Consumer<? super Failure> consumer);

    Result<T> onFailureDo(final Runnable action);

    /**
     * Convert instance into {@link Option} of the same type. Successful instance
     * is converted into present {@link Option} and failure - into empty {@link Option}.
     * Note that during such a conversion error information may get lost.
     *
     * @return {@link Option} instance which is present in case of success and missing
     *         in case of failure.
     */
    default Option<T> asOption() {
        return fold(t1 -> Option.empty(), Option::option);
    }

    /**
     * Create an instance of successful operation result.
     *
     * @param value
     *        Operation result
     *
     * @return created instance
     */
    static <R> Result<R> ok(final R value) {
        return new ResultOk<>(value);
    }

    /**
     * Create an instance of failure operation result.
     * @param value
     *        Operation error value
     *
     * @return created instance
     */
    static <R> Result<R> fail(final Failure value) {
        return new ResultFail<R>(value);
    }

    static <T> Result<List<T>> flatten(final List<Result<T>> resultList) {
        final Failure[] failure = new Failure[1];
        final var values = new ArrayList<T>();

        resultList.apply(val -> val.fold(f -> failure[0] = f, values::add));

        return failure[0] != null ? Result.fail(failure[0])
                                  : Result.ok(List.from(values));
    }

    static <T1> Result<Tuple1<T1>> flatten(final Result<T1> value) {
        return value.flatMap(vv1 -> ok(tuple(vv1)));
    }

    static <T1, T2> Result<Tuple2<T1, T2>> flatten(final Result<T1> value1, final Result<T2> value2) {
        return value1.flatMap(vv1 -> value2.flatMap(vv2 -> ok(tuple(vv1, vv2))));
    }

    static <T1, T2, T3> Result<Tuple3<T1, T2, T3>> flatten(final Result<T1> value1, final Result<T2> value2, final Result<T3> value3) {
        return value1.flatMap(vv1 ->
                 value2.flatMap(vv2 ->
                   value3.flatMap(vv3 -> ok(tuple(vv1, vv2, vv3)))));
    }

    static <T1, T2, T3, T4> Result<Tuple4<T1, T2, T3, T4>> flatten(final Result<T1> value1,
                                                                   final Result<T2> value2,
                                                                   final Result<T3> value3,
                                                                   final Result<T4> value4) {
        return value1.flatMap(vv1 ->
                 value2.flatMap(vv2 ->
                   value3.flatMap(vv3 ->
                     value4.flatMap(vv4 -> ok(tuple(vv1, vv2, vv3, vv4))))));
    }

    static <T1, T2, T3, T4, T5> Result<Tuple5<T1, T2, T3, T4, T5>> flatten(final Result<T1> value1,
                                                                           final Result<T2> value2,
                                                                           final Result<T3> value3,
                                                                           final Result<T4> value4,
                                                                           final Result<T5> value5) {
        return value1.flatMap(vv1 ->
                 value2.flatMap(vv2 ->
                   value3.flatMap(vv3 ->
                     value4.flatMap(vv4 ->
                       value5.flatMap(vv5 -> ok(tuple(vv1, vv2, vv3, vv4, vv5)))))));
    }

    static <T1, T2, T3, T4, T5, T6> Result<Tuple6<T1, T2, T3, T4, T5, T6>> flatten(final Result<T1> value1,
                                                                                   final Result<T2> value2,
                                                                                   final Result<T3> value3,
                                                                                   final Result<T4> value4,
                                                                                   final Result<T5> value5,
                                                                                   final Result<T6> value6) {
        return value1.flatMap(vv1 ->
                 value2.flatMap(vv2 ->
                   value3.flatMap(vv3 ->
                     value4.flatMap(vv4 ->
                       value5.flatMap(vv5 ->
                         value6.flatMap(vv6 -> ok(tuple(vv1, vv2, vv3, vv4, vv5, vv6))))))));
    }

    static <T1, T2, T3, T4, T5, T6, T7> Result<Tuple7<T1, T2, T3, T4, T5, T6, T7>> flatten(final Result<T1> value1,
                                                                                           final Result<T2> value2,
                                                                                           final Result<T3> value3,
                                                                                           final Result<T4> value4,
                                                                                           final Result<T5> value5,
                                                                                           final Result<T6> value6,
                                                                                           final Result<T7> value7) {
        return value1.flatMap(vv1 ->
                 value2.flatMap(vv2 ->
                   value3.flatMap(vv3 ->
                     value4.flatMap(vv4 ->
                       value5.flatMap(vv5 ->
                         value6.flatMap(vv6 ->
                           value7.flatMap(vv7 -> ok(tuple(vv1, vv2, vv3, vv4, vv5, vv6, vv7)))))))));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8> Result<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> flatten(final Result<T1> value1,
                                                                                                   final Result<T2> value2,
                                                                                                   final Result<T3> value3,
                                                                                                   final Result<T4> value4,
                                                                                                   final Result<T5> value5,
                                                                                                   final Result<T6> value6,
                                                                                                   final Result<T7> value7,
                                                                                                   final Result<T8> value8) {
        return value1.flatMap(vv1 ->
                 value2.flatMap(vv2 ->
                   value3.flatMap(vv3 ->
                     value4.flatMap(vv4 ->
                       value5.flatMap(vv5 ->
                         value6.flatMap(vv6 ->
                           value7.flatMap(vv7 ->
                             value8.flatMap(vv8 -> ok(tuple(vv1, vv2, vv3, vv4, vv5, vv6, vv7, vv8))))))))));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Result<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> flatten(final Result<T1> value1,
                                                                                                           final Result<T2> value2,
                                                                                                           final Result<T3> value3,
                                                                                                           final Result<T4> value4,
                                                                                                           final Result<T5> value5,
                                                                                                           final Result<T6> value6,
                                                                                                           final Result<T7> value7,
                                                                                                           final Result<T8> value8,
                                                                                                           final Result<T9> value9) {
        return value1.flatMap(vv1 ->
                 value2.flatMap(vv2 ->
                   value3.flatMap(vv3 ->
                     value4.flatMap(vv4 ->
                       value5.flatMap(vv5 ->
                         value6.flatMap(vv6 ->
                           value7.flatMap(vv7 ->
                             value8.flatMap(vv8 ->
                               value9.flatMap(vv9 -> ok(tuple(vv1, vv2, vv3, vv4, vv5, vv6, vv7, vv8, vv9)))))))))));
    }

    class ResultOk<R> implements Result<R> {
        private final R value;

        protected ResultOk(final R value) {
            this.value = value;
        }

        @Override
        public <T> T fold(final FN1<? extends T, ? super Failure> leftMapper,
                          final FN1<? extends T, ? super R> rightMapper) {
            return rightMapper.apply(value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }

            return (obj instanceof Result<?> result)
                   ? result.fold($ -> false, val -> Objects.equals(val, value))
                   : false;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", "Result-success(", ")")
                    .add(value.toString())
                    .toString();
        }

        @Override
        public Result<R> onSuccess(final Consumer<R> consumer) {
            consumer.accept(value);
            return this;
        }

        @Override
        public Result<R> onSuccessDo(final Runnable action) {
            action.run();
            return this;
        }

        @Override
        public Result<R> onFailure(final Consumer<? super Failure> consumer) {
            return this;
        }

        @Override
        public Result<R> onFailureDo(final Runnable action) {
            return this;
        }
    }

    class ResultFail<R> implements Result<R> {
        private final Failure value;

        protected ResultFail(final Failure value) {
            this.value = value;
        }

        @Override
        public <T> T fold(final FN1<? extends T, ? super Failure> leftMapper,
                          final FN1<? extends T, ? super R> rightMapper) {
            return leftMapper.apply(value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }

            return (obj instanceof Result<?> result)
                   ? result.fold(val -> Objects.equals(val, value), $ -> false)
                   : false;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", "Result-failure(", ")")
                    .add(value.toString())
                    .toString();
        }

        @Override
        public Result<R> onSuccess(final Consumer<R> consumer) {
            return this;
        }

        @Override
        public Result<R> onSuccessDo(final Runnable action) {
            return this;
        }

        @Override
        public Result<R> onFailure(final Consumer<? super Failure> consumer) {
            consumer.accept(value);
            return this;
        }

        @Override
        public Result<R> onFailureDo(final Runnable action) {
            action.run();
            return this;
        }
    }
}

package org.reactivetoolbox.core.functional;

/*
 * Copyright (c) 2017-2019 Sergiy Yevtushenko
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

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Functions.FN0;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Functions.FN2;
import org.reactivetoolbox.core.functional.Functions.FN3;
import org.reactivetoolbox.core.functional.Functions.FN4;
import org.reactivetoolbox.core.functional.Functions.FN5;
import org.reactivetoolbox.core.functional.Functions.FN6;
import org.reactivetoolbox.core.functional.Functions.FN7;
import org.reactivetoolbox.core.functional.Functions.FN8;
import org.reactivetoolbox.core.functional.Functions.FN9;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Tuple classes with various size and convenient static factories for tuples. All tuple classes are immutable. <br/>
 * The Tuple is a container for variables. Tuples with size 1 to 9 are provided. Each variable may have different type
 * and all type information is preserved.
 */
public final class Tuples {
    private Tuples() {
    }

    /**
     * Factory method for tuple with zero values.
     *
     * @return created tuple newInstance.
     */
    public static Tuple0 of() {
        return new Tuple0();
    }

    /**
     * Factory method for tuple with single value.
     *
     * @param param1
     *        Value to store in tuple.
     * @return created tuple newInstance.
     */
    public static <T1> Tuple1<T1> of(final T1 param1) {
        return new Tuple1<>(param1);
    }

    /**
     * Factory method for tuple with two values of different types.
     *
     * @param param1
     *        Value to store in tuple.
     * @param param2
     *        Value to store in tuple.
     * @return created tuple newInstance.
     */
    public static <T1, T2> Tuple2<T1, T2> of(final T1 param1, final T2 param2) {
        return new Tuple2<>(param1, param2);
    }

    /**
     * Factory method for tuple with three values of different types.
     *
     * @param param1
     *        Value to store in tuple.
     * @param param2
     *        Value to store in tuple.
     * @param param3
     *        Value to store in tuple.
     * @return created tuple newInstance.
     */
    public static <T1, T2, T3> Tuple3<T1, T2, T3> of(final T1 param1, final T2 param2, final T3 param3) {
        return new Tuple3<>(param1, param2, param3);
    }

    /**
     * Factory method for tuple with four values of different types.
     *
     * @param param1
     *        Value to store in tuple.
     * @param param2
     *        Value to store in tuple.
     * @param param3
     *        Value to store in tuple.
     * @param param4
     *        Value to store in tuple.
     * @return created tuple newInstance.
     */
    public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> of(final T1 param1, final T2 param2, final T3 param3,
                                                             final T4 param4) {
        return new Tuple4<>(param1, param2, param3, param4);
    }

    /**
     * Factory method for tuple with five values of different types.
     *
     * @param param1
     *        Value to store in tuple.
     * @param param2
     *        Value to store in tuple.
     * @param param3
     *        Value to store in tuple.
     * @param param4
     *        Value to store in tuple.
     * @param param5
     *        Value to store in tuple.
     * @return created tuple newInstance.
     */
    public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> of(final T1 param1, final T2 param2, final T3 param3,
                                                                     final T4 param4, final T5 param5) {
        return new Tuple5<>(param1, param2, param3, param4, param5);
    }

    /**
     * Factory method for tuple with six values of different types.
     *
     * @param param1
     *        Value to store in tuple.
     * @param param2
     *        Value to store in tuple.
     * @param param3
     *        Value to store in tuple.
     * @param param4
     *        Value to store in tuple.
     * @param param5
     *        Value to store in tuple.
     * @param param6
     *        Value to store in tuple.
     * @return created tuple newInstance.
     */
    public static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6>
           of(final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6) {
        return new Tuple6<>(param1, param2, param3, param4, param5, param6);
    }

    /**
     * Factory method for tuple with seven values of different types.
     *
     * @param param1
     *        Value to store in tuple.
     * @param param2
     *        Value to store in tuple.
     * @param param3
     *        Value to store in tuple.
     * @param param4
     *        Value to store in tuple.
     * @param param5
     *        Value to store in tuple.
     * @param param6
     *        Value to store in tuple.
     * @param param7
     *        Value to store in tuple.
     * @return created tuple newInstance.
     */
    public static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7>
           of(final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6,
              final T7 param7) {
        return new Tuple7<>(param1, param2, param3, param4, param5, param6, param7);
    }

    /**
     * Factory method for tuple with eight values of different types.
     *
     * @param param1
     *        Value to store in tuple.
     * @param param2
     *        Value to store in tuple.
     * @param param3
     *        Value to store in tuple.
     * @param param4
     *        Value to store in tuple.
     * @param param5
     *        Value to store in tuple.
     * @param param6
     *        Value to store in tuple.
     * @param param7
     *        Value to store in tuple.
     * @param param8
     *        Value to store in tuple.
     * @return created tuple newInstance.
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>
           of(final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6,
              final T7 param7, final T8 param8) {
        return new Tuple8<>(param1, param2, param3, param4, param5, param6, param7, param8);
    }

    /**
     * Factory method for tuple with nine values of different types.
     *
     * @param param1
     *        Value to store in tuple.
     * @param param2
     *        Value to store in tuple.
     * @param param3
     *        Value to store in tuple.
     * @param param4
     *        Value to store in tuple.
     * @param param5
     *        Value to store in tuple.
     * @param param6
     *        Value to store in tuple.
     * @param param7
     *        Value to store in tuple.
     * @param param8
     *        Value to store in tuple.
     * @param param9
     *        Value to store in tuple.
     * @return created tuple newInstance.
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>
           of(final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6,
              final T7 param7, final T8 param8, final T9 param9) {
        return new Tuple9<>(param1, param2, param3, param4, param5, param6, param7, param8, param9);
    }

    /**
     * Transform {@link Either} with failure or success into {@link Either} with which contains either failure or
     * tuple with success value.
     *
     * @param value
     *        Input value
     *
     * @return {@link Either} with failure if input parameter contains failure or with {@link Tuple} which contains
     *          success value from input parameter
     */
    public static <T1> Either<? extends BaseError, Tuple1<T1>> zip(final Either<? extends BaseError, T1> value) {
        return value.flatMap(vv1 -> Either.success(of(vv1)));
    }

    /**
     * Transform input {@link Either}'s with failure or success into {@link Either} with which contains either failure
     * or tuple with success values. Any parameter with failure will result to result with failure. The value of the
     * failure will be one from first input parameter with failure.
     *
     * @param value1
     *        Input value #1
     * @param value2
     *        Input value #2
     *
     * @return {@link Either} with failure if any input parameters contain failure or with {@link Tuple} which contains
     *          success values from input parameters
     */
    public static <T1, T2> Either<? extends BaseError, Tuple2<T1, T2>> zip(final Either<? extends BaseError, T1> value1,
                                                                           final Either<? extends BaseError, T2> value2) {
        return value1.flatMap(vv1 ->
                value2.flatMap(vv2 -> Either.success(of(vv1, vv2))));
    }

    /**
     * Transform input {@link Either}'s with failure or success into {@link Either} with which contains either failure
     * or tuple with success values. Any parameter with failure will result to result with failure. The value of the
     * failure will be one from first input parameter with failure.
     *
     * @param value1
     *        Input value #1
     * @param value2
     *        Input value #2
     * @param value3
     *        Input value #3
     *
     * @return {@link Either} with failure if any input parameters contain failure or with {@link Tuple} which contains
     *          success values from input parameters
     */
    public static <T1, T2, T3>  Either<? extends BaseError, Tuple3<T1, T2, T3>> zip(final Either<? extends BaseError, T1> value1,
                                                                                    final Either<? extends BaseError, T2> value2,
                                                                                    final Either<? extends BaseError, T3> value3) {
        return value1.flatMap(vv1 ->
                value2.flatMap(vv2 ->
                 value3.flatMap(vv3 -> Either.success(of(vv1, vv2, vv3)))));
    }

    /**
     * Transform input {@link Either}'s with failure or success into {@link Either} with which contains either failure
     * or tuple with success values. Any parameter with failure will result to result with failure. The value of the
     * failure will be one from first input parameter with failure.
     *
     * @param value1
     *        Input value #1
     * @param value2
     *        Input value #2
     * @param value3
     *        Input value #3
     * @param value4
     *        Input value #4
     *
     * @return {@link Either} with failure if any input parameters contain failure or with {@link Tuple} which contains
     *          success values from input parameters
     */
    public static <T1, T2, T3, T4>  Either<? extends BaseError, Tuple4<T1, T2, T3, T4>> zip(final Either<? extends BaseError, T1> value1,
                                                                                            final Either<? extends BaseError, T2> value2,
                                                                                            final Either<? extends BaseError, T3> value3,
                                                                                            final Either<? extends BaseError, T4> value4) {
        return value1.flatMap(vv1 ->
                value2.flatMap(vv2 ->
                 value3.flatMap(vv3 ->
                  value4.flatMap(vv4 -> Either.success(of(vv1, vv2, vv3, vv4))))));
    }

    /**
     * Transform input {@link Either}'s with failure or success into {@link Either} with which contains either failure
     * or tuple with success values. Any parameter with failure will result to result with failure. The value of the
     * failure will be one from first input parameter with failure.
     *
     * @param value1
     *        Input value #1
     * @param value2
     *        Input value #2
     * @param value3
     *        Input value #3
     * @param value4
     *        Input value #4
     * @param value5
     *        Input value #5
     *
     * @return {@link Either} with failure if any input parameters contain failure or with {@link Tuple} which contains
     *          success values from input parameters
     */
    public static <T1, T2, T3, T4, T5>  Either<? extends BaseError, Tuple5<T1, T2, T3, T4, T5>> zip(final Either<? extends BaseError, T1> value1,
                                                                                                    final Either<? extends BaseError, T2> value2,
                                                                                                    final Either<? extends BaseError, T3> value3,
                                                                                                    final Either<? extends BaseError, T4> value4,
                                                                                                    final Either<? extends BaseError, T5> value5) {
        return value1.flatMap(vv1 ->
                value2.flatMap(vv2 ->
                 value3.flatMap(vv3 ->
                  value4.flatMap(vv4 ->
                   value5.flatMap(vv5 -> Either.success(of(vv1, vv2, vv3, vv4, vv5)))))));
    }

    /**
     * Transform input {@link Either}'s with failure or success into {@link Either} with which contains either failure
     * or tuple with success values. Any parameter with failure will result to result with failure. The value of the
     * failure will be one from first input parameter with failure.
     *
     * @param value1
     *        Input value #1
     * @param value2
     *        Input value #2
     * @param value3
     *        Input value #3
     * @param value4
     *        Input value #4
     * @param value5
     *        Input value #5
     * @param value6
     *        Input value #6
     *
     * @return {@link Either} with failure if any input parameters contain failure or with {@link Tuple} which contains
     *          success values from input parameters
     */
    public static <T1, T2, T3, T4, T5, T6>  Either<? extends BaseError, Tuple6<T1, T2, T3, T4, T5, T6>> zip(final Either<? extends BaseError, T1> value1,
                                                                                                            final Either<? extends BaseError, T2> value2,
                                                                                                            final Either<? extends BaseError, T3> value3,
                                                                                                            final Either<? extends BaseError, T4> value4,
                                                                                                            final Either<? extends BaseError, T5> value5,
                                                                                                            final Either<? extends BaseError, T6> value6) {
        return value1.flatMap(vv1 ->
                value2.flatMap(vv2 ->
                 value3.flatMap(vv3 ->
                  value4.flatMap(vv4 ->
                   value5.flatMap(vv5 ->
                    value6.flatMap(vv6 -> Either.success(of(vv1, vv2, vv3, vv4, vv5, vv6))))))));
    }

    /**
     * Transform input {@link Either}'s with failure or success into {@link Either} with which contains either failure
     * or tuple with success values. Any parameter with failure will result to result with failure. The value of the
     * failure will be one from first input parameter with failure.
     *
     * @param value1
     *        Input value #1
     * @param value2
     *        Input value #2
     * @param value3
     *        Input value #3
     * @param value4
     *        Input value #4
     * @param value5
     *        Input value #5
     * @param value6
     *        Input value #6
     * @param value7
     *        Input value #7
     *
     * @return {@link Either} with failure if any input parameters contain failure or with {@link Tuple} which contains
     *          success values from input parameters
     */
    public static <T1, T2, T3, T4, T5, T6, T7>  Either<? extends BaseError, Tuple7<T1, T2, T3, T4, T5, T6, T7>> zip(final Either<? extends BaseError, T1> value1,
                                                                                                                    final Either<? extends BaseError, T2> value2,
                                                                                                                    final Either<? extends BaseError, T3> value3,
                                                                                                                    final Either<? extends BaseError, T4> value4,
                                                                                                                    final Either<? extends BaseError, T5> value5,
                                                                                                                    final Either<? extends BaseError, T6> value6,
                                                                                                                    final Either<? extends BaseError, T7> value7) {
        return value1.flatMap(vv1 ->
                value2.flatMap(vv2 ->
                 value3.flatMap(vv3 ->
                  value4.flatMap(vv4 ->
                   value5.flatMap(vv5 ->
                    value6.flatMap(vv6 ->
                     value7.flatMap(vv7 -> Either.success(of(vv1, vv2, vv3, vv4, vv5, vv6, vv7)))))))));
    }

    /**
     * Transform input {@link Either}'s with failure or success into {@link Either} with which contains either failure
     * or tuple with success values. Any parameter with failure will result to result with failure. The value of the
     * failure will be one from first input parameter with failure.
     *
     * @param value1
     *        Input value #1
     * @param value2
     *        Input value #2
     * @param value3
     *        Input value #3
     * @param value4
     *        Input value #4
     * @param value5
     *        Input value #5
     * @param value6
     *        Input value #6
     * @param value7
     *        Input value #7
     * @param value8
     *        Input value #8
     *
     * @return {@link Either} with failure if any input parameters contain failure or with {@link Tuple} which contains
     *          success values from input parameters
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8>  Either<? extends BaseError, Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> zip(final Either<? extends BaseError, T1> value1,
                                                                                                                            final Either<? extends BaseError, T2> value2,
                                                                                                                            final Either<? extends BaseError, T3> value3,
                                                                                                                            final Either<? extends BaseError, T4> value4,
                                                                                                                            final Either<? extends BaseError, T5> value5,
                                                                                                                            final Either<? extends BaseError, T6> value6,
                                                                                                                            final Either<? extends BaseError, T7> value7,
                                                                                                                            final Either<? extends BaseError, T8> value8) {
        return value1.flatMap(vv1 ->
                value2.flatMap(vv2 ->
                 value3.flatMap(vv3 ->
                  value4.flatMap(vv4 ->
                   value5.flatMap(vv5 ->
                    value6.flatMap(vv6 ->
                     value7.flatMap(vv7 ->
                      value8.flatMap(vv8 -> Either.success(of(vv1, vv2, vv3, vv4, vv5, vv6, vv7, vv8))))))))));
    }

    /**
     * Transform input {@link Either}'s with failure or success into {@link Either} with which contains either failure
     * or tuple with success values. Any parameter with failure will result to result with failure. The value of the
     * failure will be one from first input parameter with failure.
     *
     * @param value1
     *        Input value #1
     * @param value2
     *        Input value #2
     * @param value3
     *        Input value #3
     * @param value4
     *        Input value #4
     * @param value5
     *        Input value #5
     * @param value6
     *        Input value #6
     * @param value7
     *        Input value #7
     * @param value8
     *        Input value #8
     * @param value9
     *        Input value #9
     *
     * @return {@link Either} with failure if any input parameters contain failure or with {@link Tuple} which contains
     *          success values from input parameters
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9>  Either<? extends BaseError, Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> zip(final Either<? extends BaseError, T1> value1,
                                                                                                                                    final Either<? extends BaseError, T2> value2,
                                                                                                                                    final Either<? extends BaseError, T3> value3,
                                                                                                                                    final Either<? extends BaseError, T4> value4,
                                                                                                                                    final Either<? extends BaseError, T5> value5,
                                                                                                                                    final Either<? extends BaseError, T6> value6,
                                                                                                                                    final Either<? extends BaseError, T7> value7,
                                                                                                                                    final Either<? extends BaseError, T8> value8,
                                                                                                                                    final Either<? extends BaseError, T9> value9) {
        return value1.flatMap(vv1 ->
                value2.flatMap(vv2 ->
                 value3.flatMap(vv3 ->
                  value4.flatMap(vv4 ->
                   value5.flatMap(vv5 ->
                    value6.flatMap(vv6 ->
                     value7.flatMap(vv7 ->
                      value8.flatMap(vv8 ->
                       value9.flatMap(vv9 -> Either.success(of(vv1, vv2, vv3, vv4, vv5, vv6, vv7, vv8, vv9)))))))))));
    }

    /**
     * Base class for all tuples.
     */
    public static class Tuple {
        protected final Object[] values;

        protected Tuple(final Object... values) {
            this.values = values;
        }

        public int size() {
            return values.length;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Tuple tuple = (Tuple) o;
            return Arrays.equals(values, tuple.values);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(values);
        }

        @Override
        public String toString() {
            return Arrays.toString(values);
        }

        /**
         * Return all values contained in tuple as stream.
         * <p>
         * <b>WARNING: this method is inherently type-unsafe, because all information about types is lost. Use with
         * extreme care!!!</b>
         *
         * @return {@link Stream} of values contained in tuple
         */
        @SuppressWarnings("unchecked")
        public <R> Stream<R> stream() {
            return Stream.of(values).map(v -> (R) v);
        }
    }

    /**
     * Empty tuple
     */
    public static class Tuple0 extends Tuple {
        private Tuple0() {
            super();
        }

        public <T> T map(final FN0<T> mapper) {
            return mapper.apply();
        }

        public <T1> Tuple1<T1> append(final T1 value) {
            return new Tuple1<>(value);
        }
    }

    /**
     * Tuple with 1 value
     */
    public static class Tuple1<T1> extends Tuple {
        private Tuple1(final T1 param1) {
            super(param1);
        }

        @SuppressWarnings("unchecked")
        public <T> T map(final FN1<T, T1> mapper) {
            return mapper.apply((T1) values[0]);
        }

        @SuppressWarnings("unchecked")
        public <T2> Tuple2<T1, T2> append(final T2 value) {
            return new Tuple2<>((T1) values[0], value);
        }

        @SuppressWarnings("unchecked")
        public <R1> Tuple1<R1> map(final Tuple1<FN1<R1, ? super T1>> mapper) {
            return of(((FN1<R1, ? super T1>) mapper.values[0]).apply((T1) values[0]));
        }
    }

    /**
     * Tuple with 2 values
     */
    public static class Tuple2<T1, T2> extends Tuple {
        private Tuple2(final T1 param1, final T2 param2) {
            super(param1, param2);
        }

        @SuppressWarnings("unchecked")
        public <T> T map(final FN2<T, ? super T1, ? super T2> mapper) {
            return mapper.apply((T1) values[0], (T2) values[1]);
        }

        @SuppressWarnings("unchecked")
        public <T3> Tuple3<T1, T2, T3> append(final T3 value) {
            return new Tuple3<>((T1) values[0], (T2) values[1], value);
        }

        @SuppressWarnings("unchecked")
        public <R1, R2> Tuple2<R1, R2> map(final Tuple2<FN1<R1, ? super T1>, FN1<R2, ? super T2>> mapper) {
            return of(((FN1<R1, ? super T1>) mapper.values[0]).apply((T1) values[0]),
                      ((FN1<R2, ? super T2>) mapper.values[1]).apply((T2) values[1]));
        }

        @SuppressWarnings("unchecked")
        public <R1, R2> Tuple2<R1, R2> map(final FN1<R1, ? super T1> mapper1, final FN1<R2, ? super T2> mapper2) {
            return of(mapper1.apply((T1) values[0]),
                      mapper2.apply((T2) values[1]));
        }
    }

    /**
     * Tuple with 3 values
     */
    public static class Tuple3<T1, T2, T3> extends Tuple {
        private Tuple3(final T1 param1, final T2 param2, final T3 param3) {
            super(param1, param2, param3);
        }

        @SuppressWarnings("unchecked")
        public <T> T map(final FN3<T, T1, T2, T3> mapper) {
            return mapper.apply((T1) values[0], (T2) values[1], (T3) values[2]);
        }

        @SuppressWarnings("unchecked")
        public <T4> Tuple4<T1, T2, T3, T4> append(final T4 value) {
            return new Tuple4<>((T1) values[0], (T2) values[1], (T3) values[2], value);
        }

        @SuppressWarnings("unchecked")
        public <R1, R2, R3> Tuple3<R1, R2, R3> map(
                final Tuple3<FN1<R1, ? super T1>, FN1<R2, ? super T2>, FN1<R3, ? super T3>> mapper) {
            return of(((FN1<R1, ? super T1>) mapper.values[0]).apply((T1) values[0]),
                      ((FN1<R2, ? super T2>) mapper.values[1]).apply((T2) values[1]),
                      ((FN1<R3, ? super T3>) mapper.values[2]).apply((T3) values[2]));
        }
    }

    /**
     * Tuple with 4 values
     */
    public static class Tuple4<T1, T2, T3, T4> extends Tuple {
        private Tuple4(final T1 param1, final T2 param2, final T3 param3, final T4 param4) {
            super(param1, param2, param3, param4);
        }

        @SuppressWarnings("unchecked")
        public <T> T map(final FN4<T, T1, T2, T3, T4> mapper) {
            return mapper.apply((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3]);
        }

        @SuppressWarnings("unchecked")
        public <T5> Tuple5<T1, T2, T3, T4, T5> append(final T5 value) {
            return new Tuple5<>((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], value);
        }

        @SuppressWarnings("unchecked")
        public <R1, R2, R3, R4> Tuple4<R1, R2, R3, R4> map(
                final Tuple4<FN1<R1, ? super T1>, FN1<R2, ? super T2>, FN1<R3, ? super T3>, FN1<R4, ? super T4>> mapper) {
            return of(((FN1<R1, ? super T1>) mapper.values[0]).apply((T1) values[0]),
                      ((FN1<R2, ? super T2>) mapper.values[1]).apply((T2) values[1]),
                      ((FN1<R3, ? super T3>) mapper.values[2]).apply((T3) values[2]),
                      ((FN1<R4, ? super T4>) mapper.values[3]).apply((T4) values[3]));
        }
    }

    /**
     * Tuple with 5 values
     */
    public static class Tuple5<T1, T2, T3, T4, T5> extends Tuple {
        private Tuple5(final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5) {
            super(param1, param2, param3, param4, param5);
        }

        @SuppressWarnings("unchecked")
        public <T> T map(final FN5<T, T1, T2, T3, T4, T5> mapper) {
            return mapper.apply((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4]);
        }

        @SuppressWarnings("unchecked")
        public <T6> Tuple6<T1, T2, T3, T4, T5, T6> append(final T6 value) {
            return new Tuple6<>((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4], value);
        }

        @SuppressWarnings("unchecked")
        public <R1, R2, R3, R4, R5> Tuple5<R1, R2, R3, R4, R5> map(
                final Tuple5<FN1<R1, ? super T1>, FN1<R2, ? super T2>, FN1<R3, ? super T3>, FN1<R4, ? super T4>,
                        FN1<R5, ? super T5>> mapper) {
            return of(((FN1<R1, ? super T1>) mapper.values[0]).apply((T1) values[0]),
                      ((FN1<R2, ? super T2>) mapper.values[1]).apply((T2) values[1]),
                      ((FN1<R3, ? super T3>) mapper.values[2]).apply((T3) values[2]),
                      ((FN1<R4, ? super T4>) mapper.values[3]).apply((T4) values[3]),
                      ((FN1<R5, ? super T5>) mapper.values[4]).apply((T5) values[4]));
        }
    }

    /**
     * Tuple with 6 values
     */
    public static class Tuple6<T1, T2, T3, T4, T5, T6> extends Tuple {
        private Tuple6(final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5,
                       final T6 param6) {
            super(param1, param2, param3, param4, param5, param6);
        }

        @SuppressWarnings("unchecked")
        public <T> T map(final FN6<T, T1, T2, T3, T4, T5, T6> mapper) {
            return mapper.apply((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4], (T6) values[5]);
        }

        @SuppressWarnings("unchecked")
        public <T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> append(final T7 value) {
            return new Tuple7<>((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4], (T6) values[5], value);
        }

        @SuppressWarnings("unchecked")
        public <R1, R2, R3, R4, R5, R6> Tuple6<R1, R2, R3, R4, R5, R6> map(
                final Tuple6<FN1<R1, ? super T1>, FN1<R2, ? super T2>, FN1<R3, ? super T3>, FN1<R4, ? super T4>,
                        FN1<R5, ? super T5>, FN1<R6, ? super T6>> mapper) {
            return of(((FN1<R1, ? super T1>) mapper.values[0]).apply((T1) values[0]),
                      ((FN1<R2, ? super T2>) mapper.values[1]).apply((T2) values[1]),
                      ((FN1<R3, ? super T3>) mapper.values[2]).apply((T3) values[2]),
                      ((FN1<R4, ? super T4>) mapper.values[3]).apply((T4) values[3]),
                      ((FN1<R5, ? super T5>) mapper.values[4]).apply((T5) values[4]),
                      ((FN1<R6, ? super T6>) mapper.values[5]).apply((T6) values[5]));
        }
    }

    /**
     * Tuple with 7 values
     */
    public static class Tuple7<T1, T2, T3, T4, T5, T6, T7> extends Tuple {
        private Tuple7(final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5,
                       final T6 param6, final T7 param7) {
            super(param1, param2, param3, param4, param5, param6, param7);
        }

        @SuppressWarnings("unchecked")
        public <T> T map(final FN7<T, T1, T2, T3, T4, T5, T6, T7> mapper) {
            return mapper.apply((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4], (T6) values[5], (T7) values[6]);
        }

        @SuppressWarnings("unchecked")
        public <T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> append(final T8 value) {
            return new Tuple8<>((T1) values[0],
                                (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4], (T6) values[5], (T7) values[6], value);
        }

        @SuppressWarnings("unchecked")
        public <R1, R2, R3, R4, R5, R6, R7> Tuple7<R1, R2, R3, R4, R5, R6, R7> map(
                final Tuple7<FN1<R1, ? super T1>, FN1<R2, ? super T2>, FN1<R3, ? super T3>, FN1<R4, ? super T4>,
                        FN1<R5, ? super T5>, FN1<R6, ? super T6>, FN1<R7, ? super T7>> mapper) {
            return of(((FN1<R1, ? super T1>) mapper.values[0]).apply((T1) values[0]),
                      ((FN1<R2, ? super T2>) mapper.values[1]).apply((T2) values[1]),
                      ((FN1<R3, ? super T3>) mapper.values[2]).apply((T3) values[2]),
                      ((FN1<R4, ? super T4>) mapper.values[3]).apply((T4) values[3]),
                      ((FN1<R5, ? super T5>) mapper.values[4]).apply((T5) values[4]),
                      ((FN1<R6, ? super T6>) mapper.values[5]).apply((T6) values[5]),
                      ((FN1<R7, ? super T7>) mapper.values[6]).apply((T7) values[6]));
        }
    }

    /**
     * Tuple with 8 values
     */
    public static class Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> extends Tuple {
        private Tuple8(final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5,
                       final T6 param6, final T7 param7, final T8 param8) {
            super(param1, param2, param3, param4, param5, param6, param7, param8);
        }

        @SuppressWarnings("unchecked")
        public <T> T map(final FN8<T, T1, T2, T3, T4, T5, T6, T7, T8> mapper) {
            return mapper.apply((T1) values[0], (T2) values[1],
                                (T3) values[2], (T4) values[3], (T5) values[4], (T6) values[5], (T7) values[6], (T8) values[7]);
        }

        @SuppressWarnings("unchecked")
        public <T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> append(final T9 value) {
            return new Tuple9<>((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3],
                                (T5) values[4], (T6) values[5], (T7) values[6], (T8) values[7], value);
        }

        @SuppressWarnings("unchecked")
        public <R1, R2, R3, R4, R5, R6, R7, R8> Tuple8<R1, R2, R3, R4, R5, R6, R7, R8> map(
                final Tuple8<FN1<R1, ? super T1>, FN1<R2, ? super T2>, FN1<R3, ? super T3>, FN1<R4, ? super T4>,
                        FN1<R5, ? super T5>, FN1<R6, ? super T6>, FN1<R7, ? super T7>, FN1<R8, ? super T8>> mapper) {
            return of(((FN1<R1, ? super T1>) mapper.values[0]).apply((T1) values[0]),
                      ((FN1<R2, ? super T2>) mapper.values[1]).apply((T2) values[1]),
                      ((FN1<R3, ? super T3>) mapper.values[2]).apply((T3) values[2]),
                      ((FN1<R4, ? super T4>) mapper.values[3]).apply((T4) values[3]),
                      ((FN1<R5, ? super T5>) mapper.values[4]).apply((T5) values[4]),
                      ((FN1<R6, ? super T6>) mapper.values[5]).apply((T6) values[5]),
                      ((FN1<R7, ? super T7>) mapper.values[6]).apply((T7) values[6]),
                      ((FN1<R8, ? super T8>) mapper.values[7]).apply((T8) values[7]));
        }
    }

    /**
     * Tuple with 9 values
     */
    public static class Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends Tuple {
        private Tuple9(final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5,
                       final T6 param6, final T7 param7, final T8 param8, final T9 param9) {
            super(param1, param2, param3, param4, param5, param6, param7, param8, param9);
        }

        @SuppressWarnings("unchecked")
        public <T> T map(final FN9<T, T1, T2, T3, T4, T5, T6, T7, T8, T9> mapper) {
            return mapper.apply((T1) values[0], (T2) values[1], (T3) values[2], (T4) values[3], (T5) values[4],
                                (T6) values[5], (T7) values[6], (T8) values[7], (T9) values[8]);
        }

        @SuppressWarnings("unchecked")
        public <R1, R2, R3, R4, R5, R6, R7, R8, R9> Tuple9<R1, R2, R3, R4, R5, R6, R7, R8, R9> map(
                final Tuple9<FN1<R1, ? super T1>, FN1<R2, ? super T2>, FN1<R3, ? super T3>, FN1<R4, ? super T4>,
                        FN1<R5, ? super T5>, FN1<R6, ? super T6>, FN1<R7, ? super T7>, FN1<R8, ? super T8>,
                        FN1<R9, ? super T9>> mapper) {
            return of(((FN1<R1, ? super T1>) mapper.values[0]).apply((T1) values[0]),
                      ((FN1<R2, ? super T2>) mapper.values[1]).apply((T2) values[1]),
                      ((FN1<R3, ? super T3>) mapper.values[2]).apply((T3) values[2]),
                      ((FN1<R4, ? super T4>) mapper.values[3]).apply((T4) values[3]),
                      ((FN1<R5, ? super T5>) mapper.values[4]).apply((T5) values[4]),
                      ((FN1<R6, ? super T6>) mapper.values[5]).apply((T6) values[5]),
                      ((FN1<R7, ? super T7>) mapper.values[6]).apply((T7) values[6]),
                      ((FN1<R8, ? super T8>) mapper.values[7]).apply((T8) values[7]),
                      ((FN1<R9, ? super T9>) mapper.values[8]).apply((T9) values[8]));
        }
    }
}

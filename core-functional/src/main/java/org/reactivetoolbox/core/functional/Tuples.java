package org.reactivetoolbox.core.functional;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Functions.FN0;

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
import java.util.function.Consumer;

/**
 * Tuple classes with various size and convenient static factories for tuples. All tuple classes are immutable. <br/>
 * The Tuple is a container for variables. Tuples with size 1 to 9 are provided. Each variable may have different type
 * and all type information is preserved.
 */
//TODO: rename 'flatMap' -> 'to'
public final class Tuples {
    private Tuples() {
    }

    /**
     * Factory method for tuple with single get.
     *
     * @param param1
     *        Value to store in tuple.
     * @return created tuple newInstance.
     */
    public static Tuple0 of() {
        return new Tuple0();
    }

    /**
     * Factory method for tuple with single get.
     *
     * @param param1
     *        Value to store in tuple.
     * @return created tuple newInstance.
     */
    public static <T1> Tuple1<T1> of(final T1 param1) {
        return new Tuple1<>(param1);
    }

    /**
     * Factory method for tuple with two values with different type.
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
     * Factory method for tuple with three values with different type.
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
     * Factory method for tuple with four values with different type.
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
     * Factory method for tuple with five values with different type.
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
     * Factory method for tuple with six values with different type.
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
     * Factory method for tuple with seven values with different type.
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
     * Factory method for tuple with eight values with different type.
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
     * Factory method for tuple with nine values with different type.
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

    public static <T1> Either<? extends BaseError, Tuple1<T1>> squeeze(final Either<? extends BaseError, T1> value) {
        return value.flatMap(vv1 -> Either.success(of(vv1)));
    }

    public static <T1, T2> Either<? extends BaseError, Tuple2<T1, T2>> squeeze(final Either<? extends BaseError, T1> value1,
                                                                               final Either<? extends BaseError, T2> value2) {
        return value1.flatMap(vv1 ->
                value2.flatMap(vv2 -> Either.success(of(vv1, vv2))));
    }

    public static <T1, T2, T3>  Either<? extends BaseError, Tuple3<T1, T2, T3>> squeeze(final Either<? extends BaseError, T1> value1,
                                                                                        final Either<? extends BaseError, T2> value2,
                                                                                        final Either<? extends BaseError, T3> value3) {
        return value1.flatMap(vv1 ->
                value2.flatMap(vv2 ->
                 value3.flatMap(vv3 -> Either.success(of(vv1, vv2, vv3)))));
    }

    public static <T1, T2, T3, T4>  Either<? extends BaseError, Tuple4<T1, T2, T3, T4>> squeeze(final Either<? extends BaseError, T1> value1,
                                                                                                final Either<? extends BaseError, T2> value2,
                                                                                                final Either<? extends BaseError, T3> value3,
                                                                                                final Either<? extends BaseError, T4> value4) {
        return value1.flatMap(vv1 ->
                value2.flatMap(vv2 ->
                 value3.flatMap(vv3 ->
                  value4.flatMap(vv4 -> Either.success(of(vv1, vv2, vv3, vv4))))));
    }

    public static <T1, T2, T3, T4, T5>  Either<? extends BaseError, Tuple5<T1, T2, T3, T4, T5>> squeeze(final Either<? extends BaseError, T1> value1,
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

    public static <T1, T2, T3, T4, T5, T6>  Either<? extends BaseError, Tuple6<T1, T2, T3, T4, T5, T6>> squeeze(final Either<? extends BaseError, T1> value1,
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

    public static <T1, T2, T3, T4, T5, T6, T7>  Either<? extends BaseError, Tuple7<T1, T2, T3, T4, T5, T6, T7>> squeeze(final Either<? extends BaseError, T1> value1,
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

    public static <T1, T2, T3, T4, T5, T6, T7, T8>  Either<? extends BaseError, Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> squeeze(final Either<? extends BaseError, T1> value1,
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

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9>  Either<? extends BaseError, Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> squeeze(final Either<? extends BaseError, T1> value1,
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
        private final Object[] values;

        protected Tuple(final Object... values) {
            this.values = values;
        }

        protected Object get(final int i) {
            return values[i];
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
         * Submit all values contained in tuple to provided consumer.
         * <p>
         * <b>WARNING: this method is inherently type-unsafe, because all information about types is lost. Use with
         * extreme care!!!</b>
         *
         * @param consumer
         *        The {@link Consumer} to which stored values will be passed.
         */
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void forEach(final Consumer consumer) {
            for (final Object value : values) {
                consumer.accept(value);
            }
        }
    }

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

    public static class Tuple1<T1> extends Tuple {
        private Tuple1(final T1 param1) {
            super(param1);
        }

        @SuppressWarnings("unchecked")
        public T1 get1() {
            return (T1) get(0);
        }

        public <T> T map(final FN1<T, T1> mapper) {
            return mapper.apply(get1());
        }

        @SuppressWarnings("unchecked")
        //TODO: convert it to zip?
        protected <TT1> Tuple1<TT1> compose(Object... values) {
            return new Tuple1<>((TT1) values[0]);
        }

        public <T2> Tuple2<T1, T2> append(final T2 value) {
            return new Tuple2<>(get1(), value);
        }
    }

    public static class Tuple2<T1, T2> extends Tuple {
        private Tuple2(final T1 param1, final T2 param2) {
            super(param1, param2);
        }

        @SuppressWarnings("unchecked")
        public T1 get1() {
            return (T1) get(0);
        }

        @SuppressWarnings("unchecked")
        public T2 get2() {
            return (T2) get(1);
        }

        public <T> T map(final FN2<T, T1, T2> mapper) {
            return mapper.apply(get1(), get2());
        }

        public <T3> Tuple3<T1, T2, T3> append(final T3 value) {
            return new Tuple3<>(get1(), get2(), value);
        }
    }

    public static class Tuple3<T1, T2, T3> extends Tuple {
        private Tuple3(final T1 param1, final T2 param2, final T3 param3) {
            super(param1, param2, param3);
        }

        @SuppressWarnings("unchecked")
        public T1 get1() {
            return (T1) get(0);
        }

        @SuppressWarnings("unchecked")
        public T2 get2() {
            return (T2) get(1);
        }

        @SuppressWarnings("unchecked")
        public T3 get3() {
            return (T3) get(2);
        }

        public <T> T map(final FN3<T, T1, T2, T3> mapper) {
            return mapper.apply(get1(), get2(), get3());
        }

        public <T4> Tuple4<T1, T2, T3, T4> append(final T4 value) {
            return new Tuple4<>(get1(), get2(), get3(), value);
        }
    }

    public static class Tuple4<T1, T2, T3, T4> extends Tuple {
        private Tuple4(final T1 param1, final T2 param2, final T3 param3, final T4 param4) {
            super(param1, param2, param3, param4);
        }

        @SuppressWarnings("unchecked")
        public T1 get1() {
            return (T1) get(0);
        }

        @SuppressWarnings("unchecked")
        public T2 get2() {
            return (T2) get(1);
        }

        @SuppressWarnings("unchecked")
        public T3 get3() {
            return (T3) get(2);
        }

        @SuppressWarnings("unchecked")
        public T4 get4() {
            return (T4) get(3);
        }

        public <T> T map(final FN4<T, T1, T2, T3, T4> mapper) {
            return mapper.apply(get1(), get2(), get3(), get4());
        }

        public <T5> Tuple5<T1, T2, T3, T4, T5> append(final T5 value) {
            return new Tuple5<>(get1(), get2(), get3(), get4(), value);
        }
    }

    public static class Tuple5<T1, T2, T3, T4, T5> extends Tuple {
        private Tuple5(final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5) {
            super(param1, param2, param3, param4, param5);
        }

        @SuppressWarnings("unchecked")
        public T1 get1() {
            return (T1) get(0);
        }

        @SuppressWarnings("unchecked")
        public T2 get2() {
            return (T2) get(1);
        }

        @SuppressWarnings("unchecked")
        public T3 get3() {
            return (T3) get(2);
        }

        @SuppressWarnings("unchecked")
        public T4 get4() {
            return (T4) get(3);
        }

        @SuppressWarnings("unchecked")
        public T5 get5() {
            return (T5) get(4);
        }

        public <T> T map(final FN5<T, T1, T2, T3, T4, T5> mapper) {
            return mapper.apply(get1(), get2(), get3(), get4(), get5());
        }

        public <T6> Tuple6<T1, T2, T3, T4, T5, T6> append(final T6 value) {
            return new Tuple6<>(get1(), get2(), get3(), get4(), get5(), value);
        }
    }

    public static class Tuple6<T1, T2, T3, T4, T5, T6> extends Tuple {
        private Tuple6(final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5,
                       final T6 param6) {
            super(param1, param2, param3, param4, param5, param6);
        }

        @SuppressWarnings("unchecked")
        public T1 get1() {
            return (T1) get(0);
        }

        @SuppressWarnings("unchecked")
        public T2 get2() {
            return (T2) get(1);
        }

        @SuppressWarnings("unchecked")
        public T3 get3() {
            return (T3) get(2);
        }

        @SuppressWarnings("unchecked")
        public T4 get4() {
            return (T4) get(3);
        }

        @SuppressWarnings("unchecked")
        public T5 get5() {
            return (T5) get(4);
        }

        @SuppressWarnings("unchecked")
        public T6 get6() {
            return (T6) get(5);
        }

        public <T> T map(final FN6<T, T1, T2, T3, T4, T5, T6> mapper) {
            return mapper.apply(get1(), get2(), get3(), get4(), get5(), get6());
        }

        public <T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> append(final T7 value) {
            return new Tuple7<>(get1(), get2(), get3(), get4(), get5(), get6(), value);
        }
    }

    public static class Tuple7<T1, T2, T3, T4, T5, T6, T7> extends Tuple {
        private Tuple7(final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5,
                       final T6 param6, final T7 param7) {
            super(param1, param2, param3, param4, param5, param6, param7);
        }

        @SuppressWarnings("unchecked")
        public T1 get1() {
            return (T1) get(0);
        }

        @SuppressWarnings("unchecked")
        public T2 get2() {
            return (T2) get(1);
        }

        @SuppressWarnings("unchecked")
        public T3 get3() {
            return (T3) get(2);
        }

        @SuppressWarnings("unchecked")
        public T4 get4() {
            return (T4) get(3);
        }

        @SuppressWarnings("unchecked")
        public T5 get5() {
            return (T5) get(4);
        }

        @SuppressWarnings("unchecked")
        public T6 get6() {
            return (T6) get(5);
        }

        @SuppressWarnings("unchecked")
        public T7 get7() {
            return (T7) get(6);
        }

        public <T> T map(final FN7<T, T1, T2, T3, T4, T5, T6, T7> mapper) {
            return mapper.apply(get1(), get2(), get3(), get4(), get5(), get6(), get7());
        }

        public <T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> append(final T8 value) {
            return new Tuple8<>(get1(), get2(), get3(), get4(), get5(), get6(), get7(), value);
        }
    }

    public static class Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> extends Tuple {
        private Tuple8(final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5,
                       final T6 param6, final T7 param7, final T8 param8) {
            super(param1, param2, param3, param4, param5, param6, param7, param8);
        }

        @SuppressWarnings("unchecked")
        public T1 get1() {
            return (T1) get(0);
        }

        @SuppressWarnings("unchecked")
        public T2 get2() {
            return (T2) get(1);
        }

        @SuppressWarnings("unchecked")
        public T3 get3() {
            return (T3) get(2);
        }

        @SuppressWarnings("unchecked")
        public T4 get4() {
            return (T4) get(3);
        }

        @SuppressWarnings("unchecked")
        public T5 get5() {
            return (T5) get(4);
        }

        @SuppressWarnings("unchecked")
        public T6 get6() {
            return (T6) get(5);
        }

        @SuppressWarnings("unchecked")
        public T7 get7() {
            return (T7) get(6);
        }

        @SuppressWarnings("unchecked")
        public T8 get8() {
            return (T8) get(7);
        }

        public <T> T map(final FN8<T, T1, T2, T3, T4, T5, T6, T7, T8> mapper) {
            return mapper.apply(get1(), get2(), get3(), get4(), get5(), get6(), get7(), get8());
        }

        public <T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> append(final T9 value) {
            return new Tuple9<>(get1(), get2(), get3(), get4(), get5(), get6(), get7(), get8(), value);
        }
    }

    public static class Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends Tuple {
        private Tuple9(final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5,
                       final T6 param6, final T7 param7, final T8 param8, final T9 param9) {
            super(param1, param2, param3, param4, param5, param6, param7, param8, param9);
        }

        @SuppressWarnings("unchecked")
        public T1 get1() {
            return (T1) get(0);
        }

        @SuppressWarnings("unchecked")
        public T2 get2() {
            return (T2) get(1);
        }

        @SuppressWarnings("unchecked")
        public T3 get3() {
            return (T3) get(2);
        }

        @SuppressWarnings("unchecked")
        public T4 get4() {
            return (T4) get(3);
        }

        @SuppressWarnings("unchecked")
        public T5 get5() {
            return (T5) get(4);
        }

        @SuppressWarnings("unchecked")
        public T6 get6() {
            return (T6) get(5);
        }

        @SuppressWarnings("unchecked")
        public T7 get7() {
            return (T7) get(6);
        }

        @SuppressWarnings("unchecked")
        public T8 get8() {
            return (T8) get(7);
        }

        @SuppressWarnings("unchecked")
        public T9 get9() {
            return (T9) get(8);
        }

        public <T> T map(final FN9<T, T1, T2, T3, T4, T5, T6, T7, T8, T9> mapper) {
            return mapper.apply(get1(), get2(), get3(), get4(), get5(), get6(), get7(), get8(), get9());
        }
    }
}

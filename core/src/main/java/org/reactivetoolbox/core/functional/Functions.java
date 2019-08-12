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

import org.reactivetoolbox.core.functional.Tuples.Tuple0;
import org.reactivetoolbox.core.functional.Tuples.Tuple1;
import org.reactivetoolbox.core.functional.Tuples.Tuple2;
import org.reactivetoolbox.core.functional.Tuples.Tuple3;
import org.reactivetoolbox.core.functional.Tuples.Tuple4;
import org.reactivetoolbox.core.functional.Tuples.Tuple5;
import org.reactivetoolbox.core.functional.Tuples.Tuple6;
import org.reactivetoolbox.core.functional.Tuples.Tuple7;
import org.reactivetoolbox.core.functional.Tuples.Tuple8;
import org.reactivetoolbox.core.functional.Tuples.Tuple9;

/**
 * Collection of basic functions which accept 0-9 parameters and return single result.
 * Note that these functions are not supposed to throw any exceptions
 */
public interface Functions {
    @FunctionalInterface
    interface FN0<R> {
        R apply();

        default R forTuple(final Tuple0 input) {
            return input.map(this);
        }
    }

    @FunctionalInterface
    interface FN1<R, T1> {
        R apply(T1 param1);

        default R forTuple(final Tuple1<T1> input) {
            return input.map(this);
        }

        default FN0<R> bind(final T1 param) {
            return () -> apply(param);
        }
    }

    @FunctionalInterface
    interface FN2<R, T1, T2> {
        R apply(T1 param1, T2 param2);

        default R forTuple(final Tuple2<T1,T2> input) {
            return input.map(this);
        }

        default FN1<R, T2> bind(final T1 param) {
            return (v2) -> apply(param, v2);
        }
    }

    @FunctionalInterface
    interface FN3<R, T1, T2, T3> {
        R apply(T1 param1, T2 param2, T3 param3);

        default R forTuple(final Tuple3<T1, T2, T3> input) {
            return input.map(this);
        }

        default FN2<R, T2, T3> bind(final T1 param) {
            return (v2, v3) -> apply(param, v2, v3);
        }
    }

    @FunctionalInterface
    interface FN4<R, T1, T2, T3, T4> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4);

        default R forTuple(final Tuple4<T1, T2, T3, T4> input) {
            return input.map(this);
        }

        default FN3<R, T2, T3, T4> bind(final T1 param) {
            return (v2, v3, v4) -> apply(param, v2, v3, v4);
        }
    }

    @FunctionalInterface
    interface FN5<R, T1, T2, T3, T4, T5> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4, T5 param5);

        default R forTuple(final Tuple5<T1, T2, T3, T4, T5> input) {
            return input.map(this);
        }

        default FN4<R, T2, T3, T4, T5> bind(final T1 param) {
            return (v2, v3, v4, v5) -> apply(param, v2, v3, v4, v5);
        }
    }

    @FunctionalInterface
    interface FN6<R, T1, T2, T3, T4, T5, T6> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4, T5 param5, T6 param6);

        default R forTuple(final Tuple6<T1, T2, T3, T4, T5, T6> input) {
            return input.map(this);
        }

        default FN5<R, T2, T3, T4, T5, T6> bind(final T1 param) {
            return (v2, v3, v4, v5, v6) -> apply(param, v2, v3, v4, v5, v6);
        }
    }

    @FunctionalInterface
    interface FN7<R, T1, T2, T3, T4, T5, T6, T7> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4, T5 param5, T6 param6, T7 param7);

        default R forTuple(final Tuple7<T1, T2, T3, T4, T5, T6, T7> input) {
            return input.map(this);
        }

        default FN6<R, T2, T3, T4, T5, T6, T7> bind(final T1 param) {
            return (v2, v3, v4, v5, v6, v7) -> apply(param, v2, v3, v4, v5, v6, v7);
        }
    }

    @FunctionalInterface
    interface FN8<R, T1, T2, T3, T4, T5, T6, T7, T8> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4, T5 param5, T6 param6, T7 param7, T8 param8);

        default R forTuple(final Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> input) {
            return input.map(this);
        }

        default FN7<R, T2, T3, T4, T5, T6, T7, T8> bind(final T1 param) {
            return (v2, v3, v4, v5, v6, v7, v8) -> apply(param, v2, v3, v4, v5, v6, v7, v8);
        }
    }

    @FunctionalInterface
    interface FN9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4, T5 param5, T6 param6, T7 param7, T8 param8, T9 param9);

        default R forTuple(final Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> input) {
            return input.map(this);
        }

        default FN8<R, T2, T3, T4, T5, T6, T7, T8, T9> bind(final T1 param) {
            return (v2, v3, v4, v5, v6, v7, v8, v9) -> apply(param, v2, v3, v4, v5, v6, v7, v8, v9);
        }
    }
}

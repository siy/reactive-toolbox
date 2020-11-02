/*
 * Copyright (c) 2020 Sergiy Yevtushenko
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

package org.reactivetoolbox.io.async;

import org.reactivetoolbox.core.lang.Tuple.Tuple1;
import org.reactivetoolbox.core.lang.Tuple.Tuple2;
import org.reactivetoolbox.core.lang.Tuple.Tuple3;
import org.reactivetoolbox.core.lang.Tuple.Tuple4;
import org.reactivetoolbox.core.lang.Tuple.Tuple5;
import org.reactivetoolbox.core.lang.Tuple.Tuple6;
import org.reactivetoolbox.core.lang.Tuple.Tuple7;
import org.reactivetoolbox.core.lang.Tuple.Tuple8;
import org.reactivetoolbox.core.lang.Tuple.Tuple9;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Functions.FN2;
import org.reactivetoolbox.core.lang.functional.Functions.FN3;
import org.reactivetoolbox.core.lang.functional.Functions.FN4;
import org.reactivetoolbox.core.lang.functional.Functions.FN5;
import org.reactivetoolbox.core.lang.functional.Functions.FN6;
import org.reactivetoolbox.core.lang.functional.Functions.FN7;
import org.reactivetoolbox.core.lang.functional.Functions.FN8;
import org.reactivetoolbox.core.lang.functional.Functions.FN9;

public interface PromiseMappers {

    interface Mapper1<T1> {
        Promise<Tuple1<T1>> id();

        default <R> Promise<R> map(final FN1<R, T1> mapper) {
            return id().map(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncMap(final FN1<R, T1> mapper, final Submitter submitter) {
            return id().syncMap(tuple -> tuple.map(mapper), submitter);
        }

        default <R> Promise<R> flatMap(final FN1<Promise<R>, T1> mapper) {
            return id().flatMap(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncFlatMap(final FN1<Promise<R>, T1> mapper, final Submitter submitter) {
            return id().syncFlatMap(tuple -> tuple.map(mapper), submitter);
        }
    }

    interface Mapper2<T1, T2> {
        Promise<Tuple2<T1, T2>> id();

        default <R> Promise<R> map(final FN2<R, T1, T2> mapper) {
            return id().map(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncMap(final FN2<R, T1, T2> mapper, final Submitter submitter) {
            return id().syncMap(tuple -> tuple.map(mapper), submitter);
        }

        default <R> Promise<R> flatMap(final FN2<Promise<R>, T1, T2> mapper) {
            return id().flatMap(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncFlatMap(final FN2<Promise<R>, T1, T2> mapper, final Submitter submitter) {
            return id().syncFlatMap(tuple -> tuple.map(mapper), submitter);
        }
    }

    interface Mapper3<T1, T2, T3> {
        Promise<Tuple3<T1, T2, T3>> id();

        default <R> Promise<R> map(final FN3<R, T1, T2, T3> mapper) {
            return id().map(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncMap(final FN3<R, T1, T2, T3> mapper, final Submitter submitter) {
            return id().syncMap(tuple -> tuple.map(mapper), submitter);
        }

        default <R> Promise<R> flatMap(final FN3<Promise<R>, T1, T2, T3> mapper) {
            return id().flatMap(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncFlatMap(final FN3<Promise<R>, T1, T2, T3> mapper, final Submitter submitter) {
            return id().syncFlatMap(tuple -> tuple.map(mapper), submitter);
        }
    }

    interface Mapper4<T1, T2, T3, T4> {
        Promise<Tuple4<T1, T2, T3, T4>> id();

        default <R> Promise<R> map(final FN4<R, T1, T2, T3, T4> mapper) {
            return id().map(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncMap(final FN4<R, T1, T2, T3, T4> mapper, final Submitter submitter) {
            return id().syncMap(tuple -> tuple.map(mapper), submitter);
        }

        default <R> Promise<R> flatMap(final FN4<Promise<R>, T1, T2, T3, T4> mapper) {
            return id().flatMap(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncFlatMap(final FN4<Promise<R>, T1, T2, T3, T4> mapper, final Submitter submitter) {
            return id().syncFlatMap(tuple -> tuple.map(mapper), submitter);
        }
    }

    interface Mapper5<T1, T2, T3, T4, T5> {
        Promise<Tuple5<T1, T2, T3, T4, T5>> id();

        default <R> Promise<R> map(final FN5<R, T1, T2, T3, T4, T5> mapper) {
            return id().map(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncMap(final FN5<R, T1, T2, T3, T4, T5> mapper, final Submitter submitter) {
            return id().syncMap(tuple -> tuple.map(mapper), submitter);
        }

        default <R> Promise<R> flatMap(final FN5<Promise<R>, T1, T2, T3, T4, T5> mapper) {
            return id().flatMap(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncFlatMap(final FN5<Promise<R>, T1, T2, T3, T4, T5> mapper, final Submitter submitter) {
            return id().syncFlatMap(tuple -> tuple.map(mapper), submitter);
        }
    }

    interface Mapper6<T1, T2, T3, T4, T5, T6> {
        Promise<Tuple6<T1, T2, T3, T4, T5, T6>> id();

        default <R> Promise<R> map(final FN6<R, T1, T2, T3, T4, T5, T6> mapper) {
            return id().map(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncMap(final FN6<R, T1, T2, T3, T4, T5, T6> mapper, final Submitter submitter) {
            return id().syncMap(tuple -> tuple.map(mapper), submitter);
        }

        default <R> Promise<R> flatMap(final FN6<Promise<R>, T1, T2, T3, T4, T5, T6> mapper) {
            return id().flatMap(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncFlatMap(final FN6<Promise<R>, T1, T2, T3, T4, T5, T6> mapper, final Submitter submitter) {
            return id().syncFlatMap(tuple -> tuple.map(mapper), submitter);
        }
    }

    interface Mapper7<T1, T2, T3, T4, T5, T6, T7> {
        Promise<Tuple7<T1, T2, T3, T4, T5, T6, T7>> id();

        default <R> Promise<R> map(final FN7<R, T1, T2, T3, T4, T5, T6, T7> mapper) {
            return id().map(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncMap(final FN7<R, T1, T2, T3, T4, T5, T6, T7> mapper, final Submitter submitter) {
            return id().syncMap(tuple -> tuple.map(mapper), submitter);
        }

        default <R> Promise<R> flatMap(final FN7<Promise<R>, T1, T2, T3, T4, T5, T6, T7> mapper) {
            return id().flatMap(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncFlatMap(final FN7<Promise<R>, T1, T2, T3, T4, T5, T6, T7> mapper, final Submitter submitter) {
            return id().syncFlatMap(tuple -> tuple.map(mapper), submitter);
        }
    }

    interface Mapper8<T1, T2, T3, T4, T5, T6, T7, T8> {
        Promise<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> id();

        default <R> Promise<R> map(final FN8<R, T1, T2, T3, T4, T5, T6, T7, T8> mapper) {
            return id().map(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncMap(final FN8<R, T1, T2, T3, T4, T5, T6, T7, T8> mapper, final Submitter submitter) {
            return id().syncMap(tuple -> tuple.map(mapper), submitter);
        }

        default <R> Promise<R> flatMap(final FN8<Promise<R>, T1, T2, T3, T4, T5, T6, T7, T8> mapper) {
            return id().flatMap(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncFlatMap(final FN8<Promise<R>, T1, T2, T3, T4, T5, T6, T7, T8> mapper, final Submitter submitter) {
            return id().syncFlatMap(tuple -> tuple.map(mapper), submitter);
        }
    }

    interface Mapper9<T1, T2, T3, T4, T5, T6, T7, T8, T9> {
        Promise<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> id();

        default <R> Promise<R> map(final FN9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> mapper) {
            return id().map(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncMap(final FN9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> mapper, final Submitter submitter) {
            return id().syncMap(tuple -> tuple.map(mapper), submitter);
        }

        default <R> Promise<R> flatMap(final FN9<Promise<R>, T1, T2, T3, T4, T5, T6, T7, T8, T9> mapper) {
            return id().flatMap(tuple -> tuple.map(mapper));
        }

        default <R> Promise<R> syncFlatMap(final FN9<Promise<R>, T1, T2, T3, T4, T5, T6, T7, T8, T9> mapper, final Submitter submitter) {
            return id().syncFlatMap(tuple -> tuple.map(mapper), submitter);
        }
    }
}

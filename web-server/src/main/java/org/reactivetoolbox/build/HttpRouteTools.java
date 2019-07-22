package org.reactivetoolbox.build;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;

import static org.reactivetoolbox.core.async.Promises.fulfilled;
import static org.reactivetoolbox.core.functional.Either.failure;
import static org.reactivetoolbox.core.functional.Either.success;
import static org.reactivetoolbox.core.functional.Tuples.Tuple1;
import static org.reactivetoolbox.core.functional.Tuples.Tuple2;
import static org.reactivetoolbox.core.functional.Tuples.Tuple3;
import static org.reactivetoolbox.core.functional.Tuples.Tuple4;
import static org.reactivetoolbox.core.functional.Tuples.Tuple5;
import static org.reactivetoolbox.core.functional.Tuples.Tuple6;
import static org.reactivetoolbox.core.functional.Tuples.Tuple7;
import static org.reactivetoolbox.core.functional.Tuples.Tuple8;
import static org.reactivetoolbox.core.functional.Tuples.Tuple9;
import static org.reactivetoolbox.core.functional.Tuples.of;

public class HttpRouteTools {

    public static <T> Either<? extends BaseError, T> ok(final T value) {
        return success(value);
    }

    public static <T> Promise<Either<? extends BaseError, T>> readyOk(final T value) {
        return fulfilled(ok(value));
    }

    public static <T> Promise<Either<? extends BaseError, T>> readyFail(final BaseError failure) {
        return fulfilled(failure(failure));
    }

    public static <T1> Either<? extends BaseError, Tuple1<T1>> valid(final T1 v1) {
        return success(of(v1));
    }

    public static <T1, T2> Either<? extends BaseError, Tuple2<T1, T2>> valid(final T1 v1,
                                                                             final T2 v2) {
        return success(of(v1, v2));
    }

    public static <T1, T2, T3> Either<? extends BaseError, Tuple3<T1, T2, T3>> valid(final T1 v1,
                                                                                    final T2 v2,
                                                                                    final T3 v3) {
        return success(of(v1, v2, v3));
    }

    public static <T1, T2, T3, T4> Either<? extends BaseError, Tuple4<T1, T2, T3, T4>> valid(final T1 v1,
                                                                                             final T2 v2,
                                                                                             final T3 v3,
                                                                                             final T4 v4) {
        return success(of(v1, v2, v3, v4));
    }

    public static <T1, T2, T3, T4, T5> Either<? extends BaseError, Tuple5<T1, T2, T3, T4, T5>> valid(final T1 v1,
                                                                                                     final T2 v2,
                                                                                                     final T3 v3,
                                                                                                     final T4 v4,
                                                                                                     final T5 v5) {
        return success(of(v1, v2, v3, v4, v5));
    }

    public static <T1, T2, T3, T4, T5, T6> Either<? extends BaseError, Tuple6<T1, T2, T3, T4, T5, T6>> valid(final T1 v1,
                                                                                                             final T2 v2,
                                                                                                             final T3 v3,
                                                                                                             final T4 v4,
                                                                                                             final T5 v5,
                                                                                                             final T6 v6) {
        return success(of(v1, v2, v3, v4, v5, v6));
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Either<? extends BaseError, Tuple7<T1, T2, T3, T4, T5, T6, T7>> valid(final T1 v1,
                                                                                                                     final T2 v2,
                                                                                                                     final T3 v3,
                                                                                                                     final T4 v4,
                                                                                                                     final T5 v5,
                                                                                                                     final T6 v6,
                                                                                                                     final T7 v7) {
        return success(of(v1, v2, v3, v4, v5, v6, v7));
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> Either<? extends BaseError, Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> valid(final T1 v1,
                                                                                                                             final T2 v2,
                                                                                                                             final T3 v3,
                                                                                                                             final T4 v4,
                                                                                                                             final T5 v5,
                                                                                                                             final T6 v6,
                                                                                                                             final T7 v7,
                                                                                                                             final T8 v8) {
        return success(of(v1, v2, v3, v4, v5, v6, v7, v8));
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Either<? extends BaseError, Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> valid(final T1 v1,
                                                                                                                                     final T2 v2,
                                                                                                                                     final T3 v3,
                                                                                                                                     final T4 v4,
                                                                                                                                     final T5 v5,
                                                                                                                                     final T6 v6,
                                                                                                                                     final T7 v7,
                                                                                                                                     final T8 v8,
                                                                                                                                     final T9 v9) {
        return success(of(v1, v2, v3, v4, v5, v6, v7, v8, v9));
    }
}

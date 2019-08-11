package org.reactivetoolbox.value.validation;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Tuples.Tuple1;
import org.reactivetoolbox.core.functional.Tuples.Tuple2;
import org.reactivetoolbox.core.functional.Tuples.Tuple3;
import org.reactivetoolbox.core.functional.Tuples.Tuple4;
import org.reactivetoolbox.core.functional.Tuples.Tuple5;
import org.reactivetoolbox.core.functional.Tuples.Tuple6;
import org.reactivetoolbox.core.functional.Tuples.Tuple7;
import org.reactivetoolbox.core.functional.Tuples.Tuple8;
import org.reactivetoolbox.core.functional.Tuples.Tuple9;
import org.reactivetoolbox.value.conversion.ProcessingContext;
import org.reactivetoolbox.value.conversion.var.Var;

import static org.reactivetoolbox.core.functional.Either.success;
import static org.reactivetoolbox.core.functional.Tuples.of;

@FunctionalInterface
public interface Validator<R, T> extends FN1<Either<? extends BaseError, R>, T> {
    static <T1> Either<? extends BaseError, Tuple1<T1>> valid(final T1 v1) {
        return success(of(v1));
    }

    static <T1, T2> Either<? extends BaseError, Tuple2<T1, T2>> valid(final T1 v1,
                                                                      final T2 v2) {
        return success(of(v1, v2));
    }

    static <T1, T2, T3> Either<? extends BaseError, Tuple3<T1, T2, T3>> valid(final T1 v1,
                                                                              final T2 v2,
                                                                              final T3 v3) {
        return success(of(v1, v2, v3));
    }

    static <T1, T2, T3, T4> Either<? extends BaseError, Tuple4<T1, T2, T3, T4>> valid(final T1 v1,
                                                                                      final T2 v2,
                                                                                      final T3 v3,
                                                                                      final T4 v4) {
        return success(of(v1, v2, v3, v4));
    }

    static <T1, T2, T3, T4, T5> Either<? extends BaseError, Tuple5<T1, T2, T3, T4, T5>> valid(final T1 v1,
                                                                                              final T2 v2,
                                                                                              final T3 v3,
                                                                                              final T4 v4,
                                                                                              final T5 v5) {
        return success(of(v1, v2, v3, v4, v5));
    }

    static <T1, T2, T3, T4, T5, T6> Either<? extends BaseError, Tuple6<T1, T2, T3, T4, T5, T6>> valid(final T1 v1,
                                                                                                      final T2 v2,
                                                                                                      final T3 v3,
                                                                                                      final T4 v4,
                                                                                                      final T5 v5,
                                                                                                      final T6 v6) {
        return success(of(v1, v2, v3, v4, v5, v6));
    }

    static <T1, T2, T3, T4, T5, T6, T7> Either<? extends BaseError, Tuple7<T1, T2, T3, T4, T5, T6, T7>> valid(
            final T1 v1,
            final T2 v2,
            final T3 v3,
            final T4 v4,
            final T5 v5,
            final T6 v6,
            final T7 v7) {
        return success(of(v1, v2, v3, v4, v5, v6, v7));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8> Either<? extends BaseError, Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> valid(
            final T1 v1,
            final T2 v2,
            final T3 v3,
            final T4 v4,
            final T5 v5,
            final T6 v6,
            final T7 v7,
            final T8 v8) {
        return success(of(v1, v2, v3, v4, v5, v6, v7, v8));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Either<? extends BaseError, Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> valid(
            final T1 v1,
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

    default Var<R> modify(final Var<T> input) {
        return Var.of((ProcessingContext context) -> input.converter()
                                                          .apply(context)
                                                          .flatMap(this::apply),
                      input.description());
    }
}

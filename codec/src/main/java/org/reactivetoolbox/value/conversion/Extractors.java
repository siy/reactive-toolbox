package org.reactivetoolbox.value.conversion;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Functions.FN2;
import org.reactivetoolbox.core.functional.Functions.FN3;
import org.reactivetoolbox.core.functional.Functions.FN4;
import org.reactivetoolbox.core.functional.Functions.FN5;
import org.reactivetoolbox.core.functional.Functions.FN6;
import org.reactivetoolbox.core.functional.Functions.FN7;
import org.reactivetoolbox.core.functional.Functions.FN8;
import org.reactivetoolbox.core.functional.Functions.FN9;
import org.reactivetoolbox.core.functional.Tuples.Tuple1;
import org.reactivetoolbox.core.functional.Tuples.Tuple2;
import org.reactivetoolbox.core.functional.Tuples.Tuple3;
import org.reactivetoolbox.core.functional.Tuples.Tuple4;
import org.reactivetoolbox.core.functional.Tuples.Tuple5;
import org.reactivetoolbox.core.functional.Tuples.Tuple6;
import org.reactivetoolbox.core.functional.Tuples.Tuple7;
import org.reactivetoolbox.core.functional.Tuples.Tuple8;
import org.reactivetoolbox.core.functional.Tuples.Tuple9;
import org.reactivetoolbox.value.conversion.var.Var;

import static org.reactivetoolbox.core.functional.Tuples.zip;

public interface Extractors {
    interface E1<T1> extends FN1<Either<? extends BaseError, Tuple1<T1>>, Var<T1>> { }
    interface E2<T1, T2> extends FN2<Either<? extends BaseError, Tuple2<T1, T2>>, Var<T1>, Var<T2>> { }
    interface E3<T1, T2, T3> extends FN3<Either<? extends BaseError, Tuple3<T1, T2, T3>>, Var<T1>, Var<T2>, Var<T3>> { }
    interface E4<T1, T2, T3, T4> extends FN4<Either<? extends BaseError, Tuple4<T1, T2, T3, T4>>, Var<T1>, Var<T2>, Var<T3>, Var<T4>> { }
    interface E5<T1, T2, T3, T4, T5> extends FN5<Either<? extends BaseError, Tuple5<T1, T2, T3, T4, T5>>, Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>> { }
    interface E6<T1, T2, T3, T4, T5, T6> extends FN6<Either<? extends BaseError, Tuple6<T1, T2, T3, T4, T5, T6>>, Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>, Var<T6>> { }
    interface E7<T1, T2, T3, T4, T5, T6, T7> extends FN7<Either<? extends BaseError, Tuple7<T1, T2, T3, T4, T5, T6, T7>>, Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>, Var<T6>, Var<T7>> { }
    interface E8<T1, T2, T3, T4, T5, T6, T7, T8> extends FN8<Either<? extends BaseError, Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>>, Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>, Var<T6>, Var<T7>, Var<T8>> { }
    interface E9<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends FN9<Either<? extends BaseError, Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>, Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>, Var<T6>, Var<T7>, Var<T8>, Var<T9>> { }

    static <T1> E1<T1> extract1(final ProcessingContext context) {
        return (v1) -> zip(v1.converter().apply(context));
    }

    static <T1, T2> E2<T1, T2> extract2(final ProcessingContext context) {
        return (v1, v2) -> zip(v1.converter().apply(context),
                               v2.converter().apply(context));
    }

    static <T1, T2, T3> E3<T1, T2, T3> extract3(final ProcessingContext context) {
        return (v1, v2, v3) -> zip(v1.converter().apply(context),
                                   v2.converter().apply(context),
                                   v3.converter().apply(context));
    }

    static <T1, T2, T3, T4> E4<T1, T2, T3, T4> extract4(final ProcessingContext context) {
        return (v1, v2, v3, v4) -> zip(v1.converter().apply(context),
                                       v2.converter().apply(context),
                                       v3.converter().apply(context),
                                       v4.converter().apply(context));
    }

    static <T1, T2, T3, T4, T5> E5<T1, T2, T3, T4, T5> extract5(final ProcessingContext context) {
        return (v1, v2, v3, v4, v5) -> zip(v1.converter().apply(context),
                                           v2.converter().apply(context),
                                           v3.converter().apply(context),
                                           v4.converter().apply(context),
                                           v5.converter().apply(context));
    }

    static <T1, T2, T3, T4, T5, T6> E6<T1, T2, T3, T4, T5, T6> extract6(final ProcessingContext context) {
        return (v1, v2, v3, v4, v5, v6) -> zip(v1.converter().apply(context),
                                               v2.converter().apply(context),
                                               v3.converter().apply(context),
                                               v4.converter().apply(context),
                                               v5.converter().apply(context),
                                               v6.converter().apply(context));
    }

    static <T1, T2, T3, T4, T5, T6, T7> E7<T1, T2, T3, T4, T5, T6, T7> extract7(final ProcessingContext context) {
        return (v1, v2, v3, v4, v5, v6, v7) -> zip(v1.converter().apply(context),
                                                   v2.converter().apply(context),
                                                   v3.converter().apply(context),
                                                   v4.converter().apply(context),
                                                   v5.converter().apply(context),
                                                   v6.converter().apply(context),
                                                   v7.converter().apply(context));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8> E8<T1, T2, T3, T4, T5, T6, T7, T8> extract8(final ProcessingContext context) {
        return (v1, v2, v3, v4, v5, v6, v7, v8) -> zip(v1.converter().apply(context),
                                                       v2.converter().apply(context),
                                                       v3.converter().apply(context),
                                                       v4.converter().apply(context),
                                                       v5.converter().apply(context),
                                                       v6.converter().apply(context),
                                                       v7.converter().apply(context),
                                                       v8.converter().apply(context));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, T9> E9<T1, T2, T3, T4, T5, T6, T7, T8, T9> extract9(final ProcessingContext context) {
        return (v1, v2, v3, v4, v5, v6, v7, v8, v9) -> zip(v1.converter().apply(context),
                                                           v2.converter().apply(context),
                                                           v3.converter().apply(context),
                                                           v4.converter().apply(context),
                                                           v5.converter().apply(context),
                                                           v6.converter().apply(context),
                                                           v7.converter().apply(context),
                                                           v8.converter().apply(context),
                                                           v9.converter().apply(context));
    }
}

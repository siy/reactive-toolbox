package org.reactivetoolbox.build;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN2;
import org.reactivetoolbox.core.functional.Functions.FN3;
import org.reactivetoolbox.core.functional.Functions.FN4;
import org.reactivetoolbox.core.functional.Functions.FN5;
import org.reactivetoolbox.core.functional.Functions.FN6;
import org.reactivetoolbox.core.functional.Functions.FN7;
import org.reactivetoolbox.core.functional.Functions.FN8;
import org.reactivetoolbox.core.functional.Functions.FN9;
import org.reactivetoolbox.core.functional.Tuples.Tuple2;
import org.reactivetoolbox.core.functional.Tuples.Tuple3;
import org.reactivetoolbox.core.functional.Tuples.Tuple4;
import org.reactivetoolbox.core.functional.Tuples.Tuple5;
import org.reactivetoolbox.core.functional.Tuples.Tuple6;
import org.reactivetoolbox.core.functional.Tuples.Tuple7;
import org.reactivetoolbox.core.functional.Tuples.Tuple8;
import org.reactivetoolbox.core.functional.Tuples.Tuple9;

/**
 * Interfaces for cross-parameter validators.
 */
public interface Validators {

    interface V2<T1, T2> extends FN2<Either<? extends BaseError, Tuple2<T1, T2>>, T1, T2> {
    }

    interface V3 <T1, T2, T3> extends FN3<Either<? extends BaseError, Tuple3<T1, T2, T3>>, T1, T2, T3> {
    }

    interface V4 <T1, T2, T3, T4> extends FN4<Either<? extends BaseError, Tuple4<T1, T2, T3, T4>>, T1, T2, T3, T4> {
    }

    interface V5 <T1, T2, T3, T4, T5> extends FN5<Either<? extends BaseError, Tuple5<T1, T2, T3, T4, T5>>, T1, T2, T3, T4, T5> {
    }

    interface V6 <T1, T2, T3, T4, T5, T6> extends FN6<Either<? extends BaseError, Tuple6<T1, T2, T3, T4, T5, T6>>, T1, T2, T3, T4, T5, T6> {
    }

    interface V7 <T1, T2, T3, T4, T5, T6, T7> extends FN7<Either<? extends BaseError, Tuple7<T1, T2, T3, T4, T5, T6, T7>>, T1, T2, T3, T4, T5, T6, T7> {
    }

    interface V8 <T1, T2, T3, T4, T5, T6, T7, T8> extends FN8<Either<? extends BaseError, Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>>, T1, T2, T3, T4, T5, T6, T7, T8> {
    }

    interface V9 <T1, T2, T3, T4, T5, T6, T7, T8, T9> extends FN9<Either<? extends BaseError, Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
    }
}

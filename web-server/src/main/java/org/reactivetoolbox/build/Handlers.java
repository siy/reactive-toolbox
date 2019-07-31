package org.reactivetoolbox.build;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.functional.Either;
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

public interface Handlers {
    interface Handler0<R> extends FN0<Promise<Either<? extends BaseError, R>>> {}
    interface Handler1<R, T1> extends FN1<Promise<Either<? extends BaseError, R>>, T1> {}
    interface Handler2<R, T1, T2> extends FN2<Promise<Either<? extends BaseError, R>>, T1, T2> {}
    interface Handler3<R, T1, T2, T3> extends FN3<Promise<Either<? extends BaseError, R>>, T1, T2, T3> {}
    interface Handler4<R, T1, T2, T3, T4> extends FN4<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4> {}
    interface Handler5<R, T1, T2, T3, T4, T5> extends FN5<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5> {}
    interface Handler6<R, T1, T2, T3, T4, T5, T6> extends FN6<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5, T6> {}
    interface Handler7<R, T1, T2, T3, T4, T5, T6, T7> extends FN7<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5, T6, T7> {}
    interface Handler8<R, T1, T2, T3, T4, T5, T6, T7, T8> extends FN8<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5, T6, T7, T8> {}
    interface Handler9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> extends FN9<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5, T6, T7, T8, T9> {}
}

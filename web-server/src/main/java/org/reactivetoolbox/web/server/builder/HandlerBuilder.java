package org.reactivetoolbox.web.server.builder;

import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Tuples.Tuple1;
import org.reactivetoolbox.core.functional.Tuples.Tuple2;
import org.reactivetoolbox.core.functional.Tuples.Tuple3;
import org.reactivetoolbox.core.functional.Tuples.Tuple4;
import org.reactivetoolbox.web.server.parameter.Parameters.Parameter;

import java.util.function.Supplier;

public class HandlerBuilder {
    public <T> void perform(final Supplier<Either<?, T>> handler) {
    }

    public <T> Performer<Tuple1<T>> with(final Parameter<T> first) {
        return new Performer<>();
    }

    public <T1, T2> Performer<Tuple2<T1, T2>> with(final Parameter<T1> first, final Parameter<T2> second) {
        return new Performer<>();
    }

    public <T1, T2, T3> Performer<Tuple3<T1, T2, T3>> with(final Parameter<T1> first, final Parameter<T2> second,
                                                           final Parameter<T3> third) {
        return new Performer<>();
    }

    public <T1, T2, T3, T4> Performer<Tuple4<T1, T2, T3, T4>>
           with(final Parameter<T1> first, final Parameter<T2> second, final Parameter<T3> third,
                final Parameter<T4> fourth) {
        return new Performer<>();
    }
}

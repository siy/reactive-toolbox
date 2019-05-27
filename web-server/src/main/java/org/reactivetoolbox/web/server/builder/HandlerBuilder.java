package org.reactivetoolbox.web.server.builder;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Functions.FN0;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Tuples.Tuple1;
import org.reactivetoolbox.core.functional.Tuples.Tuple2;
import org.reactivetoolbox.core.functional.Tuples.Tuple3;
import org.reactivetoolbox.core.functional.Tuples.Tuple4;
import org.reactivetoolbox.core.functional.Tuples.Tuple9;
import org.reactivetoolbox.web.server.Server;
import org.reactivetoolbox.web.server.parameter.Parameters.Parameter;

public class HandlerBuilder {
    private final Http method;
    private final Server server;
    private final String path;

    public HandlerBuilder(final Http method, final Server server, final String path) {
        this.method = method;
        this.server = server;
        this.path = path;
    }

    public <T> void perform(final FN0<Promise<?, T>> handler) {
        server.registerHandler(method, path, (Void) -> handler.apply(), new Parameter<?>[0]);
    }

    public <T> Performer<Tuple1<T>> with(final Parameter<T> param1) {
        return new Performer<>(param1);
    }

    public <T1, T2> Performer<Tuple2<T1, T2>> with(final Parameter<T1> param1, final Parameter<T2> param2) {
        return new Performer<>(param1, param2);
    }

    public <T1, T2, T3> Performer<Tuple3<T1, T2, T3>> with(final Parameter<T1> param1, final Parameter<T2> param2,
                                                           final Parameter<T3> param3) {
        return new Performer<>(param1, param2, param3);
    }

    public <T1, T2, T3, T4> Performer<Tuple4<T1, T2, T3, T4>>
           with(final Parameter<T1> param1, final Parameter<T2> param2, final Parameter<T3> param3,
                final Parameter<T4> param4) {
        return new Performer<>(param1, param2, param3, param4);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> Performer<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>
           with(final Parameter<T1> param1, final Parameter<T2> param2, final Parameter<T3> param3,
                final Parameter<T4> param4, final Parameter<T5> param5, final Parameter<T6> param6,
                final Parameter<T7> param7, final Parameter<T8> param8, final Parameter<T9> param9) {
        return new Performer<>(param1, param2, param3, param4, param5, param6, param7, param8, param9);
    }

    public class Performer<T> {
        private final Parameter<?>[] parameters;
        private Performer(final Parameter<?> ...parameters) {
            this.parameters = parameters;
        }

        public <E extends BaseError, R> void perform(final FN1<Promise<E, R>, T> handler) {
            server.registerHandler(method, path, handler, parameters);
        }
    }
}

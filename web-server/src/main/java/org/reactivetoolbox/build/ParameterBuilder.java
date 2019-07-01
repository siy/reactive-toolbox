package org.reactivetoolbox.build;

import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions;
import org.reactivetoolbox.core.functional.Functions.FN0;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Functions.FN2;
import org.reactivetoolbox.core.functional.Functions.FN3;
import org.reactivetoolbox.core.functional.Tuples.Tuple1;
import org.reactivetoolbox.core.functional.Tuples.Tuple2;
import org.reactivetoolbox.core.functional.Tuples.Tuple3;
import org.reactivetoolbox.eventbus.Route;
import org.reactivetoolbox.web.server.AuthenticationError;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.parameter.Parameters.Parameter;
import org.reactivetoolbox.web.server.parameter.validation.ValidationError;

public class ParameterBuilder {
    private final HttpRouteBuilder route;

    private ParameterBuilder(HttpRouteBuilder route) {
        this.route = route;
    }

    public static ParameterBuilder0 of(final HttpRouteBuilder routeBuilder) {
        return new ParameterBuilder0(routeBuilder);
    }

    public static <T1> ParameterBuilder1<T1> of(final HttpRouteBuilder routeBuilder, final Parameter<T1> param1) {
        return new ParameterBuilder1<>(routeBuilder, param1);
    }

    public static <T1, T2> ParameterBuilder2<T1, T2> of(final HttpRouteBuilder routeBuilder, final Parameter<T1> param1, final Parameter<T2> param2) {
        return new ParameterBuilder2<>(routeBuilder, param1, param2);
    }

    public static <T1, T2, T3> ParameterBuilder3<T1, T2, T3> of(final HttpRouteBuilder routeBuilder, final Parameter<T1> param1, final Parameter<T2> param2, final Parameter<T3> param3) {
        return new ParameterBuilder3<>(routeBuilder, param1, param2, param3);
    }

    protected HttpRouteBuilder route() {
        return route;
    }

    public static class ParameterBuilder0 extends ParameterBuilder {
        private ParameterBuilder0(final HttpRouteBuilder routeBuilder) {
            super(routeBuilder);
        }

        public <R> Route<RequestContext> invoke(final FN0<Promise<R>> handler) {
            return route().withHandler(ignored -> handler.apply()).build();
        }
    }

    public static class ParameterBuilder1<T1> extends ParameterBuilder {
        private ParameterBuilder1(final HttpRouteBuilder routeBuilder, final Parameter<T1> param1) {
            super(routeBuilder);
        }

        public <R> Route<RequestContext> invoke(final FN1<Promise<R>, T1> handler) {
            return route().withHandler((Tuple1<T1> parameters) -> parameters.map(handler)).build();
        }
    }

    public static class ParameterBuilder2<T1, T2> extends ParameterBuilder {
        private ParameterBuilder2(final HttpRouteBuilder routeBuilder, final Parameter<T1> param1, final Parameter<T2> param2) {
            super(routeBuilder);
        }

        public <R> Route<RequestContext> invoke(final FN2<Promise<R>, T1, T2> handler) {
            return route().withHandler((Tuple2<T1, T2> parameters) -> parameters.map(handler)).build();
        }
    }

    public static class ParameterBuilder3<T1, T2, T3> extends ParameterBuilder {
        private ParameterBuilder3(final HttpRouteBuilder routeBuilder, final Parameter<T1> param1, final Parameter<T2> param2, final Parameter<T3> param3) {
            super(routeBuilder);
        }

        public <R> Route<RequestContext> invoke(final FN3<Promise<R>, T1, T2, T3> handler) {
            return route().withHandler((Tuple3<T1, T2, T3> parameters) -> parameters.map(handler)).build();
        }

        //TODO: support for custom cross-parameter validation
        public ParameterBuilder3<T1, T2, T3> validate(final FN1<Either<ValidationError, Tuple3<T1, T2, T3>>, Tuple3<T1, T2, T3>> validator) {
            return this;
        }
    }
}

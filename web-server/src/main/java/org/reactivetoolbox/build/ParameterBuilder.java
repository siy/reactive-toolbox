package org.reactivetoolbox.build;

import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
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

public class ParameterBuilder {
    private final HttpRouteBuilder route;

    private ParameterBuilder(HttpRouteBuilder route) {
        this.route = route;
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

    private void ensure(final FN1<Either<AuthenticationError, RequestContext>, RequestContext> authenticationVerifier) {
        route.withAuthHandler(authenticationVerifier);
    }

    protected HttpRouteBuilder route() {
        return route;
    }

    public static class ParameterBuilder1<T1> extends ParameterBuilder {
        private ParameterBuilder1(final HttpRouteBuilder routeBuilder, final Parameter<T1> param1) {
            super(routeBuilder);
        }

        public ParameterBuilder1<T1> ensure(final FN1<Either<AuthenticationError, RequestContext>, RequestContext> authenticationVerifier) {
            super.ensure(authenticationVerifier);
            return this;
        }

        public <R> Route<RequestContext> thenHandleWith(final FN1<Promise<R>, T1> handler) {
            return route().withHandler((Tuple1<T1> parameters) -> parameters.map(handler)).build();
        }
    }

    public static class ParameterBuilder2<T1, T2> extends ParameterBuilder {
        private ParameterBuilder2(final HttpRouteBuilder routeBuilder, final Parameter<T1> param1, final Parameter<T2> param2) {
            super(routeBuilder);
        }

        public ParameterBuilder2<T1, T2> ensure(final FN1<Either<AuthenticationError, RequestContext>, RequestContext> authenticationVerifier) {
            super.ensure(authenticationVerifier);
            return this;
        }

        public <R> Route<RequestContext> thenHandleWith(final FN2<Promise<R>, T1, T2> handler) {
            return route().withHandler((Tuple2<T1, T2> parameters) -> parameters.map(handler)).build();
        }
    }

    public static class ParameterBuilder3<T1, T2, T3> extends ParameterBuilder {
        private ParameterBuilder3(final HttpRouteBuilder routeBuilder, final Parameter<T1> param1, final Parameter<T2> param2, final Parameter<T3> param3) {
            super(routeBuilder);
        }

        public ParameterBuilder3<T1, T2, T3> ensure(final FN1<Either<AuthenticationError, RequestContext>, RequestContext> authenticationVerifier) {
            super.ensure(authenticationVerifier);
            return this;
        }

        public <R> Route<RequestContext> thenHandleWith(final FN3<Promise<R>, T1, T2, T3> handler) {
            return route().withHandler((Tuple3<T1, T2, T3> parameters) -> parameters.map(handler)).build();
        }
    }
}

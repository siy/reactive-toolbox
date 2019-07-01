package org.reactivetoolbox.build;

import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions;
import org.reactivetoolbox.core.functional.Functions.*;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Functions.FN2;
import org.reactivetoolbox.core.functional.Functions.FN3;
import org.reactivetoolbox.core.functional.Functions.FN9;
import org.reactivetoolbox.core.functional.Tuples;
import org.reactivetoolbox.core.functional.Tuples.*;
import org.reactivetoolbox.core.functional.Tuples.Tuple2;
import org.reactivetoolbox.core.functional.Tuples.Tuple3;
import org.reactivetoolbox.core.functional.Tuples.Tuple9;
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

    public static <T1, T2, T3, T4> ParameterBuilder4<T1, T2, T3, T4> of(final HttpRouteBuilder routeBuilder,
                                                                        final Parameter<T1> param1,
                                                                        final Parameter<T2> param2,
                                                                        final Parameter<T3> param3,
                                                                        final Parameter<T4> param4) {
        return new ParameterBuilder4<>(routeBuilder, param1, param2, param3, param4);
    }

    public static <T1, T2, T3, T4, T5> ParameterBuilder5<T1, T2, T3, T4, T5> of(final HttpRouteBuilder routeBuilder,
                                                                                final Parameter<T1> param1,
                                                                                final Parameter<T2> param2,
                                                                                final Parameter<T3> param3,
                                                                                final Parameter<T4> param4,
                                                                                final Parameter<T5> param5) {
        return new ParameterBuilder5<>(routeBuilder, param1, param2, param3, param4, param5);
    }

    public static <T1, T2, T3, T4, T5, T6> ParameterBuilder6<T1, T2, T3, T4, T5, T6> of(final HttpRouteBuilder routeBuilder,
                                                                                        final Parameter<T1> param1,
                                                                                        final Parameter<T2> param2,
                                                                                        final Parameter<T3> param3,
                                                                                        final Parameter<T4> param4,
                                                                                        final Parameter<T5> param5,
                                                                                        final Parameter<T6> param6) {
        return new ParameterBuilder6<>(routeBuilder, param1, param2, param3, param4, param5, param6);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> ParameterBuilder7<T1, T2, T3, T4, T5, T6, T7> of(final HttpRouteBuilder routeBuilder,
                                                                                                final Parameter<T1> param1,
                                                                                                final Parameter<T2> param2,
                                                                                                final Parameter<T3> param3,
                                                                                                final Parameter<T4> param4,
                                                                                                final Parameter<T5> param5,
                                                                                                final Parameter<T6> param6,
                                                                                                final Parameter<T7> param7) {
        return new ParameterBuilder7<>(routeBuilder, param1, param2, param3, param4, param5, param6, param7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> ParameterBuilder8<T1, T2, T3, T4, T5, T6, T7, T8> of(final HttpRouteBuilder routeBuilder,
                                                                                                        final Parameter<T1> param1,
                                                                                                        final Parameter<T2> param2,
                                                                                                        final Parameter<T3> param3,
                                                                                                        final Parameter<T4> param4,
                                                                                                        final Parameter<T5> param5,
                                                                                                        final Parameter<T6> param6,
                                                                                                        final Parameter<T7> param7,
                                                                                                        final Parameter<T8> param8) {
        return new ParameterBuilder8<>(routeBuilder, param1, param2, param3, param4, param5, param6, param7, param8);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> ParameterBuilder9<T1, T2, T3, T4, T5, T6, T7, T8, T9> of(final HttpRouteBuilder routeBuilder,
                                                                                                                final Parameter<T1> param1,
                                                                                                                final Parameter<T2> param2,
                                                                                                                final Parameter<T3> param3,
                                                                                                                final Parameter<T4> param4,
                                                                                                                final Parameter<T5> param5,
                                                                                                                final Parameter<T6> param6,
                                                                                                                final Parameter<T7> param7,
                                                                                                                final Parameter<T8> param8,
                                                                                                                final Parameter<T9> param9) {
        return new ParameterBuilder9<>(routeBuilder, param1, param2, param3, param4, param5, param6, param7, param8, param9);
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
        private final Tuple1<Parameter<T1>> parameters;

        private ParameterBuilder1(final HttpRouteBuilder routeBuilder, final Parameter<T1> param1) {
            super(routeBuilder);
        }

        public <R> Route<RequestContext> invoke(final FN1<Promise<R>, T1> handler) {
            return route().withHandler((Tuple1<T1> parameters) -> parameters.map(handler)).build();
        }
    }

    public static class ParameterBuilder2<T1, T2> extends ParameterBuilder {
        private ParameterBuilder2(final HttpRouteBuilder routeBuilder,
                                  final Parameter<T1> param1,
                                  final Parameter<T2> param2) {
            super(routeBuilder);
        }

        public <R> Route<RequestContext> invoke(final FN2<Promise<R>, T1, T2> handler) {
            return route().withHandler((Tuple2<T1, T2> parameters) -> parameters.map(handler)).build();
        }

        public ParameterBuilder2<T1, T2> validate(final FN2<Either<ValidationError, Tuple2<T1, T2>>, T1, T2> validator) {
            return this;
        }
    }

    public static class ParameterBuilder3<T1, T2, T3> extends ParameterBuilder {
        private ParameterBuilder3(final HttpRouteBuilder routeBuilder,
                                  final Parameter<T1> param1,
                                  final Parameter<T2> param2,
                                  final Parameter<T3> param3) {
            super(routeBuilder);
        }

        public <R> Route<RequestContext> invoke(final FN3<Promise<R>, T1, T2, T3> handler) {
            return route().withHandler((Tuple3<T1, T2, T3> parameters) -> parameters.map(handler)).build();
        }

        public ParameterBuilder3<T1, T2, T3> validate(final FN3<Either<ValidationError, Tuple3<T1, T2, T3>>, T1, T2, T3> validator) {
            return this;
        }
    }

    public static class ParameterBuilder4<T1, T2, T3, T4> extends ParameterBuilder {
        private ParameterBuilder4(final HttpRouteBuilder routeBuilder,
                                  final Parameter<T1> param1,
                                  final Parameter<T2> param2,
                                  final Parameter<T3> param3,
                                  final Parameter<T4> param4) {
            super(routeBuilder);
        }

        public <R> Route<RequestContext> invoke(final FN4<Promise<R>, T1, T2, T3, T4> handler) {
            return route().withHandler((Tuple4<T1, T2, T3, T4> parameters) -> parameters.map(handler)).build();
        }

        public ParameterBuilder4<T1, T2, T3, T4> validate(final FN4<Either<ValidationError, Tuple4<T1, T2, T3, T4>>, T1, T2, T3, T4> validator) {
            return this;
        }
    }

    public static class ParameterBuilder5<T1, T2, T3, T4, T5> extends ParameterBuilder {
        private ParameterBuilder5(final HttpRouteBuilder routeBuilder,
                                  final Parameter<T1> param1,
                                  final Parameter<T2> param2,
                                  final Parameter<T3> param3,
                                  final Parameter<T4> param4,
                                  final Parameter<T5> param5) {
            super(routeBuilder);
        }

        public <R> Route<RequestContext> invoke(final FN5<Promise<R>, T1, T2, T3, T4, T5> handler) {
            return route().withHandler((Tuple5<T1, T2, T3, T4, T5> parameters) -> parameters.map(handler)).build();
        }

        public ParameterBuilder5<T1, T2, T3, T4, T5> validate(final FN5<Either<ValidationError, Tuple5<T1, T2, T3, T4, T5>>, T1, T2, T3, T4, T5> validator) {
            return this;
        }
    }

    public static class ParameterBuilder6<T1, T2, T3, T4, T5, T6> extends ParameterBuilder {
        private ParameterBuilder6(final HttpRouteBuilder routeBuilder,
                                  final Parameter<T1> param1,
                                  final Parameter<T2> param2,
                                  final Parameter<T3> param3,
                                  final Parameter<T4> param4,
                                  final Parameter<T5> param5,
                                  final Parameter<T6> param6) {
            super(routeBuilder);
        }

        public <R> Route<RequestContext> invoke(final FN6<Promise<R>, T1, T2, T3, T4, T5, T6> handler) {
            return route().withHandler((Tuple6<T1, T2, T3, T4, T5, T6> parameters) -> parameters.map(handler)).build();
        }

        public ParameterBuilder6<T1, T2, T3, T4, T5, T6> validate(final FN6<Either<ValidationError, Tuple6<T1, T2, T3, T4, T5, T6>>, T1, T2, T3, T4, T5, T6> validator) {
            return this;
        }
    }

    public static class ParameterBuilder7<T1, T2, T3, T4, T5, T6, T7> extends ParameterBuilder {
        private ParameterBuilder7(final HttpRouteBuilder routeBuilder,
                                  final Parameter<T1> param1,
                                  final Parameter<T2> param2,
                                  final Parameter<T3> param3,
                                  final Parameter<T4> param4,
                                  final Parameter<T5> param5,
                                  final Parameter<T6> param6,
                                  final Parameter<T7> param7) {
            super(routeBuilder);
        }

        public <R> Route<RequestContext> invoke(final FN7<Promise<R>, T1, T2, T3, T4, T5, T6, T7> handler) {
            return route().withHandler((Tuple7<T1, T2, T3, T4, T5, T6, T7> parameters) -> parameters.map(handler)).build();
        }

        public ParameterBuilder7<T1, T2, T3, T4, T5, T6, T7> validate(final FN7<Either<ValidationError, Tuple7<T1, T2, T3, T4, T5, T6, T7>>, T1, T2, T3, T4, T5, T6, T7> validator) {
            return this;
        }
    }

    public static class ParameterBuilder8<T1, T2, T3, T4, T5, T6, T7, T8> extends ParameterBuilder {
        private ParameterBuilder8(final HttpRouteBuilder routeBuilder,
                                  final Parameter<T1> param1,
                                  final Parameter<T2> param2,
                                  final Parameter<T3> param3,
                                  final Parameter<T4> param4,
                                  final Parameter<T5> param5,
                                  final Parameter<T6> param6,
                                  final Parameter<T7> param7,
                                  final Parameter<T8> param8) {
            super(routeBuilder);
        }

        public <R> Route<RequestContext> invoke(final FN8<Promise<R>, T1, T2, T3, T4, T5, T6, T7, T8> handler) {
            return route().withHandler((Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> parameters) -> parameters.map(handler)).build();
        }

        public ParameterBuilder8<T1, T2, T3, T4, T5, T6, T7, T8> validate(final FN8<Either<ValidationError, Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>>, T1, T2, T3, T4, T5, T6, T7, T8> validator) {
            return this;
        }
    }

    public static class ParameterBuilder9<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends ParameterBuilder {
        private ParameterBuilder9(final HttpRouteBuilder routeBuilder,
                                  final Parameter<T1> param1,
                                  final Parameter<T2> param2,
                                  final Parameter<T3> param3,
                                  final Parameter<T4> param4,
                                  final Parameter<T5> param5,
                                  final Parameter<T6> param6,
                                  final Parameter<T7> param7,
                                  final Parameter<T8> param8,
                                  final Parameter<T9> param9) {
            super(routeBuilder);
        }

        public <R> Route<RequestContext> invoke(final FN9<Promise<R>, T1, T2, T3, T4, T5, T6, T7, T8, T9> handler) {
            return route().withHandler((Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> parameters) -> parameters.map(handler)).build();
        }

        public ParameterBuilder9<T1, T2, T3, T4, T5, T6, T7, T8, T9> validate(final FN9<Either<ValidationError, Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>, T1, T2, T3, T4, T5, T6, T7, T8, T9> validator) {
            return this;
        }
    }
}

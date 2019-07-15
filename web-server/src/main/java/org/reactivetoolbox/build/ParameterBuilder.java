package org.reactivetoolbox.build;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
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
import org.reactivetoolbox.core.functional.Tuples;
import org.reactivetoolbox.core.functional.Tuples.Tuple1;
import org.reactivetoolbox.core.functional.Tuples.Tuple2;
import org.reactivetoolbox.core.functional.Tuples.Tuple3;
import org.reactivetoolbox.core.functional.Tuples.Tuple4;
import org.reactivetoolbox.core.functional.Tuples.Tuple5;
import org.reactivetoolbox.core.functional.Tuples.Tuple6;
import org.reactivetoolbox.core.functional.Tuples.Tuple7;
import org.reactivetoolbox.core.functional.Tuples.Tuple8;
import org.reactivetoolbox.core.functional.Tuples.Tuple9;
import org.reactivetoolbox.eventbus.Route;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.parameter.Parameters.Parameter;

public class ParameterBuilder {
    private final HttpRouteBuilder route;

    private ParameterBuilder(HttpRouteBuilder route) {
        this.route = route;
    }

    public static ParameterBuilder0 of(final HttpRouteBuilder routeBuilder) {
        return new ParameterBuilder0(routeBuilder);
    }

    public static <T1> ParameterBuilder1<T1> of(final HttpRouteBuilder routeBuilder,
                                                final Parameter<T1> param1) {
        return new ParameterBuilder1<>(routeBuilder, param1);
    }

    public static <T1, T2> ParameterBuilder2<T1, T2> of(final HttpRouteBuilder routeBuilder,
                                                        final Parameter<T1> param1,
                                                        final Parameter<T2> param2) {
        return new ParameterBuilder2<>(routeBuilder, param1, param2);
    }

    public static <T1, T2, T3> ParameterBuilder3<T1, T2, T3> of(final HttpRouteBuilder routeBuilder,
                                                                final Parameter<T1> param1,
                                                                final Parameter<T2> param2,
                                                                final Parameter<T3> param3) {
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
        return new ParameterBuilder6<>(routeBuilder, param1, param2, param3, param4, param5,
                                       param6);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> ParameterBuilder7<T1, T2, T3, T4, T5, T6, T7> of(
            final HttpRouteBuilder routeBuilder,
            final Parameter<T1> param1,
            final Parameter<T2> param2,
            final Parameter<T3> param3,
            final Parameter<T4> param4,
            final Parameter<T5> param5,
            final Parameter<T6> param6,
            final Parameter<T7> param7) {
        return new ParameterBuilder7<>(routeBuilder, param1, param2, param3, param4, param5, param6,
                                       param7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> ParameterBuilder8<T1, T2, T3, T4, T5, T6, T7, T8> of(
            final HttpRouteBuilder routeBuilder,
            final Parameter<T1> param1,
            final Parameter<T2> param2,
            final Parameter<T3> param3,
            final Parameter<T4> param4,
            final Parameter<T5> param5,
            final Parameter<T6> param6,
            final Parameter<T7> param7,
            final Parameter<T8> param8) {
        return new ParameterBuilder8<>(routeBuilder, param1, param2, param3, param4, param5, param6,
                                       param7, param8);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> ParameterBuilder9<T1, T2, T3, T4, T5, T6, T7, T8, T9> of(
            final HttpRouteBuilder routeBuilder,
            final Parameter<T1> param1,
            final Parameter<T2> param2,
            final Parameter<T3> param3,
            final Parameter<T4> param4,
            final Parameter<T5> param5,
            final Parameter<T6> param6,
            final Parameter<T7> param7,
            final Parameter<T8> param8,
            final Parameter<T9> param9) {
        return new ParameterBuilder9<>(routeBuilder, param1, param2, param3, param4, param5, param6,
                                       param7, param8, param9);
    }

    protected HttpRouteBuilder routeBuilder() {
        return route;
    }

    public static class ParameterBuilder0 extends ParameterBuilder {
        private ParameterBuilder0(final HttpRouteBuilder routeBuilder) {
            super(routeBuilder);
        }

        public <R> RouteEnricher<RequestContext, R> invoke(final FN0<Promise<Either<? extends BaseError, R>>> handler) {
            return routeBuilder().withHandler(ignored -> Either.success(handler.apply()));
        }
    }

    public static class ParameterBuilder1<T1> extends ParameterBuilder {
        private final Parameter<T1> param1;

        private ParameterBuilder1(final HttpRouteBuilder routeBuilder, final Parameter<T1> param1) {
            super(routeBuilder);
            this.param1 = param1;
        }

        public <R> RouteEnricher<RequestContext, R> invoke(final FN1<Promise<Either<? extends BaseError, R>>, T1> handler) {
            final var converter = param1.converter();

            return routeBuilder().withHandler(
                    context -> Tuples.of(converter.apply(context))
                            .map(Tuples::squeeze)
                            .flatMap(Either::<BaseError, Tuple1<T1>>success)
                            .mapSuccess(params -> params.map(handler)));
        }
    }

    public static class ParameterBuilder2<T1, T2> extends ParameterBuilder {
        private final Parameter<T1> param1;
        private final Parameter<T2> param2;
        private FN2<Either<? extends BaseError, Tuple2<T1, T2>>, T1, T2> validator =
                (param1, param2) -> Either.success(Tuples.of(param1, param2));

        private ParameterBuilder2(final HttpRouteBuilder routeBuilder,
                                  final Parameter<T1> param1,
                                  final Parameter<T2> param2) {
            super(routeBuilder);
            this.param1 = param1;
            this.param2 = param2;
        }

        public <R> RouteEnricher<RequestContext, R> invoke(final FN2<Promise<Either<? extends BaseError, R>>, T1, T2> handler) {
            final var converter1 = param1.converter();
            final var converter2 = param2.converter();

            return routeBuilder().withHandler(
                    context -> Tuples.of(converter1.apply(context),
                                         converter2.apply(context))
                            .map(Tuples::squeeze)
                            .flatMap(tuple -> tuple.map(validator))
                            .mapSuccess(params -> params.map(handler)));
        }

        public ParameterBuilder2<T1, T2> validate(final FN2<Either<? extends BaseError, Tuple2<T1, T2>>, T1, T2> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class ParameterBuilder3<T1, T2, T3> extends ParameterBuilder {
        private final Parameter<T1> param1;
        private final Parameter<T2> param2;
        private final Parameter<T3> param3;
        private FN3<Either<? extends BaseError, Tuple3<T1, T2, T3>>, T1, T2, T3> validator =
                (param1, param2, param3) -> Either.success(Tuples.of(param1, param2, param3));

        private ParameterBuilder3(final HttpRouteBuilder routeBuilder,
                                  final Parameter<T1> param1,
                                  final Parameter<T2> param2,
                                  final Parameter<T3> param3) {
            super(routeBuilder);
            this.param1 = param1;
            this.param2 = param2;
            this.param3 = param3;
        }

        public <R> RouteEnricher<RequestContext, R> invoke(final FN3<Promise<Either<? extends BaseError, R>>, T1, T2, T3> handler) {
            final var converter1 = param1.converter();
            final var converter2 = param2.converter();
            final var converter3 = param3.converter();

            return routeBuilder().withHandler(
                    context -> Tuples.of(converter1.apply(context),
                                         converter2.apply(context),
                                         converter3.apply(context))
                            .map(Tuples::squeeze)
                            .flatMap(tuple -> tuple.map(validator))
                            .mapSuccess(params -> params.map(handler)));
        }

        public ParameterBuilder3<T1, T2, T3> validate(final FN3<Either<? extends BaseError, Tuple3<T1, T2, T3>>, T1, T2, T3> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class ParameterBuilder4<T1, T2, T3, T4> extends ParameterBuilder {
        private final Parameter<T1> param1;
        private final Parameter<T2> param2;
        private final Parameter<T3> param3;
        private final Parameter<T4> param4;
        private FN4<Either<? extends BaseError, Tuple4<T1, T2, T3, T4>>, T1, T2, T3, T4> validator =
                (param1, param2, param3, param4) -> Either.success(
                        Tuples.of(param1, param2, param3, param4));

        private ParameterBuilder4(final HttpRouteBuilder routeBuilder,
                                  final Parameter<T1> param1,
                                  final Parameter<T2> param2,
                                  final Parameter<T3> param3,
                                  final Parameter<T4> param4) {
            super(routeBuilder);

            this.param1 = param1;
            this.param2 = param2;
            this.param3 = param3;
            this.param4 = param4;
        }

        public <R> RouteEnricher<RequestContext, R> invoke(final FN4<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4> handler) {
            final var converter1 = param1.converter();
            final var converter2 = param2.converter();
            final var converter3 = param3.converter();
            final var converter4 = param4.converter();

            return routeBuilder().withHandler(
                    context -> Tuples.of(converter1.apply(context),
                                         converter2.apply(context),
                                         converter3.apply(context),
                                         converter4.apply(context))
                            .map(Tuples::squeeze)
                            .flatMap(tuple -> tuple.map(validator))
                            .mapSuccess(params -> params.map(handler)));
        }

        public ParameterBuilder4<T1, T2, T3, T4> validate(final FN4<Either<? extends BaseError, Tuple4<T1, T2, T3, T4>>, T1, T2, T3, T4> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class ParameterBuilder5<T1, T2, T3, T4, T5> extends ParameterBuilder {
        private final Parameter<T1> param1;
        private final Parameter<T2> param2;
        private final Parameter<T3> param3;
        private final Parameter<T4> param4;
        private final Parameter<T5> param5;
        private FN5<Either<? extends BaseError, Tuple5<T1, T2, T3, T4, T5>>, T1, T2, T3, T4, T5>
                validator = (param1, param2, param3, param4, param5) -> Either.success(
                Tuples.of(param1, param2, param3, param4, param5));

        private ParameterBuilder5(final HttpRouteBuilder routeBuilder,
                                  final Parameter<T1> param1,
                                  final Parameter<T2> param2,
                                  final Parameter<T3> param3,
                                  final Parameter<T4> param4,
                                  final Parameter<T5> param5) {
            super(routeBuilder);
            this.param1 = param1;
            this.param2 = param2;
            this.param3 = param3;
            this.param4 = param4;
            this.param5 = param5;
        }

        public <R> RouteEnricher<RequestContext, R> invoke(final FN5<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5> handler) {
            final var converter1 = param1.converter();
            final var converter2 = param2.converter();
            final var converter3 = param3.converter();
            final var converter4 = param4.converter();
            final var converter5 = param5.converter();

            return routeBuilder().withHandler(
                    context -> Tuples.of(converter1.apply(context),
                                         converter2.apply(context),
                                         converter3.apply(context),
                                         converter4.apply(context),
                                         converter5.apply(context))
                            .map(Tuples::squeeze)
                            .flatMap(tuple -> tuple.map(validator))
                            .mapSuccess(params -> params.map(handler)));
        }

        public ParameterBuilder5<T1, T2, T3, T4, T5> validate(final FN5<Either<? extends BaseError, Tuple5<T1, T2, T3, T4, T5>>, T1, T2, T3, T4, T5> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class ParameterBuilder6<T1, T2, T3, T4, T5, T6> extends ParameterBuilder {
        private final Parameter<T1> param1;
        private final Parameter<T2> param2;
        private final Parameter<T3> param3;
        private final Parameter<T4> param4;
        private final Parameter<T5> param5;
        private final Parameter<T6> param6;
        private FN6<Either<? extends BaseError, Tuple6<T1, T2, T3, T4, T5, T6>>, T1, T2, T3, T4, T5, T6>
                validator = (param1, param2, param3, param4, param5, param6) -> Either.success(
                Tuples.of(param1, param2, param3, param4, param5, param6));

        private ParameterBuilder6(final HttpRouteBuilder routeBuilder,
                                  final Parameter<T1> param1,
                                  final Parameter<T2> param2,
                                  final Parameter<T3> param3,
                                  final Parameter<T4> param4,
                                  final Parameter<T5> param5,
                                  final Parameter<T6> param6) {
            super(routeBuilder);
            this.param1 = param1;
            this.param2 = param2;
            this.param3 = param3;
            this.param4 = param4;
            this.param5 = param5;
            this.param6 = param6;
        }

        public <R> RouteEnricher<RequestContext, R> invoke(final FN6<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5, T6> handler) {
            final var converter1 = param1.converter();
            final var converter2 = param2.converter();
            final var converter3 = param3.converter();
            final var converter4 = param4.converter();
            final var converter5 = param5.converter();
            final var converter6 = param6.converter();

            return routeBuilder().withHandler(
                    context -> Tuples.of(converter1.apply(context),
                                         converter2.apply(context),
                                         converter3.apply(context),
                                         converter4.apply(context),
                                         converter5.apply(context),
                                         converter6.apply(context))
                            .map(Tuples::squeeze)
                            .flatMap(tuple -> tuple.map(validator))
                            .mapSuccess(params -> params.map(handler)));
        }

        public ParameterBuilder6<T1, T2, T3, T4, T5, T6> validate(final FN6<Either<? extends BaseError, Tuple6<T1, T2, T3, T4, T5, T6>>, T1, T2, T3, T4, T5, T6> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class ParameterBuilder7<T1, T2, T3, T4, T5, T6, T7> extends ParameterBuilder {
        private final Parameter<T1> param1;
        private final Parameter<T2> param2;
        private final Parameter<T3> param3;
        private final Parameter<T4> param4;
        private final Parameter<T5> param5;
        private final Parameter<T6> param6;
        private final Parameter<T7> param7;
        private FN7<Either<? extends BaseError, Tuple7<T1, T2, T3, T4, T5, T6, T7>>, T1, T2, T3, T4, T5, T6, T7>
                validator =
                (param1, param2, param3, param4, param5, param6, param7) -> Either.success(
                        Tuples.of(param1, param2, param3, param4, param5, param6, param7));

        private ParameterBuilder7(final HttpRouteBuilder routeBuilder,
                                  final Parameter<T1> param1,
                                  final Parameter<T2> param2,
                                  final Parameter<T3> param3,
                                  final Parameter<T4> param4,
                                  final Parameter<T5> param5,
                                  final Parameter<T6> param6,
                                  final Parameter<T7> param7) {
            super(routeBuilder);
            this.param1 = param1;
            this.param2 = param2;
            this.param3 = param3;
            this.param4 = param4;
            this.param5 = param5;
            this.param6 = param6;
            this.param7 = param7;
        }

        public <R> RouteEnricher<RequestContext, R> invoke(final FN7<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5, T6, T7> handler) {
            final var converter1 = param1.converter();
            final var converter2 = param2.converter();
            final var converter3 = param3.converter();
            final var converter4 = param4.converter();
            final var converter5 = param5.converter();
            final var converter6 = param6.converter();
            final var converter7 = param7.converter();

            return routeBuilder().withHandler(
                    context -> Tuples.of(converter1.apply(context),
                                         converter2.apply(context),
                                         converter3.apply(context),
                                         converter4.apply(context),
                                         converter5.apply(context),
                                         converter6.apply(context),
                                         converter7.apply(context))
                            .map(Tuples::squeeze)
                            .flatMap(tuple -> tuple.map(validator))
                            .mapSuccess(params -> params.map(handler)));
        }

        public ParameterBuilder7<T1, T2, T3, T4, T5, T6, T7> validate(final FN7<Either<? extends BaseError, Tuple7<T1, T2, T3, T4, T5, T6, T7>>, T1, T2, T3, T4, T5, T6, T7> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class ParameterBuilder8<T1, T2, T3, T4, T5, T6, T7, T8> extends ParameterBuilder {
        private final Parameter<T1> param1;
        private final Parameter<T2> param2;
        private final Parameter<T3> param3;
        private final Parameter<T4> param4;
        private final Parameter<T5> param5;
        private final Parameter<T6> param6;
        private final Parameter<T7> param7;
        private final Parameter<T8> param8;
        private FN8<Either<? extends BaseError, Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>>, T1, T2, T3, T4, T5, T6, T7, T8>
                validator =
                (param1, param2, param3, param4, param5, param6, param7, param8) -> Either.success(
                        Tuples.of(param1, param2, param3, param4, param5, param6, param7, param8));

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
            this.param1 = param1;
            this.param2 = param2;
            this.param3 = param3;
            this.param4 = param4;
            this.param5 = param5;
            this.param6 = param6;
            this.param7 = param7;
            this.param8 = param8;
        }

        public <R> RouteEnricher<RequestContext, R> invoke(final FN8<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5, T6, T7, T8> handler) {
            final var converter1 = param1.converter();
            final var converter2 = param2.converter();
            final var converter3 = param3.converter();
            final var converter4 = param4.converter();
            final var converter5 = param5.converter();
            final var converter6 = param6.converter();
            final var converter7 = param7.converter();
            final var converter8 = param8.converter();

            return routeBuilder().withHandler(
                    context -> Tuples.of(converter1.apply(context),
                                         converter2.apply(context),
                                         converter3.apply(context),
                                         converter4.apply(context),
                                         converter5.apply(context),
                                         converter6.apply(context),
                                         converter7.apply(context),
                                         converter8.apply(context))
                            .map(Tuples::squeeze)
                            .flatMap(tuple -> tuple.map(validator))
                            .mapSuccess(params -> params.map(handler)));
        }

        public ParameterBuilder8<T1, T2, T3, T4, T5, T6, T7, T8> validate(final FN8<Either<? extends BaseError, Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>>, T1, T2, T3, T4, T5, T6, T7, T8> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class ParameterBuilder9<T1, T2, T3, T4, T5, T6, T7, T8, T9>
            extends ParameterBuilder {
        private final Parameter<T1> param1;
        private final Parameter<T2> param2;
        private final Parameter<T3> param3;
        private final Parameter<T4> param4;
        private final Parameter<T5> param5;
        private final Parameter<T6> param6;
        private final Parameter<T7> param7;
        private final Parameter<T8> param8;
        private final Parameter<T9> param9;
        private FN9<Either<? extends BaseError, Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>, T1, T2, T3, T4, T5, T6, T7, T8, T9>
                validator =
                (param1, param2, param3, param4, param5, param6, param7, param8, param9) -> Either.success(
                        Tuples.of(param1, param2, param3, param4, param5, param6, param7, param8,
                                  param9));

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
            this.param1 = param1;
            this.param2 = param2;
            this.param3 = param3;
            this.param4 = param4;
            this.param5 = param5;
            this.param6 = param6;
            this.param7 = param7;
            this.param8 = param8;
            this.param9 = param9;
        }

        public <R> RouteEnricher<RequestContext, R> invoke(final FN9<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5, T6, T7, T8, T9> handler) {
            final var converter1 = param1.converter();
            final var converter2 = param2.converter();
            final var converter3 = param3.converter();
            final var converter4 = param4.converter();
            final var converter5 = param5.converter();
            final var converter6 = param6.converter();
            final var converter7 = param7.converter();
            final var converter8 = param8.converter();
            final var converter9 = param9.converter();

            return routeBuilder().withHandler(
                    context -> Tuples.of(converter1.apply(context),
                                         converter2.apply(context),
                                         converter3.apply(context),
                                         converter4.apply(context),
                                         converter5.apply(context),
                                         converter6.apply(context),
                                         converter7.apply(context),
                                         converter8.apply(context),
                                         converter9.apply(context))
                            .map(Tuples::squeeze)
                            .flatMap(tuple -> tuple.map(validator))
                            .mapSuccess(params -> params.map(handler)));
        }

        public ParameterBuilder9<T1, T2, T3, T4, T5, T6, T7, T8, T9> validate(final FN9<Either<? extends BaseError, Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>, T1, T2, T3, T4, T5, T6, T7, T8, T9> validator) {
            this.validator = validator;
            return this;
        }
    }
}

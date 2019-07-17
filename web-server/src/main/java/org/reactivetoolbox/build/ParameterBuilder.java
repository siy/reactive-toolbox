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
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.parameter.Parameters.P;

import static org.reactivetoolbox.core.functional.Tuples.squeeze;

public class ParameterBuilder {
    public static ParameterBuilder0 of(final Path path, final String description) {
        return new ParameterBuilder0(path, description);
    }

    public static <T1> ParameterBuilder1<T1> of(final Path path,
                                                final String description,
                                                final P<T1> param1) {
        return new ParameterBuilder1<>(path, description, Tuples.of(param1));
    }

    public static <T1, T2> ParameterBuilder2<T1, T2> of(final Path path,
                                                        final String description,
                                                        final P<T1> param1,
                                                        final P<T2> param2) {
        return new ParameterBuilder2<>(path, description, Tuples.of(param1, param2));
    }

    public static <T1, T2, T3> ParameterBuilder3<T1, T2, T3> of(final Path path,
                                                                final String description,
                                                                final P<T1> param1,
                                                                final P<T2> param2,
                                                                final P<T3> param3) {
        return new ParameterBuilder3<>(path, description, Tuples.of(param1, param2, param3));
    }

    public static <T1, T2, T3, T4> ParameterBuilder4<T1, T2, T3, T4> of(final Path path,
                                                                        final String description,
                                                                        final P<T1> param1,
                                                                        final P<T2> param2,
                                                                        final P<T3> param3,
                                                                        final P<T4> param4) {
        return new ParameterBuilder4<>(path, description, Tuples.of(param1, param2, param3, param4));
    }

    public static <T1, T2, T3, T4, T5> ParameterBuilder5<T1, T2, T3, T4, T5> of(final Path path,
                                                                                final String description,
                                                                                final P<T1> param1,
                                                                                final P<T2> param2,
                                                                                final P<T3> param3,
                                                                                final P<T4> param4,
                                                                                final P<T5> param5) {
        return new ParameterBuilder5<>(path, description, Tuples.of(param1, param2, param3, param4, param5));
    }

    public static <T1, T2, T3, T4, T5, T6> ParameterBuilder6<T1, T2, T3, T4, T5, T6> of(final Path path,
                                                                                        final String description,
                                                                                        final P<T1> param1,
                                                                                        final P<T2> param2,
                                                                                        final P<T3> param3,
                                                                                        final P<T4> param4,
                                                                                        final P<T5> param5,
                                                                                        final P<T6> param6) {
        return new ParameterBuilder6<>(path, description, Tuples.of(param1, param2, param3, param4, param5, param6));
    }

    public static <T1, T2, T3, T4, T5, T6, T7> ParameterBuilder7<T1, T2, T3, T4, T5, T6, T7> of(
            final Path path,
            final String description,
            final P<T1> param1,
            final P<T2> param2,
            final P<T3> param3,
            final P<T4> param4,
            final P<T5> param5,
            final P<T6> param6,
            final P<T7> param7) {
        return new ParameterBuilder7<>(path, description, Tuples.of(param1, param2, param3, param4, param5, param6, param7));
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> ParameterBuilder8<T1, T2, T3, T4, T5, T6, T7, T8> of(
            final Path path,
            final String description,
            final P<T1> param1,
            final P<T2> param2,
            final P<T3> param3,
            final P<T4> param4,
            final P<T5> param5,
            final P<T6> param6,
            final P<T7> param7,
            final P<T8> param8) {
        return new ParameterBuilder8<>(path, description, Tuples.of(param1, param2, param3, param4, param5, param6, param7, param8));
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> ParameterBuilder9<T1, T2, T3, T4, T5, T6, T7, T8, T9> of(
            final Path path,
            final String description,
            final P<T1> param1,
            final P<T2> param2,
            final P<T3> param3,
            final P<T4> param4,
            final P<T5> param5,
            final P<T6> param6,
            final P<T7> param7,
            final P<T8> param8,
            final P<T9> param9) {
        return new ParameterBuilder9<>(path, description, Tuples.of(param1, param2, param3, param4, param5, param6, param7, param8, param9));
    }

    public static class ParameterBuilder0 {
        private final Path path;

        private ParameterBuilder0(final Path path, final String description) {
            this.path = path;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN0<Promise<Either<? extends BaseError, R>>> handler) {
            return RouteEnricher.of(path, ignored -> Either.success(handler.apply()));
        }
    }

    public static class ParameterBuilder1<T1> {
        private final Path path;
        private final String description;
        private final Tuple1<P<T1>> parameters;

        private ParameterBuilder1(final Path path, final String description, final Tuple1<P<T1>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN1<Promise<Either<? extends BaseError, R>>, T1> handler) {
            return RouteEnricher.of(path,
                    context -> parameters.map(v1 -> squeeze(v1.converter().apply(context)))
                            .flatMap(Either::<BaseError, Tuple1<T1>>success)
                            .mapSuccess(params -> params.map(handler)));
        }
    }

    public static class ParameterBuilder2<T1, T2> {
        private final Path path;
        private final String description;
        private final Tuple2<P<T1>, P<T2>> parameters;
        private FN2<Either<? extends BaseError, Tuple2<T1, T2>>, T1, T2> validator =
                (param1, param2) -> Either.success(Tuples.of(param1, param2));

        private ParameterBuilder2(final Path path,
                                  final String description,
                                  final Tuple2<P<T1>, P<T2>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN2<Promise<Either<? extends BaseError, R>>, T1, T2> handler) {
            return RouteEnricher.of(path,
                    context -> parameters.map((v1, v2) -> squeeze(v1.converter().apply(context),
                                                                  v2.converter().apply(context)))
                            .flatMap(tuple -> tuple.map(validator))
                            .mapSuccess(params -> params.map(handler)));
        }

        public ParameterBuilder2<T1, T2> and(final FN2<Either<? extends BaseError, Tuple2<T1, T2>>, T1, T2> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class ParameterBuilder3<T1, T2, T3> {
        private final Path path;
        private final String description;
        private final Tuple3<P<T1>, P<T2>, P<T3>> parameters;
        private FN3<Either<? extends BaseError, Tuple3<T1, T2, T3>>, T1, T2, T3> validator = (param1,
                                                                                              param2,
                                                                                              param3) -> Either.success(Tuples.of(param1,
                                                                                                                                  param2,
                                                                                                                                  param3));

        private ParameterBuilder3(final Path path,
                                  final String description,
                                  final Tuple3<P<T1>, P<T2>, P<T3>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN3<Promise<Either<? extends BaseError, R>>, T1, T2, T3> handler) {
            return RouteEnricher.of(path,
                    context -> parameters.map((v1, v2, v3) -> squeeze(v1.converter().apply(context),
                                                                      v2.converter().apply(context),
                                                                      v3.converter().apply(context)))
                            .flatMap(tuple -> tuple.map(validator))
                            .mapSuccess(params -> params.map(handler)));
        }

        public ParameterBuilder3<T1, T2, T3> and(final FN3<Either<? extends BaseError, Tuple3<T1, T2, T3>>, T1, T2, T3> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class ParameterBuilder4<T1, T2, T3, T4> {
        private final Path path;
        private final String description;
        private final Tuple4<P<T1>, P<T2>, P<T3>, P<T4>> parameters;
        private FN4<Either<? extends BaseError, Tuple4<T1, T2, T3, T4>>, T1, T2, T3, T4> validator = (param1,
                                                                                                      param2,
                                                                                                      param3,
                                                                                                      param4) -> Either.success(Tuples.of(param1,
                                                                                                                                          param2,
                                                                                                                                          param3,
                                                                                                                                          param4));

        private ParameterBuilder4(final Path path,
                                  final String description,
                                  final Tuple4<P<T1>, P<T2>, P<T3>, P<T4>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN4<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4> handler) {
            return RouteEnricher.of(path,
                    context -> parameters.map((v1, v2, v3, v4) -> squeeze(v1.converter().apply(context),
                                                                          v2.converter().apply(context),
                                                                          v3.converter().apply(context),
                                                                          v4.converter().apply(context)))
                            .flatMap(tuple -> tuple.map(validator))
                            .mapSuccess(params -> params.map(handler)));
        }

        public ParameterBuilder4<T1, T2, T3, T4> and(final FN4<Either<? extends BaseError, Tuple4<T1, T2, T3, T4>>, T1, T2, T3, T4> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class ParameterBuilder5<T1, T2, T3, T4, T5> {
        private final Path path;
        private final String description;
        private final Tuple5<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>>
                parameters;
        private FN5<Either<? extends BaseError, Tuple5<T1, T2, T3, T4, T5>>, T1, T2, T3, T4, T5>
                validator = (param1, param2, param3, param4, param5) -> Either.success(
                Tuples.of(param1, param2, param3, param4, param5));

        private ParameterBuilder5(final Path path,
                                  final String description,
                                  final Tuple5<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN5<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5> handler) {
            return RouteEnricher.of(path,
                    context -> parameters.map((v1, v2, v3, v4, v5) -> squeeze(v1.converter().apply(context),
                                                                              v2.converter().apply(context),
                                                                              v3.converter().apply(context),
                                                                              v4.converter().apply(context),
                                                                              v5.converter().apply(context)))
                            .flatMap(tuple -> tuple.map(validator))
                            .mapSuccess(params -> params.map(handler)));
        }

        public ParameterBuilder5<T1, T2, T3, T4, T5> and(final FN5<Either<? extends BaseError, Tuple5<T1, T2, T3, T4, T5>>, T1, T2, T3, T4, T5> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class ParameterBuilder6<T1, T2, T3, T4, T5, T6> {
        private final Path path;
        private final String description;
        private final Tuple6<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>> parameters;
        private FN6<Either<? extends BaseError, Tuple6<T1, T2, T3, T4, T5, T6>>, T1, T2, T3, T4, T5, T6> validator = (param1,
                                                                                                                      param2,
                                                                                                                      param3,
                                                                                                                      param4,
                                                                                                                      param5,
                                                                                                                      param6) -> Either.success(Tuples.of(param1,
                                                                                                                                                          param2,
                                                                                                                                                          param3,
                                                                                                                                                          param4,
                                                                                                                                                          param5,
                                                                                                                                                          param6));

        private ParameterBuilder6(final Path path,
                                  final String description,
                                  final Tuple6<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN6<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5, T6> handler) {
            return RouteEnricher.of(path,
                    context -> parameters.map((v1, v2, v3, v4, v5, v6) -> squeeze(v1.converter().apply(context),
                                                                                  v2.converter().apply(context),
                                                                                  v3.converter().apply(context),
                                                                                  v4.converter().apply(context),
                                                                                  v5.converter().apply(context),
                                                                                  v6.converter().apply(context)))
                            .flatMap(tuple -> tuple.map(validator))
                            .mapSuccess(params -> params.map(handler)));
        }

        public ParameterBuilder6<T1, T2, T3, T4, T5, T6> and(final FN6<Either<? extends BaseError, Tuple6<T1, T2, T3, T4, T5, T6>>, T1, T2, T3, T4, T5, T6> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class ParameterBuilder7<T1, T2, T3, T4, T5, T6, T7> {
        private final Path path;
        private final String description;
        private final Tuple7<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>, P<T7>> parameters;
        private FN7<Either<? extends BaseError, Tuple7<T1, T2, T3, T4, T5, T6, T7>>, T1, T2, T3, T4, T5, T6, T7> validator = (param1, param2, param3, param4, param5, param6, param7) -> Either.success(Tuples.of(param1, param2, param3, param4, param5, param6, param7));

        private ParameterBuilder7(final Path path,
                                  final String description,
                                  final Tuple7<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>, P<T7>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN7<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5, T6, T7> handler) {
            return RouteEnricher.of(path,
                    context -> parameters.map((v1, v2, v3, v4, v5, v6, v7) -> squeeze(v1.converter().apply(context),
                                                                                             v2.converter().apply(context),
                                                                                             v3.converter().apply(context),
                                                                                             v4.converter().apply(context),
                                                                                             v5.converter().apply(context),
                                                                                             v6.converter().apply(context),
                                                                                             v7.converter().apply(context)))
                            .flatMap(tuple -> tuple.map(validator))
                            .mapSuccess(params -> params.map(handler)));
        }

        public ParameterBuilder7<T1, T2, T3, T4, T5, T6, T7> and(final FN7<Either<? extends BaseError, Tuple7<T1, T2, T3, T4, T5, T6, T7>>, T1, T2, T3, T4, T5, T6, T7> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class ParameterBuilder8<T1, T2, T3, T4, T5, T6, T7, T8> {
        private final Path path;
        private final String description;
        private final Tuple8<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>, P<T7>, P<T8>> parameters;
        private FN8<Either<? extends BaseError, Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>>, T1, T2, T3, T4, T5, T6, T7, T8> validator = (param1,
                                                                                                                                      param2,
                                                                                                                                      param3,
                                                                                                                                      param4,
                                                                                                                                      param5,
                                                                                                                                      param6,
                                                                                                                                      param7,
                                                                                                                                      param8) -> Either.success(Tuples.of(param1,
                                                                                                                                                                          param2,
                                                                                                                                                                          param3,
                                                                                                                                                                          param4,
                                                                                                                                                                          param5,
                                                                                                                                                                          param6,
                                                                                                                                                                          param7,
                                                                                                                                                                          param8));

        private ParameterBuilder8(final Path path,
                                  final String description,
                                  final Tuple8<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>, P<T7>, P<T8>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN8<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5, T6, T7, T8> handler) {
            return RouteEnricher.of(path,
                    context -> parameters.map((v1, v2, v3, v4, v5, v6, v7, v8) -> squeeze(v1.converter().apply(context),
                                                                                          v2.converter().apply(context),
                                                                                          v3.converter().apply(context),
                                                                                          v4.converter().apply(context),
                                                                                          v5.converter().apply(context),
                                                                                          v6.converter().apply(context),
                                                                                          v7.converter().apply(context),
                                                                                          v8.converter().apply(context)))
                            .flatMap(tuple -> tuple.map(validator))
                            .mapSuccess(params -> params.map(handler)));
        }

        public ParameterBuilder8<T1, T2, T3, T4, T5, T6, T7, T8> and(final FN8<Either<? extends BaseError, Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>>, T1, T2, T3, T4, T5, T6, T7, T8> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class ParameterBuilder9<T1, T2, T3, T4, T5, T6, T7, T8, T9> {
        private final Path path;
        private final String description;
        private final Tuple9<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>, P<T7>, P<T8>, P<T9>> parameters;
        private FN9<Either<? extends BaseError, Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>, T1, T2, T3, T4, T5, T6, T7, T8, T9> validator = (param1,
                                                                                                                                              param2,
                                                                                                                                              param3,
                                                                                                                                              param4,
                                                                                                                                              param5,
                                                                                                                                              param6,
                                                                                                                                              param7,
                                                                                                                                              param8,
                                                                                                                                              param9) -> Either.success(Tuples.of(param1,
                                                                                                                                                                                  param2,
                                                                                                                                                                                  param3,
                                                                                                                                                                                  param4,
                                                                                                                                                                                  param5,
                                                                                                                                                                                  param6,
                                                                                                                                                                                  param7,
                                                                                                                                                                                  param8,
                                                                                                                                                                                  param9));

        private ParameterBuilder9(final Path path,
                                  final String description,
                                  final Tuple9<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>, P<T7>, P<T8>, P<T9>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN9<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5, T6, T7, T8, T9> handler) {
            return RouteEnricher.of(path,
                    context -> parameters.map((v1, v2, v3, v4, v5, v6, v7, v8, v9) -> squeeze(v1.converter().apply(context),
                                                                                              v2.converter().apply(context),
                                                                                              v3.converter().apply(context),
                                                                                              v4.converter().apply(context),
                                                                                              v5.converter().apply(context),
                                                                                              v6.converter().apply(context),
                                                                                              v7.converter().apply(context),
                                                                                              v8.converter().apply(context),
                                                                                              v9.converter().apply(context)))
                            .flatMap(tuple -> tuple.map(validator))
                            .mapSuccess(params -> params.map(handler)));
        }

        public ParameterBuilder9<T1, T2, T3, T4, T5, T6, T7, T8, T9> and(final FN9<Either<? extends BaseError, Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>, T1, T2, T3, T4, T5, T6, T7, T8, T9> validator) {
            this.validator = validator;
            return this;
        }
    }
}

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
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.core.functional.Tuples;
import org.reactivetoolbox.core.functional.Tuples.Tuple;
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
import org.reactivetoolbox.eventbus.RouteDescription;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.parameter.ParameterDescription;
import org.reactivetoolbox.web.server.parameter.Parameters.P;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.reactivetoolbox.core.functional.Tuples.zip;

/*
 * Copyright (c) 2017-2019 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Assembling flow for parameter collection and mapping to request handler.
 */
public class HttpParameterAssembler {
    /**
     * Prepare assembling of zero-parameter route
     *
     * @param path
     *        Route path
     * @param description
     *        Route description
     * @return route prepared for attaching handler
     */
    public static PB0 of(final Path path, final String description) {
        return new PB0(path, description);
    }

    /**
     * Prepare assembling of route with single parameter
     *
     * @param path
     *        Route path
     * @param description
     *        Route description
     * @param param1
     *        Parameter definition for parameter #1
     * @return route prepared for attaching handler
     */
    public static <T1> PB1<T1> of(final Path path,
                                  final String description,
                                  final P<T1> param1) {
        return new PB1<>(path, description, Tuples.of(param1));
    }

    /**
     * Prepare assembling of route with 2 parameters
     *
     * @param path
     *        Route path
     * @param description
     *        Route description
     * @param param1
     *        Parameter definition for parameter #1
     * @param param2
     *        Parameter definition for parameter #2
     * @return route prepared for attaching handler
     */
    public static <T1, T2> PB2<T1, T2> of(final Path path,
                                          final String description,
                                          final P<T1> param1,
                                          final P<T2> param2) {
        return new PB2<>(path, description, Tuples.of(param1, param2));
    }

    /**
     * Prepare assembling of route with 3 parameters
     *
     * @param path
     *        Route path
     * @param description
     *        Route description
     * @param param1
     *        Parameter definition for parameter #1
     * @param param2
     *        Parameter definition for parameter #2
     * @param param3
     *        Parameter definition for parameter #3
     * @return route prepared for attaching handler
     */
    public static <T1, T2, T3> PB3<T1, T2, T3> of(final Path path,
                                                  final String description,
                                                  final P<T1> param1,
                                                  final P<T2> param2,
                                                  final P<T3> param3) {
        return new PB3<>(path, description, Tuples.of(param1, param2, param3));
    }

    /**
     * Prepare assembling of route with 4 parameters
     *
     * @param path
     *        Route path
     * @param description
     *        Route description
     * @param param1
     *        Parameter definition for parameter #1
     * @param param2
     *        Parameter definition for parameter #2
     * @param param3
     *        Parameter definition for parameter #3
     * @param param4
     *        Parameter definition for parameter #4
     * @return route prepared for attaching handler
     */
    public static <T1, T2, T3, T4> PB4<T1, T2, T3, T4> of(final Path path,
                                                          final String description,
                                                          final P<T1> param1,
                                                          final P<T2> param2,
                                                          final P<T3> param3,
                                                          final P<T4> param4) {
        return new PB4<>(path, description, Tuples.of(param1, param2, param3, param4));
    }

    /**
     * Prepare assembling of route with 5 parameters
     *
     * @param path
     *        Route path
     * @param description
     *        Route description
     * @param param1
     *        Parameter definition for parameter #1
     * @param param2
     *        Parameter definition for parameter #2
     * @param param3
     *        Parameter definition for parameter #3
     * @param param4
     *        Parameter definition for parameter #4
     * @param param5
     *        Parameter definition for parameter #5
     * @return route prepared for attaching handler
     */
    public static <T1, T2, T3, T4, T5> PB5<T1, T2, T3, T4, T5> of(final Path path,
                                                                  final String description,
                                                                  final P<T1> param1,
                                                                  final P<T2> param2,
                                                                  final P<T3> param3,
                                                                  final P<T4> param4,
                                                                  final P<T5> param5) {
        return new PB5<>(path, description, Tuples.of(param1, param2, param3, param4, param5));
    }

    /**
     * Prepare assembling of route with 6 parameters
     *
     * @param path
     *        Route path
     * @param description
     *        Route description
     * @param param1
     *        Parameter definition for parameter #1
     * @param param2
     *        Parameter definition for parameter #2
     * @param param3
     *        Parameter definition for parameter #3
     * @param param4
     *        Parameter definition for parameter #4
     * @param param5
     *        Parameter definition for parameter #5
     * @param param6
     *        Parameter definition for parameter #6
     * @return route prepared for attaching handler
     */
    public static <T1, T2, T3, T4, T5, T6> PB6<T1, T2, T3, T4, T5, T6> of(final Path path,
                                                                          final String description,
                                                                          final P<T1> param1,
                                                                          final P<T2> param2,
                                                                          final P<T3> param3,
                                                                          final P<T4> param4,
                                                                          final P<T5> param5,
                                                                          final P<T6> param6) {
        return new PB6<>(path, description, Tuples.of(param1, param2, param3, param4, param5, param6));
    }

    /**
     * Prepare assembling of route with 7 parameters
     *
     * @param path
     *        Route path
     * @param description
     *        Route description
     * @param param1
     *        Parameter definition for parameter #1
     * @param param2
     *        Parameter definition for parameter #2
     * @param param3
     *        Parameter definition for parameter #3
     * @param param4
     *        Parameter definition for parameter #4
     * @param param5
     *        Parameter definition for parameter #5
     * @param param6
     *        Parameter definition for parameter #6
     * @param param7
     *        Parameter definition for parameter #7
     * @return route prepared for attaching handler
     */
    public static <T1, T2, T3, T4, T5, T6, T7> PB7<T1, T2, T3, T4, T5, T6, T7> of(final Path path,
                                                                                  final String description,
                                                                                  final P<T1> param1,
                                                                                  final P<T2> param2,
                                                                                  final P<T3> param3,
                                                                                  final P<T4> param4,
                                                                                  final P<T5> param5,
                                                                                  final P<T6> param6,
                                                                                  final P<T7> param7) {
        return new PB7<>(path, description, Tuples.of(param1, param2, param3, param4, param5, param6, param7));
    }

    /**
     * Prepare assembling of route with 8 parameters
     *
     * @param path
     *        Route path
     * @param description
     *        Route description
     * @param param1
     *        Parameter definition for parameter #1
     * @param param2
     *        Parameter definition for parameter #2
     * @param param3
     *        Parameter definition for parameter #3
     * @param param4
     *        Parameter definition for parameter #4
     * @param param5
     *        Parameter definition for parameter #5
     * @param param6
     *        Parameter definition for parameter #6
     * @param param7
     *        Parameter definition for parameter #7
     * @param param8
     *        Parameter definition for parameter #8
     * @return route prepared for attaching handler
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8> PB8<T1, T2, T3, T4, T5, T6, T7, T8> of(final Path path,
                                                                                          final String description,
                                                                                          final P<T1> param1,
                                                                                          final P<T2> param2,
                                                                                          final P<T3> param3,
                                                                                          final P<T4> param4,
                                                                                          final P<T5> param5,
                                                                                          final P<T6> param6,
                                                                                          final P<T7> param7,
                                                                                          final P<T8> param8) {
        return new PB8<>(path, description, Tuples.of(param1, param2, param3, param4, param5, param6, param7, param8));
    }

    /**
     * Prepare assembling of route with 9 parameters
     *
     * @param path
     *        Route path
     * @param description
     *        Route description
     * @param param1
     *        Parameter definition for parameter #1
     * @param param2
     *        Parameter definition for parameter #2
     * @param param3
     *        Parameter definition for parameter #3
     * @param param4
     *        Parameter definition for parameter #4
     * @param param5
     *        Parameter definition for parameter #5
     * @param param6
     *        Parameter definition for parameter #6
     * @param param7
     *        Parameter definition for parameter #7
     * @param param8
     *        Parameter definition for parameter #8
     * @param param9
     *        Parameter definition for parameter #9
     * @return route prepared for attaching handler
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> PB9<T1, T2, T3, T4, T5, T6, T7, T8, T9> of(final Path path,
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
        return new PB9<>(path, description, Tuples.of(param1, param2, param3, param4, param5, param6, param7, param8, param9));
    }

    private static List<Option<ParameterDescription>> describe(final Tuple parameters) {
        final Stream<Option<ParameterDescription>> stream = parameters.<P>stream().map(P::description);
        return stream.collect(Collectors.toList());
    }

    public static class PB0 {
        private final Path path;
        private final String description;

        private PB0(final Path path, final String description) {
            this.path = path;
            this.description = description;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN0<Promise<Either<? extends BaseError, R>>> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, Collections.emptyList());

            return RouteEnricher.of(path, routeDescription, ignored -> Either.success(handler.apply()));
        }
    }

    public static class PB1<T1> {
        private final Path path;
        private final String description;
        private final Tuple1<P<T1>> parameters;

        private PB1(final Path path, final String description, final Tuple1<P<T1>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN1<Promise<Either<? extends BaseError, R>>, T1> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path, routeDescription,
                                    context -> parameters.map(v1 -> Tuples.zip(v1.converter().apply(context)))
                                                         .flatMap(Either::<BaseError, Tuple1<T1>>success)
                                                         .mapSuccess(params -> params.map(handler)));
        }
    }

    public static class PB2<T1, T2> {
        private final Path path;
        private final String description;
        private final Tuple2<P<T1>, P<T2>> parameters;
        private FN2<Either<? extends BaseError, Tuple2<T1, T2>>, T1, T2> validator =
            (param1, param2) -> Either.success(Tuples.of(param1, param2));

        private PB2(final Path path,
                    final String description,
                    final Tuple2<P<T1>, P<T2>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN2<Promise<Either<? extends BaseError, R>>, T1, T2> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path, routeDescription,
                                    context -> parameters.map((v1, v2) -> Tuples.zip(v1.converter().apply(context),
                                                                                     v2.converter().apply(context)))
                                                         .flatMap(tuple -> tuple.map(validator))
                                                         .mapSuccess(params -> params.map(handler)));
        }

        public PB2<T1, T2> and(final FN2<Either<? extends BaseError, Tuple2<T1, T2>>, T1, T2> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class PB3<T1, T2, T3> {
        private final Path path;
        private final String description;
        private final Tuple3<P<T1>, P<T2>, P<T3>> parameters;
        private FN3<Either<? extends BaseError, Tuple3<T1, T2, T3>>, T1, T2, T3> validator = (param1,
                                                                                              param2,
                                                                                              param3) -> Either.success(Tuples.of(param1,
                                                                                                                                  param2,
                                                                                                                                  param3));

        private PB3(final Path path,
                    final String description,
                    final Tuple3<P<T1>, P<T2>, P<T3>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN3<Promise<Either<? extends BaseError, R>>, T1, T2, T3> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path, routeDescription,
                                    context -> parameters.map((v1, v2, v3) -> Tuples.zip(v1.converter().apply(context),
                                                                                         v2.converter().apply(context),
                                                                                         v3.converter().apply(context)))
                                                         .flatMap(tuple -> tuple.map(validator))
                                                         .mapSuccess(params -> params.map(handler)));
        }

        public PB3<T1, T2, T3> and(final FN3<Either<? extends BaseError, Tuple3<T1, T2, T3>>, T1, T2, T3> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class PB4<T1, T2, T3, T4> {
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

        private PB4(final Path path,
                    final String description,
                    final Tuple4<P<T1>, P<T2>, P<T3>, P<T4>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN4<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path, routeDescription,
                                    context -> parameters.map((v1, v2, v3, v4) -> Tuples.zip(v1.converter().apply(context),
                                                                                             v2.converter().apply(context),
                                                                                             v3.converter().apply(context),
                                                                                             v4.converter().apply(context)))
                                                         .flatMap(tuple -> tuple.map(validator))
                                                         .mapSuccess(params -> params.map(handler)));
        }

        public PB4<T1, T2, T3, T4> and(final FN4<Either<? extends BaseError, Tuple4<T1, T2, T3, T4>>, T1, T2, T3, T4> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class PB5<T1, T2, T3, T4, T5> {
        private final Path path;
        private final String description;
        private final Tuple5<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>> parameters;
        private FN5<Either<? extends BaseError, Tuple5<T1, T2, T3, T4, T5>>, T1, T2, T3, T4, T5> validator = (param1,
                                                                                                              param2,
                                                                                                              param3,
                                                                                                              param4,
                                                                                                              param5) -> Either.success(Tuples.of(
            param1,
            param2,
            param3,
            param4,
            param5));

        private PB5(final Path path,
                    final String description,
                    final Tuple5<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN5<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path, routeDescription,
                                    context -> parameters.map((v1, v2, v3, v4, v5) -> Tuples.zip(v1.converter().apply(context),
                                                                                                 v2.converter().apply(context),
                                                                                                 v3.converter().apply(context),
                                                                                                 v4.converter().apply(context),
                                                                                                 v5.converter().apply(context)))
                                                         .flatMap(tuple -> tuple.map(validator))
                                                         .mapSuccess(params -> params.map(handler)));
        }

        public PB5<T1, T2, T3, T4, T5> and(final FN5<Either<? extends BaseError, Tuple5<T1, T2, T3, T4, T5>>, T1, T2, T3, T4, T5> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class PB6<T1, T2, T3, T4, T5, T6> {
        private final Path path;
        private final String description;
        private final Tuple6<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>> parameters;
        private FN6<Either<? extends BaseError, Tuple6<T1, T2, T3, T4, T5, T6>>, T1, T2, T3, T4, T5, T6> validator = (param1,
                                                                                                                      param2,
                                                                                                                      param3,
                                                                                                                      param4,
                                                                                                                      param5,
                                                                                                                      param6) -> Either.success(Tuples
                                                                                                                                                    .of(param1,
                                                                                                                                                        param2,
                                                                                                                                                        param3,
                                                                                                                                                        param4,
                                                                                                                                                        param5,
                                                                                                                                                        param6));

        private PB6(final Path path,
                    final String description,
                    final Tuple6<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN6<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5, T6> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path, routeDescription,
                                    context -> parameters.map((v1, v2, v3, v4, v5, v6) -> Tuples.zip(v1.converter().apply(context),
                                                                                                     v2.converter().apply(context),
                                                                                                     v3.converter().apply(context),
                                                                                                     v4.converter().apply(context),
                                                                                                     v5.converter().apply(context),
                                                                                                     v6.converter().apply(context)))
                                                         .flatMap(tuple -> tuple.map(validator))
                                                         .mapSuccess(params -> params.map(handler)));
        }

        public PB6<T1, T2, T3, T4, T5, T6> and(final FN6<Either<? extends BaseError, Tuple6<T1, T2, T3, T4, T5, T6>>, T1, T2, T3, T4, T5, T6> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class PB7<T1, T2, T3, T4, T5, T6, T7> {
        private final Path path;
        private final String description;
        private final Tuple7<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>, P<T7>> parameters;
        private FN7<Either<? extends BaseError, Tuple7<T1, T2, T3, T4, T5, T6, T7>>, T1, T2, T3, T4, T5, T6, T7> validator = (param1, param2, param3, param4, param5, param6, param7) -> Either
            .success(Tuples.of(param1, param2, param3, param4, param5, param6, param7));

        private PB7(final Path path,
                    final String description,
                    final Tuple7<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>, P<T7>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN7<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5, T6, T7> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path, routeDescription,
                                    context -> parameters.map((v1, v2, v3, v4, v5, v6, v7) -> Tuples.zip(v1.converter().apply(context),
                                                                                                         v2.converter().apply(context),
                                                                                                         v3.converter().apply(context),
                                                                                                         v4.converter().apply(context),
                                                                                                         v5.converter().apply(context),
                                                                                                         v6.converter().apply(context),
                                                                                                         v7.converter().apply(context)))
                                                         .flatMap(tuple -> tuple.map(validator))
                                                         .mapSuccess(params -> params.map(handler)));
        }

        public PB7<T1, T2, T3, T4, T5, T6, T7> and(final FN7<Either<? extends BaseError, Tuple7<T1, T2, T3, T4, T5, T6, T7>>, T1, T2, T3, T4, T5, T6, T7> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class PB8<T1, T2, T3, T4, T5, T6, T7, T8> {
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
                                                                                                                                      param8) -> Either
            .success(Tuples.of(param1, param2, param3, param4, param5, param6, param7, param8));

        private PB8(final Path path,
                    final String description,
                    final Tuple8<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>, P<T7>, P<T8>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN8<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5, T6, T7, T8> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path, routeDescription,
                                    context -> parameters.map((v1, v2, v3, v4, v5, v6, v7, v8) -> Tuples.zip(v1.converter().apply(context),
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

        public PB8<T1, T2, T3, T4, T5, T6, T7, T8> and(final FN8<Either<? extends BaseError, Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>>, T1, T2, T3, T4, T5, T6, T7, T8> validator) {
            this.validator = validator;
            return this;
        }
    }

    public static class PB9<T1, T2, T3, T4, T5, T6, T7, T8, T9> {
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
                                                                                                                                              param9) -> Either
            .success(Tuples.of(param1, param2, param3, param4, param5, param6, param7, param8, param9));

        private PB9(final Path path,
                    final String description,
                    final Tuple9<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>, P<T7>, P<T8>, P<T9>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final FN9<Promise<Either<? extends BaseError, R>>, T1, T2, T3, T4, T5, T6, T7, T8, T9> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path, routeDescription,
                                    context -> parameters.map((v1, v2, v3, v4, v5, v6, v7, v8, v9) -> zip(v1.converter().apply(context),
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

        public PB9<T1, T2, T3, T4, T5, T6, T7, T8, T9> and(final FN9<Either<? extends BaseError, Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>, T1, T2, T3, T4, T5, T6, T7, T8, T9> validator) {
            this.validator = validator;
            return this;
        }
    }
}

package org.reactivetoolbox.build;

import org.reactivetoolbox.build.Handlers.Handler0;
import org.reactivetoolbox.build.Handlers.Handler1;
import org.reactivetoolbox.build.Handlers.Handler2;
import org.reactivetoolbox.build.Handlers.Handler3;
import org.reactivetoolbox.build.Handlers.Handler4;
import org.reactivetoolbox.build.Handlers.Handler5;
import org.reactivetoolbox.build.Handlers.Handler6;
import org.reactivetoolbox.build.Handlers.Handler7;
import org.reactivetoolbox.build.Handlers.Handler8;
import org.reactivetoolbox.build.Handlers.Handler9;
import org.reactivetoolbox.build.Validators.V2;
import org.reactivetoolbox.build.Validators.V3;
import org.reactivetoolbox.build.Validators.V4;
import org.reactivetoolbox.build.Validators.V5;
import org.reactivetoolbox.build.Validators.V6;
import org.reactivetoolbox.build.Validators.V7;
import org.reactivetoolbox.build.Validators.V8;
import org.reactivetoolbox.build.Validators.V9;
import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
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

import static org.reactivetoolbox.core.functional.Either.success;

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
 * Classes which hold parameter batches during assembling request processing flow
 */
public interface Parameters {

    class PB0 {
        private final Path path;
        private final String description;

        PB0(final Path path, final String description) {
            this.path = path;
            this.description = description;
        }

        public <R> RouteEnricher<R, RequestContext> then(final Handler0<R> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, Collections.emptyList());

            return RouteEnricher.of(path, routeDescription, ignored -> success(handler.apply()));
        }
    }

    class PB1<T1> {
        private final Path path;
        private final String description;
        private final Tuple1<P<T1>> parameters;

        PB1(final Path path, final String description, final Tuple1<P<T1>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final Handler1<R, T1> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path, routeDescription,
                                    context -> parameters.map(v1 -> Tuples.zip(v1.converter().apply(context)))
                                                         .flatMap(Either::<BaseError, Tuple1<T1>>success)
                                                         .mapSuccess(params -> params.map(handler)));
        }
    }

    class PB2<T1, T2> {
        private final Path path;
        private final String description;
        private final Tuple2<P<T1>, P<T2>> parameters;
        private V2<T1, T2> validator = HttpRouteTools::valid;

        PB2(final Path path,
            final String description,
            final Tuple2<P<T1>, P<T2>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final Handler2<R, T1, T2> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path,
                                    routeDescription,
                                    context -> convertAndValidate(context).mapSuccess(params -> params.map(handler)));
        }

        private Either<BaseError, Tuple2<T1, T2>> convertAndValidate(final RequestContext context) {
            return parameters.map((v1, v2) -> Tuples.zip(v1.converter().apply(context),
                                                         v2.converter().apply(context)))
                             .flatMap(tuple -> tuple.map(validator));
        }

        public PB2<T1, T2> and(final V2<T1, T2> validator) {
            this.validator = validator;
            return this;
        }
    }

    class PB3<T1, T2, T3> {
        private final Path path;
        private final String description;
        private final Tuple3<P<T1>, P<T2>, P<T3>> parameters;
        private V3<T1, T2, T3> validator = HttpRouteTools::valid;

        PB3(final Path path,
            final String description,
            final Tuple3<P<T1>, P<T2>, P<T3>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final Handler3<R, T1, T2, T3> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path,
                                    routeDescription,
                                    context -> convertAndValidate(context).mapSuccess(params -> params.map(handler)));
        }

        private Either<BaseError, Tuple3<T1, T2, T3>> convertAndValidate(final RequestContext context) {
            return parameters.map((v1, v2, v3) -> Tuples.zip(v1.converter().apply(context),
                                                             v2.converter().apply(context),
                                                             v3.converter().apply(context)))
                             .flatMap(tuple -> tuple.map(validator));
        }

        public PB3<T1, T2, T3> and(final V3<T1, T2, T3> validator) {
            this.validator = validator;
            return this;
        }
    }

    class PB4<T1, T2, T3, T4> {
        private final Path path;
        private final String description;
        private final Tuple4<P<T1>, P<T2>, P<T3>, P<T4>> parameters;
        private V4<T1, T2, T3, T4> validator = HttpRouteTools::valid;

        PB4(final Path path,
            final String description,
            final Tuple4<P<T1>, P<T2>, P<T3>, P<T4>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final Handler4<R, T1, T2, T3, T4> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path,
                                    routeDescription,
                                    context -> convertAndValidate(context).mapSuccess(params -> params.map(handler)));
        }

        private Either<BaseError, Tuple4<T1, T2, T3, T4>> convertAndValidate(final RequestContext context) {
            return parameters.map((v1, v2, v3, v4) -> Tuples.zip(v1.converter().apply(context),
                                                                 v2.converter().apply(context),
                                                                 v3.converter().apply(context),
                                                                 v4.converter().apply(context)))
                             .flatMap(tuple -> tuple.map(validator));
        }

        public PB4<T1, T2, T3, T4> and(final V4<T1, T2, T3, T4> validator) {
            this.validator = validator;
            return this;
        }
    }

    class PB5<T1, T2, T3, T4, T5> {
        private final Path path;
        private final String description;
        private final Tuple5<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>> parameters;
        private V5<T1, T2, T3, T4, T5> validator = HttpRouteTools::valid;

        PB5(final Path path,
            final String description,
            final Tuple5<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final Handler5<R, T1, T2, T3, T4, T5> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path,
                                    routeDescription,
                                    context -> convertAndValidate(context).mapSuccess(params -> params.map(handler)));
        }

        private Either<BaseError, Tuple5<T1, T2, T3, T4, T5>> convertAndValidate(final RequestContext context) {
            return parameters.map((v1, v2, v3, v4, v5) -> Tuples.zip(v1.converter().apply(context),
                                                                     v2.converter().apply(context),
                                                                     v3.converter().apply(context),
                                                                     v4.converter().apply(context),
                                                                     v5.converter().apply(context)))
                             .flatMap(tuple -> tuple.map(validator));
        }

        public PB5<T1, T2, T3, T4, T5> and(final V5<T1, T2, T3, T4, T5> validator) {
            this.validator = validator;
            return this;
        }
    }

    class PB6<T1, T2, T3, T4, T5, T6> {
        private final Path path;
        private final String description;
        private final Tuple6<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>> parameters;
        private V6<T1, T2, T3, T4, T5, T6> validator = HttpRouteTools::valid;

        PB6(final Path path,
            final String description,
            final Tuple6<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final Handler6<R, T1, T2, T3, T4, T5, T6> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path,
                                    routeDescription,
                                    context -> convertAndValidate(context).mapSuccess(params -> params.map(handler)));
        }

        private Either<BaseError, Tuple6<T1, T2, T3, T4, T5, T6>> convertAndValidate(final RequestContext context) {
            return parameters.map((v1, v2, v3, v4, v5, v6) -> Tuples.zip(v1.converter().apply(context),
                                                                         v2.converter().apply(context),
                                                                         v3.converter().apply(context),
                                                                         v4.converter().apply(context),
                                                                         v5.converter().apply(context),
                                                                         v6.converter().apply(context)))
                             .flatMap(tuple -> tuple.map(validator));
        }

        public PB6<T1, T2, T3, T4, T5, T6> and(final V6<T1, T2, T3, T4, T5, T6> validator) {
            this.validator = validator;
            return this;
        }
    }

    class PB7<T1, T2, T3, T4, T5, T6, T7> {
        private final Path path;
        private final String description;
        private final Tuple7<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>, P<T7>> parameters;
        private V7<T1, T2, T3, T4, T5, T6, T7> validator = HttpRouteTools::valid;

        PB7(final Path path,
            final String description,
            final Tuple7<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>, P<T7>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final Handler7<R, T1, T2, T3, T4, T5, T6, T7> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path,
                                    routeDescription,
                                    context -> convertAndValidate(context).mapSuccess(params -> params.map(handler)));
        }

        private Either<BaseError, Tuple7<T1, T2, T3, T4, T5, T6, T7>> convertAndValidate(final RequestContext context) {
            return parameters.map((v1, v2, v3, v4, v5, v6, v7) -> Tuples.zip(v1.converter().apply(context),
                                                                             v2.converter().apply(context),
                                                                             v3.converter().apply(context),
                                                                             v4.converter().apply(context),
                                                                             v5.converter().apply(context),
                                                                             v6.converter().apply(context),
                                                                             v7.converter().apply(context)))
                             .flatMap(tuple -> tuple.map(validator));
        }

        public PB7<T1, T2, T3, T4, T5, T6, T7> and(final V7<T1, T2, T3, T4, T5, T6, T7> validator) {
            this.validator = validator;
            return this;
        }
    }

    class PB8<T1, T2, T3, T4, T5, T6, T7, T8> {
        private final Path path;
        private final String description;
        private final Tuple8<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>, P<T7>, P<T8>> parameters;
        private V8<T1, T2, T3, T4, T5, T6, T7, T8> validator = HttpRouteTools::valid;

        PB8(final Path path,
            final String description,
            final Tuple8<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>, P<T7>, P<T8>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final Handler8<R, T1, T2, T3, T4, T5, T6, T7, T8> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path,
                                    routeDescription,
                                    context -> convertAndValidate(context).mapSuccess(params -> params.map(handler)));
        }

        private Either<BaseError, Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> convertAndValidate(final RequestContext context) {
            return parameters.map((v1, v2, v3, v4, v5, v6, v7, v8) -> Tuples.zip(v1.converter().apply(context),
                                                                                 v2.converter().apply(context),
                                                                                 v3.converter().apply(context),
                                                                                 v4.converter().apply(context),
                                                                                 v5.converter().apply(context),
                                                                                 v6.converter().apply(context),
                                                                                 v7.converter().apply(context),
                                                                                 v8.converter().apply(context)))
                             .flatMap(tuple -> tuple.map(validator));
        }

        public PB8<T1, T2, T3, T4, T5, T6, T7, T8> and(final V8<T1, T2, T3, T4, T5, T6, T7, T8> validator) {
            this.validator = validator;
            return this;
        }
    }

    class PB9<T1, T2, T3, T4, T5, T6, T7, T8, T9> {
        private final Path path;
        private final String description;
        private final Tuple9<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>, P<T7>, P<T8>, P<T9>> parameters;
        private V9<T1, T2, T3, T4, T5, T6, T7, T8, T9> validator = HttpRouteTools::valid;

        PB9(final Path path,
            final String description,
            final Tuple9<P<T1>, P<T2>, P<T3>, P<T4>, P<T5>, P<T6>, P<T7>, P<T8>, P<T9>> parameters) {
            this.path = path;
            this.description = description;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, RequestContext> then(final Handler9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> handler) {
            final RouteDescription routeDescription = HttpRouteDescription.of(path, description, describe(parameters));

            return RouteEnricher.of(path,
                                    routeDescription,
                                    context -> convertAndValidate(context).mapSuccess(params -> params.map(handler)));
        }

        private Either<BaseError, Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> convertAndValidate(final RequestContext context) {
            return parameters.map((v1, v2, v3, v4, v5, v6, v7, v8, v9) -> Tuples.zip(v1.converter().apply(context),
                                                                                     v2.converter().apply(context),
                                                                                     v3.converter().apply(context),
                                                                                     v4.converter().apply(context),
                                                                                     v5.converter().apply(context),
                                                                                     v6.converter().apply(context),
                                                                                     v7.converter().apply(context),
                                                                                     v8.converter().apply(context),
                                                                                     v9.converter().apply(context)))
                             .flatMap(tuple -> tuple.map(validator));
        }

        public PB9<T1, T2, T3, T4, T5, T6, T7, T8, T9> and(final V9<T1, T2, T3, T4, T5, T6, T7, T8, T9> validator) {
            this.validator = validator;
            return this;
        }
    }

    private static List<Option<ParameterDescription>> describe(final Tuple parameters) {
        final Stream<Option<ParameterDescription>> stream = parameters.<P>stream().map(P::description);
        return stream.collect(Collectors.toList());
    }
}

package org.reactivetoolbox.web.server.http;

import org.reactivetoolbox.build.DescribedPath;
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
import org.reactivetoolbox.build.HttpRouteDescription;
import org.reactivetoolbox.build.RouteEnricher;
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
import org.reactivetoolbox.value.conversion.Extractors;
import org.reactivetoolbox.value.conversion.var.Var;
import org.reactivetoolbox.value.conversion.var.VarDescription;

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
public interface HttpParameters {
    class PB0 {
        private final DescribedPath describedPath;

        PB0(final DescribedPath describedPath) {
            this.describedPath = describedPath;
        }

        public <R> RouteEnricher<R, HttpProcessingContext> then(final Handler0<R> handler) {
            return RouteEnricher.of(HttpRouteDescription.of(describedPath, Collections.emptyList()),
                                    ignored -> success(handler.apply()));
        }
    }

    class PB1<T1> {
        private final DescribedPath describedPath;
        private final Tuple1<Var<T1>> parameters;

        PB1(final DescribedPath describedPath, final Tuple1<Var<T1>> parameters) {
            this.describedPath = describedPath;
            this.parameters = parameters;
        }

        public <R> RouteEnricher<R, HttpProcessingContext> then(final Handler1<R, T1> handler) {
            return RouteEnricher.of(HttpRouteDescription.of(describedPath, describe(parameters)),
                                    context -> parameters.map(Extractors.extract1(context))
                                                         .flatMap(Either::<BaseError, Tuple1<T1>>success)
                                                         .mapSuccess(handler::forTuple));
        }
    }

    class PB2<T1, T2> {
        private final DescribedPath describedPath;
        private final Tuple2<Var<T1>, Var<T2>> parameters;
        private final V2<T1, T2> validator;

        PB2(final DescribedPath describedPath, final Tuple2<Var<T1>, Var<T2>> parameters, final V2<T1, T2> validator) {
            this.describedPath = describedPath;
            this.parameters = parameters;
            this.validator = validator;
        }

        public <R> RouteEnricher<R, HttpProcessingContext> then(final Handler2<R, T1, T2> handler) {


            return RouteEnricher.of(HttpRouteDescription.of(describedPath, describe(parameters)),
                                    context -> parameters.map(Extractors.extract2(context))
                                                         .flatMap(validator::forTuple)
                                                         .mapSuccess(handler::forTuple));
        }

        public PB2<T1, T2> and(final V2<T1, T2> validator) {
            return new PB2<>(describedPath, parameters, validator);
        }
    }

    class PB3<T1, T2, T3> {
        private final DescribedPath describedPath;
        private final Tuple3<Var<T1>, Var<T2>, Var<T3>> parameters;
        private final V3<T1, T2, T3> validator;

        PB3(final DescribedPath describedPath,
            final Tuple3<Var<T1>, Var<T2>, Var<T3>> parameters,
            final V3<T1, T2, T3> validator) {
            this.describedPath = describedPath;
            this.parameters = parameters;
            this.validator = validator;
        }

        public <R> RouteEnricher<R, HttpProcessingContext> then(final Handler3<R, T1, T2, T3> handler) {
            return RouteEnricher.of(HttpRouteDescription.of(describedPath, describe(parameters)),
                                    context -> parameters.map(Extractors.extract3(context))
                                                         .flatMap(validator::forTuple)
                                                         .mapSuccess(handler::forTuple));
        }

        public PB3<T1, T2, T3> and(final V3<T1, T2, T3> validator) {
            return new PB3<>(describedPath, parameters, validator);
        }
    }

    class PB4<T1, T2, T3, T4> {
        private final DescribedPath describedPath;
        private final Tuple4<Var<T1>, Var<T2>, Var<T3>, Var<T4>> parameters;
        private final V4<T1, T2, T3, T4> validator;

        PB4(final DescribedPath describedPath,
            final Tuple4<Var<T1>, Var<T2>, Var<T3>, Var<T4>> parameters,
            final V4<T1, T2, T3, T4> validator) {
            this.describedPath = describedPath;
            this.parameters = parameters;
            this.validator = validator;
        }

        public <R> RouteEnricher<R, HttpProcessingContext> then(final Handler4<R, T1, T2, T3, T4> handler) {
            return RouteEnricher.of(HttpRouteDescription.of(describedPath, describe(parameters)),
                                    context -> parameters.map(Extractors.extract4(context))
                                                         .flatMap(validator::forTuple)
                                                         .mapSuccess(handler::forTuple));
        }

        public PB4<T1, T2, T3, T4> and(final V4<T1, T2, T3, T4> validator) {
            return new PB4<>(describedPath, parameters, validator);
        }
    }

    class PB5<T1, T2, T3, T4, T5> {
        private final DescribedPath describedPath;
        private final Tuple5<Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>> parameters;
        private final V5<T1, T2, T3, T4, T5> validator;

        PB5(final DescribedPath describedPath,
            final Tuple5<Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>> parameters,
            final V5<T1, T2, T3, T4, T5> validator) {
            this.describedPath = describedPath;
            this.parameters = parameters;
            this.validator = validator;
        }

        public <R> RouteEnricher<R, HttpProcessingContext> then(final Handler5<R, T1, T2, T3, T4, T5> handler) {
            return RouteEnricher.of(HttpRouteDescription.of(describedPath, describe(parameters)),
                                    context -> parameters.map(Extractors.extract5(context))
                                                         .flatMap(validator::forTuple)
                                                         .mapSuccess(handler::forTuple));
        }

        public PB5<T1, T2, T3, T4, T5> and(final V5<T1, T2, T3, T4, T5> validator) {
            return new PB5<>(describedPath, parameters, validator);
        }
    }

    class PB6<T1, T2, T3, T4, T5, T6> {
        private final DescribedPath describedPath;
        private final Tuple6<Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>, Var<T6>> parameters;
        private final V6<T1, T2, T3, T4, T5, T6> validator;

        PB6(final DescribedPath describedPath,
            final Tuple6<Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>, Var<T6>> parameters,
            final V6<T1, T2, T3, T4, T5, T6> validator) {
            this.describedPath = describedPath;
            this.parameters = parameters;
            this.validator = validator;
        }

        public <R> RouteEnricher<R, HttpProcessingContext> then(final Handler6<R, T1, T2, T3, T4, T5, T6> handler) {
            return RouteEnricher.of(HttpRouteDescription.of(describedPath, describe(parameters)),
                                    context -> parameters.map(Extractors.extract6(context))
                                                         .flatMap(validator::forTuple)
                                                         .mapSuccess(handler::forTuple));
        }

        public PB6<T1, T2, T3, T4, T5, T6> and(final V6<T1, T2, T3, T4, T5, T6> validator) {
            return new PB6<>(describedPath, parameters, validator);
        }
    }

    class PB7<T1, T2, T3, T4, T5, T6, T7> {
        private final DescribedPath describedPath;
        private final Tuple7<Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>, Var<T6>, Var<T7>> parameters;
        private final V7<T1, T2, T3, T4, T5, T6, T7> validator;

        PB7(final DescribedPath describedPath,
            final Tuple7<Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>, Var<T6>, Var<T7>> parameters,
            final V7<T1, T2, T3, T4, T5, T6, T7> validator) {
            this.describedPath = describedPath;
            this.parameters = parameters;
            this.validator = validator;
        }

        public <R> RouteEnricher<R, HttpProcessingContext> then(final Handler7<R, T1, T2, T3, T4, T5, T6, T7> handler) {
            return RouteEnricher.of(HttpRouteDescription.of(describedPath, describe(parameters)),
                                    context -> parameters.map(Extractors.extract7(context))
                                                         .flatMap(validator::forTuple)
                                                         .mapSuccess(handler::forTuple));
        }

        public PB7<T1, T2, T3, T4, T5, T6, T7> and(final V7<T1, T2, T3, T4, T5, T6, T7> validator) {
            return new PB7<>(describedPath, parameters, validator);
        }
    }

    class PB8<T1, T2, T3, T4, T5, T6, T7, T8> {
        private final DescribedPath describedPath;
        private final Tuple8<Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>, Var<T6>, Var<T7>, Var<T8>> parameters;
        private final V8<T1, T2, T3, T4, T5, T6, T7, T8> validator;

        PB8(final DescribedPath describedPath,
            final Tuple8<Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>, Var<T6>, Var<T7>, Var<T8>> parameters,
            final V8<T1, T2, T3, T4, T5, T6, T7, T8> validator) {
            this.describedPath = describedPath;
            this.parameters = parameters;
            this.validator = validator;
        }

        public <R> RouteEnricher<R, HttpProcessingContext> then(final Handler8<R, T1, T2, T3, T4, T5, T6, T7, T8> handler) {
            return RouteEnricher.of(HttpRouteDescription.of(describedPath, describe(parameters)),
                                    context -> parameters.map(Extractors.extract8(context))
                                                         .flatMap(validator::forTuple)
                                                         .mapSuccess(handler::forTuple));
        }

        public PB8<T1, T2, T3, T4, T5, T6, T7, T8> and(final V8<T1, T2, T3, T4, T5, T6, T7, T8> validator) {
            return new PB8<>(describedPath, parameters, validator);
        }
    }

    class PB9<T1, T2, T3, T4, T5, T6, T7, T8, T9> {
        private final DescribedPath describedPath;
        private final Tuple9<Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>, Var<T6>, Var<T7>, Var<T8>, Var<T9>> parameters;
        private final V9<T1, T2, T3, T4, T5, T6, T7, T8, T9> validator;

        PB9(final DescribedPath describedPath,
            final Tuple9<Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>, Var<T6>, Var<T7>, Var<T8>, Var<T9>> parameters,
            final V9<T1, T2, T3, T4, T5, T6, T7, T8, T9> validator) {
            this.describedPath = describedPath;
            this.parameters = parameters;
            this.validator = validator;
        }

        public <R> RouteEnricher<R, HttpProcessingContext> then(final Handler9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> handler) {
            return RouteEnricher.of(HttpRouteDescription.of(describedPath, describe(parameters)),
                                    context -> parameters.map(Extractors.extract9(context))
                                                         .flatMap(validator::forTuple)
                                                         .mapSuccess(handler::forTuple));
        }

        public PB9<T1, T2, T3, T4, T5, T6, T7, T8, T9> and(final V9<T1, T2, T3, T4, T5, T6, T7, T8, T9> validator) {
            return new PB9<>(describedPath, parameters, validator);
        }
    }

    private static List<Option<VarDescription>> describe(final Tuple parameters) {
        final Stream<Option<VarDescription>> stream = parameters.<Var>stream().map(Var::description);
        return stream.collect(Collectors.toList());
    }
}

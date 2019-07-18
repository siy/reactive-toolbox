package org.reactivetoolbox.build;

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

import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.eventbus.Handler;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.Route;
import org.reactivetoolbox.eventbus.RouteBase;
import org.reactivetoolbox.eventbus.RouteDescription;

import java.util.List;
import java.util.stream.Stream;

//TODO: Javadoc
public class RouteEnricher<R, T> implements RouteBase<T> {
    private final Path path;
    private final Handler<R, T> handler;
    private final String methodDescription;
    private final List<String> parameterDescriptions;

    private RouteEnricher(final Path path,
                          final String methodDescription,
                          final List<String> parameterDescriptions,
                          final Handler<R, T> handler) {
        this.path = path;
        this.handler = handler;
        this.methodDescription = methodDescription;
        this.parameterDescriptions = parameterDescriptions;
    }

    public static <R, T> RouteEnricher<R, T> of(final Path path,
                                                final String methodDescription,
                                                final List<String> parameterDescriptions,
                                                final Handler<R, T> handler) {
        return new RouteEnricher<>(path, methodDescription, parameterDescriptions, handler);
    }

    public RouteEnricher<R, T> after(final Enricher<R, T> enricher) {
        return new RouteEnricher<>(path, methodDescription, parameterDescriptions,
                                   context -> handler.apply(context).mapSuccess(result ->enricher.apply(context, result)));
    }

    @Override
    public Stream<Route<T>> asRoute() {
        return Stream.of(Route.of(path, handler));
    }

    @Override
    public Stream<Option<RouteDescription>> descriptions() {
        return Stream.empty();
    }
}

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

import java.util.stream.Stream;

/**
 * HTTP route assembling helper class which allows attaching of post-processing of the handler invocation result.
 * Postprocessing handlers should satisfy {@link Enricher} interface.
 *
 * @param <R>
 * @param <T>
 * @see Enricher
 */
public class RouteEnricher<R, T> implements RouteBase<T> {
    private final Path path;
    private final RouteDescription description;
    private final Handler<R, T> handler;

    private RouteEnricher(final Path path,
                          final RouteDescription description,
                          final Handler<R, T> handler) {
        this.path = path;
        this.description = description;
        this.handler = handler;
    }

    public static <R, T> RouteEnricher<R, T> of(final Path path,
                                                final RouteDescription description,
                                                final Handler<R, T> handler) {
        return new RouteEnricher<>(path, description, handler);
    }

    public RouteEnricher<R, T> after(final Enricher<R, T> enricher) {
        return new RouteEnricher<>(path, description,
                                   context -> handler.apply(context).mapSuccess(result ->enricher.apply(context, result)));
    }

    @Override
    public Stream<Route<T>> stream() {
        return Stream.of(Route.of(path, handler));
    }

    @Override
    public Stream<Option<RouteDescription>> descriptions() {
        return Stream.empty();
    }

    @Override
    public RouteBase<T> root(final Option<String> root) {
        return new RouteEnricher<>(path.root(root), description, handler);
    }
}

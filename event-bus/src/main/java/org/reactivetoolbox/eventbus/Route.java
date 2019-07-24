package org.reactivetoolbox.eventbus;

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

import java.util.stream.Stream;

/**
 * The route in the router. It contains routing path (potentially with pattern) and handler assigned to this route.
 *
 * @param <T>
 * @see {@link Router}
 * @see {@link Envelope}
 * @see {@link RouteBase}
 */
public class Route<T> implements RouteBase<T> {
    private final Path path;
    private final Handler<?, T> handler;

    private Route(final Path path, final Handler<?, T> handler) {
        this.path = path;
        this.handler = handler;
    }

    /**
     * Create route for specified path and with provided handler.
     *
     * @param path
     *        Route path
     * @param handler
     *        Route handler
     * @return Created route
     */
    public static <R> Route<R> of(final String path, final Handler<?, R> handler) {
        return of(Path.of(path), handler);
    }

    /**
     * Create route for specified path and with provided handler.
     *
     * @param path
     *        Route path
     * @param handler
     *        Route handler
     * @return Created route
     */
    public static <R> Route<R> of(final Path path, final Handler<?, R> handler) {
        return new Route<>(path, handler);
    }

    /**
     * Get path assigned to this route
     *
     * @return Route path
     */
    public Path path() {
        return path;
    }

    /**
     * Get handler assigned to this route
     *
     * @return Route handler
     */
    @SuppressWarnings("unchecked")
    public <R> Handler<R, T> handler() {
        return (Handler<R, T>) handler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Route<T>> stream() {
        return Stream.of(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Option<RouteDescription>> descriptions() {
        return Stream.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RouteBase<T> root(final Option<String> root) {
        return new Route<>(path.root(root), handler);
    }
}

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

import java.util.Collections;
import java.util.List;

//TODO: Javadoc
public class Route<T> implements RouteBase<T> {
    private final Path path;
    private final Handler<?, T> handler;

    private Route(final Path path, final Handler<?, T> handler) {
        this.path = path;
        this.handler = handler;
    }

    public static <R> Route<R> of(final String path, final Handler<?, R> handler) {
        return of(Path.of(path), handler);
    }

    public static <R> Route<R> of(final Path path, final Handler<?, R> handler) {
        return new Route<>(path, handler);
    }

    public Path path() {
        return path;
    }

    @SuppressWarnings("unchecked")
    public <R> Handler<R, T> handler() {
        return (Handler<R, T>) handler;
    }

    @Override
    public Route<T> asRoute() {
        return this;
    }

    @Override
    public Option<String> routeDescription() {
        return Option.empty();
    }

    @Override
    public List<String> parameterDescription() {
        return Collections.emptyList();
    }
}

package org.reactivetoolbox.eventbus.impl;

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

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.eventbus.Envelope;
import org.reactivetoolbox.eventbus.Route;
import org.reactivetoolbox.eventbus.RouteBase;
import org.reactivetoolbox.eventbus.Router;
import org.reactivetoolbox.eventbus.RoutingError;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

//TODO: Javadoc
public final class RouterImpl<T> implements Router<T> {
    private final ConcurrentMap<String, Route<T>> exactRoutes = new ConcurrentHashMap<>();
    private final ConcurrentNavigableMap<String, List<Route<T>>> prefixedRoutes = new ConcurrentSkipListMap<>();

    @Override
    public <R> Either<? extends BaseError, Promise<Either<? extends BaseError, R>>> deliver(final Envelope<T> event) {
        final Option<Route<T>> routeOption = findRoute(event);

        return routeOption.<Either<? extends BaseError, Promise<Either<? extends BaseError, R>>>>map(route -> event.onDelivery().flatMap(request -> route.<R>handler().apply(request)))
                .otherwise(() -> Either.failure(RoutingError.NO_SUCH_ROUTE));
    }

    private Option<Route<T>> findRoute(final Envelope<T> event) {
        return Option.of(exactRoutes.get(event.target().prefix()))
                    .or(() -> Option.of(prefixedRoutes.floorEntry(event.target().prefix()))
                            .map(entry -> entry.getValue()
                                    .stream()
                                    .filter(element -> element.path().matches(event.target().prefix()))
                                    .findFirst()
                                    .map(Option::of)
                                    .orElseGet(Option::empty))
                            .otherwise(Option::empty));
    }

    private void addSingle(final Route<T> route) {
        if (route.path().isExact()) {
            exactRoutes.putIfAbsent(route.path().prefix(), route);
        } else {
            prefixedRoutes.compute(route.path().prefix(), (key, value) -> {
                var list = value == null ? new ArrayList<Route<T>>() : value;
                list.add(route);
                return list;
            });
        }
    }

    @SafeVarargs
    @Override
    public final Router<T> with(final RouteBase<T>... routes) {
        for(var route : routes) {
            addSingle(route.asRoute());
        }
        return this;
    }
}

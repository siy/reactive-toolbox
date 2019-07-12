package org.reactivetoolbox.eventbus.impl;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Pair;
import org.reactivetoolbox.eventbus.Envelope;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.Route;
import org.reactivetoolbox.eventbus.Router;
import org.reactivetoolbox.eventbus.RoutingError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class RouterImpl<T> implements Router<T> {
    private final ConcurrentMap<String, Route<T>> exactRoutes = new ConcurrentHashMap<>();
    private final ConcurrentNavigableMap<String, List<Route<T>>> prefixedRoutes = new ConcurrentSkipListMap<>();

    @Override
    public Either<? extends BaseError, Promise<Either<? extends BaseError, ?>>> deliver(final Envelope<T> event) {
        var route = exactRoutes.get(event.target().prefix());

        if (route != null) {
            return event.onDelivery()
                    .flatMap(value -> event.onDelivery().flatMap(val -> route.handler().apply(val)));
        }

        var entry = prefixedRoutes.floorEntry(event.target().prefix());

        if (entry != null) {
            final Optional<Route<T>> routeOptional = entry.getValue()
                    .stream()
                    .filter(element -> element.path().matches(event.target().prefix()))
                    .findFirst();

            if (routeOptional.isPresent()) {
                return event.onDelivery().flatMap(value -> routeOptional.get().handler().apply(value));
            }
        }

        return Either.failure(RoutingError.NO_SUCH_ROUTE);
    }

    @SuppressWarnings("unchecked")
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
    public final Router<T> with(final Route<T>... routes) {
        for(var route : routes) {
            addSingle(route);
        }
        return this;
    }
}

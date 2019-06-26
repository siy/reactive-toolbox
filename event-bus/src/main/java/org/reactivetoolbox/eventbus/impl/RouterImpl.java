package org.reactivetoolbox.eventbus.impl;

import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.eventbus.Envelope;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.Router;
import org.reactivetoolbox.eventbus.RoutingError;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class RouterImpl<T> implements Router<T> {
    private final ConcurrentMap<String, FN1<Promise, T>> exactRoutes = new ConcurrentHashMap<>();
    private final ConcurrentNavigableMap<String, FN1<Promise, T>> prefixedRoutes = new ConcurrentSkipListMap<>();

    @Override
    public <R> Either<RoutingError, Promise<R>> deliver(final Envelope<T> event) {

        var handler = exactRoutes.get(event.target());

        if (handler != null) {
            return event.onDelivery()
                    .map(value -> Either.success((Promise<R>) handler.apply(value)));
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> Router<T> add(final Path path, final FN1<Promise<R>, T> handler) {
        final ConcurrentMap routes = path.isExact() ? exactRoutes : prefixedRoutes;
        final var destination = (ConcurrentMap<String, FN1<Promise<R>, T>>) routes;

        destination.putIfAbsent(path.prefix(), handler);

        return this;
    }
}

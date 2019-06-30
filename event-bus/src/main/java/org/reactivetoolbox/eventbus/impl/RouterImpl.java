package org.reactivetoolbox.eventbus.impl;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

//TODO: use Route instead of Pair<>
public class RouterImpl<T> implements Router<T> {
    private final ConcurrentMap<String, FN1<Promise, T>> exactRoutes = new ConcurrentHashMap<>();
    private final ConcurrentNavigableMap<String, List<Pair<Path, FN1<Promise, T>>>> prefixedRoutes = new ConcurrentSkipListMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <R> Either<RoutingError, Promise<R>> deliver(final Envelope<T> event) {
        var handler = exactRoutes.get(event.target().prefix());

        if (handler != null) {
            return event.onDelivery()
                    .flatMap(value -> Either.success((Promise<R>) handler.apply(value)));
        }

        var entry = prefixedRoutes.floorEntry(event.target().prefix());

        if (entry != null) {
            return entry.getValue().stream()
                    .filter(element -> element.left().matches(event.target().prefix()))
                    .findFirst()
                    .map(element -> event.onDelivery().flatMap(value -> Either.success((Promise<R>) element.right().apply(value))))
                    .orElseGet(() -> Either.failure(RoutingError.NO_SUCH_ROUTE));
        }

        return Either.failure(RoutingError.NO_SUCH_ROUTE);
    }

    @Override
    public <R> Router<T> add(final Path path, final FN1<Promise<R>, T> handler) {
        addSingle(path, handler);

        return this;
    }

    @SuppressWarnings("unchecked")
    private void addSingle(final Path path, final FN1 castedHandler) {
        if (path.isExact()) {
            exactRoutes.putIfAbsent(path.prefix(), castedHandler);
        } else {
            prefixedRoutes.compute(path.prefix(), (key, value) -> {
                var list = value == null ? new ArrayList<Pair<Path, FN1<Promise, T>>>() : value;
                list.add(Pair.of(path, castedHandler));
                return list;
            });
        }
    }

    @Override
    public Router<T> with(final Route<T>... routes) {
        for(var route : routes) {
            addSingle(route.path(), route.handler());
        }
        return this;
    }
}

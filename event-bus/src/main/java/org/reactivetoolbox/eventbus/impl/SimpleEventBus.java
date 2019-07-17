package org.reactivetoolbox.eventbus.impl;

import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.eventbus.Envelope;
import org.reactivetoolbox.eventbus.EventBus;
import org.reactivetoolbox.eventbus.Router;
import org.reactivetoolbox.eventbus.RoutingError;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.reactivetoolbox.eventbus.RoutingError.NO_SUCH_ROUTE;

public class SimpleEventBus implements EventBus {
    private final ConcurrentMap<Class<? extends Envelope>, Router> routers = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <R, T> Either<RoutingError, Promise<R>> send(final Envelope<T> event) {
        return Option.of(routers.get(event.getClass()))
                    .map(router -> router.<R>deliver(event))
                    .otherwise(() -> RoutingError.<Promise<R>>create(NO_SUCH_ROUTE));
    }

    @Override
    public <T> EventBus router(final Class<Envelope<T>> envelopeType, final Router<T> router) {
        routers.putIfAbsent(envelopeType, router);
        return this;
    }

    @Override
    public <T> EventBus upsertRouter(final Class<Envelope<T>> envelopeType, final Router<T> router) {
        routers.put(envelopeType, router);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Option<Router<T>> router(final Class<Envelope<T>> envelopeType) {
        return Option.of(routers.get(envelopeType));
    }
}

package org.reactivetoolbox.eventbus.impl;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises2.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.eventbus.Envelope;
import org.reactivetoolbox.eventbus.EventBus;
import org.reactivetoolbox.eventbus.Router;
import org.reactivetoolbox.eventbus.RouterError;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SimpleEventBus implements EventBus {
    private static final Either<RouterError, ?> ROUTING_ERROR = Either.failure(RouterError.NO_SUCH_ROUTE);
    private final ConcurrentMap<Class<? extends Envelope>, Router> routers = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <E extends BaseError, R, T> Either<? extends RouterError, Promise<E, R>> send(final Envelope<T> event) {
        return Optional.ofNullable(routers.get(event.getClass()))
            .map(router -> router.deliver(event))
            .orElse(ROUTING_ERROR);
    }

    @Override
    public <T> EventBus addRouter(final Class<Envelope<T>> key, final Router<T> router) {
        routers.put(key, router);
        return this;
    }
}

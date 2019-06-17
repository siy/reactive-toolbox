package org.reactivetoolbox.eventbus.impl;

import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.eventbus.Envelope;
import org.reactivetoolbox.eventbus.EventBus;
import org.reactivetoolbox.eventbus.Router;
import org.reactivetoolbox.eventbus.RoutingError;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.reactivetoolbox.eventbus.RoutingError.NO_SUCH_ROUTE;

public class SimpleEventBus implements EventBus {
    private final ConcurrentMap<Class<? extends Envelope>, Router> routers = new ConcurrentHashMap<>();

//    @Override
//    public <T> EventBus addRouter(final Class<Envelope<T>> key, final Router<T> router) {
//        routers.put(key, router);
//        return this;
//    }

    @Override
    public <E, R, T> Either<RoutingError, Promise<Either<E, R>>> send(final Envelope<T> event) {
        return Optional.ofNullable(routers.get(event.getClass()))
                .map(router -> router.deliver(event))
                .orElseGet(() -> RoutingError.create(NO_SUCH_ROUTE));
    }
}

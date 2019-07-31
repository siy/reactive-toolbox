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

import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.eventbus.Envelope;
import org.reactivetoolbox.eventbus.EventBus;
import org.reactivetoolbox.eventbus.Router;
import org.reactivetoolbox.eventbus.RoutingError;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.reactivetoolbox.eventbus.RoutingError.NO_SUCH_ROUTE;

//TODO: Javadoc
public class SimpleEventBus implements EventBus {
    private final ConcurrentMap<Class<? extends Envelope>, Router> routers = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <R, T> Either<RoutingError, Promise<R>> send(final Envelope<T> event) {
        return Option.of(routers.get(event.getClass()))
                    .map(router -> router.<R>deliver(event))
                    .otherwiseGet(() -> NO_SUCH_ROUTE.<Promise<R>>asFailure());
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

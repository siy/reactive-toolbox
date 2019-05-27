package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;

import java.net.URI;

public interface EventBus {
    <E extends BaseError, R, T> Promise<E, R> send(URI destination, T event);

    Router getRouter(Scheme scheme);

    //TODO: add inter-node routing API, for now all events considered local
}

package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.functional.Either;

public interface Envelope<T> {
    Either<RoutingError, T> onDelivery();

    //TODO: use dedicated type here
    String target();
}

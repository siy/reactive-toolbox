package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;

public interface Envelope<T> {
    Either<? extends BaseError, T> onDelivery();

    Path target();
}

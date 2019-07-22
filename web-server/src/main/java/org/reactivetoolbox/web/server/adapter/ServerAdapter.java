package org.reactivetoolbox.web.server.adapter;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;

public interface ServerAdapter {
    Promise<Either<? extends BaseError, ServerAdapter>> start();

    Promise<Either<? extends BaseError, ServerAdapter>> stop();
}

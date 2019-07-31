package org.reactivetoolbox.web.server;

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

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.eventbus.Router;
import org.reactivetoolbox.web.server.adapter.ServerAdapter;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.reactivetoolbox.build.HttpRouteTools.readyFail;

/**
 * HTTP Server implementation
 */
public class Server {
    private final Router<RequestContext> router;
    private final ServerAdapter adapter;
    private final AtomicBoolean running = new AtomicBoolean();

    private Server(final Router<RequestContext> router, final ServerAdapter adapter) {
        this.router = router;
        this.adapter = adapter;
    }

    public static Server of(final Router<RequestContext> router, final ServerAdapter adapter) {
        return new Server(router, adapter);
    }

    public Router<RequestContext> router() {
        return router;
    }

    public Promise<Either<? extends BaseError, Server>> start() {
        if (running.compareAndSet(false, true)) {
            final var result = Promise.<Either<? extends BaseError, Server>>give();

            adapter.start()
                   .then(startResult -> result.resolve(startResult.mapSuccess(ad -> this)));

            return result;
        } else {
            return readyFail(ServerError.ALREADY_RUNNING);
        }
    }

    public Promise<Either<? extends BaseError, Server>> stop() {
        if (running.compareAndSet(true, false)) {
            final var result = Promise.<Either<? extends BaseError, Server>>give();

            adapter.stop()
                   .then(stopResult -> result.resolve(stopResult.mapSuccess(ad -> this)));

            return result;
        } else {
            return readyFail(ServerError.NOT_YET_RUNNING);
        }
    }
}

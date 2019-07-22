package org.reactivetoolbox.web.server.adapter.undertow;

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

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.CanonicalPathHandler;
import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.Router;
import org.reactivetoolbox.web.server.HttpEnvelope;
import org.reactivetoolbox.web.server.HttpMethod;
import org.reactivetoolbox.web.server.Request;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.Response;
import org.reactivetoolbox.web.server.ServerError;
import org.reactivetoolbox.web.server.adapter.ServerAdapter;
import org.reactivetoolbox.web.server.parameter.conversion.ConverterFactory;

import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * Implementation of the {@link ServerAdapter} for Undertow (
 */
public class UndertowServerAdapter implements ServerAdapter, HttpHandler {
    private static final Either<ServerError, Promise<Either<? extends BaseError, Object>>> ERROR_404 = Either.failure(ServerError.BAD_REQUEST);

    private final Undertow server;
    private final Router<RequestContext> router;
    private final Functions.FN1<ByteBuffer[], Object> serializer;
    private final Function<Undertow, Either<Throwable, ServerAdapter>> serverStart = Either.lift((server) -> {  server.start(); return this; });
    private final Function<Undertow, Either<Throwable, ServerAdapter>> serverStop = Either.lift((server) -> { server.start(); return this; });

    private UndertowServerAdapter(final Router<RequestContext> router) {
        this.router = router;
        serializer = ConverterFactory.pluggable()
                                     .getResultSerializer();
        server = Undertow.builder()
                         .addHttpListener(8080, "0.0.0.0")
                         .setHandler(new CanonicalPathHandler(this))
                         .build();
    }

    public static ServerAdapter with(final Router<RequestContext> router) {
        return new UndertowServerAdapter(router);
    }

    @Override
    public Promise<Either<? extends BaseError, ServerAdapter>> start() {
        return Promises.fulfilled(serverStart.apply(server)
                                             .mapFailure(t -> ServerError.SERVER_FAILED_TO_START));
    }

    @Override
    public Promise<Either<? extends BaseError, ServerAdapter>> stop() {
        return Promises.fulfilled(serverStop.apply(server)
                                            .mapFailure(t -> ServerError.SERVER_FAILED_TO_STOP));
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        deliver(exchange).otherwise(ERROR_404)
                         .mapSuccess(val -> serializeSuccess(exchange, val))
                         .mapFailure(err -> serializeError(exchange, err));
    }

    private Promise<Either<? extends BaseError, Object>> serializeSuccess(final HttpServerExchange exchange,
                                                                          final Promise<Either<? extends BaseError, Object>> successResult) {
        exchange.setResponseCode(200)
                .getResponseSender()
                .send(serializer.apply(successResult));
        return successResult;
    }

    private BaseError serializeError(final HttpServerExchange exchange, final BaseError failureResult) {
        exchange.setResponseCode(failureResult.code())
                .getResponseSender()
                .send(failureResult.message());
        return failureResult;
    }

    private Option<Either<? extends BaseError, Promise<Either<? extends BaseError, Object>>>> deliver(final HttpServerExchange exchange) {
        return HttpMethod.fromString(exchange.getRequestMethod().toString())
                         .map(method -> Path.of(exchange.getRelativePath(), method))
                         .map(path -> router.deliver(HttpEnvelope.of(path, new UndertowRequestContext(exchange))));
    }

    private static class UndertowRequest implements Request {
        private final HttpServerExchange exchange;

        public UndertowRequest(final HttpServerExchange exchange) {
            this.exchange = exchange;
        }
    }

    private static class UndertowResponse implements Response {
        private final HttpServerExchange exchange;

        public UndertowResponse(final HttpServerExchange exchange) {
            this.exchange = exchange;
        }

        @Override
        public Response setHeader(final String name, final String value) {
            //TODO: implement it
            return this;
        }
    }

    private static class UndertowRequestContext implements RequestContext {
        private final HttpServerExchange exchange;
        private final UndertowRequest request;
        private final UndertowResponse response;

        private UndertowRequestContext(final HttpServerExchange exchange) {
            this.exchange = exchange;
            request = new UndertowRequest(exchange);
            response = new UndertowResponse(exchange);
        }

        @Override
        public Request request() {
            return request;
        }

        @Override
        public Response response() {
            return response;
        }
    }
}

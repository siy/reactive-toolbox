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
import io.undertow.util.HttpString;
import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises;
import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.core.functional.Pair;
import org.reactivetoolbox.eventbus.Envelope;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.Router;
import org.reactivetoolbox.web.server.HttpEnvelope;
import org.reactivetoolbox.web.server.HttpMethod;
import org.reactivetoolbox.web.server.Request;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.Response;
import org.reactivetoolbox.web.server.ServerError;
import org.reactivetoolbox.web.server.adapter.ServerAdapter;
import org.reactivetoolbox.web.server.parameter.conversion.ValueConverter;
import org.reactivetoolbox.web.server.parameter.conversion.simple.SimpleValueConverters;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.reactivetoolbox.core.functional.Either.lift;

/**
 * Implementation of the {@link ServerAdapter} for Undertow (
 */
public class UndertowServerAdapter implements ServerAdapter, HttpHandler {
    private static final Either<ServerError, Promise<Either<? extends BaseError, Object>>> ERROR_404 = Either.failure(ServerError.BAD_REQUEST);
    private static final Either<ServerError, Promise<Either<? extends BaseError, Object>>> ERROR_405 = Either.failure(ServerError.METHOD_NOT_ALLOWED);

    private final Undertow server;
    private final Router<RequestContext> router;
    private final Function<Undertow, Either<Throwable, ServerAdapter>> serverStart = lift((server) -> {  server.start(); return this; });
    private final Function<Undertow, Either<Throwable, ServerAdapter>> serverStop = lift((server) -> { server.start(); return this; });

    private UndertowServerAdapter(final Router<RequestContext> router) {
        this.router = router;
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

        final var envelope = toEnvelope(exchange);
        final var serializer = envelope.map(Envelope::payload)
                                       .map(RequestContext::resultSerializer);

        deliver(envelope)
                .otherwise(ERROR_405)
                .onFailure(failure -> serializeError(exchange, failure))
                .onSuccess(promise -> promise.then(either -> either.onFailure(failure -> serializeError(exchange, failure))
                                                                   .onSuccess(success -> serializer.consume(fn -> serializeSuccess(exchange, fn, success)))));
    }

    private void serializeSuccess(final HttpServerExchange exchange, final FN1<ByteBuffer, Object> fn, final Object success) {
        exchange.getResponseSender().send(fn.apply(success));
    }

    private void serializeError(final HttpServerExchange exchange, final BaseError failureResult) {
        exchange.setStatusCode(failureResult.code())
                .getResponseSender()
                .send(failureResult.message());
    }

    private Option<Either<? extends BaseError, Promise<Either<? extends BaseError, Object>>>> deliver(final Option<Envelope<RequestContext>> envelope) {
        return envelope.map(router::deliver);
    }

    private Option<Envelope<RequestContext>> toEnvelope(final HttpServerExchange exchange) {
        return HttpMethod.fromString(exchange.getRequestMethod().toString())
                         .map(method -> Path.of(exchange.getRelativePath(), method))
                         .map(path -> HttpEnvelope.of(path, new UndertowRequestContext(path, exchange)));
    }

    private static class UndertowRequest implements Request {
        private final Map<String, String> pathParameters = new HashMap<>();
        private final HttpServerExchange exchange;
        private final RequestContext context;

        public UndertowRequest(final HttpServerExchange exchange,
                               final RequestContext context) {
            this.exchange = exchange;
            this.context = context;
        }

        @Override
        public Request pathParameters(final List<Pair<String, String>> pairs) {
            pairs.stream().forEach(pair -> pathParameters.put(pair.left(), pair.right()));
            return this;
        }

        @Override
        public RequestContext context() {
            return context;
        }

        @Override
        public Option<String> pathParameter(final String name) {
            return Option.of(pathParameters.get(name));
        }

        @Override
        public Option<String> queryParameter(final String name) {
            return Option.of(exchange.getQueryParameters().get(name))
                         .flatMap(dq -> Option.of(dq.pollFirst()));
        }

        @Override
        public List<String> queryParameters(final String name) {
            final Option<List<String>> values = Option.of(exchange.getQueryParameters().get(name))
                                                      .map(ArrayList::new);

            return values.otherwise(Collections::emptyList);
        }

        @Override
        public Map<String, List<String>> queryParameters() {
            return exchange.getQueryParameters()
                           .entrySet()
                           .stream()
                           .map(entry -> Pair.of(entry.getKey(), new ArrayList<>(entry.getValue())))
                           .collect(Collectors.toMap(Pair::left, Pair::right));
        }

        @Override
        public Option<String> header(final String name) {
            return Option.of(exchange.getRequestHeaders().get(name))
                         .map(values -> values.stream().findFirst().map(Option::of).orElse(Option.empty()))
                         .otherwise(Option::empty);
        }

        @Override
        public Option<String> bodyParameter(final String name) {
            //TODO: implement it
            return null;
        }

        @Override
        public Option<String> body() {
            //TODO: implement it
            return null;
        }
    }

    private static class UndertowResponse implements Response {
        private final HttpServerExchange exchange;
        private final RequestContext context;

        public UndertowResponse(final HttpServerExchange exchange,
                                final RequestContext context) {
            this.exchange = exchange;
            this.context = context;
        }

        @Override
        public Response setHeader(final String name, final String value) {
            Option.of(HttpString.tryFromString(name))
                  .map(header -> exchange.getResponseHeaders().add(header, value));
            return this;
        }

        @Override
        public RequestContext context() {
            return context;
        }
    }

    private static class UndertowRequestContext implements RequestContext {
        private final Path path;
        private final HttpServerExchange exchange;
        private final UndertowRequest request;
        private final UndertowResponse response;

        private UndertowRequestContext(final Path path, final HttpServerExchange exchange) {
            this.path = path;
            this.exchange = exchange;
            request = new UndertowRequest(exchange, this);
            response = new UndertowResponse(exchange, this);
        }

        @Override
        public Request request() {
            return request;
        }

        @Override
        public Response response() {
            return response;
        }

        @Override
        public Path path() {
            return path;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> ValueConverter<T> valueConverter(final Class<T> type) {
            return (ValueConverter<T>) CONVERTERS.get(type);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Option<T> contextComponent(final Class<T> type) {
            if (type == RequestContext.class) {
                return Option.of((T) this);
            } else if (type == Request.class) {
                return Option.of((T) request);
            } else if (type == Response.class) {
                return Option.of((T) response);
            }
            return Option.empty();
        }

        @Override
        public FN1<ByteBuffer, Object> resultSerializer() {
            //TODO: finish it
            return UndertowServerAdapter::serializer;
        }
    }

    //TODO: rework it with pooled direct ByteBuffers
    private static ByteBuffer serializer(final Object output) {
        if (output instanceof String || output instanceof Number || output instanceof Temporal) {
            return ByteBuffer.wrap(output.toString().getBytes(StandardCharsets.UTF_8));
        }

        //TODO: add other cases -
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);

        return ByteBuffer.wrap(baos.toByteArray());
    }

    private static final Map<Class<?>, ValueConverter<?>> CONVERTERS = new HashMap<>();

    static {
        CONVERTERS.put(String.class, (ValueConverter<String>) SimpleValueConverters::toString);
        CONVERTERS.put(Integer.class, (ValueConverter<Integer>) SimpleValueConverters::toInteger);
        CONVERTERS.put(Long.class, (ValueConverter<Long>) SimpleValueConverters::toLong);
        CONVERTERS.put(Double.class, (ValueConverter<Double>) SimpleValueConverters::toDouble);
    }
}

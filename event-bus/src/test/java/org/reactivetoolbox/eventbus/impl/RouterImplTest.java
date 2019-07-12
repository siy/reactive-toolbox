package org.reactivetoolbox.eventbus.impl;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.async.Promises;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.eventbus.Envelope;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.Route;
import org.reactivetoolbox.eventbus.RoutingError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class RouterImplTest {

    @Test
    void exactPathCanBeMatched() {
        var router = new RouterImpl<String>();

        router.with(Route.of("/one/two", msg -> Either.success(Promises.fulfilled(Either.success(msg + msg)))));

        router.deliver(StringEnvelope.of("/one/two", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals("ValueValue", value)));

        router.deliver(StringEnvelope.of("/one/two/", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals("ValueValue", value)));
    }

    @Test
    void parametrizedPathCanBeMatched() {
        var router = new RouterImpl<String>();

        router.with(Route.of("/one/{two}", msg -> Either.success(Promises.fulfilled(Either.success(msg + msg + 2)))),
                    Route.of("/one/two/{three}", msg -> Either.success(Promises.fulfilled(Either.success(msg + msg + 3)))));

        router.deliver(StringEnvelope.of("/one/param1", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals("ValueValue2", value)));

        router.deliver(StringEnvelope.of("/one/two/param1", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals("ValueValue3", value)));
    }

    @Test
    void parametrizedPathCantBeMatchedIfParameterIsMissing() {
        var router = new RouterImpl<String>();

        router.with(Route.of("/one/two/{three}", msg -> Either.success(Promises.fulfilled(Either.success(msg + msg + 3)))));

        router.deliver(StringEnvelope.of("/one/two", "Value"))
                .onSuccess(success -> fail("Received unexpected " + success))
                .onSuccess(failure -> assertEquals(RoutingError.NO_SUCH_ROUTE, failure));
    }

    @Test
    void mixedPathsCanBeMatched() {
        var router = new RouterImpl<String>();

        router.with(Route.of("/one/two", msg -> Either.success(Promises.fulfilled(Either.success(msg + msg + 1)))),
                    Route.of("/one/{two}", msg -> Either.success(Promises.fulfilled(Either.success(msg + msg + 2)))),
                    Route.of("/one/two/{three}", msg -> Either.success(Promises.fulfilled(Either.success(msg + msg + 3)))));

        router.deliver(StringEnvelope.of("/one/two", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals("ValueValue1", value)));

        router.deliver(StringEnvelope.of("/one/two/", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals("ValueValue1", value)));

        router.deliver(StringEnvelope.of("/one/param1", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals("ValueValue2", value)));

        router.deliver(StringEnvelope.of("/one/two/param1", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals("ValueValue3", value)));
    }

    private static final class StringEnvelope implements Envelope<String> {
        private final String payload;
        private final Path target;

        private StringEnvelope(final String target, final String payload) {
            this.payload = payload;
            this.target = Path.of(target);
        }

        public static final StringEnvelope of(final String target, final String payload) {
            return new StringEnvelope(target, payload);
        }

        @Override
        public Either<RoutingError, String> onDelivery() {
            return Either.success(payload);
        }

        @Override
        public Path target() {
            return target;
        }
    }
}
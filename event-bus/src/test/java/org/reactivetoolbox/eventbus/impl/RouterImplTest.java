package org.reactivetoolbox.eventbus.impl;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.async.Promises;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.eventbus.Envelope;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.Route;
import org.reactivetoolbox.eventbus.RoutingError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.reactivetoolbox.core.functional.Either.success;

class RouterImplTest {

    @Test
    void exactPathCanBeMatched() {
        final var router = new RouterImpl<String>();

        router.with(Option.empty(), Route.of("/one/two", msg -> success(Promises.fulfilled(success(msg + msg)))));

        router.deliver(StringEnvelope.of("/one/two", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals(success("ValueValue"), value)));

        router.deliver(StringEnvelope.of("/one/two/", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals(success("ValueValue"), value)));
    }

    @Test
    void parametrizedPathCanBeMatched() {
        final var router = new RouterImpl<String>();

        router.with(Option.empty(),
                    Route.of("/one/{two}", msg -> success(Promises.fulfilled(success(msg + msg + 2)))),
                    Route.of("/one/two/{three}", msg -> success(Promises.fulfilled(success(msg + msg + 3)))));

        router.deliver(StringEnvelope.of("/one/param1", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals(success("ValueValue2"), value)));

        router.deliver(StringEnvelope.of("/one/two/param1", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals(success("ValueValue3"), value)));
    }

    @Test
    void parametrizedPathCantBeMatchedIfParameterIsMissing() {
        final var router = new RouterImpl<String>();

        router.with(Option.empty(), Route.of("/one/two/{three}", msg -> success(Promises.fulfilled(success(msg + msg + 3)))));

        router.deliver(StringEnvelope.of("/one/two", "Value"))
                .onSuccess(success -> fail("Received unexpected " + success))
                .onSuccess(failure -> assertEquals(RoutingError.NO_SUCH_ROUTE, failure));
    }

    @Test
    void mixedPathsCanBeMatched() {
        final var router = new RouterImpl<String>();

        router.with(Option.empty(),
                    Route.of("/one/two", msg -> success(Promises.fulfilled(success(msg + msg + 1)))),
                    Route.of("/one/{two}", msg -> success(Promises.fulfilled(success(msg + msg + 2)))),
                    Route.of("/one/two/{three}", msg -> success(Promises.fulfilled(
                            success(msg + msg + 3)))));

        router.deliver(StringEnvelope.of("/one/two", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals(success("ValueValue1"), value)));

        router.deliver(StringEnvelope.of("/one/two/", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals(success("ValueValue1"), value)));

        router.deliver(StringEnvelope.of("/one/param1", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals(success("ValueValue2"), value)));

        router.deliver(StringEnvelope.of("/one/two/param1", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals(success("ValueValue3"), value)));
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
        public Either<? extends BaseError, String> onDelivery(final Route<String> route) {
            return success(payload);
        }

        @Override
        public Path target() {
            return target;
        }

        @Override
        public String payload() {
            return payload;
        }
    }
}
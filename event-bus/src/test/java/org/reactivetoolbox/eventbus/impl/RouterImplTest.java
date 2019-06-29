package org.reactivetoolbox.eventbus.impl;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.async.Promises;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.eventbus.Envelope;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.eventbus.Router;
import org.reactivetoolbox.eventbus.RoutingError;

import static org.junit.jupiter.api.Assertions.*;

class RouterImplTest {

    @Test
    void exactPathCanBeMatched() {
        var router = new RouterImpl<String>();

        router.add(Path.of("/one/two"), msg -> Promises.fulfilled(msg + msg));

        //TODO: fix path matching
        router.deliver(StringEnvelope.of("/one/two/", "Value"))
                .onFailure(failure -> fail("Received unexpected " + failure))
                .onSuccess(success -> success.then(value -> assertEquals("ValueValue", value)));

    }

    private static final class StringEnvelope implements Envelope<String> {
        private final String payload;
        private final String target;

        private StringEnvelope(final String payload, final String target) {
            this.payload = payload;
            this.target = target;
        }

        public static final StringEnvelope of(final String target, final String payload) {
            return new StringEnvelope(payload, target);
        }

        @Override
        public Either<RoutingError, String> onDelivery() {
            return Either.success(payload);
        }

        @Override
        public String target() {
            return target;
        }
    }
}
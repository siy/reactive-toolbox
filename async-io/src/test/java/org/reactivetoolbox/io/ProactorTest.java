package org.reactivetoolbox.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.io.scheduler.Timeout;

import java.util.concurrent.atomic.AtomicBoolean;

class ProactorTest {
    private final Proactor proactor = Proactor.proactor();

    @Test
    void nopCanBeSubmitted() {
        final var promise = proactor.nop()
                                    .onResult(Assertions::assertNotNull)
                                    .onResult(System.out::println);
        proactor.process(); //For submission
        proactor.process(); //For completion

        promise.syncWait();
    }

    @Test
    void delayCanBeSubmitted() {
        final AtomicBoolean ready = new AtomicBoolean(false);
        final var promise = proactor.delay(Timeout.timeout(100).millis())
                                    .onResult(Assertions::assertNotNull)
                                    .onResult(System.out::println)
                                    .onResult(v -> ready.lazySet(true));
        do {
            proactor.process(); //For submission
            proactor.process(); //For completion
        } while(!ready.get());
    }
}
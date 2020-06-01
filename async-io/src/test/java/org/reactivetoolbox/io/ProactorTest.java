package org.reactivetoolbox.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.io.async.OpenFlags;
import org.reactivetoolbox.io.async.OpenMode;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.scheduler.Timeout;

import java.nio.file.Path;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ProactorTest {
    private final Proactor proactor = Proactor.proactor();

    @Test
    void nopCanBeSubmitted() {
        final var promise = proactor.nop()
                                    .onResult(Assertions::assertNotNull)
                                    .onResult(System.out::println);
        waitForResult(promise);
    }

    @Test
    void delayCanBeSubmitted() {
        final var promise = proactor.delay(Timeout.timeout(100).millis())
                                    .onResult(Assertions::assertNotNull)
                                    .onResult(System.out::println);

        waitForResult(promise);
    }

    @Test
    void fileCanBeOpenedAndClosed() {
        final var promise = proactor.open(Path.of("target/classes/org/reactivetoolbox/io/Proactor.class"),
                                          EnumSet.of(OpenFlags.O_RDONLY),
                                          EnumSet.noneOf(OpenMode.class))
                                    .onResult(System.out::println)
                                    .onResult(v -> v.onSuccess(fd -> assertTrue(fd.descriptor() > 0))
                                                    .onFailure(f -> fail()))
                                    .chainMap(proactor::closeFileDescriptor)
                                    .onResult(System.out::println)
                                    .onResult(v -> v.onFailure(f -> fail()));

        waitForResult(promise);
    }

    private void waitForResult(final Promise<?> promise) {
        final AtomicBoolean ready = new AtomicBoolean(false);

        promise.onResult(v -> ready.lazySet(true));

        do {
            proactor.processIO(); //For submission
            proactor.processIO(); //For completion
        } while (!ready.get());
    }
}
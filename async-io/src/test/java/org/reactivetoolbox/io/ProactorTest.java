package org.reactivetoolbox.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.file.FilePermission;
import org.reactivetoolbox.io.async.file.OpenFlags;
import org.reactivetoolbox.io.async.net.AddressFamily;
import org.reactivetoolbox.io.async.net.InetPort;
import org.reactivetoolbox.io.async.net.SocketAddressIn;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketOption;
import org.reactivetoolbox.io.async.net.SocketType;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;

import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.reactivetoolbox.core.lang.Tuple.tuple;
import static org.reactivetoolbox.core.lang.functional.Option.option;
import static org.reactivetoolbox.io.async.Promise.promise;
import static org.reactivetoolbox.io.async.net.Inet4Address.inet4Address;
import static org.reactivetoolbox.io.scheduler.Timeout.timeout;

//TODO: remaining tests
class ProactorTest {
    private final Proactor proactor = Proactor.proactor();

    @Test
    void nopCanBeSubmitted() {
        final var finalResult = new AtomicReference<Result<?>>();
        final var promise = proactor.nop(promise())
                                    .onResult(Assertions::assertNotNull)
                                    .onResult(System.out::println)
                                    .onResult(finalResult::set);
        waitForResult(promise);
        finalResult.get().onFailure($ -> fail());
    }

    @Test
    void delayCanBeSubmitted() {
        final var finalResult = new AtomicReference<Result<?>>();
        final var promise = proactor.delay(promise(), timeout(100).millis())
                                    .onResult(Assertions::assertNotNull)
                                    .onResult(System.out::println)
                                    .onResult(finalResult::set);

        waitForResult(promise);
        finalResult.get().onFailure($ -> fail());
    }

    @Test
    void fileCanBeOpenedAndClosed() {
        final var finalResult = new AtomicReference<Result<?>>();
        final var promise = proactor.open(promise(),
                                          Path.of("target/classes/org/reactivetoolbox/io/Proactor.class"),
                                          EnumSet.of(OpenFlags.READ_ONLY),
                                          EnumSet.noneOf(FilePermission.class))
                                    .onResult(System.out::println)
                                    .onResult(v -> v.onSuccess(fd -> assertTrue(fd.descriptor() > 0))
                                                    .onFailure(f -> fail()))
                                    .flatMap(fd -> proactor.closeFileDescriptor(promise(), fd))
                                    .onResult(System.out::println)
                                    .onResult(finalResult::set);

        waitForResult(promise);
        finalResult.get().onFailure($ -> fail());
    }

    @Test
    void fileCanBeOpenedReadAndClosed() {
        final var finalResult = new AtomicReference<Result<?>>();
        try (final OffHeapBuffer buffer = OffHeapBuffer.fixedSize(1024 * 1024)) {
            final var promise = proactor.open(promise(),
                                              Path.of("target/classes/org/reactivetoolbox/io/Proactor.class"),
                                              EnumSet.of(OpenFlags.READ_ONLY),
                                              EnumSet.noneOf(FilePermission.class))
                                        .onResult(System.out::println)
                                        .onResult(v -> v.onSuccess(fd -> assertTrue(fd.descriptor() > 0))
                                                        .onFailure(f -> fail()))
                                        .flatMap(fd1 -> proactor.read(promise(), fd1, buffer).map(sz -> tuple(fd1, sz)))
                                        .onResult(System.out::println)
                                        .flatMap(fdSz -> fdSz.map((fd, sz) -> proactor.closeFileDescriptor(promise(), fd)))
                                        .onResult(System.out::println)
                                        .onResult(v -> v.onFailure(f -> fail()))
                                        .onResult(finalResult::set);

            waitForResult(promise);
            finalResult.get().onFailure($ -> fail());
        }
    }

    @Test
    void externalHostCanBeConnectedAndRead() throws UnknownHostException {
        final var finalResult = new AtomicReference<Result<?>>();
        final var addr = java.net.Inet4Address.getByName("www.google.com");
        final var address = inet4Address(addr.getAddress()).fold($ -> fail(),
                                                                 inetAddress -> SocketAddressIn.create(InetPort.inetPort(80),
                                                                                                       inetAddress));

        System.out.println("Address: " + address);

        try (final OffHeapBuffer preparedText = OffHeapBuffer.fromBytes("GET /\n".getBytes(StandardCharsets.US_ASCII))) {
            try (final OffHeapBuffer buffer = OffHeapBuffer.fixedSize(256)) {
                buffer.clear().used(buffer.size());
                final var promise = proactor.socket(promise(),
                                                    AddressFamily.INET,
                                                    SocketType.STREAM,
                                                    SocketFlag.none(),
                                                    SocketOption.reuseAll())
                                            .onResult(r1 -> System.out.println("Socket created: " + r1))
                                            .flatMap(fd -> proactor.connect(promise(), fd, address, option(timeout(1).seconds()))
                                                                   .onFailure($ -> proactor.closeFileDescriptor(promise(), fd))
                                                                   .map(sz -> fd))
                                            .onResult(r1 -> System.out.println("Socket connected: " + r1))
                                            .flatMap(fd -> proactor.write(promise(), fd, preparedText, option(timeout(1).seconds()))
                                                                   .map(sz -> fd))
                                            .flatMap(fd -> proactor.read(promise(), fd, buffer, option(timeout(1).seconds()))
                                                                   .onFailure($ -> proactor.closeFileDescriptor(promise(), fd))
                                                                   .map(sz -> fd))
                                            .onResult(System.out::println)
                                            .flatMap(fd -> proactor.closeFileDescriptor(promise(), fd))
                                            .onResult(finalResult::set);
                waitForResult(promise);
                finalResult.get().onFailure($ -> fail());

                System.out.println("Content: [" + new String(buffer.export(), StandardCharsets.UTF_8) + "]");
            }
        }
    }

    private void waitForResult(final Promise<?> promise) {
        final AtomicBoolean ready = new AtomicBoolean(false);

        promise.onResult(v -> ready.lazySet(true));

        do {
            proactor.processIO(); //For submission
            proactor.processIO(); //For completion
        } while (!ready.get());

        promise.syncWait();
    }
}
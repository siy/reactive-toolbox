package org.reactivetoolbox.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.file.OpenFlags;
import org.reactivetoolbox.io.async.file.OpenMode;
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
import static org.reactivetoolbox.core.lang.functional.Option.option;
import static org.reactivetoolbox.io.async.net.Inet4Address.inet4Address;
import static org.reactivetoolbox.io.scheduler.Timeout.timeout;

//TODO: remaining tests
class ProactorTest {
    private final Proactor proactor = Proactor.proactor();

    @Test
    void nopCanBeSubmitted() {
        final var finalResult = new AtomicReference<Result<?>>();
        final var promise = proactor.nop()
                                    .onResult(Assertions::assertNotNull)
                                    .onResult(System.out::println)
                                    .onResult(finalResult::set);
        waitForResult(promise);
        finalResult.get().onFailure($ -> fail());
    }

    @Test
    void delayCanBeSubmitted() {
        final var finalResult = new AtomicReference<Result<?>>();
        final var promise = proactor.delay(timeout(100).millis())
                                    .onResult(Assertions::assertNotNull)
                                    .onResult(System.out::println)
                                    .onResult(finalResult::set);

        waitForResult(promise);
        finalResult.get().onFailure($ -> fail());
    }

    @Test
    void fileCanBeOpenedAndClosed() {
        final var finalResult = new AtomicReference<Result<?>>();
        final var promise = proactor.open(Path.of("target/classes/org/reactivetoolbox/io/Proactor.class"),
                                          EnumSet.of(OpenFlags.O_RDONLY),
                                          EnumSet.noneOf(OpenMode.class))
                                    .onResult(System.out::println)
                                    .onResult(v -> v.onSuccess(fd -> assertTrue(fd.descriptor() > 0))
                                                    .onFailure(f -> fail()))
                                    .andThen(proactor::closeFileDescriptor)
                                    .onResult(System.out::println)
                                    .onResult(finalResult::set);

        waitForResult(promise);
        finalResult.get().onFailure($ -> fail());
    }

    @Test
    void fileCanBeOpenedReadAndClosed() {
        final var finalResult = new AtomicReference<Result<?>>();
        try (final OffHeapBuffer buffer = OffHeapBuffer.fixedSize(1024 * 1024)) {
            final var promise = proactor.open(Path.of("target/classes/org/reactivetoolbox/io/Proactor.class"),
                                              EnumSet.of(OpenFlags.O_RDONLY),
                                              EnumSet.noneOf(OpenMode.class))
                                        .onResult(System.out::println)
                                        .onResult(v -> v.onSuccess(fd -> assertTrue(fd.descriptor() > 0))
                                                        .onFailure(f -> fail()))
                                        .andThen(fd1 -> proactor.read(fd1, buffer))
                                        .onResult(System.out::println)
                                        .andThen(fdAndSize -> fdAndSize.map((fd, sz) -> {
                                            System.out.println(sz);
                                            return proactor.closeFileDescriptor(fd);
                                        }))
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
            try (final OffHeapBuffer buffer = OffHeapBuffer.fixedSize(8192)) {
                buffer.clear().used(buffer.size());
                final var promise = proactor.socket(AddressFamily.INET,
                                                    SocketType.STREAM,
                                                    SocketFlag.noFlags(),
                                                    SocketOption.reuseAll())
                                            .onResult(r1 -> System.out.println("Socket created: " + r1))
                                            .andThen(fd -> proactor.connect(fd, address, option(timeout(1).seconds()))
                                                                   .onFailure($ -> proactor.closeFileDescriptor(fd)))
                                            .onResult(r1 -> System.out.println("Socket connected: " + r1))
                                            .andThen(fd -> proactor.write(fd, preparedText, option(timeout(1).seconds()))
                                                                   .map(t -> t.map((fd1, sz1) -> fd1)))
                                            .andThen(fd -> proactor.read(fd, buffer, option(timeout(1).seconds()))
                                                                   .onFailure($ -> proactor.closeFileDescriptor(fd)))
                                            .onResult(System.out::println)
                                            .andThen(fd_size -> fd_size.map((fd, sz) -> proactor.closeFileDescriptor(fd)))
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
    }
}
package org.reactivetoolbox.io.examples.async.server;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.net.context.ActiveServerContext;

import java.time.Instant;

import static org.reactivetoolbox.io.async.net.lifecycle.ReadWriteLifeCycle.readWrite;
import static org.reactivetoolbox.io.async.net.server.TcpServer.tcpServer;
import static org.reactivetoolbox.io.async.net.server.TcpServerConfiguration.configuration;
import static org.reactivetoolbox.io.scheduler.impl.DaemonThreadFactory.threadFactory;

/**
 * 1 - ~54-62K, 2 - ~77-99K, 3 - ~119-133K, 5 - ~185-195K
 * 10 - ~258-288K, 20 - ~273-283K, 30 - ~312-316K, 50 - ~308-322K
 * 100 - ~310-341K, 200 - ~316-320, 300 - ~290-298, 500 - ~
 * Netty:
 * 1 - ~66-68K, 2 - ~104-106K, 3 - ~140-141K, 5 - ~165-172K
 * 10 - ~266-290K, 20 - ~242-257K, 30 - ~261-278K, 50 - ~280-293K
 * 100 - ~286-305K, 200 - ~277-300K, 300 - ~263-281K, 500 - ~254-269K
 */
//@Disabled
public class EchoServerTest {
    @Disabled
    @Test
    void doNothing() {

    }

    @Test
    void runEchoServer() {
        System.out.println(Instant.now());
        final var promise = Promise.<Unit>waitablePromise();
        tcpServer(configuration(8081, readWrite(ActiveServerContext::echo /*, option(timeout(10).seconds())*/)))
                .start()
                .onFailure(failure -> System.out.println("Server failed to start: " + failure))
                .onSuccess(activeServerContext -> {
                    System.out.println("Listening for incoming connections");

                    Runtime.getRuntime()
                           .addShutdownHook(threadFactory("Shutdown hook")
                                                    .newThread(activeServerContext::shutdown));
                })
                .flatMap(ActiveServerContext::shutdownPromise)
                .chainTo(promise);

        promise.syncWait();

        System.out.println(Instant.now());
    }
}

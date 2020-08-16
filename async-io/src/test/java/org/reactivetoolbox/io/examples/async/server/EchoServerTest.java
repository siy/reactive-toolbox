package org.reactivetoolbox.io.examples.async.server;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.net.context.ActiveServerContext;

import java.time.Instant;

import static org.reactivetoolbox.core.lang.functional.Option.option;
import static org.reactivetoolbox.io.async.net.lifecycle.ReadWriteLifeCycle.readWrite;
import static org.reactivetoolbox.io.async.net.server.TcpServer.tcpServer;
import static org.reactivetoolbox.io.async.net.server.TcpServerConfiguration.configuration;
import static org.reactivetoolbox.io.scheduler.Timeout.timeout;
import static org.reactivetoolbox.io.scheduler.impl.DaemonThreadFactory.threadFactory;

@Disabled
public class EchoServerTest {

    @Disabled
    @Test
    void doNothing() {
    }

    @Test
    void runEchoServer() {
        final var promise = Promise.<Unit>waitablePromise();

        tcpServer(configuration(8081, readWrite(ActiveServerContext::echo, option(timeout(10).seconds()))))
                .start()
                .onFailure(failure -> promise.logger().info("Server failed to start: {0}", failure))
                .onSuccess(activeServerContext -> {
                    promise.logger().info("Listening for incoming connections at {0}", activeServerContext.serverAddress());

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
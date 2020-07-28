package org.reactivetoolbox.io.examples.async.server;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.io.async.net.context.ActiveServerContext;

import static org.reactivetoolbox.io.async.net.lifecycle.ReadWriteLifeCycle.readWrite;
import static org.reactivetoolbox.io.async.net.server.TcpServer.tcpServer;
import static org.reactivetoolbox.io.async.net.server.TcpServerConfiguration.configuration;
import static org.reactivetoolbox.io.scheduler.impl.DaemonThreadFactory.threadFactory;

@Disabled
public class EchoServerTest {
    @Test
    void runEchoServer() {
        tcpServer(configuration(8081, readWrite(ActiveServerContext::echo)))
                .start()
                .onSuccess(activeServerContext -> {
                    System.out.println("Listening for incoming connections");

                    Runtime.getRuntime()
                           .addShutdownHook(threadFactory("Shutdown hook").newThread(activeServerContext::shutdown));
                })
                .onFailure(failure -> System.out.println("Server failed to start: " + failure))
                .flatMap(ActiveServerContext::shutdownPromise)
                .syncWait();
    }
}

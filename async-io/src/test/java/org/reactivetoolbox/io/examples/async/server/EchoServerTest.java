/*
 * Copyright (c) 2020 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.reactivetoolbox.io.examples.async.server;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.io.async.net.context.ActiveServerContext;
import org.reactivetoolbox.io.async.net.lifecycle.ReadWriteLifeCycle;

import static org.reactivetoolbox.io.async.net.lifecycle.ReadWriteLifeCycle.readWrite;
import static org.reactivetoolbox.io.async.net.server.TcpServer.tcpServer;
import static org.reactivetoolbox.io.async.net.server.TcpServerConfiguration.configuration;
import static org.reactivetoolbox.io.scheduler.impl.DaemonThreadFactory.threadFactory;

@Disabled
public class EchoServerTest {

    @Disabled
    @Test
    void doNothing() {
    }

    @Test
    void runEchoServer() {
        final var promise =
                tcpServer(configuration(8081,
                                        readWrite(ReadWriteLifeCycle::echo/*, option(timeout(10).seconds())*/)))
                        .start();

        promise.onFailure(failure -> promise.logger()
                                            .info("Server failed to start: {0}", failure))
               .onSuccess(activeServerContext -> {
                   promise.logger()
                          .info("Listening for incoming connections at {0}",
                                activeServerContext.serverAddress());

                   Runtime.getRuntime()
                          .addShutdownHook(threadFactory("Shutdown hook")
                                                   .newThread(activeServerContext::shutdown));
               })
               .flatMap(ActiveServerContext::shutdownPromise)
               .syncWait();
    }
}

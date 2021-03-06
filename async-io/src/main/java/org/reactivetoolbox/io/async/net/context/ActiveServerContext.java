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

package org.reactivetoolbox.io.async.net.context;

import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.core.lang.support.ULID;
import org.reactivetoolbox.core.log.CoreLogger;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.net.ClientConnection;
import org.reactivetoolbox.io.async.net.SocketAddressIn;
import org.reactivetoolbox.io.async.net.lifecycle.LifeCycle;
import org.reactivetoolbox.io.async.net.server.TcpServerConfiguration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.reactivetoolbox.io.async.net.context.IncomingConnectionContext.connectionContext;

//TODO: some redesign is required. get rid of lifecycle???
public class ActiveServerContext {
    private final ServerContext<?> serverContext;
    private final TcpServerConfiguration configuration;
    private final ConcurrentMap<ULID, IncomingConnectionContext> connections = new ConcurrentHashMap<>();
    private final Promise<Unit> shutdownPromise = Promise.promise();

    private ActiveServerContext(final ServerContext<?> serverContext, final TcpServerConfiguration configuration) {
        this.serverContext = serverContext;
        this.configuration = configuration;
    }

    public SocketAddressIn serverAddress() {
        return configuration.address();
    }

    public static ActiveServerContext activeContext(final ServerContext<?> serverContext,
                                                    final TcpServerConfiguration configuration) {
        return new ActiveServerContext(serverContext, configuration);
    }

    public static Promise<Unit> defaultConnectionHandler(final IncomingConnectionContext incomingConnectionContext) {
        incomingConnectionContext.register();

        return Promise.promise(incomingConnectionContext::processConnection)
                      .thenDo(incomingConnectionContext::deregister);
    }

    public LifeCycle lifeCycle() {
        return configuration.lifeCycle();
    }

    public ConnectionRegistry registry() {
        return ConnectionRegistry.withMap(connections);
    }

    public boolean shutdownInProgress() {
        return serverContext.shutdownInProgress();
    }

    public FileDescriptor socket() {
        return serverContext.socket();
    }

    public CoreLogger logger() {
        return shutdownPromise.logger();
    }

    public Promise<Unit> handleConnection(final ClientConnection<?> clientConnection) {
        return configuration.connectionHandler()
                            .apply(connectionContext(this, clientConnection));
    }

    public void shutdown() {
        serverContext.shutdown(shutdownPromise);
    }

    public Promise<Unit> shutdownPromise() {
        return shutdownPromise;
    }
}

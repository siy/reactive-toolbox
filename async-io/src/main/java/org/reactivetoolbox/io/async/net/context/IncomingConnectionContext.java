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
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.net.ClientConnection;

public class IncomingConnectionContext {
    private final ActiveServerContext serverContext;
    private final ClientConnection<?> clientConnection;
    private final ULID id;

    public IncomingConnectionContext(final ActiveServerContext serverContext, final ClientConnection<?> clientConnection, final ULID id) {
        this.serverContext = serverContext;
        this.clientConnection = clientConnection;
        this.id = id;
    }

    public static IncomingConnectionContext connectionContext(final ActiveServerContext context, final ClientConnection<?> clientConnection) {
        return new IncomingConnectionContext(context, clientConnection, ULID.randomULID());
    }

    public ULID id() {
        return id;
    }

    public ActiveServerContext serverContext() {
        return serverContext;
    }

    public void register() {
        serverContext.registry().add(this);
    }

    public void deregister() {
        serverContext.registry().remove(this);
    }

    public ClientConnection<?> clientConnection() {
        return clientConnection;
    }

    public void processConnection(final Promise<Unit> completionPromise) {
        serverContext.lifeCycle()
                     .process(this, completionPromise);
    }

    public FileDescriptor socket() {
        return clientConnection.socket();
    }
}

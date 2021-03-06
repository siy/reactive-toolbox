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

import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.net.SocketAddress;

/**
 * Initial context for every client connection.
 */
public class ClientContext {
    private final FileDescriptor socket;
    private final SocketAddress<?> serverAddress;
    private final SocketAddress<?> clientAddress;

    private ClientContext(final FileDescriptor socket, final SocketAddress<?> serverAddress, final SocketAddress<?> clientAddress) {
        this.socket = socket;
        this.serverAddress = serverAddress;
        this.clientAddress = clientAddress;
    }

    public static ClientContext context(final FileDescriptor socket, final SocketAddress<?> serverAddress, final SocketAddress<?> clientAddress) {
        return new ClientContext(socket, serverAddress, clientAddress);
    }
}

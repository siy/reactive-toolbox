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
import org.reactivetoolbox.core.log.CoreLogger;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;

public class ReadConnectionContext {
    private final IncomingConnectionContext incomingConnectionContext;
    private final OffHeapBuffer buffer;
    private final Promise<Unit> onClose;

    private ReadConnectionContext(final IncomingConnectionContext incomingConnectionContext,
                                  final OffHeapBuffer buffer,
                                  final Promise<Unit> onClose) {
        this.incomingConnectionContext = incomingConnectionContext;
        this.buffer = buffer;
        this.onClose = onClose;
    }

    public static ReadConnectionContext readConnectionContext(final IncomingConnectionContext incomingConnectionContext,
                                                              final OffHeapBuffer buffer,
                                                              final Promise<Unit> onClose) {
        return new ReadConnectionContext(incomingConnectionContext, buffer, onClose);
    }

    public IncomingConnectionContext connectionContext() {
        return incomingConnectionContext;
    }

    public OffHeapBuffer buffer() {
        return buffer;
    }

    public FileDescriptor socket() {
        return incomingConnectionContext.clientConnection().socket();
    }

    public Promise<Unit> onClose() {
        return onClose;
    }

    public CoreLogger logger() {
        return onClose.logger();
    }
}

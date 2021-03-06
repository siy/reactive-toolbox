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

package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.net.AddressFamily;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketOption;
import org.reactivetoolbox.io.async.net.SocketType;
import org.reactivetoolbox.io.uring.UringHolder;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.util.Set;
import java.util.function.BiConsumer;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_NOP;

public class SocketExchangeEntry extends AbstractExchangeEntry<SocketExchangeEntry, FileDescriptor> {
    private AddressFamily addressFamily;
    private SocketType socketType;
    private Set<SocketFlag> openFlags;
    private Set<SocketOption> options;

    protected SocketExchangeEntry(final PlainObjectPool<SocketExchangeEntry> pool) {
        super(IORING_OP_NOP, pool);
    }

    @Override
    protected void doAccept(final int result, final int flags, final Submitter submitter) {
        completion.accept(UringHolder.socket(addressFamily,
                                             socketType,
                                             openFlags,
                                             options),
                          submitter);
    }

    public SocketExchangeEntry prepare(final BiConsumer<Result<FileDescriptor>, Submitter> completion,
                                       final AddressFamily addressFamily,
                                       final SocketType socketType,
                                       final Set<SocketFlag> openFlags,
                                       final Set<SocketOption> options) {
        this.addressFamily = addressFamily;
        this.socketType = socketType;
        this.openFlags = openFlags;
        this.options = options;
        return super.prepare(completion);
    }
}

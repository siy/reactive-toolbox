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
import org.reactivetoolbox.io.Bitmask;
import org.reactivetoolbox.io.NativeFailureType;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.file.SpliceDescriptor;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.util.function.BiConsumer;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_SPLICE;

public class SpliceExchangeEntry extends AbstractExchangeEntry<SpliceExchangeEntry, SizeT> {
    private SpliceDescriptor descriptor;
    private byte flags;

    protected SpliceExchangeEntry(final PlainObjectPool<SpliceExchangeEntry> pool) {
        super(IORING_OP_SPLICE, pool);
    }

    @Override
    protected void doAccept(final int res, final int flags, final Submitter submitter) {
        completion.accept(byteCountToResult(res), submitter);
    }

    @Override
    public SubmitQueueEntry apply(final SubmitQueueEntry entry) {
        return super.apply(entry)
                    .flags(flags)
                    .fd(descriptor.toDescriptor().descriptor())
                    .len((int) descriptor.bytesToCopy().value())
                    .off(descriptor.toOffset().value())
                    .spliceFdIn(descriptor.fromDescriptor().descriptor())
                    .spliceOffIn(descriptor.fromOffset().value())
                    .spliceFlags(Bitmask.combine(descriptor.flags()));
    }

    public SpliceExchangeEntry prepare(final BiConsumer<Result<SizeT>, Submitter> completion,
                                       final SpliceDescriptor descriptor,
                                       final byte flags) {
        this.flags = flags;
        this.descriptor = descriptor;
        return super.prepare(completion);
    }

    private Result<SizeT> byteCountToResult(final int res) {
        return res > 0
               ? sizeResult(res)
               : NativeFailureType.result(res);
    }
}

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
import org.reactivetoolbox.io.NativeFailureType;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapIoVector;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.util.function.BiConsumer;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_WRITEV;

public class WriteVectorExchangeEntry extends AbstractExchangeEntry<WriteVectorExchangeEntry, SizeT> {
    private static final Result<SizeT> EOF_RESULT = Result.fail(NativeFailureType.ENODATA.asFailure());

    private OffHeapIoVector ioVector;
    private byte flags;
    private int descriptor;
    private long offset;

    protected WriteVectorExchangeEntry(final PlainObjectPool<WriteVectorExchangeEntry> pool) {
        super(IORING_OP_WRITEV, pool);
    }

    @Override
    protected void doAccept(final int res, final int flags, final Submitter submitter) {
        completion.accept(byteCountToResult(res), submitter);
        ioVector.dispose();
        ioVector = null;
    }

    @Override
    public SubmitQueueEntry apply(final SubmitQueueEntry entry) {
        return super.apply(entry)
                    .flags(flags)
                    .fd(descriptor)
                    .addr(ioVector.address())
                    .len(ioVector.length())
                    .off(offset);
    }

    public WriteVectorExchangeEntry prepare(final BiConsumer<Result<SizeT>, Submitter> completion,
                                            final int descriptor,
                                            final long offset,
                                            final byte flags,
                                            final OffHeapIoVector ioVector) {
        this.descriptor = descriptor;
        this.offset = offset;
        this.flags = flags;
        this.ioVector = ioVector;
        return super.prepare(completion);
    }

    private Result<SizeT> byteCountToResult(final int res) {
        return res > 0
               ? sizeResult(res)
               : NativeFailureType.result(res);
    }
}

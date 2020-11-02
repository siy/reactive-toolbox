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

import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_NOP;

public class NopExchangeEntry extends AbstractExchangeEntry<NopExchangeEntry, Unit> {
    protected NopExchangeEntry(final PlainObjectPool<NopExchangeEntry> pool) {
        super(IORING_OP_NOP, pool);
    }

    @Override
    protected void doAccept(final int result, final int flags, final Submitter submitter) {
        completion.accept(UNIT_RESULT, submitter);
    }
}

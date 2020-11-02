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

package org.reactivetoolbox.io.examples.async;

import org.junit.jupiter.api.Test;

import static org.reactivetoolbox.core.Errors.TIMEOUT;
import static org.reactivetoolbox.core.lang.functional.Result.fail;
import static org.reactivetoolbox.io.async.Promises.all;
import static org.reactivetoolbox.io.scheduler.Timeout.timeout;

public class PromiseAllExample_Test {
    private final AsyncService service = new AsyncService();

    @Test
    void simpleAsyncTask() {
        service.slowRetrieveInteger(42)
               .onSuccess(System.out::println)
               .syncWait();
    }

    @Test
    void simpleAsyncTaskWithTimeout() {
        service.slowRetrieveInteger(4242)
               .when(timeout(10).seconds(), fail(TIMEOUT))
               .onSuccess(System.out::println)
               .syncWait();
    }

    @Test
    void waitForAllResults1() {
        all(service.slowRetrieveInteger(123),
            service.slowRetrieveString("text 1"),
            service.slowRetrieveUuid())
                .onSuccess(System.out::println)
                .syncWait();
    }
}

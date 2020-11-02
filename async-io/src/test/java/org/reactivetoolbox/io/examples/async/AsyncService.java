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

import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.meta.AppMetaRepository;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.scheduler.TaskScheduler;
import org.reactivetoolbox.io.scheduler.Timeout;

import java.util.UUID;

public class AsyncService {
    private static final Timeout DEFAULT_DELAY = Timeout.timeout(50).millis();

    public Promise<Integer> slowRetrieveInteger(final Integer value) {
        return slowRetrieveInteger(DEFAULT_DELAY, value);
    }

    public Promise<Integer> slowRetrieveInteger(final Timeout delay, final Integer value) {
        return slowRetrieve(Integer.class, delay, value);
    }

    public Promise<String> slowRetrieveString(final String value) {
        return slowRetrieveString(DEFAULT_DELAY, value);
    }

    public Promise<String> slowRetrieveString(final Timeout delay, final String value) {
        return slowRetrieve(String.class, delay, value);
    }

    public Promise<UUID> slowRetrieveUuid() {
        return slowRetrieveUuid(UUID.randomUUID());
    }

    public Promise<UUID> slowRetrieveUuid(final UUID value) {
        return slowRetrieveUuid(DEFAULT_DELAY, value);
    }

    public Promise<UUID> slowRetrieveUuid(final Timeout delay, final UUID value) {
        return slowRetrieve(UUID.class, delay, value);
    }

    private static <T> Promise<T> slowRetrieve(final Class<T> clazz, final Timeout delay, final T value) {
        return Promise.<T>promise().when(delay, Result.ok(value));
    }

    private static final class TaskSchedulerHolder {
        private static final TaskScheduler taskScheduler = AppMetaRepository.instance().get(TaskScheduler.class);

        static TaskScheduler instance() {
            return taskScheduler;
        }
    }
}

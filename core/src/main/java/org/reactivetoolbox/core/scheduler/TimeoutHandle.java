package org.reactivetoolbox.core.scheduler;
/*
 * Copyright (c) 2017-2019 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * {@link TimeoutScheduler} interface for task submission.
 */
public interface TimeoutHandle {
    /**
     * Submit task for execution after specified timeout
     *
     * @param timeout
     *        Execution timeout
     * @param runnable
     *        Task to execute
     * @return Current instance
     */
    TimeoutHandle submit(Timeout timeout, Runnable runnable);

    /**
     * Release handle. Once handle is released, no tasks can be submitted.
     */
    void release();
}

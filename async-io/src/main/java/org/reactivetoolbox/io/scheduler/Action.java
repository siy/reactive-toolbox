/*
 * Copyright (c) 2019, 2020 Sergiy Yevtushenko
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

package org.reactivetoolbox.io.scheduler;

import org.reactivetoolbox.io.async.Submitter;

/**
 * Single task which accepts current time stamp and returns a decision, should it be scheduled for next execution round or not. If {@link #perform(long, Submitter)} returns
 * {@code true} then task is considered finished and not scheduled for the next round. Otherwise task will be called again as soon as possible according to scheduling policy used
 * by scheduler implementation.
 */
@FunctionalInterface
public interface Action {
    /**
     * @param nanoTime
     *         Current {@link System#nanoTime()} timestamp
     * @return {@code true} if task is completed or {@code false} if task should be rescheduled again
     */
    boolean perform(final long nanoTime);
}

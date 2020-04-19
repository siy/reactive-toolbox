package org.reactivetoolbox.core.scheduler;

/*
 * Copyright (c) 2019 Sergiy Yevtushenko
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
 * Single task which accepts current time stamp and returns a decision, should
 * it be scheduled for next execution round or not. If {@link #isDone(long)}
 * returns <code>true</code> then task is considered finished and not scheduled
 * for the next round.
 */
@FunctionalInterface
public interface RunnablePredicate {
    boolean isDone(final long nanoTime);
}

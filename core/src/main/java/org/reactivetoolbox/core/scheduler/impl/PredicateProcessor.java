package org.reactivetoolbox.core.scheduler.impl;

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

import org.reactivetoolbox.core.scheduler.RunnablePredicate;

import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Single processing pipeline for {@link RunnablePredicate} tasks. Incoming tasks are placed into incoming queue,
 * while tasks are processed from processing queue. Once processing queue is empty queues are swapped.
 * In order to make this processor work, its {@link #processTimeoutsOnce()} method must be invoked as frequently
 * as possible.
 */
public class PredicateProcessor {
    private volatile Queue<RunnablePredicate> processingQueue = new LinkedTransferQueue<>();
    private final AtomicReference<Queue<RunnablePredicate>> incomingQueue = new AtomicReference<>(new LinkedTransferQueue<>());

    public void submit(final RunnablePredicate runnablePredicate) {
        incomingQueue.get().add(runnablePredicate);
    }

    public void processTimeoutsOnce() {
        while (true) {
            final var element = processingQueue.poll();
            if (element == null) {
                swapQueues();
                return;
            }

            if (!element.isDone(System.nanoTime())) {
                submit(element);
            }
        }
    }

    private void swapQueues() {
        processingQueue = incomingQueue.compareAndExchange(incomingQueue.get(), processingQueue);
    }
}

package org.reactivetoolbox.io.scheduler.impl;

/*
 * Copyright (c) 2019, 2020 Sergiy Yevtushenko
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

import org.reactivetoolbox.io.Proactor;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.scheduler.Action;

import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Single processing pipeline for {@link Action} tasks. Incoming tasks are placed into incoming queue,
 * while tasks are processed from processing queue. Once processing queue is empty queues are swapped.
 * In order to make this processor work, its {@link #processTasks(Submitter)} method must be invoked
 * as frequently as possible.
 */
public class ActionProcessor {
    private volatile Queue<Action> processingQueue = new LinkedTransferQueue<>();
    private final AtomicReference<Queue<Action>> incomingQueue = new AtomicReference<>(new LinkedTransferQueue<>());

    private ActionProcessor() {}

    public static ActionProcessor actionProcessor() {
        return new ActionProcessor();
    }

    public void submit(final Action action) {
        incomingQueue.get().add(action);
    }

    public void processTasks(final Submitter submitter) {
        while (true) {
            final var element = processingQueue.poll();
            if (element == null) {
                swapQueues();
                return;
            }

            if (!element.perform(System.nanoTime(), submitter)) {
                submit(element);
            }
        }
    }

    private void swapQueues() {
        processingQueue = incomingQueue.compareAndExchange(incomingQueue.get(), processingQueue);
    }
}

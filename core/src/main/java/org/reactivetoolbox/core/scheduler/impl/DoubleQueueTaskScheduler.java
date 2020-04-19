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

import org.reactivetoolbox.core.log.CoreLogger;
import org.reactivetoolbox.core.meta.AppMetaRepository;
import org.reactivetoolbox.core.scheduler.RunnablePredicate;
import org.reactivetoolbox.core.scheduler.TaskScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.IntStream.range;

/**
 * Task scheduler tuned to large number of short tasks.
 */
public class DoubleQueueTaskScheduler implements TaskScheduler {
    private final ExecutorService executor;
    private final PredicateProcessor[] processors;
    private int counter = 0;

    private DoubleQueueTaskScheduler(final int size) {
        executor = Executors.newFixedThreadPool(size, DaemonThreadFactory.of("Task Scheduler Thread #%d"));
        processors = new PredicateProcessor[size];

        range(0, size).forEach(n -> {
            processors[n] = new PredicateProcessor();
            executor.execute(() -> {
                while (!executor.isShutdown()) {
                    processors[n].processTimeoutsOnce();
                    try {
                        Thread.sleep(0);
                    } catch (final InterruptedException e){
                        logger().debug("Exception in scheduler processing loop", e);
                    }
                }
            });
        });
    }

    public static DoubleQueueTaskScheduler with(final int size) {
        return new DoubleQueueTaskScheduler(size);
    }

    @Override
    public TaskScheduler submit(final RunnablePredicate predicate) {
        if (executor.isShutdown()) {
            throw new IllegalStateException("Attempt to submit new task after scheduler is shut down");
        }
        processors[counter = (counter + 1) % processors.length].submit(predicate);
        return this;
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }


    @Override
    public CoreLogger logger() {
        return SingletonHolder.logger();
    }

    private static final class SingletonHolder {
        private static final CoreLogger LOGGER = AppMetaRepository.instance().get(CoreLogger.class);

        static CoreLogger logger() {
            return LOGGER;
        }
    }
}

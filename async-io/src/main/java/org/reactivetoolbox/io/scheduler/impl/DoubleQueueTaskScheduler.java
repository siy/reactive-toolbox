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

import org.reactivetoolbox.core.log.CoreLogger;
import org.reactivetoolbox.core.meta.AppMetaRepository;
import org.reactivetoolbox.io.Proactor;
import org.reactivetoolbox.io.scheduler.Action;
import org.reactivetoolbox.io.scheduler.TaskScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.IntStream.range;
import static org.reactivetoolbox.core.lang.collection.List.list;
import static org.reactivetoolbox.io.Proactor.proactor;
import static org.reactivetoolbox.io.scheduler.impl.ActionProcessor.actionProcessor;

/**
 * Task scheduler tuned to large number of short tasks.
 */
public class DoubleQueueTaskScheduler implements TaskScheduler {
    private final ExecutorService executor;
    private final ActionProcessor[] processors;
    private final Proactor[] proactors;
    private int counter = 0;

    private DoubleQueueTaskScheduler(final int size) {
        executor = Executors.newFixedThreadPool(size, DaemonThreadFactory.of("Task Scheduler Thread #%d"));
        processors = new ActionProcessor[size];
        proactors = new Proactor[size];

        range(0, size).forEach(n -> {
            processors[n] = actionProcessor();
            proactors[n] = proactor();
            executor.execute(() -> {
                while (!executor.isShutdown()) {
                    processors[n].processTasks(proactors[n]);
                    proactors[n].processIO();
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
    public TaskScheduler submit(final Action action) {
        if (executor.isShutdown()) {
            throw new IllegalStateException("Attempt to submit new task after scheduler is shut down");
        }
        final int index = counter = (counter + 1) % processors.length;
        processors[index].submit(action);
        return this;
    }

    @Override
    public void shutdown() {
        executor.shutdown();
        list(proactors).apply(Proactor::close);
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

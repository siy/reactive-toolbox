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
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.util.StackingCollector;
import org.reactivetoolbox.io.scheduler.TaskScheduler;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.util.stream.IntStream.range;

/**
 * Task scheduler tuned to handle large number of short tasks.
 */
public class PipelinedTaskScheduler implements TaskScheduler {
    private final ExecutorService executor;
    private final java.util.List<StackingCollector<Runnable>> pipelines = new ArrayList<>();
    private final java.util.List<Proactor> proactors = new ArrayList<>();
    private int counter = 0;

    private PipelinedTaskScheduler(final int size) {
        executor = Executors.newFixedThreadPool(size, DaemonThreadFactory.threadFactory("Task Scheduler Thread #%d"));

        range(0, size).forEach(n -> {
            final var pipeline = StackingCollector.<Runnable>stackingCollector();
            final var proactor = Proactor.proactor();
            pipelines.add(pipeline);
            proactors.add(proactor);
            executor.execute(createWorker(pipeline, proactor));
        });
    }

    public static PipelinedTaskScheduler with(final int size) {
        return new PipelinedTaskScheduler(size);
    }

    @Override
    public TaskScheduler submit(final Runnable runnable) {
        final var index = counter = (counter + 1) % pipelines.size();
        pipelines.get(index).push(runnable);
        return this;
    }

    @Override
    public TaskScheduler submit(final Consumer<Submitter> ioAction) {
        final var index = counter = (counter + 1) % pipelines.size();
        pipelines.get(index).push(() -> ioAction.accept(proactors.get(index)));
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

    private Runnable createWorker(final StackingCollector<Runnable> pipeline, final Proactor proactor) {
        return () -> {
            int idleRunCount = 0;

            while (!executor.isShutdown()) {
                var head = pipeline.swapHead();

                if (head == null) {
                    idleRunCount++;

                    if (idleRunCount == 2048) {
                        Thread.yield();
                        idleRunCount = 0;
                    }
                } else {
                    final int cnt = 0;
                    while (head != null) {
                        head.element.run();
                        head = head.nextNode;
                    }
                }
                proactor.processIO();
            }
            proactor.close();
        };
    }

    private static final class SingletonHolder {
        private static final CoreLogger LOGGER = AppMetaRepository.instance().get(CoreLogger.class);

        static CoreLogger logger() {
            return LOGGER;
        }
    }
}

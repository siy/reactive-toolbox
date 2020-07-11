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

import static java.util.stream.IntStream.range;

/**
 * Task scheduler tuned to handle large number of short tasks.
 */
public class PipelinedTaskScheduler implements TaskScheduler {
    private final ExecutorService executor;
    private final java.util.List<StackingCollector<Runnable>> processors = new ArrayList<>();
    private final ThreadLocal<Proactor> proactors = new ThreadLocal<>();
    private int counter = 0;

    private PipelinedTaskScheduler(final int size) {
        executor = Executors.newFixedThreadPool(size, DaemonThreadFactory.of("Task Scheduler Thread #%d"));

        range(0, size).forEach(n -> {
            final var pipeline = StackingCollector.<Runnable>stackingCollector();
            processors.add(pipeline);
            executor.execute(createWorker(pipeline));
        });
    }

    public static PipelinedTaskScheduler with(final int size) {
        return new PipelinedTaskScheduler(size);
    }

    @Override
    public TaskScheduler submit(final Runnable runnable) {
        if (executor.isShutdown()) {
            throw new IllegalStateException("Attempt to submit new task after scheduler is shut down");
        }
        final var index = counter = (counter + 1) % processors.size();
        processors.get(index).push(runnable);
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

    @Override
    public Submitter localSubmitter() {
        return proactors.get();
    }

    private Runnable createWorker(final StackingCollector<Runnable> pipeline) {
        return () -> {
            final var proactor = Proactor.proactor();
            proactors.set(proactor);

            int idleRunCount = 0;

            while (!executor.isShutdown()) {
                if (!pipeline.swapAndApply(Runnable::run)) {
                    idleRunCount++;

                    if (idleRunCount == 2048) {
                        Thread.yield();
                        idleRunCount = 0;
                    }
                }
                proactor.processIO();
            }
            proactors.remove();
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

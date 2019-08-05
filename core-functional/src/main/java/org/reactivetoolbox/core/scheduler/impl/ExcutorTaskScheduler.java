package org.reactivetoolbox.core.scheduler.impl;

import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.scheduler.TaskScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ExcutorTaskScheduler implements TaskScheduler {
    private final ExecutorService service;

    private ExcutorTaskScheduler(final int size) {
        service = Executors.newFixedThreadPool(size, DaemonThreadFactory.of("Task Scheduler Thread #%d"));
    }

    public static ExcutorTaskScheduler with(final int size) {
        return new ExcutorTaskScheduler(size);
    }

    @Override
    public <T> Promise<T> submit(final Promise<T> promise, final Consumer<Promise<T>>task) {
        service.submit(() -> task.accept(promise));
        return promise;
    }
}

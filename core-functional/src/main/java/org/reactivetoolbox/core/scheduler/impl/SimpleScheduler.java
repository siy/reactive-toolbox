package org.reactivetoolbox.core.scheduler.impl;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.scheduler.OneTimeTask;
import org.reactivetoolbox.core.scheduler.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.IntStream.range;

public class SimpleScheduler {
    private final RingBuffer<OneTimeTask> buffer;
    private final ExecutorService collector;

    private SimpleScheduler(final int size) {
        buffer = RingBuffer.with(size);
        collector = Executors.newFixedThreadPool(size/4, SimpleScheduler::newDaemonThread);
        range(0, size/4)
                .forEach(n -> collector.submit(this::taskProcessor));
        //collector.submit(this::taskProcessor);
        //executor = Executors.newFixedThreadPool(1, SimpleScheduler::newDaemonThread);
    }

    public static SimpleScheduler with(final int size) {
        return new SimpleScheduler(size);
    }

    public Either<? extends BaseError, TimeoutScheduler> request() {
        return buffer.request()
                     .mapSuccess(TimeoutScheduler::new);
    }

    public static class TimeoutScheduler {
        private final BufferEntry<OneTimeTask> entry;

        public TimeoutScheduler(final BufferEntry<OneTimeTask> entry) {
            this.entry = entry;
        }

        public TimeoutScheduler submit(final Runnable runnable, final Timeout timeout) {
            entry.add(OneTimeTask.of(runnable, timeout));
            return this;
        }

        public TimeoutScheduler release() {
            entry.release();
            return this;
        }
    }

    private void taskProcessor() {
        final NavigableMap<Timeout, List<Runnable>> tasks = new TreeMap<>();

        while (true)
        {
            buffer.request()
                  .onSuccess(bufferEntry -> processSubmission(bufferEntry, tasks));
            Thread.yield();
        }
    }

    private void processSubmission(final BufferEntry<OneTimeTask> bufferEntry,
                                   final NavigableMap<Timeout, List<Runnable>> tasks) {
        final var elements = bufferEntry.purge();
        bufferEntry.release();

        elements.forEach(task -> tasks.compute(task.timeout(),
                                               (key, oldVal) -> {
                                                    final List<Runnable> newVal = (oldVal == null) ? new ArrayList<>() : oldVal;
                                                    newVal.add(task.runnable());
                                                    return newVal;
                                               }));

        if (tasks.isEmpty()) {
            return;
        }

        final var timeNow = Timeout.of(0).millis();
        final var iter = tasks.entrySet().iterator();

        while (iter.hasNext()) {
            final var entry = iter.next();

            if (entry.getKey().compareTo(timeNow) > 0) {
                return;
            }

            iter.remove();
            entry.getValue().forEach(Runnable::run);
        }
    }

    private static final AtomicInteger counter = new AtomicInteger();

    public static Thread newDaemonThread(final Runnable r) {
        final var result = new Thread(r);
        result.setName("Daemon Thread #" + counter.getAndIncrement());
        result.setDaemon(true);
        return result;
    }
}

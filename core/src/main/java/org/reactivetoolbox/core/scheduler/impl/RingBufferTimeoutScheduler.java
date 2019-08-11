package org.reactivetoolbox.core.scheduler.impl;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.scheduler.TimeoutHandle;
import org.reactivetoolbox.core.scheduler.TimeoutScheduler;
import org.reactivetoolbox.core.scheduler.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.IntStream.range;

public class RingBufferTimeoutScheduler implements TimeoutScheduler {
    private final RingBuffer<OneTimeTask> buffer;
    private final ExecutorService collector;

    private RingBufferTimeoutScheduler(final int size) {
        final int bufferSize = Math.max(size, 4) * 4;

        buffer = RingBuffer.with(bufferSize);
        collector = Executors.newFixedThreadPool(bufferSize/4,
                                                 DaemonThreadFactory.of("Timeout Scheduler Thread #%d"));
        range(0, size/4)
                .forEach(n -> collector.execute(this::taskProcessor));
    }

    public static TimeoutScheduler with(final int size) {
        return new RingBufferTimeoutScheduler(size);
    }

    @Override
    public Either<? extends BaseError, TimeoutHandle> request() {
        return buffer.request()
                     .mapSuccess(BufferEntryTimeoutHandle::of);
    }

    private void taskProcessor() {
        final NavigableMap<Timestamp, List<Runnable >> tasks = new TreeMap<>();

        do {
            buffer.request()
                  .onSuccess(bufferEntry -> processSubmission(bufferEntry, tasks));
            Thread.yield();
        } while (!collector.isShutdown());
    }

    private void processSubmission(final BufferEntry<OneTimeTask> bufferEntry,
                                   final NavigableMap<Timestamp, List<Runnable>> tasks) {
        final var elements = bufferEntry.purge();
        bufferEntry.release();

        elements.forEach(task -> tasks.compute(task.timestamp(),
                                               (key, oldVal) -> {
                                                    final List<Runnable> newVal = (oldVal == null) ? new ArrayList<>() : oldVal;
                                                    newVal.add(task.runnable());
                                                    return newVal;
                                               }));

        if (tasks.isEmpty()) {
            return;
        }

        final var timeNow = Timestamp.of(System.nanoTime());
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
}

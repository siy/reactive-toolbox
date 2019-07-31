package org.reactivetoolbox.core.scheduler.impl;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DaemonThreadFactory implements ThreadFactory {
    private final AtomicInteger counter = new AtomicInteger();
    private final String pattern;

    private DaemonThreadFactory(final String pattern) {
        this.pattern = pattern;
    }

    public static DaemonThreadFactory of(final String pattern) {
        return new DaemonThreadFactory(pattern);
    }

    @Override
    public Thread newThread(final Runnable r) {
        final var result = new Thread(r);
        result.setName(String.format(pattern, counter.getAndIncrement()));
        result.setDaemon(true);
        return result;
    }
}

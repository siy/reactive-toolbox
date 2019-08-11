package org.reactivetoolbox.core.scheduler.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BufferEntry<T> {
    private final AtomicBoolean locked = new AtomicBoolean();
    private final List<T> elements = new ArrayList<>();

    public List<T> purge() {
        if (locked.get()) {
            final var result = List.copyOf(elements);
            elements.clear();
            return result;
        }
        return List.of();
    }

    public boolean add(final T element) {
        if (locked.get()) {
            elements.add(element);
            return true;
        }
        return false;
    }

    public boolean lock() {
        return locked.compareAndSet(false, true);
    }

    public boolean release() {
        return locked.compareAndSet(true, false);
    }

    public boolean locked() {
        return locked.get();
    }
}

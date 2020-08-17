package org.reactivetoolbox.io.uring.utils;

public interface Poolable<T extends Poolable<T>> {
    T next();
    T next(final T next);
}

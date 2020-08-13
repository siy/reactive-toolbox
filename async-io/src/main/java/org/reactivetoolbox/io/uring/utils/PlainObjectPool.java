package org.reactivetoolbox.io.uring.utils;

import org.reactivetoolbox.core.lang.functional.Functions.FN1;

public class PlainObjectPool<T extends Poolable> {
    private T head;
    private final FN1<T, PlainObjectPool<T>> factory;

    public PlainObjectPool(final FN1<T, PlainObjectPool<T>> factory) {
        this.factory = factory;
    }

    public T alloc() {
        final T result = head;

        if (head == null) {
            return factory.apply(this);
        }

        //noinspection unchecked
        head = (T) result.next();
        //noinspection unchecked
        result.next(null);

        return result;
    }

    public void release(final T element) {
        //noinspection unchecked
        element.next(head);
        head = element;
    }

    public void clear() {
        while (head != null) {
            final T element = head;
            //noinspection unchecked
            head = (T) element.next();
            //noinspection unchecked
            element.next(null);
        }
    }
}

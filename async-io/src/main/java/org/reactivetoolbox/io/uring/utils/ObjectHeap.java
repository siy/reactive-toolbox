package org.reactivetoolbox.io.uring.utils;

import java.util.Arrays;

/**
 * Temporary storage for objects.
 * Main use case - storing object corresponding to in-flight requests
 * and obtaining integer index which can be passed instead of the whole object
 * to external entity. Upon request completion object then is released from the
 * heap by using corresponding integer index.
 *
 * @param <T>
 */
public class ObjectHeap<T> {
    private static final int INITIAL_SIZE = 256;

    private Object[] elements;
    private int[] indexes;
    private int firstFree = -1;
    private int nextFree = 0;
    private int count = 0;

    private ObjectHeap(final int initialCapacity) {
        elements = new Object[initialCapacity];
        indexes = new int[initialCapacity];
    }

    public static <T> ObjectHeap<T> objectHeap() {
        return objectHeap(INITIAL_SIZE);
    }

    public static <T> ObjectHeap<T> objectHeap(final int initialCapacity) {
        return new ObjectHeap<>(initialCapacity);
    }

    public T releaseUnsafe(final int key) {
        if (key < 0 || key >= nextFree || elements[key] == null || key == firstFree) {
            System.out.printf("key: %d, nextFree: %d, elements[key] is %s, firstFree: %d",
                              key, nextFree, elements[key], firstFree);
            return null;
        }

        indexes[key] = firstFree;
        firstFree = key;
        final T result = (T) elements[key];
        elements[key] = null;
        count--;
        return result;
    }

    public int allocKey(final T value) {
        // There are some free elements
        if (firstFree >= 0) {
            return allocInFreeChain(value);
        }

        // No free elements, but still some space
        if (nextFree < elements.length) {
            return allocNew(value);
        }

        // No free elements and no free space, realloc everything
        indexes = Arrays.copyOf(indexes, indexes.length * 2);
        elements = Arrays.copyOf(elements, elements.length * 2);

        return allocNew(value);
    }

    private int allocNew(final T value) {
        final int index = nextFree++;
        indexes[index] = firstFree;
        firstFree = index;
        return allocInFreeChain(value);
    }

    private int allocInFreeChain(final T value) {
        final int result = firstFree;
        elements[result] = value;
        firstFree = indexes[result];
        count++;
        return result;
    }

    public int count() {
        return count;
    }
}
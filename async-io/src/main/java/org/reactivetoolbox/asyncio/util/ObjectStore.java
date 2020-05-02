package org.reactivetoolbox.asyncio.util;

import java.util.Arrays;

public class ObjectStore<T> {
    private static final int INITIAL_SIZE = 256;

    private Object[] elements;
    private int[] indexes;
    private int firstFree = -1;
    private int nextFree = 0;
    private int count = 0;

    private ObjectStore(final int initialCapacity) {
        elements = new Object[initialCapacity];
        indexes = new int[initialCapacity];
    }

    public static <T> ObjectStore<T> objectPool() {
        return objectPool(INITIAL_SIZE);
    }

    public static <T> ObjectStore<T> objectPool(final int initialCapacity) {
        return new ObjectStore<>(initialCapacity);
    }

    @SuppressWarnings("unchecked")
    T release(final int key) {
        if (key < 0 || key >= nextFree || elements[key] == null) {
            return null;
        }

        indexes[key] = firstFree;
        firstFree = key;
        final T result = (T) elements[key];
        elements[key] = null;
        count--;
        return result;
    }

    int alloc(final T value) {
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

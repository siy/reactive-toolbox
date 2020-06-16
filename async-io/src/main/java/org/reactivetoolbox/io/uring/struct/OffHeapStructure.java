package org.reactivetoolbox.io.uring.struct;

/**
 * Base class for classes which are used for passing data to JNI side.
 * Note that these classes are allocating and deallocating off-heap memory.
 * Lifecycle of instances of such classes should be carefully tracked to avoid
 * memory leaks.
 */
public interface OffHeapStructure<T extends OffHeapStructure<?>> extends RawStructure<T> {
    void dispose();
}

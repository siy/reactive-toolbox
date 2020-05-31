package org.reactivetoolbox.io.uring.structs;

/**
 * Base class for classes which are used for passing data to JNI side.
 * Note that these classes are allocating and deallocating off-heap memory.
 * Lifecycle of instances of such classes should be carefully traced to avoid
 * memory leaks.
 */
public interface DisposableStructure<T extends DisposableStructure<?>> extends RawStructure<T> {
    void dispose();
}

package org.reactivetoolbox.io.uring.structs;

public interface RawStructure<T extends RawStructure<T>> {
    T clear();
    T reposition(final long address);
}

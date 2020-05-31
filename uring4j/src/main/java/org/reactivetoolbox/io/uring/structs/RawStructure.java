package org.reactivetoolbox.io.uring.structs;

public interface RawStructure<T extends RawStructure<?>> {
    long address();

    int size();

    T clear();
}

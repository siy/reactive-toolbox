package org.reactivetoolbox.io.uring.struct;

public interface RawStructure<T extends RawStructure<?>> {
    long address();

    int size();

    T clear();
}

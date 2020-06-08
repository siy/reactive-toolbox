package org.reactivetoolbox.io.uring.struct;

public interface ExternalRawStructure<T extends ExternalRawStructure<T>> extends RawStructure<T> {
    T reposition(final long address);
}

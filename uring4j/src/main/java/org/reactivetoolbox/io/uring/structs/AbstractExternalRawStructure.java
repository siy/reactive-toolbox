package org.reactivetoolbox.io.uring.structs;

abstract class AbstractExternalRawStructure<T extends ExternalRawStructure<T>> extends AbstractRawStructure<T> implements ExternalRawStructure<T> {
    AbstractExternalRawStructure(final long address, final int size) {
        super(address, size);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T reposition(final long address) {
        address(address);
        return (T) this;
    }
}

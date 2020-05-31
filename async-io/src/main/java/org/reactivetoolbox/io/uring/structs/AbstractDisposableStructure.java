package org.reactivetoolbox.io.uring.structs;

import org.reactivetoolbox.io.raw.RawMemory;

abstract class AbstractDisposableStructure<T extends AbstractDisposableStructure<?>> extends AbstractRawStructure<T> implements DisposableStructure<T> {
    private boolean released = false;

    protected AbstractDisposableStructure(final int size) {
        super(RawMemory.allocate(size), size);
    }

    @Override
    public void dispose() {
        if (released) {
            return;
        }

        RawMemory.dispose(address());
        released = true;
    }
}

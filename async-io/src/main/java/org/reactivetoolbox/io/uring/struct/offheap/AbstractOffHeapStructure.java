package org.reactivetoolbox.io.uring.struct.offheap;

import org.reactivetoolbox.io.raw.RawMemory;
import org.reactivetoolbox.io.uring.struct.AbstractRawStructure;
import org.reactivetoolbox.io.uring.struct.OffHeapStructure;

public abstract class AbstractOffHeapStructure<T extends AbstractOffHeapStructure<?>> extends AbstractRawStructure<T>
        implements OffHeapStructure<T>, AutoCloseable {
    private boolean released = false;

    protected AbstractOffHeapStructure(final int size) {
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

    @Override
    public void close() {
        dispose();
    }
}

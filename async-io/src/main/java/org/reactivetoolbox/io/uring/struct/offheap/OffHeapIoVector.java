package org.reactivetoolbox.io.uring.struct.offheap;

import org.reactivetoolbox.io.async.util.OffHeapBuffer;
import org.reactivetoolbox.io.uring.struct.AbstractOffHeapStructure;
import org.reactivetoolbox.io.uring.struct.raw.IoVector;
import org.reactivetoolbox.io.uring.struct.shape.IoVectorOffsets;

public class OffHeapIoVector extends AbstractOffHeapStructure<OffHeapIoVector> {
    private final IoVector shape;

    private OffHeapIoVector(final int count) {
        super(count * IoVectorOffsets.SIZE);
        shape = IoVector.at(address());
    }

    private void addBuffer(final OffHeapBuffer buffer) {
        shape.base(buffer.address()).len(buffer.used()).reposition(shape.address() + IoVectorOffsets.SIZE);
    }

    private void resetShape() {
        shape.reposition(address());
    }

    public OffHeapIoVector forBuffers(final OffHeapBuffer ... buffers) {
        final var vector = new OffHeapIoVector(buffers.length);
        for(var buffer : buffers) {
            addBuffer(buffer);
        }
        resetShape();
        return vector;
    }
}

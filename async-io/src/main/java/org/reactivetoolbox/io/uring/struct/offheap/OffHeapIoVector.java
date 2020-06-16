package org.reactivetoolbox.io.uring.struct.offheap;

import org.reactivetoolbox.io.async.util.OffHeapBuffer;
import org.reactivetoolbox.io.uring.struct.raw.IoVector;
import org.reactivetoolbox.io.uring.struct.shape.IoVectorOffsets;

public class OffHeapIoVector extends AbstractOffHeapStructure<OffHeapIoVector> {
    private final IoVector shape;
    private final int count;

    private OffHeapIoVector(final int count) {
        super(count * IoVectorOffsets.SIZE);
        this.count = count;
        clear();
        shape = IoVector.at(address());
    }

    private void addBuffer(final OffHeapBuffer buffer) {
        shape.base(buffer.address())
             .len(buffer.used())
             .reposition(shape.address() + IoVectorOffsets.SIZE);
    }

    private void resetShape() {
        shape.reposition(address());
    }

    public static OffHeapIoVector withBuffers(final OffHeapBuffer ... buffers) {
        final var vector = new OffHeapIoVector(buffers.length);
        for(final var buffer : buffers) {
            vector.addBuffer(buffer);
        }
        vector.resetShape();
        return vector;
    }

    public int length() {
        return count;
    }
}

package org.reactivetoolbox.io.uring.struct.offheap;

import org.reactivetoolbox.io.async.file.stat.FileStat;
import org.reactivetoolbox.io.uring.struct.raw.RawStatx;
import org.reactivetoolbox.io.uring.struct.shape.StatxOffsets;

public class OffHeapFileStat extends AbstractOffHeapStructure<OffHeapFileStat>{
    private final RawStatx shape;

    private OffHeapFileStat() {
        super(StatxOffsets.SIZE);
        clear();
        shape = RawStatx.at(address());
    }

    public static OffHeapFileStat fileStat() {
        return new OffHeapFileStat();
    }

    public FileStat extract() {
        return shape.detach();
    }
}

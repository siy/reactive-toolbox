package org.reactivetoolbox.io.uring.struct.shape;

import org.reactivetoolbox.io.raw.RawProperty;

import static org.reactivetoolbox.io.raw.RawProperty.raw;

public interface StatxOffsets {
    int SIZE = 256;
    RawProperty stx_mask = raw(0, 4);
    RawProperty stx_blksize = raw(4, 4);
    RawProperty stx_attributes = raw(8, 8);
    RawProperty stx_nlink = raw(16, 4);
    RawProperty stx_uid = raw(20, 4);
    RawProperty stx_gid = raw(24, 4);
    RawProperty stx_mode = raw(28, 2);
    RawProperty stx_ino = raw(32, 8);
    RawProperty stx_size = raw(40, 8);
    RawProperty stx_blocks = raw(48, 8);
    RawProperty stx_attributes_mask = raw(56, 8);
    RawProperty stx_rdev_major = raw(128, 4);
    RawProperty stx_rdev_minor = raw(132, 4);
    RawProperty stx_dev_major = raw(136, 4);
    RawProperty stx_dev_minor = raw(140, 4);
    RawProperty stx_atime = raw(64, 16);
    RawProperty stx_btime = raw(80, 16);
    RawProperty stx_ctime = raw(96, 16);
    RawProperty stx_mtime = raw(112, 16);
}
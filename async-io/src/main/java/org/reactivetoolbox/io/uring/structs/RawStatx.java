package org.reactivetoolbox.io.uring.structs;

import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_atime;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_attributes;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_attributes_mask;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_blksize;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_blocks;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_btime;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_ctime;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_dev_major;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_dev_minor;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_gid;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_ino;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_mask;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_mode;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_mtime;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_nlink;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_rdev_major;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_rdev_minor;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_size;
import static org.reactivetoolbox.io.uring.structs.StatxOffsets.stx_uid;

public class RawStatx extends AbstractExternalRawStructure<RawStatx> {
    private final RawStatxTimestamp atime = RawStatxTimestamp.at(0);
    private final RawStatxTimestamp btime = RawStatxTimestamp.at(0);
    private final RawStatxTimestamp ctime = RawStatxTimestamp.at(0);
    private final RawStatxTimestamp mtime = RawStatxTimestamp.at(0);

    private RawStatx(final long address) {
        super(address, StatxOffsets.SIZE);

        repositionInner(address);
    }

    private void repositionInner(final long address) {
        atime.reposition(address + stx_atime.offset());
        btime.reposition(address + stx_btime.offset());
        ctime.reposition(address + stx_ctime.offset());
        mtime.reposition(address + stx_mtime.offset());
    }

    @Override
    public RawStatx reposition(final long address) {
        repositionInner(address);
        return super.reposition(address);
    }

    /* What results were written [uncond] */
    public int mask() {
        return getInt(stx_mask);
    }

    /* Preferred general I/O size [uncond] */
    public int blockSize() {
        return getInt(stx_blksize);
    }

    /* Flags conveying information about the file [uncond] */
    public long attributes() {
        return getLong(stx_attributes);
    }

    /* Number of hard links */
    public int numLinks() {
        return getInt(stx_nlink);
    }

    /* User ID of owner */
    public int uid() {
        return getInt(stx_uid);
    }

    /* Group ID of owner */
    public int gid() {
        return getInt(stx_gid);
    }

    /* File mode */
    public short mode() {
        return getShort(stx_mode);
    }

    /* Inode number */
    public long ino() {
        return getLong(stx_ino);
    }

    /* File size */
    public long fileSize() {
        return getLong(stx_size);
    }

    /* Number of 512-byte blocks allocated */
    public long blocks() {
        return getLong(stx_blocks);
    }

    /* Mask to show what's supported in stx_attributes */
    public long attributesMask() {
        return getLong(stx_attributes_mask);
    }

    /* Device ID of special file [if bdev/cdev] */
    public int rdevMajor() {
        return getInt(stx_rdev_major);
    }

    /* Device ID of special file [if bdev/cdev] */
    public int rdevMinor() {
        return getInt(stx_rdev_minor);
    }

    /* ID of device containing file [uncond] */
    public int devMajor() {
        return getInt(stx_dev_major);
    }

    /* ID of device containing file [uncond] */
    public int devMinor() {
        return getInt(stx_dev_minor);
    }

    /* Last access time */
    public RawStatxTimestamp lastAccessTime() {
        return atime;
    }

    /* File creation time */
    public RawStatxTimestamp creationTime() {
        return btime;
    }

    /* Last attribute change time */
    public RawStatxTimestamp changeTime() {
        return ctime;
    }

    /* Last data modification time */
    public RawStatxTimestamp modificationTime() {
        return mtime;
    }
}

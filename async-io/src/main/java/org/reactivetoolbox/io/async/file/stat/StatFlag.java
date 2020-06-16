package org.reactivetoolbox.io.async.file.stat;

import org.reactivetoolbox.io.Bitmask;

public enum StatFlag implements Bitmask {
    EMPTY_PATH(0x00001000),         /* Operate on FD rather than path (if path is empty) */
    SYMLINK_NOFOLLOW(0x00000100),   /* Don't follow symlink and return info about link itself */
    NO_AUTOMOUNT(0x00000800),       /* Don't trigger automount */
    STATX_FORCE_SYNC(0x00002000),   /* Force sync before obtaining info */
    STATX_DONT_SYNC(0x00004000);    /* Don't do sync at all */

    private final int mask;

    StatFlag(final int mask) {
        this.mask = mask;
    }

    @Override
    public int mask() {
        return mask;
    }
}

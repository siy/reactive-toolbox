package org.reactivetoolbox.io.async.file;

import org.reactivetoolbox.io.Bitmask;

/**
 * File open flags.
 */
public enum OpenFlags implements Bitmask {
    READ_ONLY(00000000),
    WRITE_ONLY(00000001),
    READ_WRITE(00000002),
    CREATE(00000100),
    EXCL(00000200),
    NOCTTY(00000400),
    TRUNCATE(00001000),
    APPEND(00002000),
    NONBLOCK(00004000),
    DSYNC(00010000),
    DIRECT(00040000),
    LARGEFILE(00100000),
    DIRECTORY(00200000),
    NOFOLLOW(00400000),
    NOATIME(01000000),
    CLOEXEC(02000000),
    SYNC((04000000 | 00010000)),
    PATH(010000000),
    TMPFILE((020000000 | 00200000)),
    NDELAY(00004000);

    private final int mask;

    OpenFlags(final int mask) {
        this.mask = mask;
    }

    @Override
    public int mask() {
        return mask;
    }
}

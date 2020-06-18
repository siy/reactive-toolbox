package org.reactivetoolbox.io.async.file;

import org.reactivetoolbox.io.Bitmask;
//TODO: cleanup constant naming
public enum OpenFlags implements Bitmask {
    O_RDONLY(00000000),
    O_WRONLY(00000001),
    O_RDWR(00000002),
    O_CREAT(00000100),
    O_EXCL(00000200),
    O_NOCTTY(00000400),
    O_TRUNC(00001000),
    O_APPEND(00002000),
    O_NONBLOCK(00004000),
    O_DSYNC(00010000),
    O_DIRECT(00040000),
    O_LARGEFILE(00100000),
    O_DIRECTORY(00200000),
    O_NOFOLLOW(00400000),
    O_NOATIME(01000000),
    O_CLOEXEC(02000000),
    O_SYNC((04000000 | 00010000)),
    O_PATH(010000000),
    O_TMPFILE((020000000 | 00200000)),
    O_NDELAY(00004000);

    private final int mask;

    OpenFlags(final int mask) {
        this.mask = mask;
    }

    @Override
    public int mask() {
        return mask;
    }
}

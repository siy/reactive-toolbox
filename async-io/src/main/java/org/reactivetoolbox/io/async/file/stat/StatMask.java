package org.reactivetoolbox.io.async.file.stat;

import org.reactivetoolbox.io.Bitmask;

import java.util.EnumSet;

/**
 * Flags which control which information is requested and returned in the {@link FileStat}.
 */
public enum StatMask implements Bitmask {
    TYPE(0x000000001),    /* Want/got stx_mode & S_IFMT */
    MODE(0x000000002),    /* Want/got stx_mode & ~S_IFMT */
    NLINK(0x000000004),   /* Want/got stx_nlink */
    UID(0x000000008),     /* Want/got stx_uid */
    GID(0x000000010),     /* Want/got stx_gid */
    ATIME(0x000000020),   /* Want/got stx_atime */
    MTIME(0x000000040),   /* Want/got stx_mtime */
    CTIME(0x000000080),   /* Want/got stx_ctime */
    INO(0x000000100),     /* Want/got stx_ino */
    FSIZE(0x000000200),    /* Want/got stx_size */
    BLOCKS(0x000000400),  /* Want/got stx_blocks */
    BTIME(0x000000800);   /* Want/got stx_btime */

    private final int mask;
    private static final EnumSet<StatMask> BASIC = EnumSet.complementOf(EnumSet.of(BTIME));
    private static final EnumSet<StatMask> ALL = EnumSet.allOf(StatMask.class);

    public static EnumSet<StatMask> basic() {
        return BASIC;
    }

    public static EnumSet<StatMask> all() {
        return ALL;
    }

    StatMask(final int mask) {
        this.mask = mask;
    }

    public static EnumSet<StatMask> fromInt(final int value) {
        final var result = EnumSet.noneOf(StatMask.class);

        for(final var statMask : values()) {
            if ((value & statMask.mask) != 0) {
                result.add(statMask);
            }
        }

        return result;
    }

    @Override
    public int mask() {
        return mask;
    }
}

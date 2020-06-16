package org.reactivetoolbox.io.async.file.stat;

import java.util.EnumSet;

public enum FilePermission {
    SUID(04000),     //set-user-ID bit
    SGID(02000),     //set-group-ID bit
    SVTX(01000),     //sticky bit
    RUSR(00400),     //owner has read permission
    WUSR(00200),     //owner has write permission
    XUSR(00100),     //owner has execute permission
    RGRP(00040),     //group has read permission
    WGRP(00020),     //group has write permission
    XGRP(00010),     //group has execute permission
    ROTH(00004),     //others have read permission
    WOTH(00002),     //others have write permission
    XOTH(00001),     //others have execute permission
            ;

    private final short mask;

    FilePermission(final int mask) {
        this.mask = (short) mask;
    }

    public short mask() {
        return mask;
    }

    public static EnumSet<FilePermission> fromShort(final short value) {
        final EnumSet<FilePermission> result = EnumSet.noneOf(FilePermission.class);

        for (var permission : values()) {
            if ((value & permission.mask()) != 0) {
                result.add(permission);
            }
        }

        return result;
    }
}

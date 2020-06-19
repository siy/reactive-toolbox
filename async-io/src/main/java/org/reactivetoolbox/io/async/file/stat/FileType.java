package org.reactivetoolbox.io.async.file.stat;

/**
 * File descriptor types.
 */
public enum FileType {
    REGULAR(0100000),   /* Regular file.  */
    DIRECTORY(0040000), /* Directory.  */
    CHARACTER(0020000), /* Character device.  */
    BLOCK(0060000),     /* Block device.  */
    FIFO(0010000),      /* FIFO.  */
    LINK(0120000),      /* Symbolic link.  */
    SOCKET(0140000),	/* Socket.  */
    ;

    private final short mask;

    FileType(final int mask) {
        this.mask = (short) mask;
    }

    public short mask() {
        return mask;
    }

    public static FileType unsafeFromShort(final short value) {
        for(var type : values()) {
            if ((value & type.mask) == type.mask) {
                return type;
            }
        }
        return null;
    }
}

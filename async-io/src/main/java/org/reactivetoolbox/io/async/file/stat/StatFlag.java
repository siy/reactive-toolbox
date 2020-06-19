package org.reactivetoolbox.io.async.file.stat;

import org.reactivetoolbox.io.Bitmask;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.file.FileDescriptor;

import java.nio.file.Path;
import java.util.EnumSet;

/**
 * Flags which control behavior of {@link Submitter#stat(Path, EnumSet, EnumSet)} and {@link Submitter#stat(FileDescriptor, EnumSet, EnumSet)}
 * methods.
 * <p>
 * Note that {@link #EMPTY_PATH} is used internally. This flag controls what is used to point to file - path or file descriptor.
 * While this is not an error to pass this flag to methods above, it is ignored.
 * <p>
 * Flags {@link #STATX_DONT_SYNC} and {@link #STATX_FORCE_SYNC} have mutually exclusive meaning so if they both are passed, result is undefined
 * and may depend on Linux kernel version. If none of these flags passed then default behavior is used. The default behavior depends
 * on file system where file resides. So, if consistent behavior is necessary then one of these flags should be provided. If underlying file
 * system is remote, using {@link #STATX_FORCE_SYNC} might trigger additional synchronization with relevant network communication.
 */
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

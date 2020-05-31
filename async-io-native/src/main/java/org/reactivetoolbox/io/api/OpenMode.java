package org.reactivetoolbox.io.api;

import org.reactivetoolbox.io.Bitmask;

public enum OpenMode implements Bitmask {
    S_IRWXU(00700), /* user (file owner) has read, write, and execute permission */
    S_IRUSR(00400), /* user has read permission */
    S_IWUSR(00200), /* user has write permission */
    S_IXUSR(00100), /* user has execute permission */
    S_IRWXG(00070), /* group has read, write, and execute permission */
    S_IRGRP(00040), /* group has read permission */
    S_IWGRP(00020), /* group has write permission */
    S_IXGRP(00010), /* group has execute permission */
    S_IRWXO(00007), /* others have read, write, and execute permission */
    S_IROTH(00004), /* others have read permission */
    S_IWOTH(00002), /* others have write permission */
    S_IXOTH(00001), /* others have execute permission */
    S_ISUID(04000), /* set-user-ID bit */
    S_ISGID(02000), /* set-group-ID bit */
    S_ISVTX(01000); /* sticky bit */

    private final int mask;

    OpenMode(final int mask) {
        this.mask = mask;
    }

    @Override
    public int mask() {
        return mask;
    }
}

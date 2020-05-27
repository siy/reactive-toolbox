package org.reactivetoolbox.asyncio;

public class FileStatData {
    private int stx_mask;    /* What results were written [uncond] */
    private int stx_blksize;    /* Preferred general I/O size [uncond] */
    private long stx_attributes;    /* Flags conveying information about the file [uncond] */

    private int stx_nlink;    /* Number of hard links */
    private int stx_uid;    /* User ID of owner */
    private int stx_gid;    /* Group ID of owner */

    private short stx_mode;    /* File mode */
    private long stx_ino;    /* Inode number */
    private long stx_size;    /* File size */
    private long stx_blocks;    /* Number of 512-byte blocks allocated */
    private long stx_attributes_mask; /* Mask to show what's supported in stx_attributes */

//    private struct statx_timestamp stx_atime;    /* Last access time */
//    private struct statx_timestamp stx_btime;    /* File creation time */
//    private struct statx_timestamp stx_ctime;    /* Last attribute change time */
//    private struct statx_timestamp stx_mtime;    /* Last data modification time */

    private int stx_rdev_major;    /* Device ID of special file [if bdev/cdev] */
    private int stx_rdev_minor;
    private int stx_dev_major;    /* ID of device containing file [uncond] */
    private int stx_dev_minor;

}

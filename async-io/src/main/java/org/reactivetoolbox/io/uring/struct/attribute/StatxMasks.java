package org.reactivetoolbox.io.uring.struct.attribute;

//TODO: find better location?
public interface StatxMasks {
    int STATX_TYPE		    = 0x000000001;	/* Want/got stx_mode & S_IFMT */
    int STATX_MODE		    = 0x000000002;	/* Want/got stx_mode & ~S_IFMT */
    int STATX_NLINK	        = 0x000000004;	/* Want/got stx_nlink */
    int STATX_UID		    = 0x000000008;	/* Want/got stx_uid */
    int STATX_GID		    = 0x000000010;	/* Want/got stx_gid */
    int STATX_ATIME	        = 0x000000020;	/* Want/got stx_atime */
    int STATX_MTIME		    = 0x000000040;	/* Want/got stx_mtime */
    int STATX_CTIME		    = 0x000000080;	/* Want/got stx_ctime */
    int STATX_INO		    = 0x000000100;	/* Want/got stx_ino */
    int STATX_SIZE		    = 0x000000200;	/* Want/got stx_size */
    int STATX_BLOCKS		= 0x000000400;	/* Want/got stx_blocks */
    int STATX_BASIC_STATS	= 0x0000007ff;	/* The stuff in the normal stat struct */
    int STATX_BTIME		    = 0x000000800;	/* Want/got stx_btime */
    int STATX_ALL	        = 0x000000fff;	/* All currently supported flags */
    int STATX__RESERVED	    = 0x080000000;	/* Reserved for future struct statx expansion */
}

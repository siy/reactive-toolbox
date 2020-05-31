package org.reactivetoolbox.io.uring.structs;

//TODO: find better location?
public interface StatxAttributes {
    int STATX_ATTR_COMPRESSED   = 0x00000004; /* [I] File is compressed by the fs */
    int STATX_ATTR_IMMUTABLE    = 0x00000010; /* [I] File is marked immutable */
    int STATX_ATTR_APPEND	    = 0x00000020; /* [I] File is append-only */
    int STATX_ATTR_NODUMP	    = 0x00000040; /* [I] File is not to be dumped */
    int STATX_ATTR_ENCRYPTED    = 0x00000800; /* [I] File requires key to decrypt in fs */
    int STATX_ATTR_AUTOMOUNT    = 0x00001000; /* Dir: Automount trigger */
}

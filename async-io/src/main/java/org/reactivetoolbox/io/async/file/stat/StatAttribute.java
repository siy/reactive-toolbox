package org.reactivetoolbox.io.async.file.stat;

import java.util.EnumSet;

public enum StatAttribute {
    COMPRESSED(0x00000004L), /* [I] File is compressed by the fs */
    IMMUTABLE(0x00000010L),  /* [I] File is marked immutable */
    APPEND(0x00000020L),     /* [I] File is append-only */
    NODUMP(0x00000040L),     /* [I] File is not to be dumped */
    ENCRYPTED(0x00000800L),  /* [I] File requires key to decrypt in fs */
    AUTOMOUNT(0x00001000L);  /* Dir: Automount trigger */

    private final long mask;

    private long mask() {
        return mask;
    }

    StatAttribute(final long mask) {
        this.mask = mask;
    }

    public static EnumSet<StatAttribute> fromLong(final long attributes) {
        final EnumSet<StatAttribute> result = EnumSet.noneOf(StatAttribute.class);

        for(var attribute : values()) {
            if ((attributes & attribute.mask) != 0) {
                result.add(attribute);
            }
        }
        return result;
    }

    public static long toBytes(final EnumSet<StatAttribute> attributes) {
        return attributes.stream()
                         .mapToLong(StatAttribute::mask)
                         .sum();
    }
}

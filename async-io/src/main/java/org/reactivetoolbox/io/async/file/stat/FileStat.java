package org.reactivetoolbox.io.async.file.stat;

import java.util.EnumSet;

public class FileStat {
    private final EnumSet<StatMask> mask;
    private final int blockSize;
    private final EnumSet<StatAttribute> attributes;
    private final int numLinks;
    private final int ownerUID;
    private final int ownerGID;
    private final FileType fileType;
    private final EnumSet<FilePermission> permissions;
    private final long inode;
    private final long size;
    private final long blocks;
    private final EnumSet<StatAttribute> attributeMask; // Supported attributes mask
    private final StatTimestamp accessTime; /* Last access time */
    private final StatTimestamp creationTime; /* File creation time */
    private final StatTimestamp attributeChangeTime; /* Last attribute change time */
    private final StatTimestamp modificationTime; /* Last data modification time */

    private final DeviceId rDevice;
    private final DeviceId fsDevice;

    private FileStat(final EnumSet<StatMask> mask,
                     final int blockSize,
                     final EnumSet<StatAttribute> attributes,
                     final int numLinks,
                     final int ownerUID,
                     final int ownerGID,
                     final FileType fileType,
                     final EnumSet<FilePermission> permissions,
                     final long inode,
                     final long size,
                     final long blocks,
                     final EnumSet<StatAttribute> attributeMask,
                     final StatTimestamp accessTime,
                     final StatTimestamp creationTime,
                     final StatTimestamp attributeChangeTime,
                     final StatTimestamp modificationTime,
                     final DeviceId rDevice,
                     final DeviceId fsDevice) {
        this.mask = mask;
        this.blockSize = blockSize;
        this.attributes = attributes;
        this.numLinks = numLinks;
        this.ownerUID = ownerUID;
        this.ownerGID = ownerGID;
        this.fileType = fileType;
        this.permissions = permissions;
        this.inode = inode;
        this.size = size;
        this.blocks = blocks;
        this.attributeMask = attributeMask;
        this.accessTime = accessTime;
        this.creationTime = creationTime;
        this.attributeChangeTime = attributeChangeTime;
        this.modificationTime = modificationTime;
        this.rDevice = rDevice;
        this.fsDevice = fsDevice;
    }

    public static FileStat fileStat(final EnumSet<StatMask> mask,
                                    final int blockSize,
                                    final EnumSet<StatAttribute> attributes,
                                    final int numLinks,
                                    final int ownerUID,
                                    final int ownerGID,
                                    final FileType fileType,
                                    final EnumSet<FilePermission> permissions,
                                    final long inode,
                                    final long size,
                                    final long blocks,
                                    final EnumSet<StatAttribute> attributeMask,
                                    final StatTimestamp accessTime,
                                    final StatTimestamp creationTime,
                                    final StatTimestamp attributeChangeTime,
                                    final StatTimestamp modificationTime,
                                    final DeviceId rDevice,
                                    final DeviceId fsDevice) {
        return new FileStat(mask,
                            blockSize,
                            attributes,
                            numLinks,
                            ownerUID,
                            ownerGID,
                            fileType,
                            permissions,
                            inode,
                            size,
                            blocks,
                            attributeMask,
                            accessTime,
                            creationTime,
                            attributeChangeTime,
                            modificationTime,
                            rDevice,
                            fsDevice);
    }

    public EnumSet<StatMask> mask() {
        return mask;
    }

    public int blockSize() {
        return blockSize;
    }

    public EnumSet<StatAttribute> attributes() {
        return attributes;
    }

    public int numLinks() {
        return numLinks;
    }

    public int ownerUID() {
        return ownerUID;
    }

    public int ownerGID() {
        return ownerGID;
    }

    public FileType fileType() {
        return fileType;
    }

    public EnumSet<FilePermission> permissions() {
        return permissions;
    }

    public long inode() {
        return inode;
    }

    public long size() {
        return size;
    }

    public long blocks() {
        return blocks;
    }

    public EnumSet<StatAttribute> attributeMask() {
        return attributeMask;
    }

    public StatTimestamp accessTime() {
        return accessTime;
    }

    public StatTimestamp creationTime() {
        return creationTime;
    }

    public StatTimestamp attributeChangeTime() {
        return attributeChangeTime;
    }

    public StatTimestamp modificationTime() {
        return modificationTime;
    }

    public DeviceId rDevice() {
        return rDevice;
    }

    public DeviceId fsDevice() {
        return fsDevice;
    }
}

package org.reactivetoolbox.io.async.file.stat;

/**
 * Container for Unix-like device ID.
 */
public class DeviceId {
    private final int major;
    private final int minor;

    private DeviceId(final int major, final int minor) {
        this.major = major;
        this.minor = minor;
    }

    public static DeviceId deviceId(final int major, final int minor) {
        return new DeviceId(major, minor);
    }

    public int major() {
        return major;
    }

    public int minor() {
        return minor;
    }

    @Override
    public String toString() {
        return "DeviceId(" + major + ", " + minor + ")";
    }
}
package org.reactivetoolbox.io.async.file;

import org.reactivetoolbox.core.lang.functional.Result;

import java.util.Objects;
import java.util.StringJoiner;

import static org.reactivetoolbox.io.async.file.DescriptorType.FILE;
import static org.reactivetoolbox.io.async.file.DescriptorType.SOCKET;
import static org.reactivetoolbox.io.async.file.DescriptorType.SOCKET6;

/**
 * General purpose Linux file descriptor.
 */
public class FileDescriptor {
    private final int fd;
    private final DescriptorType type;

    private FileDescriptor(final int fd, final DescriptorType type) {
        this.fd = fd;
        this.type = type;
    }

    public boolean isSocket() {
        return type != FILE;
    }

    public boolean isSocket6() {
        return type == SOCKET6;
    }

    public int descriptor() {
        return fd;
    }

    public static FileDescriptor file(final int fd) {
        return new FileDescriptor(fd, FILE);
    }

    public static FileDescriptor socket(final int fd) {
        return new FileDescriptor(fd, SOCKET);
    }

    public static FileDescriptor socket6(final int fd) {
        return new FileDescriptor(fd, SOCKET6);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof FileDescriptor other) {
            return fd == other.fd && type == other.type;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fd, type);
    }

    @Override
    public String toString() {
        return "FileDescriptor(" + fd + ", " + type + ")";
    }
}

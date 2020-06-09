package org.reactivetoolbox.io.async.file;

import org.reactivetoolbox.core.lang.functional.Result;

import java.util.Objects;
import java.util.StringJoiner;

public class FileDescriptor {
    private final int fd;
    private final boolean isSocket;

    private FileDescriptor(final int fd, final boolean isSocket) {
        this.fd = fd;
        this.isSocket = isSocket;
    }

    public boolean isSocket() {
        return isSocket;
    }

    public int descriptor() {
        return fd;
    }

    public static FileDescriptor file(final int fd) {
        return new FileDescriptor(fd, false);
    }

    public static FileDescriptor socket(final int fd) {
        return new FileDescriptor(fd, true);
    }

    public static Result<FileDescriptor> fileResult(final int fd) {
        return Result.ok(file(fd));
    }

    public static Result<FileDescriptor> socketResult(final int fd) {
        return Result.ok(socket(fd));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof FileDescriptor that) {
            return fd == that.fd &&
                   isSocket == that.isSocket;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fd, isSocket);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "FileDescriptor(", ")")
                .add("" + fd)
                .add(isSocket ? "socket" : "file")
                .toString();
    }
}

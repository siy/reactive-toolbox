package org.reactivetoolbox.io.async;

import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.async.net.*;
import org.reactivetoolbox.io.scheduler.Timeout;

import java.nio.file.Path;
import java.time.Duration;
import java.util.EnumSet;

/**
 * Low level externally accessible API for submission of I/O operations.
 */
//TODO: add missing methods (see list at the end), cleanup, extract enums, do sanity checks (make sure port/address passed properly)
//TODO: carefully check javadocs
public interface Submitter {
    /**
     * Submit NOP operation.
     * This operation actually does nothing except performing round trip to OS kernel and back.
     */
    Promise<Unit> nop();

    /**
     * Submit DELAY (TIMEOUT) operation.
     * This operation resolves after specified timeout. More or less precise value of the actual delay provided as a result of the operation.
     *
     * @param timeout Requested timeout delay.
     */
    Promise<Duration> delay(final Timeout timeout);

    /**
     * Submit SPLICE operation.
     * Copies data from one file descriptor to another.
     *
     * @param descriptor Splice operation details container
     * @param timeout    Optional operation timeout.
     */
    Promise<SizeT> splice(final SpliceDescriptor descriptor,
                          final Option<Timeout> timeout);

    /**
     * Same as {@link #splice(SpliceDescriptor, Option)} except
     * no timeout is specified.
     */
    default Promise<SizeT> splice(final SpliceDescriptor descriptor) {
        return splice(descriptor, Option.empty());
    }

    /**
     * Submit READ operation.
     * Read from specified file descriptor. The number of bytes to read is defined by the provided buffer {@link OffHeapBuffer#size()}.
     * Upon successful completion but before {@code completionHandler} is invoked, {@code buffer} {@code used} value is set to number
     * of bytes actually read.
     *
     * @param fdIn    File descriptor to read from.
     * @param buffer  Data buffer.
     * @param offset  Offset to read from if file descriptor points to file.
     * @param timeout Optional operation timeout.
     */
    Promise<SizeT> read(final FileDescriptor fdIn,
                        final OffHeapBuffer buffer,
                        final OffsetT offset,
                        final Option<Timeout> timeout);

    /**
     * Same as {@link #read(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no timeout is specified.
     */
    default Promise<SizeT> read(final FileDescriptor fdIn,
                                final OffHeapBuffer buffer,
                                final OffsetT offset) {
        return read(fdIn, buffer, offset, Option.empty());
    }

    /**
     * Same as {@link #read(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no offset is specified.
     * Convenient for using with sockets or reading file at current position.
     */
    default Promise<SizeT> read(final FileDescriptor fdIn,
                                final OffHeapBuffer buffer,
                                final Option<Timeout> timeout) {
        return read(fdIn, buffer, OffsetT.ZERO, timeout);
    }

    /**
     * Same as {@link #read(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no offset and no timeout is specified.
     * Convenient for using with sockets or reading file at current position.
     */
    default Promise<SizeT> read(final FileDescriptor fdIn,
                                final OffHeapBuffer buffer) {
        return read(fdIn, buffer, OffsetT.ZERO, Option.empty());
    }

    /**
     * Submit WRITE operation.
     * Writes data into specified file descriptor at specified offset. The number of bytes to write is defined by the provided buffer
     * {@link OffHeapBuffer#used()}.
     *
     * @param fdOut   File descriptor to write to.
     * @param buffer  Data buffer.
     * @param offset  Offset in a file to start writing if file descriptor points to file.
     * @param timeout Optional operation timeout.
     */
    Promise<SizeT> write(final FileDescriptor fdOut,
                         final OffHeapBuffer buffer,
                         final OffsetT offset,
                         final Option<Timeout> timeout);

    /**
     * Same as {@link #write(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no timeout is specified.
     */
    default Promise<SizeT> write(final FileDescriptor fdOut,
                                 final OffHeapBuffer buffer,
                                 final OffsetT offset) {
        return write(fdOut, buffer, offset, Option.empty());
    }

    /**
     * Same as {@link #write(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no offset is specified.
     * Convenient for using with sockets or writing file at current position.
     */
    default Promise<SizeT> write(final FileDescriptor fdOut,
                                 final OffHeapBuffer buffer,
                                 final Option<Timeout> timeout) {
        return write(fdOut, buffer, OffsetT.ZERO, timeout);
    }

    /**
     * Same as {@link #write(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no offset and no timeout is specified.
     * Convenient for using with sockets or writing file at current position.
     */
    default Promise<SizeT> write(final FileDescriptor fdOut,
                                 final OffHeapBuffer buffer) {
        return write(fdOut, buffer, OffsetT.ZERO, Option.empty());
    }

    /**
     * Submit CLOSE operation.
     * Closes specified file descriptor (either file or socket).
     *
     * @param fd      File descriptor to close.
     * @param timeout Optional operation timeout.
     */
    Promise<Unit> closeFileDescriptor(final FileDescriptor fd, final Option<Timeout> timeout);

    /**
     * Same as {@link #closeFileDescriptor(FileDescriptor, Option)} except no timeout is specified.
     */
    default Promise<Unit> closeFileDescriptor(final FileDescriptor fd) {
        return closeFileDescriptor(fd, Option.empty());
    }

    /**
     * Submit OPEN operation.
     * Open file at specified location. Note that this method only partially covers functionality of the underlying {@code openat(2)} call. Instead
     * simple {@code open(2)} semantics is implemented.
     *
     * @param path    File path.
     * @param flags   File open flags.
     * @param mode    File open mode. Must be present only if {@code flags} contains {@link OpenFlags#O_CREAT} or {@link OpenFlags#O_TMPFILE}.
     * @param timeout Optional operation timeout.
     */
    Promise<FileDescriptor> open(final Path path,
                                 final EnumSet<OpenFlags> flags,
                                 final EnumSet<OpenMode> mode,
                                 final Option<Timeout> timeout);

    /**
     * Same as {@link #open(Path, EnumSet, EnumSet, Option)} except no timeout is specified.
     */
    default Promise<FileDescriptor> open(final Path path,
                                         final EnumSet<OpenFlags> flags,
                                         final EnumSet<OpenMode> mode) {
        return open(path, flags, mode, Option.empty());
    }

    /**
     * Create socket for making client-side connections/requests.
     *
     * @param addressFamily
     * @param socketType
     * @param openFlags
     */
    Promise<FileDescriptor> socket(final AddressFamily addressFamily,
                                   final SocketType socketType,
                                   final EnumSet<SocketFlag> openFlags);

    /**
     * Create server connector bound to specified address/port and is ready to accept incoming connection.
     *
     * @param socketAddress Socket address
     * @param socketType    Socket type
     * @param openFlags     Socket open flags
     * @param queueDepth    Depth of the listening queue
     */
    Promise<ServerConnector> server(final SocketAddress<?> socketAddress, final SocketType socketType,
                                    final EnumSet<SocketFlag> openFlags, final SizeT queueDepth);

    /**
     * Submit ACCEPT operation.
     * Accept incoming connection for server socket. Accepted connection receives its own socket which
     * then need to be used to communicate (read/write) with particular client.
     *
     * @param socket Server socket to accept connections on.
     * @param flags  Accept flags (see {@link SocketFlag} for more details)
     */
    Promise<ClientConnection> accept(final FileDescriptor socket,
                                     final EnumSet<SocketFlag> flags);

    /**
     * Submit CONNECT operation.
     * Connect to external server at provided address (host/port).
     *
     * @param socket  Socket to connect
     * @param address Address to connect to
     */
    Promise<Unit> connect(final FileDescriptor socket, final SocketAddress<?> address);

    //TODO: implement it. what should we return here?
    //Submitter batch(final Consumer<Submitter> submitterConsumer);

    //TODO: recv, send - implement later, when special cases handling will be necessary
    //TODO: statx, readv, writev.

}

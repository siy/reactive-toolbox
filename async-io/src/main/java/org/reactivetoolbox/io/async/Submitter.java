package org.reactivetoolbox.io.async;

import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.scheduler.Timeout;
import org.reactivetoolbox.io.async.net.AddressFamily;
import org.reactivetoolbox.io.async.net.SocketAddress;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketType;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.time.Duration;
import java.util.EnumSet;

/**
 * Low level externally accessible API for submission of I/O operations.
 * <p>
 * WARNING: Several API's require {@link ByteBuffer} as a parameter. In order to use these API's {@link ByteBuffer} instances
 * must be allocated using {@link ByteBuffer#allocateDirect(int)} method. Unfortunately due to design of the {@link ByteBuffer} and derived
 * classes it is impossible to enforce this requirement in the API because {@code java.nio.DirectByteBuffer} is package private class.
 * Alternative could be use of {@link java.nio.MappedByteBuffer}, but there is no requirement to have these buffers tied to file descriptors or
 * be a mapped files. The {@link java.nio.MappedByteBuffer} sheds some more light on this issue (see comments inside class).
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
     * @param timeout
     *         Requested timeout delay.
     */
    Promise<Duration> delay(final Timeout timeout);

    /**
     * Submit SPLICE operation.
     * Copies data from one file descriptor to another.
     *
     * @param descriptor
     *         Splice operation details container
     * @param timeout
     *         Optional operation timeout.
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
     * Read from specified file descriptor. The number of bytes to read is defined by the provided buffer {@link ByteBuffer#limit()}.
     * Upon successful completion but before {@code completionHandler} is invoked, {@code buffer} limit is set to number of bytes actually
     * read and the {@code buffer} position is set to zero. This makes buffer ready for further processing. If operation was nos successful,
     * then buffer is left intact.
     *
     * @param fdIn
     *         File descriptor to read from.
     * @param buffer
     *         Data buffer.
     * @param offset
     *         Offset to read from if file descriptor points to file.
     * @param timeout
     *         Optional operation timeout.
     */
    Promise<SizeT> read(final FileDescriptor fdIn,
                        final ByteBuffer buffer,
                        final OffsetT offset,
                        final Option<Timeout> timeout);

    /**
     * Same as {@link #read(FileDescriptor, ByteBuffer, OffsetT, Option)} except no timeout is specified.
     */
    default Promise<SizeT> read(final FileDescriptor fdIn,
                                final ByteBuffer buffer,
                                final OffsetT offset) {
        return read(fdIn, buffer, offset, Option.empty());
    }

    /**
     * Same as {@link #read(FileDescriptor, ByteBuffer, OffsetT, Option)} except no offset is specified.
     * Convenient for using with sockets or reading file at current position.
     */
    default Promise<SizeT> read(final FileDescriptor fdIn,
                                final ByteBuffer buffer,
                                final Option<Timeout> timeout) {
        return read(fdIn, buffer, OffsetT.ZERO, timeout);
    }

    /**
     * Same as {@link #read(FileDescriptor, ByteBuffer, OffsetT, Option)} except no offset and no timeout is specified.
     * Convenient for using with sockets or reading file at current position.
     */
    default Promise<SizeT> read(final FileDescriptor fdIn,
                                final ByteBuffer buffer) {
        return read(fdIn, buffer, OffsetT.ZERO, Option.empty());
    }

    /**
     * Submit WRITE operation.
     * Writes data into specified file descriptor at specified offset. The number of bytes to write is defined by the provided buffer
     * {@link ByteBuffer#limit()} and {@link ByteBuffer#position()} - bytes from {@link ByteBuffer#position()} to {@link ByteBuffer#limit()}
     * are written. Buffer remains intact regardless from the success of operation.
     *
     * @param fdOut
     *         File descriptor to write to.
     * @param buffer
     *         Data buffer.
     * @param offset
     *         Offset in a file to start writing if file descriptor points to file.
     * @param timeout
     *         Optional operation timeout.
     */
    Promise<SizeT> write(final FileDescriptor fdOut,
                         final ByteBuffer buffer,
                         final OffsetT offset,
                         final Option<Timeout> timeout);

    /**
     * Same as {@link #write(FileDescriptor, ByteBuffer, OffsetT, Option)} except no timeout is specified.
     */
    default Promise<SizeT> write(final FileDescriptor fdOut,
                                 final ByteBuffer buffer,
                                 final OffsetT offset) {
        return write(fdOut, buffer, offset, Option.empty());
    }

    /**
     * Same as {@link #write(FileDescriptor, ByteBuffer, OffsetT, Option)} except no offset is specified.
     * Convenient for using with sockets or writing file at current position.
     */
    default Promise<SizeT> write(final FileDescriptor fdOut,
                                 final ByteBuffer buffer,
                                 final Option<Timeout> timeout) {
        return write(fdOut, buffer, OffsetT.ZERO, timeout);
    }

    /**
     * Same as {@link #write(FileDescriptor, ByteBuffer, OffsetT, Option)} except no offset and no timeout is specified.
     * Convenient for using with sockets or writing file at current position.
     */
    default Promise<SizeT> write(final FileDescriptor fdOut,
                                 final ByteBuffer buffer) {
        return write(fdOut, buffer, OffsetT.ZERO, Option.empty());
    }

    /**
     * Submit CLOSE operation.
     * Closes specified file descriptor (either file or socket).
     *
     * @param fd
     *         File descriptor to close.
     * @param timeout
     *         Optional operation timeout.
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
     * @param path
     *         File path.
     * @param flags
     *         File open flags.
     * @param mode
     *         File open mode. Must be present only if {@code flags} contains {@link OpenFlags#O_CREAT} or {@link OpenFlags#O_TMPFILE}.
     * @param timeout
     *         Optional operation timeout.
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
     * Create server socket bound to specified address and listening for incoming connection.
     * The remaining operation which need to be done for this socket is {@link}.
     *
     * @param flags
     *         Socket open flags (see {@link SocketFlag} for more details)
     * @param port
     *         Port to to bind
     * @param address
     *         Optional address to bind. If not specified then INADD_ANY is used (i.e. socket will listen on all local interfaces).
     * @param backLog
     *         Listen backlog queue length
     */
    //TODO: fix javadoc
    Promise<FileDescriptor> serverSocket(final FileDescriptor socket,
                                         final Option<SocketAddress<?>> address,
                                         final SizeT backLog);

    /**
     * Submit ACCEPT operation.
     * Accept incoming connection for server socket. Accepted connection receives its own socket which
     * then need to be used to communicate (read/write) with particular client.
     *
     * @param socket
     *         Server socket to accept connections on.
     * @param flags
     *         Accept flags (see {@link SocketFlag} for more details)
     */
    Promise<ClientConnection> accept(final FileDescriptor socket,
                                     final EnumSet<SocketFlag> flags);

    /**
     * Submit CONNECT operation.
     * Connect to external server at provided address (host/port).
     *
     * @param socket
     *         Socket to connect
     * @param address
     *         Address to connect to
     */
    Promise<Unit> connect(final FileDescriptor socket, final SocketAddress<?> address);

    //TODO: recv, send - implement later, when special cases handling will be necessary
    //TODO: statx, readv, writev.
}

package org.reactivetoolbox.io.async;

import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.async.common.OffsetT;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.file.FilePermission;
import org.reactivetoolbox.io.async.file.OpenFlags;
import org.reactivetoolbox.io.async.file.SpliceDescriptor;
import org.reactivetoolbox.io.async.file.stat.FileStat;
import org.reactivetoolbox.io.async.file.stat.StatFlag;
import org.reactivetoolbox.io.async.file.stat.StatMask;
import org.reactivetoolbox.io.async.net.AddressFamily;
import org.reactivetoolbox.io.async.net.ClientConnection;
import org.reactivetoolbox.io.async.net.SocketAddress;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketOption;
import org.reactivetoolbox.io.async.net.SocketType;
import org.reactivetoolbox.io.async.net.context.ServerContext;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;
import org.reactivetoolbox.io.scheduler.Timeout;

import java.nio.file.Path;
import java.time.Duration;
import java.util.EnumSet;

import static org.reactivetoolbox.core.lang.functional.Option.empty;

/**
 * Low level externally accessible API for submission of I/O operations.
 */
//TODO:fix docs
public interface Submitter {
    /**
     * Close current instance.
     */
    void close();

    /**
     * Submit NOP operation.
     * <p>
     * This operation actually does nothing except performing round trip to OS kernel and back.
     *
     * @return
     */
    Promise<Unit> nop(final Promise<Unit> completion);

    /**
     * Submit DELAY (TIMEOUT) operation.
     * <p>
     * This operation resolves after specified timeout. More or less precise value of the actual delay provided as a result of the operation.
     *
     * @param timeout
     *         Requested timeout delay.
     * @return
     */
    Promise<Duration> delay(final Promise<Duration> completion, final Timeout timeout);

    /**
     * Submit SPLICE operation.
     * <p>
     * Copies data from one file descriptor to another. Returned {@link Promise} is used to deliver error or number of successfully copied bytes.
     *
     * @param descriptor
     *         Splice operation details container
     * @param timeout
     * @return
     */
    Promise<SizeT> splice(final Promise<SizeT> completion,
                          final SpliceDescriptor descriptor,
                          final Option<Timeout> timeout);

    /**
     * Same as {@link #splice(SpliceDescriptor, Option)} except no timeout is specified.
     * @return
     */
    default Promise<SizeT> splice(final Promise<SizeT> completion, final SpliceDescriptor descriptor) {
        return splice(completion, descriptor, empty());
    }

    /**
     * Submit READ operation.
     * <p>
     * Read from specified file descriptor. The number of bytes to read is defined by the provided buffer {@link OffHeapBuffer#size()}. Upon successful completion {@code buffer}
     * has its {@link OffHeapBuffer#used(int)} value set to number of bytes actually read. Returned {@link Promise} is used to deliver error or number of successfully read bytes.
     *
     * @param fdIn
     *         File descriptor to read from.
     * @param buffer
     *         Data buffer.
     * @param offset
     *         Offset to read from if file descriptor points to file.
     * @param timeout
     * @return
     */
    Promise<SizeT> read(final Promise<SizeT> completion,
                        final FileDescriptor fdIn,
                        final OffHeapBuffer buffer,
                        final OffsetT offset,
                        final Option<Timeout> timeout);

    /**
     * Same as {@link #read(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no timeout is specified.
     * @return
     */
    default Promise<SizeT> read(final Promise<SizeT> completion,
                                final FileDescriptor fdIn,
                                final OffHeapBuffer buffer,
                                final OffsetT offset) {
        return read(completion, fdIn, buffer, offset, empty());
    }

    /**
     * Same as {@link #read(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no offset is specified.
     * <p>
     * Convenient for using with sockets or reading file at current position.
     * @return
     */
    default Promise<SizeT> read(final Promise<SizeT> completion,
                                final FileDescriptor fdIn,
                                final OffHeapBuffer buffer,
                                final Option<Timeout> timeout) {
        return read(completion, fdIn, buffer, OffsetT.ZERO, timeout);
    }

    /**
     * Same as {@link #read(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no offset and no timeout is specified.
     * <p>
     * Convenient for using with sockets or reading file at current position.
     * @return
     */
    default Promise<SizeT> read(final Promise<SizeT> completion,
                                final FileDescriptor fdIn,
                                final OffHeapBuffer buffer) {
        return read(completion, fdIn, buffer, OffsetT.ZERO, empty());
    }

    /**
     * Submit WRITE operation.
     * <p>
     * Writes data into specified file descriptor at specified offset. The number of bytes to write is defined by the provided buffer {@link OffHeapBuffer#used()}. Returned {@link
     * Promise} is used to deliver error or number of successfully written bytes.
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
    Promise<SizeT> write(final Promise<SizeT> completion,
                         final FileDescriptor fdOut,
                         final OffHeapBuffer buffer,
                         final OffsetT offset,
                         final Option<Timeout> timeout);

    /**
     * Same as {@link #write(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no timeout is specified.
     */
    default Promise<SizeT> write(final Promise<SizeT> completion,
                                 final FileDescriptor fdOut,
                                 final OffHeapBuffer buffer,
                                 final OffsetT offset) {
        return write(completion, fdOut, buffer, offset, empty());
    }

    /**
     * Same as {@link #write(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no offset is specified.
     * <p>
     * Convenient for using with sockets or writing file at current position.
     */
    default Promise<SizeT> write(final Promise<SizeT> completion,
                                 final FileDescriptor fdOut,
                                 final OffHeapBuffer buffer,
                                 final Option<Timeout> timeout) {
        return write(completion, fdOut, buffer, OffsetT.ZERO, timeout);
    }

    /**
     * Same as {@link #write(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no offset and no timeout is specified.
     * <p>
     * Convenient for using with sockets or writing file at current position.
     */
    default Promise<SizeT> write(final Promise<SizeT> completion,
                                 final FileDescriptor fdOut,
                                 final OffHeapBuffer buffer) {
        return write(completion, fdOut, buffer, OffsetT.ZERO, empty());
    }

    /**
     * Submit CLOSE operation.
     * <p>
     * Closes specified file descriptor (either file or socket). Returned {@link Promise} is necessary only to deliver error and notify when operation is done.
     *
     * @param fd
     *         File descriptor to close.
     * @param timeout
     * @return
     */
    Promise<Unit> closeFileDescriptor(final Promise<Unit> completion, final FileDescriptor fd, final Option<Timeout> timeout);

    /**
     * Same as {@link #closeFileDescriptor(FileDescriptor, Option)} except no timeout is specified.
     */
    default Promise<Unit> closeFileDescriptor(final Promise<Unit> completion, final FileDescriptor fd) {
        return closeFileDescriptor(completion, fd, empty());
    }

    /**
     * Submit OPEN operation.
     * <p>
     * Open file at specified location.
     * <p>
     * Note that this method only partially covers functionality of the underlying {@code openat(2)} call. Instead simpler {@code open(2)} semantics is implemented.
     *
     * @param path
     *         File path.
     * @param flags
     *         File open flags.
     * @param mode
     *         File open mode. Must be present only if {@code flags} contains {@link OpenFlags#CREATE} or {@link OpenFlags#TMPFILE}.
     * @param timeout
     *         Optional operation timeout.
     */
    Promise<FileDescriptor> open(final Promise<FileDescriptor> completion,
                                 final Path path,
                                 final EnumSet<OpenFlags> flags,
                                 final EnumSet<FilePermission> mode,
                                 final Option<Timeout> timeout);

    /**
     * Same as {@link #open(Path, EnumSet, EnumSet, Option)} except no timeout is specified.
     */
    default Promise<FileDescriptor> open(final Promise<FileDescriptor> completion,
                                         final Path path,
                                         final EnumSet<OpenFlags> flags,
                                         final EnumSet<FilePermission> mode) {
        return open(completion, path, flags, mode, empty());
    }

    /**
     * Same as {@link #open(Path, EnumSet, EnumSet, Option)} except no timeout and no file permissions are provided.
     * <p>
     * Note that this method should not be used if call assumes file cration semantics (flags contain {@link OpenFlags#CREATE} or {@link OpenFlags#TMPFILE}).
     */
    default Promise<FileDescriptor> open(final Promise<FileDescriptor> completion,
                                         final Path path,
                                         final EnumSet<OpenFlags> flags) {
        return open(completion, path, flags, FilePermission.empty(), empty());
    }

    /**
     * Same as {@link #open(Path, EnumSet, EnumSet, Option)} except no file permissions are provided.
     * <p>
     * Note that this method should not be used if call assumes file cration semantics (flags contain {@link OpenFlags#CREATE} or {@link OpenFlags#TMPFILE}).
     */
    default Promise<FileDescriptor> open(final Promise<FileDescriptor> completion,
                                         final Path path,
                                         final EnumSet<OpenFlags> flags,
                                         final Option<Timeout> timeout) {
        return open(completion, path, flags, FilePermission.empty(), timeout);
    }

    /**
     * Create socket for making client-side connections/requests.
     *
     * @param addressFamily
     *         Socket address family (see {@link AddressFamily})
     * @param socketType
     *         Socket type. Usually it's {@link SocketType#STREAM} for TCP and {@link SocketType#DGRAM} for UDP.
     * @param openFlags
     *         Socket open flags. See {@link SocketFlag} for more details.
     * @param options
     *         Additional socket options. See {@link SocketOption} for more details.
     */
    Promise<FileDescriptor> socket(final Promise<FileDescriptor> completion,
                                   final AddressFamily addressFamily,
                                   final SocketType socketType,
                                   final EnumSet<SocketFlag> openFlags,
                                   final EnumSet<SocketOption> options);

    default Promise<FileDescriptor> socketTcpV4(final Promise<FileDescriptor> completion,
                                                final EnumSet<SocketFlag> openFlags,
                                                final EnumSet<SocketOption> options) {
        return socket(completion, AddressFamily.INET, SocketType.STREAM, openFlags, options);
    }

    default Promise<FileDescriptor> socketUdpV4(final Promise<FileDescriptor> completion,
                                                final EnumSet<SocketFlag> openFlags,
                                                final EnumSet<SocketOption> options) {
        return socket(completion, AddressFamily.INET, SocketType.DGRAM, openFlags, options);
    }

    default Promise<FileDescriptor> socketTcpV6(final Promise<FileDescriptor> completion,
                                                final EnumSet<SocketFlag> openFlags,
                                                final EnumSet<SocketOption> options) {
        return socket(completion, AddressFamily.INET6, SocketType.STREAM, openFlags, options);
    }

    default Promise<FileDescriptor> socketUdpV6(final Promise<FileDescriptor> completion,
                                                final EnumSet<SocketFlag> openFlags,
                                                final EnumSet<SocketOption> options) {
        return socket(completion, AddressFamily.INET6, SocketType.DGRAM, openFlags, options);
    }

    /**
     * Create server connector bound to specified address/port and is ready to accept incoming connection.
     *
     * @param socketAddress
     *         Socket address
     * @param socketType
     *         Socket type
     * @param openFlags
     *         Socket open flags
     * @param queueDepth
     *         Depth of the listening queue
     * @param options
     *         Socket options. See {@link SocketOption} for more details
     */
    Promise<ServerContext<?>> server(final Promise<ServerContext<?>> completion,
                                     final SocketAddress<?> socketAddress,
                                     final SocketType socketType,
                                     final EnumSet<SocketFlag> openFlags,
                                     final SizeT queueDepth,
                                     final EnumSet<SocketOption> options);

    /**
     * Submit ACCEPT operation.
     * <p>
     * Accept incoming connection for server socket.
     * <p>
     * Accepted connection receives its own socket which then can be used to communicate (read/write) with particular client.
     *
     * @param socket
     *         Server socket to accept connections on.
     * @param flags
     *         Accept flags (see {@link SocketFlag} for more details)
     */
    Promise<ClientConnection<?>> accept(final Promise<ClientConnection<?>> completion,
                                        final FileDescriptor socket,
                                        final EnumSet<SocketFlag> flags);

    /**
     * Submit CONNECT operation.
     * <p>
     * Connect to external server at provided address (host/port).
     * <p>
     * Returned {@link Promise} for convenience holds the same file descriptor as passed in {@code socket} parameter.
     *
     * @param socket
     *         Socket to connect
     * @param address
     *         Address to connect
     * @param timeout
     *         Optional operation timeout.
     */
    Promise<FileDescriptor> connect(final Promise<FileDescriptor> completion,
                                    final FileDescriptor socket,
                                    final SocketAddress<?> address,
                                    final Option<Timeout> timeout);

    /**
     * Same as {@link #connect(FileDescriptor, SocketAddress, Option)} except no timeout is specified.
     *
     * @param socket
     *         Socket to use for connection.
     * @param address
     * @return
     */
    default Promise<FileDescriptor> connect(final Promise<FileDescriptor> completion,
                                            final FileDescriptor socket,
                                            final SocketAddress<?> address) {
        return connect(completion, socket, address, empty());
    }

    /**
     * Get file status information for file specified by path.
     *
     * @param path
     *         File path
     * @param flags
     *         Flags which affect how information is retrieved, refer to {@link StatFlag} for more details
     * @param mask
     *         Specification of which information should be retrieved.
     */
    Promise<FileStat> stat(final Promise<FileStat> completion, final Path path, final EnumSet<StatFlag> flags, EnumSet<StatMask> mask);

    /**
     * Get file status information for file specified by file descriptor.
     *
     * @param fd
     *         File descriptor
     * @param flags
     *         Flags which affect how information is retrieved, refer to {@link StatFlag} for more details
     * @param mask
     *         Specification of which information should be retrieved.
     */
    Promise<FileStat> stat(final Promise<FileStat> completion, final FileDescriptor fd, final EnumSet<StatFlag> flags, EnumSet<StatMask> mask);

    /**
     * Read into buffers passed as a parameters.
     * <p>
     * Note that for proper operation this method requires that every passed buffer should have set {@link OffHeapBuffer#used()} value to actual number of bytes to be read into
     * this buffer.
     * <p>
     * For convenience returned {@link Promise} contains tuple with file descriptor and number of read bytes.
     *
     * @param fileDescriptor
     *         File descriptor to read from
     * @param offset
     *         Initial offset in the input file
     * @param timeout
     *         Optional operation timeout
     * @param buffers
     *         Set of buffers where read information will be put. Each buffer should have it's {@link OffHeapBuffer#used()} property set to actual number of bytes which application
     *         expects to see in this buffer.
     */
    Promise<SizeT> readVector(final Promise<SizeT> completion,
                              final FileDescriptor fileDescriptor,
                              final OffsetT offset,
                              final Option<Timeout> timeout,
                              final OffHeapBuffer... buffers);

    /**
     * Same as {@link #readVector(FileDescriptor, OffsetT, Option, OffHeapBuffer...)} except offset is set to zero.
     */
    default Promise<SizeT> readVector(final Promise<SizeT> completion,
                                      final FileDescriptor fileDescriptor,
                                      final Option<Timeout> timeout,
                                      final OffHeapBuffer... buffers) {
        return readVector(completion, fileDescriptor, OffsetT.ZERO, timeout, buffers);
    }

    /**
     * Same as {@link #readVector(FileDescriptor, OffsetT, Option, OffHeapBuffer...)} except offset is set to zero and timeout is omitted.
     */
    default Promise<SizeT> readVector(final Promise<SizeT> completion,
                                      final FileDescriptor fileDescriptor,
                                      final OffHeapBuffer... buffers) {
        return readVector(completion, fileDescriptor, OffsetT.ZERO, empty(), buffers);
    }

    /**
     * Write from buffers passed as a parameters.
     * <p>
     * Note that only {@link OffHeapBuffer#used()} portion of the buffer is written.
     * <p>
     * For convenience returned {@link Promise} contains tuple with file descriptor and number of written bytes.
     *
     * @param fileDescriptor
     *         File descriptor to read from
     * @param offset
     *         Initial offset in file
     * @param timeout
     *         Optional operation timeout
     * @param buffers
     *         Set of buffers to write from
     */
    Promise<SizeT> writeVector(final Promise<SizeT> completion,
                               final FileDescriptor fileDescriptor,
                               final OffsetT offset,
                               final Option<Timeout> timeout,
                               final OffHeapBuffer... buffers);

    /**
     * Same as {@link #writeVector(FileDescriptor, OffsetT, Option, OffHeapBuffer...)} except offset is set to zero.
     */
    default Promise<SizeT> writeVector(final Promise<SizeT> completion,
                                       final FileDescriptor fileDescriptor,
                                       final Option<Timeout> timeout,
                                       final OffHeapBuffer... buffers) {
        return writeVector(completion, fileDescriptor, OffsetT.ZERO, timeout, buffers);
    }

    /**
     * Same as {@link #writeVector(FileDescriptor, OffsetT, Option, OffHeapBuffer...)} except offset is set to zero and timeout is omitted.
     */
    default Promise<SizeT> writeVector(final Promise<SizeT> completion,
                                       final FileDescriptor fileDescriptor,
                                       final OffHeapBuffer... buffers) {
        return writeVector(completion, fileDescriptor, OffsetT.ZERO, empty(), buffers);
    }

    //TODO: implement it. what should we return here?
    //Submitter batch(final Consumer<Submitter> submitterConsumer);

    //TODO: recv, send - implement full support later, when special cases handling will be necessary
    default Promise<SizeT> send(final Promise<SizeT> completion,
                                final FileDescriptor socket,
                                final OffHeapBuffer buffer,
                                final Option<Timeout> timeout) {
        return write(completion, socket, buffer, OffsetT.ZERO, timeout);
    }

    default Promise<SizeT> recv(final Promise<SizeT> completion,
                                final FileDescriptor socket,
                                final OffHeapBuffer buffer,
                                final Option<Timeout> timeout) {
        return read(completion, socket, buffer, OffsetT.ZERO, timeout);
    }
}

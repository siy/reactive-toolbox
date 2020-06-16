package org.reactivetoolbox.io.async;

import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.async.common.OffsetT;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.file.OpenFlags;
import org.reactivetoolbox.io.async.file.OpenMode;
import org.reactivetoolbox.io.async.file.SpliceDescriptor;
import org.reactivetoolbox.io.async.file.stat.FileStat;
import org.reactivetoolbox.io.async.file.stat.StatFlag;
import org.reactivetoolbox.io.async.file.stat.StatMask;
import org.reactivetoolbox.io.async.net.AddressFamily;
import org.reactivetoolbox.io.async.net.ClientConnection;
import org.reactivetoolbox.io.async.net.ServerConnector;
import org.reactivetoolbox.io.async.net.SocketAddress;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketOption;
import org.reactivetoolbox.io.async.net.SocketType;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;
import org.reactivetoolbox.io.scheduler.Timeout;

import java.nio.file.Path;
import java.time.Duration;
import java.util.EnumSet;

import static org.reactivetoolbox.core.lang.functional.Option.empty;

/**
 * Low level externally accessible API for submission of I/O operations.
 */
//TODO: add missing methods (see list at the end), cleanup, extract enums, do sanity checks (make sure port/address passed properly)
//TODO: carefully check javadocs
public interface Submitter {
    /**
     * Submit NOP operation. This operation actually does nothing except performing round trip to OS kernel and back.
     */
    Promise<Unit> nop();

    /**
     * Submit DELAY (TIMEOUT) operation. This operation resolves after specified timeout. More or less precise value of the actual delay provided as a result of the operation.
     *
     * @param timeout
     *         Requested timeout delay.
     */
    Promise<Duration> delay(final Timeout timeout);

    /**
     * Submit SPLICE operation. Copies data from one file descriptor to another. Returned {@link Promise} is used to deliver error or number of successfully copied bytes.
     *
     * @param descriptor
     *         Splice operation details container
     * @param timeout
     *         Optional operation timeout.
     */
    Promise<SizeT> splice(final SpliceDescriptor descriptor,
                          final Option<Timeout> timeout);

    /**
     * Same as {@link #splice(SpliceDescriptor, Option)} except no timeout is specified.
     */
    default Promise<SizeT> splice(final SpliceDescriptor descriptor) {
        return splice(descriptor, empty());
    }

    /**
     * Submit READ operation. Read from specified file descriptor. The number of bytes to read is defined by the provided buffer {@link OffHeapBuffer#size()}. Upon successful
     * completion but before {@code completionHandler} is invoked, {@code buffer} {@code used} value is set to number of bytes actually read. Returned {@link Promise} is used to
     * deliver error or number of successfully read bytes.
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
                        final OffHeapBuffer buffer,
                        final OffsetT offset,
                        final Option<Timeout> timeout);

    /**
     * Same as {@link #read(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no timeout is specified.
     */
    default Promise<SizeT> read(final FileDescriptor fdIn,
                                final OffHeapBuffer buffer,
                                final OffsetT offset) {
        return read(fdIn, buffer, offset, empty());
    }

    /**
     * Same as {@link #read(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no offset is specified. Convenient for using with sockets or reading file at current position.
     */
    default Promise<SizeT> read(final FileDescriptor fdIn,
                                final OffHeapBuffer buffer,
                                final Option<Timeout> timeout) {
        return read(fdIn, buffer, OffsetT.ZERO, timeout);
    }

    /**
     * Same as {@link #read(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no offset and no timeout is specified. Convenient for using with sockets or reading file at
     * current position.
     */
    default Promise<SizeT> read(final FileDescriptor fdIn,
                                final OffHeapBuffer buffer) {
        return read(fdIn, buffer, OffsetT.ZERO, empty());
    }

    /**
     * Submit WRITE operation. Writes data into specified file descriptor at specified offset. The number of bytes to write is defined by the provided buffer {@link
     * OffHeapBuffer#used()}. Returned {@link Promise} is used to deliver error or number of successfully written bytes.
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
                         final OffHeapBuffer buffer,
                         final OffsetT offset,
                         final Option<Timeout> timeout);

    /**
     * Same as {@link #write(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no timeout is specified.
     */
    default Promise<SizeT> write(final FileDescriptor fdOut,
                                 final OffHeapBuffer buffer,
                                 final OffsetT offset) {
        return write(fdOut, buffer, offset, empty());
    }

    /**
     * Same as {@link #write(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no offset is specified. Convenient for using with sockets or writing file at current position.
     */
    default Promise<SizeT> write(final FileDescriptor fdOut,
                                 final OffHeapBuffer buffer,
                                 final Option<Timeout> timeout) {
        return write(fdOut, buffer, OffsetT.ZERO, timeout);
    }

    /**
     * Same as {@link #write(FileDescriptor, OffHeapBuffer, OffsetT, Option)} except no offset and no timeout is specified. Convenient for using with sockets or writing file at
     * current position.
     */
    default Promise<SizeT> write(final FileDescriptor fdOut,
                                 final OffHeapBuffer buffer) {
        return write(fdOut, buffer, OffsetT.ZERO, empty());
    }

    /**
     * Submit CLOSE operation. Closes specified file descriptor (either file or socket). Returned {@link Promise} is necessary only to deliver error and notify when operation is
     * done.
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
        return closeFileDescriptor(fd, empty());
    }

    /**
     * Submit OPEN operation. Open file at specified location. Note that this method only partially covers functionality of the underlying {@code openat(2)} call. Instead simple
     * {@code open(2)} semantics is implemented.
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
        return open(path, flags, mode, empty());
    }

    //TODO: finish Javadoc

    /**
     * Create socket for making client-side connections/requests.
     *
     * @param addressFamily
     * @param socketType
     * @param openFlags
     * @param options
     */
    Promise<FileDescriptor> socket(final AddressFamily addressFamily,
                                   final SocketType socketType,
                                   final EnumSet<SocketFlag> openFlags,
                                   final EnumSet<SocketOption> options);

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
     */
    Promise<ServerConnector> server(final SocketAddress<?> socketAddress,
                                    final SocketType socketType,
                                    final EnumSet<SocketFlag> openFlags,
                                    final SizeT queueDepth,
                                    final EnumSet<SocketOption> options);

    /**
     * Submit ACCEPT operation. Accept incoming connection for server socket. Accepted connection receives its own socket which then need to be used to communicate (read/write)
     * with particular client.
     *
     * @param socket
     *         Server socket to accept connections on.
     * @param flags
     *         Accept flags (see {@link SocketFlag} for more details)
     * @return
     */
    Promise<ClientConnection<?>> accept(final FileDescriptor socket,
                                        final EnumSet<SocketFlag> flags);

    /**
     * Submit CONNECT operation. Connect to external server at provided address (host/port). Returned {@link Promise} is necessary only to deliver error and notify when socket can
     * be used.
     *
     * @param socket
     *         Socket to connect
     * @param address
     *         Address to connect to
     * @param timeout
     *         Optional operation timeout.
     */
    Promise<Unit> connect(final FileDescriptor socket,
                          final SocketAddress<?> address,
                          final Option<Timeout> timeout);

    /**
     * Same as {@link #connect(FileDescriptor, SocketAddress, Option)} except no timeout is specified.
     *
     * @param socket
     *         Socket to use for connection.
     * @param address
     *         Remote address to connect.
     */
    default Promise<Unit> connect(final FileDescriptor socket,
                                  final SocketAddress<?> address) {
        return connect(socket, address, empty());
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
    Promise<FileStat> stat(final Path path, final EnumSet<StatFlag> flags, EnumSet<StatMask> mask);

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
    Promise<FileStat> stat(final FileDescriptor fd, final EnumSet<StatFlag> flags, EnumSet<StatMask> mask);

    /**
     * Read into buffers passed as a parameters.
     * <p>
     * Note that for proper operation this method requires that every passed buffer should have set {@link OffHeapBuffer#used()} value to actual number of bytes to be read into
     * this buffer.
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
    Promise<SizeT> readVector(final FileDescriptor fileDescriptor,
                              final OffsetT offset,
                              final Option<Timeout> timeout,
                              final OffHeapBuffer... buffers);

    /**
     * Same as {@link #readVector(FileDescriptor, OffsetT, Option, OffHeapBuffer...)} except offset is set to zero.
     */
    default Promise<SizeT> readVector(final FileDescriptor fileDescriptor, final Option<Timeout> timeout, final OffHeapBuffer... buffers) {
        return readVector(fileDescriptor, OffsetT.ZERO, timeout, buffers);
    }

    /**
     * Same as {@link #readVector(FileDescriptor, OffsetT, Option, OffHeapBuffer...)} except offset is set to zero and timeout is omitted.
     */
    default Promise<SizeT> readVector(final FileDescriptor fileDescriptor, final OffHeapBuffer... buffers) {
        return readVector(fileDescriptor, OffsetT.ZERO, empty(), buffers);
    }

    /**
     * Write from buffers passed as a parameters.
     * <p>
     * Note that only {@link OffHeapBuffer#used()} portion of the buffer is written.
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
    Promise<SizeT> writeVector(final FileDescriptor fileDescriptor, final OffsetT offset, final Option<Timeout> timeout, final OffHeapBuffer... buffers);

    /**
     * Same as {@link #writeVector(FileDescriptor, OffsetT, Option, OffHeapBuffer...)} except offset is set to zero.
     */
    default Promise<SizeT> writeVector(final FileDescriptor fileDescriptor, final Option<Timeout> timeout, final OffHeapBuffer... buffers) {
        return writeVector(fileDescriptor, OffsetT.ZERO, timeout, buffers);
    }

    /**
     * Same as {@link #writeVector(FileDescriptor, OffsetT, Option, OffHeapBuffer...)} except offset is set to zero and timeout is omitted.
     */
    default Promise<SizeT> writeVector(final FileDescriptor fileDescriptor, final OffHeapBuffer... buffers) {
        return writeVector(fileDescriptor, OffsetT.ZERO, empty(), buffers);
    }

    //TODO: implement it. what should we return here?
    //Submitter batch(final Consumer<Submitter> submitterConsumer);

    //TODO: recv, send - implement full support later, when special cases handling will be necessary
    default Promise<SizeT> send(final FileDescriptor socket,
                                final OffHeapBuffer buffer,
                                final Option<Timeout> timeout) {
        return write(socket, buffer, OffsetT.ZERO, timeout);
    }

    default Promise<SizeT> recv(final FileDescriptor socket,
                                final OffHeapBuffer buffer,
                                final Option<Timeout> timeout) {
        return read(socket, buffer, OffsetT.ZERO, timeout);
    }
}

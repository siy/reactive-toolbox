package org.reactivetoolbox.io.async;

import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
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
import java.util.Set;
import java.util.function.Consumer;

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
     */
    void nop(final Consumer<Result<Unit>> completion);

    default Promise<Unit> nop(final Promise<Unit> promise) {
        nop(promise::syncResolve);
        return promise;
    }

    /**
     * Submit DELAY (TIMEOUT) operation.
     * <p>
     * This operation resolves after specified timeout. More or less precise value of the actual delay provided as a result of the operation.
     *
     * @param timeout
     *         Requested timeout delay.
     */
    void delay(final Consumer<Result<Duration>> completion, final Timeout timeout);

    default Promise<Duration> delay(final Promise<Duration> promise, final Timeout timeout) {
        delay(promise::syncResolve, timeout);
        return promise;
    }

    default Promise<Duration> delay(final Timeout timeout) {
        return delay(Promise.promise(), timeout);
    }

    /**
     * Submit SPLICE operation.
     * <p>
     * Copies data from one file descriptor to another. Returned {@link Promise} is used to deliver error or number of successfully copied bytes.
     *
     * @param descriptor
     *         Splice operation details container
     * @param timeout
     *         Optional operation timeout.
     */

    void splice(final Consumer<Result<SizeT>> completion,
                final SpliceDescriptor descriptor,
                final Option<Timeout> timeout);

    default Promise<SizeT> splice(final Promise<SizeT> promise,
                                  final SpliceDescriptor descriptor,
                                  final Option<Timeout> timeout) {
        splice(promise::syncResolve, descriptor, timeout);
        return promise;
    }

    default Promise<SizeT> splice(final SpliceDescriptor descriptor,
                                  final Option<Timeout> timeout) {
        return splice(Promise.promise(), descriptor, timeout);
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
     *         Optional operation timeout.
     */
    void read(final Consumer<Result<SizeT>> completion,
              final FileDescriptor fdIn,
              final OffHeapBuffer buffer,
              final OffsetT offset,
              final Option<Timeout> timeout);

    default Promise<SizeT> read(final Promise<SizeT> promise,
                                final FileDescriptor fdIn,
                                final OffHeapBuffer buffer,
                                final OffsetT offset,
                                final Option<Timeout> timeout) {
        read(promise::syncResolve, fdIn, buffer, offset, timeout);
        return promise;
    }

    default Promise<SizeT> read(final FileDescriptor fdIn,
                                final OffHeapBuffer buffer,
                                final OffsetT offset,
                                final Option<Timeout> timeout) {
        return read(Promise.promise(), fdIn, buffer, offset, timeout);
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
    void write(final Consumer<Result<SizeT>> promise,
               final FileDescriptor fdOut,
               final OffHeapBuffer buffer,
               final OffsetT offset,
               final Option<Timeout> timeout);

    default Promise<SizeT> write(final Promise<SizeT> promise,
                                 final FileDescriptor fdOut,
                                 final OffHeapBuffer buffer,
                                 final OffsetT offset,
                                 final Option<Timeout> timeout) {
        write(promise::syncResolve, fdOut, buffer, offset, timeout);
        return promise;
    }

    default Promise<SizeT> write(final FileDescriptor fdOut,
                                 final OffHeapBuffer buffer,
                                 final OffsetT offset,
                                 final Option<Timeout> timeout) {
        return write(Promise.promise(), fdOut, buffer, offset, timeout);
    }

    /**
     * Submit CLOSE operation.
     * <p>
     * Closes specified file descriptor (either file or socket). Returned {@link Promise} is necessary only to deliver error and notify when operation is done.
     *
     * @param fd
     *         File descriptor to close.
     * @param timeout
     *         Optional operation timeout.
     */
    void closeFileDescriptor(final Consumer<Result<Unit>> completion,
                             final FileDescriptor fd,
                             final Option<Timeout> timeout);

    default Promise<Unit> closeFileDescriptor(final Promise<Unit> promise,
                                              final FileDescriptor fd,
                                              final Option<Timeout> timeout) {
        closeFileDescriptor(promise::syncResolve, fd, timeout);
        return promise;
    }

    default Promise<Unit> closeFileDescriptor(final FileDescriptor fd,
                                              final Option<Timeout> timeout) {
        return closeFileDescriptor(Promise.promise(), fd, timeout);
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
    void open(final Consumer<Result<FileDescriptor>> completion,
              final Path path,
              final Set<OpenFlags> flags,
              final Set<FilePermission> mode,
              final Option<Timeout> timeout);

    default Promise<FileDescriptor> open(final Promise<FileDescriptor> promise,
                                         final Path path,
                                         final Set<OpenFlags> flags,
                                         final Set<FilePermission> mode,
                                         final Option<Timeout> timeout) {
        open(promise::syncResolve, path, flags, mode, timeout);
        return promise;
    }

    default Promise<FileDescriptor> open(final Path path,
                                         final Set<OpenFlags> flags,
                                         final Set<FilePermission> mode,
                                         final Option<Timeout> timeout) {
        return open(Promise.promise(), path, flags, mode, timeout);
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
    void socket(final Consumer<Result<FileDescriptor>> completion,
                final AddressFamily addressFamily,
                final SocketType socketType,
                final Set<SocketFlag> openFlags,
                final Set<SocketOption> options);

    default Promise<FileDescriptor> socket(final Promise<FileDescriptor> promise,
                                           final AddressFamily addressFamily,
                                           final SocketType socketType,
                                           final Set<SocketFlag> openFlags,
                                           final Set<SocketOption> options) {
        socket(promise::syncResolve, addressFamily, socketType, openFlags, options);
        return promise;
    }

    default Promise<FileDescriptor> socket(final AddressFamily addressFamily,
                                           final SocketType socketType,
                                           final Set<SocketFlag> openFlags,
                                           final Set<SocketOption> options) {
        return socket(Promise.promise(), addressFamily, socketType, openFlags, options);
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
    void server(final Consumer<Result<ServerContext<?>>> completion,
                final SocketAddress<?> socketAddress,
                final SocketType socketType,
                final Set<SocketFlag> openFlags,
                final SizeT queueDepth,
                final Set<SocketOption> options);

    default Promise<ServerContext<?>> server(final Promise<ServerContext<?>> promise,
                                             final SocketAddress<?> socketAddress,
                                             final SocketType socketType,
                                             final Set<SocketFlag> openFlags,
                                             final SizeT queueDepth,
                                             final Set<SocketOption> options) {
        server(promise::syncResolve, socketAddress, socketType, openFlags, queueDepth, options);
        return promise;
    }

    default Promise<ServerContext<?>> server(final SocketAddress<?> socketAddress,
                                             final SocketType socketType,
                                             final Set<SocketFlag> openFlags,
                                             final SizeT queueDepth,
                                             final Set<SocketOption> options) {
        return server(Promise.promise(), socketAddress, socketType, openFlags, queueDepth, options);
    }

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
    void accept(final Consumer<Result<ClientConnection<?>>> completion,
                final FileDescriptor socket,
                final Set<SocketFlag> flags);

    default Promise<ClientConnection<?>> accept(final Promise<ClientConnection<?>> promise,
                                                final FileDescriptor socket,
                                                final Set<SocketFlag> flags) {
        accept(promise::syncResolve, socket, flags);
        return promise;
    }

    default Promise<ClientConnection<?>> accept(final FileDescriptor socket,
                                                final Set<SocketFlag> flags) {
        return accept(Promise.promise(), socket, flags);
    }

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
    void connect(final Consumer<Result<FileDescriptor>> completion,
                 final FileDescriptor socket,
                 final SocketAddress<?> address,
                 final Option<Timeout> timeout);

    default Promise<FileDescriptor> connect(final Promise<FileDescriptor> promise,
                                            final FileDescriptor socket,
                                            final SocketAddress<?> address,
                                            final Option<Timeout> timeout) {
        connect(promise::syncResolve, socket, address, timeout);
        return promise;
    }

    default Promise<FileDescriptor> connect(final FileDescriptor socket,
                                            final SocketAddress<?> address,
                                            final Option<Timeout> timeout) {
        return connect(Promise.promise(), socket, address, timeout);
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
    void stat(final Consumer<Result<FileStat>> completion,
              final Path path,
              final Set<StatFlag> flags,
              final Set<StatMask> mask);

    default Promise<FileStat> stat(final Promise<FileStat> promise,
                                   final Path path,
                                   final Set<StatFlag> flags,
                                   final Set<StatMask> mask) {
        stat(promise::syncResolve, path, flags, mask);
        return promise;
    }

    default Promise<FileStat> stat(final Path path,
                                   final Set<StatFlag> flags,
                                   final Set<StatMask> mask) {
        return stat(Promise.promise(), path, flags, mask);
    }

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
    void stat(final Consumer<Result<FileStat>> completion,
              final FileDescriptor fd,
              final Set<StatFlag> flags,
              final Set<StatMask> mask);

    default Promise<FileStat> stat(final Promise<FileStat> promise,
                                   final FileDescriptor fd,
                                   final Set<StatFlag> flags,
                                   final Set<StatMask> mask) {
        stat(promise::syncResolve, fd, flags, mask);
        return promise;
    }

    default Promise<FileStat> stat(final FileDescriptor fd,
                                   final Set<StatFlag> flags,
                                   final Set<StatMask> mask) {
        return stat(Promise.promise(), fd, flags, mask);
    }

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
    void readVector(final Consumer<Result<SizeT>> completion,
                    final FileDescriptor fileDescriptor,
                    final OffsetT offset,
                    final Option<Timeout> timeout,
                    final OffHeapBuffer... buffers);

    default Promise<SizeT> readVector(final Promise<SizeT> promise,
                                      final FileDescriptor fileDescriptor,
                                      final OffsetT offset,
                                      final Option<Timeout> timeout,
                                      final OffHeapBuffer... buffers) {
        readVector(promise::syncResolve, fileDescriptor, offset, timeout, buffers);
        return promise;
    }

    default Promise<SizeT> readVector(final FileDescriptor fileDescriptor,
                                      final OffsetT offset,
                                      final Option<Timeout> timeout,
                                      final OffHeapBuffer... buffers) {
        return readVector(Promise.promise(), fileDescriptor, offset, timeout, buffers);
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
    void writeVector(final Consumer<Result<SizeT>> completion,
                     final FileDescriptor fileDescriptor,
                     final OffsetT offset,
                     final Option<Timeout> timeout,
                     final OffHeapBuffer... buffers);

    default Promise<SizeT> writeVector(final Promise<SizeT> promise,
                                       final FileDescriptor fileDescriptor,
                                       final OffsetT offset,
                                       final Option<Timeout> timeout,
                                       final OffHeapBuffer... buffers) {
        writeVector(promise::syncResolve, fileDescriptor, offset, timeout, buffers);
        return promise;
    }

    default Promise<SizeT> writeVector(final FileDescriptor fileDescriptor,
                                       final OffsetT offset,
                                       final Option<Timeout> timeout,
                                       final OffHeapBuffer... buffers) {
        return writeVector(Promise.promise(), fileDescriptor, offset, timeout, buffers);
    }


    //TODO: implement it. what should we return here?
    //Submitter batch(final Consumer<Submitter> submitterConsumer);

    //TODO: recv, send - implement later, when handling for specific cases will be necessary
}

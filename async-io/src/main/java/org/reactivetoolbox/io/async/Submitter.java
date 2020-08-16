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
public interface Submitter {
    /**
     * Close current Submitter instance.
     */
    void close();

    /**
     * Submit NOP operation.
     * <p>
     * This operation actually does nothing except performing round trip to OS kernel and back.
     *
     * @param completion
     *         Callback which is invoked once operation is finished.
     */
    void nop(final Consumer<Result<Unit>> completion);

    /**
     * Same as {@link #nop(Consumer)} except {@link Promise#syncResolve(Result)} is used as a callback. The provided {@link Promise} instance is resolved with {@link Unit} upon
     * completion of operation.
     *
     * @param promise
     *         Input {@link Promise} instance to resolve upon completion.
     * @return input {@link Promise} instance.
     */
    default Promise<Unit> nop(final Promise<Unit> promise) {
        nop(promise::syncResolve);
        return promise;
    }

    /**
     * Same as {@link #nop(Promise)} except new {@link Promise} instance is created rather than received as a parameter.
     *
     * @return created {@link Promise} instance.
     */
    default Promise<Unit> nop() {
        return nop(Promise.promise());
    }

    /**
     * Submit DELAY (TIMEOUT) operation.
     * <p>
     * This operation completes after specified timeout. More or less precise value of the actual delay passed as a parameter to the callback.
     *
     * @param completion
     *         Callback which is invoked once operation is finished.
     * @param timeout
     *         Requested delay.
     */
    void delay(final Consumer<Result<Duration>> completion, final Timeout timeout);

    /**
     * Same as {@link #delay(Consumer, Timeout)} except {@link Promise#syncResolve(Result)} is used as a callback.
     *
     * @param promise
     *         Input {@link Promise} instance to resolve upon completion.
     * @param timeout
     *         Requested delay.
     * @return input {@link Promise} instance.
     */
    default Promise<Duration> delay(final Promise<Duration> promise, final Timeout timeout) {
        delay(promise::syncResolve, timeout);
        return promise;
    }

    /**
     * Same as {@link #delay(Promise, Timeout)} except new {@link Promise} instance is created rather than received as a parameter.
     *
     * @param timeout
     *         Requested delay.
     * @return created {@link Promise} instance.
     */
    default Promise<Duration> delay(final Timeout timeout) {
        return delay(Promise.promise(), timeout);
    }

    /**
     * Submit SPLICE operation.
     * <p>
     * Copies data from one file descriptor to another. Upon completion number of copied bytes is passed to the callback.
     *
     * @param completion
     *         Callback which is invoked once operation is finished.
     * @param descriptor
     *         Splice operation details container
     * @param timeout
     *         Optional operation timeout.
     */

    void splice(final Consumer<Result<SizeT>> completion,
                final SpliceDescriptor descriptor,
                final Option<Timeout> timeout);

    /**
     * Same as {@link #splice(Consumer, SpliceDescriptor, Option)} except {@link Promise#syncResolve(Result)} is used as a callback.
     *
     * @param promise
     *         Input {@link Promise} instance to resolve upon completion.
     * @param descriptor
     *         Splice operation details container
     * @param timeout
     *         Optional operation timeout.
     * @return input {@link Promise} instance.
     */
    default Promise<SizeT> splice(final Promise<SizeT> promise,
                                  final SpliceDescriptor descriptor,
                                  final Option<Timeout> timeout) {
        splice(promise::syncResolve, descriptor, timeout);
        return promise;
    }

    /**
     * Same as {@link #splice(Promise, SpliceDescriptor, Option)} except new {@link Promise} instance is created rather than received as a parameter.
     *
     * @param descriptor
     *         Splice operation details container
     * @param timeout
     *         Optional operation timeout.
     * @return created {@link Promise} instance.
     */
    default Promise<SizeT> splice(final SpliceDescriptor descriptor,
                                  final Option<Timeout> timeout) {
        return splice(Promise.promise(), descriptor, timeout);
    }

    /**
     * Submit READ operation.
     * <p>
     * Read from specified file descriptor. The number of bytes to read is defined by the provided buffer {@link OffHeapBuffer#size()}.
     * Upon successful completion {@code buffer} has its {@link OffHeapBuffer#used(int)} value set to number of bytes actually read.
     * The number of bytes read also passed as a parameter to callback upon completion.
     *
     * @param completion
     *         Callback which is invoked once operation is finished.
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

    /**
     * Same as {@link #read(Consumer, FileDescriptor, OffHeapBuffer, OffsetT, Option)} except {@link Promise#syncResolve(Result)} is used as a callback.
     *
     * @param promise
     *         Input {@link Promise} instance to resolve upon completion.
     * @param fdIn
     *         File descriptor to read from.
     * @param buffer
     *         Data buffer.
     * @param offset
     *         Offset to read from if file descriptor points to file.
     * @param timeout
     *         Optional operation timeout.
     * @return input {@link Promise} instance.
     */
    default Promise<SizeT> read(final Promise<SizeT> promise,
                                final FileDescriptor fdIn,
                                final OffHeapBuffer buffer,
                                final OffsetT offset,
                                final Option<Timeout> timeout) {
        read(promise::syncResolve, fdIn, buffer, offset, timeout);
        return promise;
    }

    /**
     * Same as {@link #read(Promise, FileDescriptor, OffHeapBuffer, OffsetT, Option)} except new {@link Promise} instance is created rather than received as a parameter.
     *
     * @param fdIn
     *         File descriptor to read from.
     * @param buffer
     *         Data buffer.
     * @param offset
     *         Offset to read from if file descriptor points to file.
     * @param timeout
     *         Optional operation timeout.
     * @return created {@link Promise} instance.
     */
    default Promise<SizeT> read(final FileDescriptor fdIn,
                                final OffHeapBuffer buffer,
                                final OffsetT offset,
                                final Option<Timeout> timeout) {
        return read(Promise.promise(), fdIn, buffer, offset, timeout);
    }

    /**
     * Submit WRITE operation.
     * <p>
     * Writes data into specified file descriptor at specified offset. The number of bytes to write is defined by the provided buffer
     * {@link OffHeapBuffer#used()}. Number of bytes actually written is passed as a parameter to provided callback.
     *
     * @param completion
     *         Callback which is invoked once operation is finished.
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

    /**
     * Same as {@link #write(Consumer, FileDescriptor, OffHeapBuffer, OffsetT, Option)} except {@link Promise#syncResolve(Result)} is used as a callback.
     *
     * @param promise
     *         Input {@link Promise} instance to resolve upon completion.
     * @param fdOut
     *         File descriptor to write to.
     * @param buffer
     *         Data buffer.
     * @param offset
     *         Offset in a file to start writing if file descriptor points to file.
     * @param timeout
     *         Optional operation timeout.
     * @return input {@link Promise} instance.
     */
    default Promise<SizeT> write(final Promise<SizeT> promise,
                                 final FileDescriptor fdOut,
                                 final OffHeapBuffer buffer,
                                 final OffsetT offset,
                                 final Option<Timeout> timeout) {
        write(promise::syncResolve, fdOut, buffer, offset, timeout);
        return promise;
    }

    /**
     * Same as {@link #write(Promise, FileDescriptor, OffHeapBuffer, OffsetT, Option)} except new {@link Promise} instance is created rather than received as a parameter.
     *
     * @param fdOut
     *         File descriptor to write to.
     * @param buffer
     *         Data buffer.
     * @param offset
     *         Offset in a file to start writing if file descriptor points to file.
     * @param timeout
     *         Optional operation timeout.
     * @return created {@link Promise} instance.
     */
    default Promise<SizeT> write(final FileDescriptor fdOut,
                                 final OffHeapBuffer buffer,
                                 final OffsetT offset,
                                 final Option<Timeout> timeout) {
        return write(Promise.promise(), fdOut, buffer, offset, timeout);
    }

    /**
     * Submit CLOSE operation.
     * <p>
     * Closes specified file descriptor (either file or socket). Upon completion callback is invoked with {@link Unit} instance as a parameter.
     *
     * @param completion
     *         Callback which is invoked once operation is finished.
     * @param fd
     *         File descriptor to close.
     * @param timeout
     *         Optional operation timeout.
     */
    void closeFileDescriptor(final Consumer<Result<Unit>> completion,
                             final FileDescriptor fd,
                             final Option<Timeout> timeout);

    /**
     * Same as {@link #closeFileDescriptor(Consumer, FileDescriptor, Option)} except {@link Promise#syncResolve(Result)} is used as a callback.
     *
     * @param promise
     *         Input {@link Promise} instance to resolve upon completion.
     * @param fd
     *         File descriptor to close.
     * @param timeout
     *         Optional operation timeout.
     * @return input {@link Promise} instance.
     */
    default Promise<Unit> closeFileDescriptor(final Promise<Unit> promise,
                                              final FileDescriptor fd,
                                              final Option<Timeout> timeout) {
        closeFileDescriptor(promise::syncResolve, fd, timeout);
        return promise;
    }

    /**
     * Same as {@link #closeFileDescriptor(Promise, FileDescriptor, Option)} except new {@link Promise} instance is created rather than received as a parameter.
     *
     * @param fd
     *         File descriptor to close.
     * @param timeout
     *         Optional operation timeout.
     * @return created {@link Promise} instance.
     */
    default Promise<Unit> closeFileDescriptor(final FileDescriptor fd,
                                              final Option<Timeout> timeout) {
        return closeFileDescriptor(Promise.promise(), fd, timeout);
    }

    /**
     * Submit OPEN operation.
     * <p>
     * Open file at specified location. Upon completion callback is invoked with file descriptor of opened file as a parameter.
     * <p>
     * Note that this method only partially covers functionality of the underlying {@code openat(2)} call.
     * Instead simpler {@code open(2)} semantics is implemented.
     *
     * @param completion
     *         Callback which is invoked once operation is finished.
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

    /**
     * Same as {@link #open(Consumer, Path, Set, Set, Option)} except {@link Promise#syncResolve(Result)} is used as a callback.
     *
     * @param promise
     *         Input {@link Promise} instance to resolve upon completion.
     * @param path
     *         File path.
     * @param flags
     *         File open flags.
     * @param mode
     *         File open mode. Must be present only if {@code flags} contains {@link OpenFlags#CREATE} or {@link OpenFlags#TMPFILE}.
     * @param timeout
     *         Optional operation timeout.
     * @return input {@link Promise} instance.
     */
    default Promise<FileDescriptor> open(final Promise<FileDescriptor> promise,
                                         final Path path,
                                         final Set<OpenFlags> flags,
                                         final Set<FilePermission> mode,
                                         final Option<Timeout> timeout) {
        open(promise::syncResolve, path, flags, mode, timeout);
        return promise;
    }

    /**
     * Same as {@link #open(Promise, Path, Set, Set, Option)} except new {@link Promise} instance is created rather than received as a parameter.
     *
     * @param path
     *         File path.
     * @param flags
     *         File open flags.
     * @param mode
     *         File open mode. Must be present only if {@code flags} contains {@link OpenFlags#CREATE} or {@link OpenFlags#TMPFILE}.
     * @param timeout
     *         Optional operation timeout.
     * @return created {@link Promise} instance.
     */
    default Promise<FileDescriptor> open(final Path path,
                                         final Set<OpenFlags> flags,
                                         final Set<FilePermission> mode,
                                         final Option<Timeout> timeout) {
        return open(Promise.promise(), path, flags, mode, timeout);
    }

    /**
     * Create socket for making client-side connections/requests. Upon completion callback is invoked with opened socket file descriptor
     * as a parameter.
     *
     * @param completion
     *         Callback which is invoked once operation is finished.
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

    /**
     * Same as {@link #socket(Consumer, AddressFamily, SocketType, Set, Set)} except {@link Promise#syncResolve(Result)} is used as a callback.
     *
     * @param promise
     *         Input {@link Promise} instance to resolve upon completion.
     * @param addressFamily
     *         Socket address family (see {@link AddressFamily})
     * @param socketType
     *         Socket type. Usually it's {@link SocketType#STREAM} for TCP and {@link SocketType#DGRAM} for UDP.
     * @param openFlags
     *         Socket open flags. See {@link SocketFlag} for more details.
     * @param options
     *         Additional socket options. See {@link SocketOption} for more details.
     * @return input {@link Promise} instance.
     */
    default Promise<FileDescriptor> socket(final Promise<FileDescriptor> promise,
                                           final AddressFamily addressFamily,
                                           final SocketType socketType,
                                           final Set<SocketFlag> openFlags,
                                           final Set<SocketOption> options) {
        socket(promise::syncResolve, addressFamily, socketType, openFlags, options);
        return promise;
    }

    /**
     * Same as {@link #socket(Promise, AddressFamily, SocketType, Set, Set)} except new {@link Promise} instance is created rather than received as a parameter.
     *
     * @param addressFamily
     *         Socket address family (see {@link AddressFamily})
     * @param socketType
     *         Socket type. Usually it's {@link SocketType#STREAM} for TCP and {@link SocketType#DGRAM} for UDP.
     * @param openFlags
     *         Socket open flags. See {@link SocketFlag} for more details.
     * @param options
     *         Additional socket options. See {@link SocketOption} for more details.
     * @return created {@link Promise} instance.
     */
    default Promise<FileDescriptor> socket(final AddressFamily addressFamily,
                                           final SocketType socketType,
                                           final Set<SocketFlag> openFlags,
                                           final Set<SocketOption> options) {
        return socket(Promise.promise(), addressFamily, socketType, openFlags, options);
    }

    /**
     * Create server connector bound to specified address/port and is ready to accept incoming connection.
     * Upon completion provided callback is invoked with the filled server context instance.
     *
     * @param completion
     *         Callback which is invoked once operation is finished.
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
     * @see ServerContext
     */
    void server(final Consumer<Result<ServerContext<?>>> completion,
                final SocketAddress<?> socketAddress,
                final SocketType socketType,
                final Set<SocketFlag> openFlags,
                final SizeT queueDepth,
                final Set<SocketOption> options);

    /**
     * Same as {@link #server(Consumer, SocketAddress, SocketType, Set, SizeT, Set)} except {@link Promise#syncResolve(Result)} is used as a callback.
     *
     * @param promise
     *         Input {@link Promise} instance to resolve upon completion.
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
     * @return input {@link Promise} instance.
     *
     * @see ServerContext
     */
    default Promise<ServerContext<?>> server(final Promise<ServerContext<?>> promise,
                                             final SocketAddress<?> socketAddress,
                                             final SocketType socketType,
                                             final Set<SocketFlag> openFlags,
                                             final SizeT queueDepth,
                                             final Set<SocketOption> options) {
        server(promise::syncResolve, socketAddress, socketType, openFlags, queueDepth, options);
        return promise;
    }

    /**
     * Same as {@link #socket(Promise, AddressFamily, SocketType, Set, Set)} except new {@link Promise} instance is created rather than received as a parameter.
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
     * @return created {@link Promise} instance.
     *
     * @see ServerContext
     */
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
     * Accept incoming connection for server socket. Upon completion filled client connection descriptor is passed to callback as a
     * parameter.
     * <p>
     * Accepted connection receives its own socket which then can be used to communicate (read/write) with particular client.
     *
     * @param completion
     *         Callback which is invoked once operation is finished.
     * @param socket
     *         Server socket to accept connections on.
     * @param flags
     *         Accept flags (see {@link SocketFlag} for more details)
     * @see ClientConnection
     */
    void accept(final Consumer<Result<ClientConnection<?>>> completion,
                final FileDescriptor socket,
                final Set<SocketFlag> flags);

    /**
     * Same as {@link #accept(Consumer, FileDescriptor, Set)} except {@link Promise#syncResolve(Result)} is used as a callback.
     *
     * @param promise
     *         Input {@link Promise} instance to resolve upon completion.
     * @param socket
     *         Server socket to accept connections on.
     * @param flags
     *         Accept flags (see {@link SocketFlag} for more details)
     * @return input {@link Promise} instance.
     *
     * @see ClientConnection
     */
    default Promise<ClientConnection<?>> accept(final Promise<ClientConnection<?>> promise,
                                                final FileDescriptor socket,
                                                final Set<SocketFlag> flags) {
        accept(promise::syncResolve, socket, flags);
        return promise;
    }

    /**
     * Same as {@link #accept(Promise, FileDescriptor, Set)} except new {@link Promise} instance is created rather than received as a parameter.
     *
     * @param socket
     *         Server socket to accept connections on.
     * @param flags
     *         Accept flags (see {@link SocketFlag} for more details)
     * @return created {@link Promise} instance.
     *
     * @see ClientConnection
     */
    default Promise<ClientConnection<?>> accept(final FileDescriptor socket,
                                                final Set<SocketFlag> flags) {
        return accept(Promise.promise(), socket, flags);
    }

    /**
     * Submit CONNECT operation.
     * <p>
     * Connect to external server at provided address (host/port). Upon completion callback is invoked with the file descriptor
     * passed as a parameter for convenience.
     * <p>
     * Returned {@link Promise} for convenience holds the same file descriptor as passed in {@code socket} parameter.
     *
     * @param completion
     *         Callback which is invoked once operation is finished.
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

    /**
     * Same as {@link #connect(Consumer, FileDescriptor, SocketAddress, Option)} except {@link Promise#syncResolve(Result)} is used as a callback.
     *
     * @param promise
     *         Input {@link Promise} instance to resolve upon completion.
     * @param socket
     *         Socket to connect
     * @param address
     *         Address to connect
     * @param timeout
     *         Optional operation timeout.
     * @return input {@link Promise} instance.
     */
    default Promise<FileDescriptor> connect(final Promise<FileDescriptor> promise,
                                            final FileDescriptor socket,
                                            final SocketAddress<?> address,
                                            final Option<Timeout> timeout) {
        connect(promise::syncResolve, socket, address, timeout);
        return promise;
    }

    /**
     * Same as {@link #connect(Promise, FileDescriptor, SocketAddress, Option)} except new {@link Promise} instance is created rather than received as a parameter.
     *
     * @param socket
     *         Socket to connect
     * @param address
     *         Address to connect
     * @param timeout
     *         Optional operation timeout.
     * @return created {@link Promise} instance.
     */
    default Promise<FileDescriptor> connect(final FileDescriptor socket,
                                            final SocketAddress<?> address,
                                            final Option<Timeout> timeout) {
        return connect(Promise.promise(), socket, address, timeout);
    }

    /**
     * Get file status information for file specified by path.
     * Upon completion callback is invoked with requested file status details as a parameter.
     *
     * @param completion
     *         Callback which is invoked once operation is finished.
     * @param path
     *         File path
     * @param flags
     *         Flags which affect how information is retrieved, refer to {@link StatFlag} for more details
     * @param mask
     *         Specification of which information should be retrieved.
     *
     * @see FileStat
     */
    void stat(final Consumer<Result<FileStat>> completion,
              final Path path,
              final Set<StatFlag> flags,
              final Set<StatMask> mask);

    /**
     * Same as {@link #stat(Consumer, Path, Set, Set)} except {@link Promise#syncResolve(Result)} is used as a callback.
     *
     * @param promise
     *         Input {@link Promise} instance to resolve upon completion.
     * @param path
     *         File path
     * @param flags
     *         Flags which affect how information is retrieved, refer to {@link StatFlag} for more details
     * @param mask
     *         Specification of which information should be retrieved.
     * @return input {@link Promise} instance.
     *
     * @see FileStat
     */
    default Promise<FileStat> stat(final Promise<FileStat> promise,
                                   final Path path,
                                   final Set<StatFlag> flags,
                                   final Set<StatMask> mask) {
        stat(promise::syncResolve, path, flags, mask);
        return promise;
    }

    /**
     * Same as {@link #stat(Promise, Path, Set, Set)} except new {@link Promise} instance is created rather than received as a parameter.
     *
     * @param path
     *         File path
     * @param flags
     *         Flags which affect how information is retrieved, refer to {@link StatFlag} for more details
     * @param mask
     *         Specification of which information should be retrieved.
     * @return created {@link Promise} instance.
     *
     * @see FileStat
     */
    default Promise<FileStat> stat(final Path path,
                                   final Set<StatFlag> flags,
                                   final Set<StatMask> mask) {
        return stat(Promise.promise(), path, flags, mask);
    }

    /**
     * Get file status information for file specified by file descriptor.
     * Upon completion callback is invoked with requested file status details as a parameter.
     *
     * @param fd
     *         File descriptor
     * @param flags
     *         Flags which affect how information is retrieved, refer to {@link StatFlag} for more details
     * @param mask
     *         Specification of which information should be retrieved.
     *
     * @see FileStat
     */
    void stat(final Consumer<Result<FileStat>> completion,
              final FileDescriptor fd,
              final Set<StatFlag> flags,
              final Set<StatMask> mask);

    /**
     * Same as {@link #stat(Promise, FileDescriptor, Set, Set)} except {@link Promise#syncResolve(Result)} is used as a callback.
     *
     * @param promise
     *         Input {@link Promise} instance to resolve upon completion.
     * @param fd
     *         File descriptor
     * @param flags
     *         Flags which affect how information is retrieved, refer to {@link StatFlag} for more details
     * @param mask
     *         Specification of which information should be retrieved.
     * @return input {@link Promise} instance.
     *
     * @see FileStat
     */
    default Promise<FileStat> stat(final Promise<FileStat> promise,
                                   final FileDescriptor fd,
                                   final Set<StatFlag> flags,
                                   final Set<StatMask> mask) {
        stat(promise::syncResolve, fd, flags, mask);
        return promise;
    }

    /**
     * Same as {@link #stat(Promise, FileDescriptor, Set, Set)} except new {@link Promise} instance is created rather than received as a parameter.
     *
     * @param fd
     *         File descriptor
     * @param flags
     *         Flags which affect how information is retrieved, refer to {@link StatFlag} for more details
     * @param mask
     *         Specification of which information should be retrieved.
     * @return created {@link Promise} instance.
     *
     * @see FileStat
     */
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
     * Upon completion callback is invoked with total number of bytes read.
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

    /**
     * Same as {@link #readVector(Consumer, FileDescriptor, OffsetT, Option, OffHeapBuffer...)} except {@link Promise#syncResolve(Result)} is used as a callback.
     *
     * @param promise
     *         Input {@link Promise} instance to resolve upon completion.
     * @param fileDescriptor
     *         File descriptor to read from
     * @param offset
     *         Initial offset in the input file
     * @param timeout
     *         Optional operation timeout
     * @param buffers
     *         Set of buffers where read information will be put. Each buffer should have it's {@link OffHeapBuffer#used()} property set to actual number of bytes which application
     *         expects to see in this buffer.
     * @return input {@link Promise} instance.
     */
    default Promise<SizeT> readVector(final Promise<SizeT> promise,
                                      final FileDescriptor fileDescriptor,
                                      final OffsetT offset,
                                      final Option<Timeout> timeout,
                                      final OffHeapBuffer... buffers) {
        readVector(promise::syncResolve, fileDescriptor, offset, timeout, buffers);
        return promise;
    }

    /**
     * Same as {@link #readVector(Promise, FileDescriptor, OffsetT, Option, OffHeapBuffer...)} except new {@link Promise} instance is created rather than received as a parameter.
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
     * @return created {@link Promise} instance.
     */
    default Promise<SizeT> readVector(final FileDescriptor fileDescriptor,
                                      final OffsetT offset,
                                      final Option<Timeout> timeout,
                                      final OffHeapBuffer... buffers) {
        return readVector(Promise.promise(), fileDescriptor, offset, timeout, buffers);
    }

    /**
     * Write from buffers passed as a parameters.
     * <p>
     * Note that only {@link OffHeapBuffer#used()} portion of the each buffer is written.
     * <p>
     * Upon completion callback is invoked with total number of bytes written.
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

    /**
     * Same as {@link #writeVector(Consumer, FileDescriptor, OffsetT, Option, OffHeapBuffer...)} except {@link Promise#syncResolve(Result)} is used as a callback.
     *
     * @param promise
     *         Input {@link Promise} instance to resolve upon completion.
     * @param fileDescriptor
     *         File descriptor to read from
     * @param offset
     *         Initial offset in file
     * @param timeout
     *         Optional operation timeout
     * @param buffers
     *         Set of buffers to write from
     * @return input {@link Promise} instance.
     */
    default Promise<SizeT> writeVector(final Promise<SizeT> promise,
                                       final FileDescriptor fileDescriptor,
                                       final OffsetT offset,
                                       final Option<Timeout> timeout,
                                       final OffHeapBuffer... buffers) {
        writeVector(promise::syncResolve, fileDescriptor, offset, timeout, buffers);
        return promise;
    }

    /**
     * Same as {@link #writeVector(Promise, FileDescriptor, OffsetT, Option, OffHeapBuffer...)} except new {@link Promise} instance is created rather than received as a parameter.
     *
     * @param fileDescriptor
     *         File descriptor to read from
     * @param offset
     *         Initial offset in file
     * @param timeout
     *         Optional operation timeout
     * @param buffers
     *         Set of buffers to write from
     * @return created {@link Promise} instance.
     */
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

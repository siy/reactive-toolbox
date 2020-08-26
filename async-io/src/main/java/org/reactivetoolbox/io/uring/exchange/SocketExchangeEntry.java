package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.net.AddressFamily;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketOption;
import org.reactivetoolbox.io.async.net.SocketType;
import org.reactivetoolbox.io.uring.UringHolder;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.util.Set;
import java.util.function.BiConsumer;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_NOP;

public class SocketExchangeEntry extends AbstractExchangeEntry<SocketExchangeEntry, FileDescriptor> {
    private AddressFamily addressFamily;
    private SocketType socketType;
    private Set<SocketFlag> openFlags;
    private Set<SocketOption> options;

    protected SocketExchangeEntry(final PlainObjectPool<SocketExchangeEntry> pool) {
        super(IORING_OP_NOP, pool);
    }

    @Override
    protected void doAccept(final int result, final int flags, final Submitter submitter) {
        completion.accept(UringHolder.socket(addressFamily,
                                             socketType,
                                             openFlags,
                                             options),
                          submitter);
    }

    public SocketExchangeEntry prepare(final BiConsumer<Result<FileDescriptor>, Submitter> completion,
                                       final AddressFamily addressFamily,
                                       final SocketType socketType,
                                       final Set<SocketFlag> openFlags,
                                       final Set<SocketOption> options) {
        this.addressFamily = addressFamily;
        this.socketType = socketType;
        this.openFlags = openFlags;
        this.options = options;
        return super.prepare(completion);
    }
}

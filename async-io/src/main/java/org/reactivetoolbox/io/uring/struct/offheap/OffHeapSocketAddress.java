package org.reactivetoolbox.io.uring.struct.offheap;

import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.async.net.SocketAddress;
import org.reactivetoolbox.io.async.net.SocketAddressIn;
import org.reactivetoolbox.io.async.net.SocketAddressIn6;
import org.reactivetoolbox.io.uring.struct.AbstractOffHeapStructure;
import org.reactivetoolbox.io.uring.struct.ExternalRawStructure;
import org.reactivetoolbox.io.uring.struct.raw.RawSocketAddress;
import org.reactivetoolbox.io.uring.struct.raw.RawSocketAddressIn;
import org.reactivetoolbox.io.uring.struct.raw.RawSocketAddressIn6;

public class OffHeapSocketAddress<T extends SocketAddress<?>, R extends ExternalRawStructure<?>> extends AbstractOffHeapStructure<OffHeapSocketAddress<T,R>> {
    private static final int SIZE = 128;    //Equal to sizeof(struct sockaddr_storage)
    private final RawSocketAddress<T, R> shape;

    private OffHeapSocketAddress(final RawSocketAddress<T, R> shape) {
        super(SIZE);
        this.shape = shape;
        shape.shape().reposition(address());
    }

    public Result<T> extract() {
        return shape.extract();
    }

    public OffHeapSocketAddress<T,R> assign(final T input) {
        shape.assign(input);
        return this;
    }

    public static OffHeapSocketAddress<SocketAddressIn, RawSocketAddressIn> addressIn() {
        return new OffHeapSocketAddress<>(RawSocketAddressIn.at(0));
    }

    public static OffHeapSocketAddress<SocketAddressIn, RawSocketAddressIn> addressIn(final SocketAddressIn addressIn) {
        return new OffHeapSocketAddress<>(RawSocketAddressIn.at(0)).assign(addressIn);
    }

    public static OffHeapSocketAddress<SocketAddressIn6, RawSocketAddressIn6> addressIn6() {
        return new OffHeapSocketAddress<>(RawSocketAddressIn6.at(0));
    }

    public static OffHeapSocketAddress<SocketAddressIn6, RawSocketAddressIn6> addressIn6(final SocketAddressIn6 addressIn6) {
        return new OffHeapSocketAddress<>(RawSocketAddressIn6.at(0)).assign(addressIn6);
    }
}

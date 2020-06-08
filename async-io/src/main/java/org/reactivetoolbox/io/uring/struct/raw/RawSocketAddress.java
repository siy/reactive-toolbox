package org.reactivetoolbox.io.uring.struct.raw;

import org.reactivetoolbox.io.async.net.SocketAddress;
import org.reactivetoolbox.io.uring.struct.ExternalRawStructure;

public interface RawSocketAddress<T extends SocketAddress<?>, R extends ExternalRawStructure<?>> {
    void assign(final T input);

    T extract();

    R shape();
}

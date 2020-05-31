package org.reactivetoolbox.io.api.net;

public interface SocketAddressIn6 extends SocketAddress<Inet6Address> {
    Inet6FlowInfo flowInfo();
    Inet6ScopeId scopeId();
}

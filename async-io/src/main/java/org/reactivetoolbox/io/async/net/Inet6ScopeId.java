package org.reactivetoolbox.io.async.net;

//TODO: finish it
public class Inet6ScopeId {
    private final int scopeId;

    private Inet6ScopeId(final int scopeId) {
        this.scopeId = scopeId;
    }

    public static Inet6ScopeId inet6ScopeId(int scopeId) {
        return new Inet6ScopeId(scopeId);
    }

    public int scopeId() {
        return scopeId;
    }
}

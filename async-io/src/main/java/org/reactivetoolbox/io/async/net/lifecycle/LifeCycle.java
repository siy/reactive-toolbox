package org.reactivetoolbox.io.async.net.lifecycle;

import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.net.context.ConnectionContext;

public interface LifeCycle {
    void process(final ConnectionContext connectionContext, final Promise<Unit> completionPromise);
}

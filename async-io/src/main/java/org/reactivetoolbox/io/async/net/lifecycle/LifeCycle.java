package org.reactivetoolbox.io.async.net.lifecycle;

import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.net.context.IncomingConnectionContext;

public interface LifeCycle {
    void process(final IncomingConnectionContext incomingConnectionContext, final Promise<Unit> completionPromise);
}

package org.reactivetoolbox.io.async.util;

import org.reactivetoolbox.core.log.CoreLogger;
import org.reactivetoolbox.core.meta.AppMetaRepository;
import org.reactivetoolbox.io.scheduler.Timeout;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class BooleanLatch extends AbstractQueuedSynchronizer {
    @Override
    protected int tryAcquireShared(final int ignore) {
        return getState() == 0 ? -1 : 1;
    }

    @Override
    protected boolean tryReleaseShared(final int ignore) {
        setState(1);
        return true;
    }

    public void signal() {
        releaseShared(1);
    }

    public boolean await() {
        try {
            acquireSharedInterruptibly(1);
            return true;
        } catch (final InterruptedException e) {
            SingletonHolder.logger()
                           .debug("await() interrupted", e);
            return false;
        }
    }

    public boolean await(final Timeout timeout) {
        try {
            return tryAcquireSharedNanos(1, timeout.asNanos());
        } catch (final InterruptedException e) {
            SingletonHolder.logger()
                           .debug("await(Timeout) interrupted", e);
            return false;
        }
    }

    private static final class SingletonHolder {
        private static final CoreLogger LOGGER = AppMetaRepository.instance().get(CoreLogger.class);

        static CoreLogger logger() {
            return LOGGER;
        }
    }
}

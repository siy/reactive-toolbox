package org.reactivetoolbox.io.async.lifecycle;

import org.reactivetoolbox.io.async.Promise;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.reactivetoolbox.io.async.lifecycle.Repeat.END;

/**
 * The lifecycle which constantly repeats same operation until error or explicit stop request.
 */
public abstract class AbstractRepeatingLifeCycle implements LifeCycle<Repeat> {
    private final AtomicBoolean stopping = new AtomicBoolean(false);

    private final Promise<LifeCycle<Repeat>> stopPromise;

    protected AbstractRepeatingLifeCycle() {
        this.stopPromise = Promise.promise();
    }

    protected Promise<LifeCycle<Repeat>> stopPromise() {
        return stopPromise;
    }

    @Override
    public Promise<LifeCycle<Repeat>> stop() {
        stopping.compareAndSet(false, true);
        return stopPromise;
    }

    @Override
    public Promise<Repeat> transition(final Repeat state) {
        return switch (state) {
            case END -> Promise.readyOk(END)
                               .onResult($ -> stopPromise.ok(this));

            case REPEAT -> stopping.get()
                           ? stopPromise.ok(this).map($ -> END)
                           : repeat();
        };
    }

    abstract protected Promise<Repeat> repeat();
}

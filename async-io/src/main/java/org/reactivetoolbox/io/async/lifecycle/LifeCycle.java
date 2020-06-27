package org.reactivetoolbox.io.async.lifecycle;

import org.reactivetoolbox.io.async.Promise;

public interface LifeCycle<T extends Terminable> {
    Promise<T> stop();

    Promise<T> transition(final T state);

    default Promise<LifeCycle<T>> start(final LifeCycle<T> cycle, final T initial) {
        return next(cycle, initial).map(s -> cycle);
    }

    private Promise<T> next(final LifeCycle<T> cycle, final T state) {
        return cycle.transition(state)
                    .andThenAsync(nextState -> nextState.isTerminal() ? Promise.readyOk(nextState)
                                                                      : next(cycle, nextState));
    }
}

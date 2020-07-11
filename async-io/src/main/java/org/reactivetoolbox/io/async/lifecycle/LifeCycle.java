package org.reactivetoolbox.io.async.lifecycle;

import org.reactivetoolbox.io.async.Promise;

public interface LifeCycle<T extends Terminable> {
    Promise<LifeCycle<T>> stop();

    Promise<T> transition(final T state);

    static <T extends Terminable> Promise<LifeCycle<T>> start(final LifeCycle<T> cycle, final T initial) {
        return Promise.asyncPromise(promise -> { cycle.next(initial); promise.ok(cycle);});
    }

    private Promise<T> next(final T state) {
        return transition(state).flatMap(nextState -> nextState.isTerminal() ? Promise.readyOk(nextState)
                                                                             : next(nextState));
    }
}

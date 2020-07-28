package org.reactivetoolbox.core.lang.functional;

import org.reactivetoolbox.core.lang.Tuple.Tuple2;
import org.reactivetoolbox.core.lang.collection.List;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;

import static org.reactivetoolbox.core.lang.Tuple.tuple;
import static org.reactivetoolbox.core.lang.functional.Unit.unit;

/**
 * State monad.
 *
 * @param <S>
 *         State type
 * @param <A>
 *         Value type
 */
public class State<S, A> {
    private final FN1<Tuple2<A, S>, S> runState;

    private State(final FN1<Tuple2<A, S>, S> runState) {
        this.runState = runState;
    }

    public static <S> State<S, S> get() {
        return new State<>(s -> tuple(s, s));
    }

    /**
     * Create State for given value.
     *
     * @param a
     *         Value
     *
     * @return created State
     */
    public static <S, A> State<S, A> state(final A a) {
        return new State<>(s -> tuple(a, s));
    }

    /**
     * Create State for given function.
     *
     * @param function
     *        Function
     *
     * @return created State
     */
    public static <S, A> State<S, A> state(final FN1<A, S> function) {
        return new State<>(s -> tuple(function.apply(s), s));
    }

    /**
     * Create new state from given transformation function.
     *
     * @param function
     *        Transformation function
     *
     * @return created State
     */
    public static <S> State<S, Unit> transition(final FN1<S, S> function) {
        return new State<>(s -> tuple(unit(), function.apply(s)));
    }

    /**
     * Create new state from given value and transformation function
     * @param function
     *        Transformation function
     * @param value
     *        Value
     *
     * @return created State
     */
    public static <S, A> State<S, A> transition(final FN1<S, S> function, final A value) {
        return new State<>(s -> tuple(value, function.apply(s)));
    }


    public static <S, A> State<S, List<A>> compose(final List<State<S, A>> fs) {
        return fs.foldRight(state(List.list()),
                            f -> acc -> f.map(acc, a -> b -> b.append(a)));
    }

    public <B> State<S, B> flatMap(final FN1<State<S, B>, A> f) {
        return new State<>(s -> run(s).map((value, state) -> f.apply(value)
                                                              .run(state)));
    }

    public <B> State<S, B> map(final FN1<B, A> mapper) {
        return flatMap(a -> state(mapper.apply(a)));
    }

    public <B, C> State<S, C> map(final State<S, B> sb, final FN1<FN1<C, B>, A> mapper) {
        return flatMap(a -> sb.map(b -> mapper.apply(a)
                                              .apply(b)));
    }

    /**
     * Retrieve value.
     *
     * @param s
     *         Input state.
     * @return retrieved value
     */
    public A eval(final S s) {
        return run(s).map((value, state) -> value);
    }

    private Tuple2<A, S> run(final S s) {
        return runState.apply(s);
    }
}

/*
 * Copyright (c) 2020 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 * @param <T>
 *         Value type
 */
public class State<S, T> {
    private final FN1<Tuple2<T, S>, S> runState;

    private State(final FN1<Tuple2<T, S>, S> runState) {
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

    public <B> State<S, B> flatMap(final FN1<State<S, B>, T> f) {
        return new State<>(s -> run(s).map((value, state) -> f.apply(value)
                                                              .run(state)));
    }

    public <B> State<S, B> map(final FN1<B, T> mapper) {
        return flatMap(t -> state(mapper.apply(t)));
    }

    public <B, C> State<S, C> map(final State<S, B> sb, final FN1<FN1<C, B>, T> mapper) {
        return flatMap(t -> sb.map(b -> mapper.apply(t)
                                              .apply(b)));
    }

    /**
     * Retrieve value.
     *
     * @param s
     *         Input state.
     * @return retrieved value
     */
    public T eval(final S s) {
        return run(s).map((value, state) -> value);
    }

    private Tuple2<T, S> run(final S s) {
        return runState.apply(s);
    }
}

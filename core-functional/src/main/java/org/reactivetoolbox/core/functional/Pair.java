package org.reactivetoolbox.core.functional;
/*
 * Copyright (c) 2017-2019 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Functions.FN2;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * General purpose container suitable for holding pair of linked values, like key and get in map.
 *
 * @param <L>
 *        type of failure get in pair
 * @param <R>
 *        type of success get in pair
 */
//TODO: docs
//TODO: add swap, flatMap
public final class Pair<L, R> {
    private final L left;
    private final R right;

    private Pair(final L left, final R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Factory method for creation of instances of {@link Pair} with given values.
     *
     * @param left
     *        failure get
     * @param right
     *        success get
     * @param <L>
     *        type of failure get
     * @param <R>
     *        type of success get
     * @return created instance
     */
    public static <L, R> Pair<L, R> of(final L left, final R right) {
        return new Pair<>(left, right);
    }

    public L left() {
        return left;
    }

    public R right() {
        return right;
    }

    public <L1, R1> Pair<L1, R1> map(final FN1<L1, L> leftMapper, final FN1<R1, R> rigthMapper) {
        return of(leftMapper.apply(left), rigthMapper.apply(right));
    }

    public <V> V map(final FN2<V, L, R> mapper) {
        return mapper.apply(left, right);
    }

    public <L1, R1> Pair<L1, R1> flatMap(final FN2<Pair<L1, R1>, L, R> mapper) {
        return mapper.apply(left, right);
    }

    public Pair<R, L> swap() {
        return of(right, left);
    }

    public Option<L> tryLeft() {
        return Option.of(left);
    }

    public Option<R> tryRight() {
        return Option.of(right);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "<", ">")
                .add(Objects.toString(left))
                .add(Objects.toString(right))
                .toString();
    }
}

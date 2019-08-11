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
 * General purpose container suitable for holding pair of linked values, like key and value in map.
 *
 * @param <L>
 *        Type of left value
 * @param <R>
 *        Type of right value
 */
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
     *        Left value
     * @param right
     *        Right value
     * @param <L>
     *        type of left value
     * @param <R>
     *        type of right value
     * @return created instance
     */
    public static <L, R> Pair<L, R> of(final L left, final R right) {
        return new Pair<>(left, right);
    }

    /**
     * Factory method for creation of instances of {@link Pair} with given values.
     *
     * @param left
     *        Left value
     * @param right
     *        Right value
     * @param <L>
     *        type of left value
     * @param <R>
     *        type of right value
     * @return created instance
     */
    public static <L, R> Pair<L, R> pair(final L left, final R right) {
        return new Pair<>(left, right);
    }

    /**
     * Get left value in pair
     *
     * @return left value
     */
    public L left() {
        return left;
    }

    /**
     * Get right value in pair
     *
     * @return right value
     */
    public R right() {
        return right;
    }

    /**
     * Transform both values in pair and construct an instance with new values.
     *
     * @param leftMapper
     *        Function used to transform left value
     * @param rightMapper
     *        Function used to transform right value
     * @param <L1>
     *        New type of left value
     * @param <R1>
     *        New type of right value
     *
     * @return Transformed pair
     */
    public <L1, R1> Pair<L1, R1> map(final FN1<L1, L> leftMapper, final FN1<R1, R> rightMapper) {
        return of(leftMapper.apply(left), rightMapper.apply(right));
    }

    /**
     * Transform pair into value of other type
     * @param mapper
     *        Function used transform pair
     * @param <V>
     *        Type of new value
     *
     * @return Result of transformation
     */
    public <V> V map(final FN2<V, L, R> mapper) {
        return mapper.apply(left, right);
    }

    /**
     * Transform pair into pair of values of other types
     *
     * @param mapper
     *        Function used to transform pair
     * @param <L1>
     *        New left value type
     * @param <R1>
     *        New right value type
     *
     * @return Pair of transformed values
     */
    public <L1, R1> Pair<L1, R1> flatMap(final FN2<Pair<L1, R1>, L, R> mapper) {
        return mapper.apply(left, right);
    }

    /**
     * Swap left and right values.
     *
     * @return Transformed pair
     */
    public Pair<R, L> swap() {
        return of(right, left);
    }

    /**
     * Get access to left value
     *
     * @return {@link Option} which contains left value
     */
    public Option<L> tryLeft() {
        return Option.of(left);
    }

    /**
     * Get access to right value
     *
     * @return {@link Option} which contains right value
     */
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

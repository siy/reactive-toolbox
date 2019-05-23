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

/**
 * General purpose container suitable for holding pair of linked values, like key and value in map.
 *
 * @param <L>
 *        type of left value in pair
 * @param <R>
 *        type of right value in pair
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
     *        left value
     * @param right
     *        right value
     * @param <L>
     *        type of left value
     * @param <R>
     *        type of right value
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
}

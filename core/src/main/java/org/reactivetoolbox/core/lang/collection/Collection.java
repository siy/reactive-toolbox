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

package org.reactivetoolbox.core.lang.collection;

import org.reactivetoolbox.core.lang.Pair;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Collection<E> {
    /**
     * Return list consisting of elements obtained from elements of current list with applied
     * transformation function.
     *
     * @param mapper
     *        Transformation function
     *
     * @return New list with transformed elements
     */
    <R> Collection<R> map(FN1<R, E> mapper);

    /**
     * Applies specified consumer to elements of current list.
     *
     * @param consumer
     *        Consumer for elements
     *
     * @return Current list
     */
    Collection<E> apply(Consumer<E> consumer);

    /**
     * Create {@link Stream} from list elements.
     *
     * @return Created stream
     */
    Stream<E> stream();

    /**
     * Return list size.
     *
     * @return number of elements in list
     */
    int size();

    /**
     * Create new list which will hold only elements which satisfy provided predicate.
     *
     * @param predicate
     *        Predicate to apply to elements
     *
     * @return List of elements for which predicate returned {@code true}
     */
    Collection<E> filter(Predicate<E> predicate);

    /**
     * Split current list into two using provided predicate. Result is a pair of lists. Left element of pair
     * contains list with elements evaluated to {@code false} by predicate. Right element of pair contains
     * list with elements evaluated to {@code true} by predicate.
     *
     * @param predicate
     *        Predicate to apply to elements
     *
     * @return Pair of lists with results
     */
    Pair<? extends Collection<E>, ? extends Collection<E>> splitBy(Predicate<E> predicate);
}

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

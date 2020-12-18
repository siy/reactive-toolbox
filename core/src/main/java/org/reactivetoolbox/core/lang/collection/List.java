/*
 * Copyright (c) 2019, 2020 Sergiy Yevtushenko
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
import org.reactivetoolbox.core.lang.functional.Functions.FN2;
import org.reactivetoolbox.core.lang.functional.Option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Immutable List
 */
//TODO: experimental, requires more considerations and, most likely, more efficient implementation
public interface List<E> extends Collection<E> {
    /**
     * Return first element from list.
     *
     * @return First element wrapped into {@link Option} if element is present or {@link Option#empty()} otherwise
     */
    Option<E> first();

    /**
     * Return first {@code N} elements from the list. If there are less elements than requested, then available
     * elements returned.
     * @param n
     *        Number of elements to return.
     *
     * @return New list with at most requested number of elements
     */
    List<E> first(int n);

    /**
     * Return last element from list.
     *
     * @return Last element wrapped into {@link Option} if element is present or {@link Option#empty()} otherwise
     */
    Option<E> last();

    /**
     * Return list which contains elements from current list followed by elements from list provided as parameter.
     *
     * @param other
     *        List to append
     *
     * @return List with elements from current list followed by elements from {@code other} list
     */
    List<E> append(List<E> other);

    default List<E> append(final E value) {
        return append(list(value));
    }

    /**
     * Return list which contains elements from list provided as parameter followed by elements of current list.
     *
     * @param other
     *        List to append to
     *
     * @return List with elements from {@code other} list followed by elements from current list
     */
    default List<E> prepend(final List<E> other) {
        return other.append(this);
    }

    @Override
    default <R> List<R> map(final FN1<R, E> mapper) {
        return mapN((n, e) -> mapper.apply(e));
    }

    /**
     * Return list consisting of elements obtained from elements of current list with applied
     * transformation function. Unlike {@link #map(FN1)} this method passes index of element
     * along with element to transformation function.
     *
     * @param mapper
     *        Transformation function
     *
     * @return New list with transformed elements
     */
    <R> List<R> mapN(final FN2<R, Integer, E> mapper);

    /**
     * Perform 'folding' or 'reduction' of list elements starting from the rightmost (last) element.
     * <p>
     * Unlike similar {@link Stream#reduce(Object, BiFunction, BinaryOperator)} method, all reduction operations are
     * expressed using single function which accepts the element value and returns function which combines (probably
     * transformed) value with accumulator. Although this sounds complicated, in practice this results to very concise
     * expressions. For example:
     * <pre>
     * list.foldRight(0, value -> sum -> sum + value); // sum of all elements in the list
     * list.foldRight("", value -> sum -> sum + " " + value); // string with list values
     * </pre>
     *
     * @param seed
     *        Initial value
     * @param function
     *        Function which will be applied to every element
     *
     * @return result of application of provided function to all list elements.
     */
    <V> V foldRight(final V seed, final FN1<FN1<V, V>, E> function);

    /**
     * Perform 'folding' or 'reduction' of list elements starting from the leftmost (first) element.
     * <p>
     * Unlike similar {@link Stream#reduce(Object, BiFunction, BinaryOperator)} method, all reduction operations are
     * expressed using single function which accepts the element value and returns function which combines (probably
     * transformed) value with accumulator. Although this sounds complicated, in practice this results to very concise
     * expressions. For example:
     * <pre>
     * list.foldLeft(0, value -> sum -> sum + value); // sum of all elements in the list
     * list.foldLeft("", value -> sum -> sum + " " + value); // string with list values
     * </pre>
     *
     * @param seed
     *        Initial value
     * @param function
     *        Function which will be applied to every element
     *
     * @return result of application of provided function to all list elements.
     */
    <V> V foldLeft(final V seed, final FN1<FN1<V, V>, E> function);

    @Override
    default List<E> apply(final Consumer<E> consumer) {
        return applyN((n, e) -> consumer.accept(e));
    }

    /**
     * Applies specified consumer to elements of current list.
     * Unlike {@link #apply(Consumer)} element index is passed along with element to consumer.
     *
     * @param consumer
     *        Consumer for elements
     *
     * @return Current list
     */
    List<E> applyN(BiConsumer<Integer, E> consumer);

    /**
     * Create new list which will hold the same elements but sorted according to
     * provided comparator.
     *
     * @param comparator
     *        Element comparator
     *
     * @return Sorted list
     */
    List<E> sort(Comparator<E> comparator);

    /**
     * Create new list which will hold the same elements sorted.
     *
     * @return Sorted list
     */
    List<E> sort();

    /**
     * Create new list which contains same elements reordered using given source of random numbers.
     *
     * @param random
     *        Random number source
     *
     * @return Shuffled list
     */
    List<E> shuffle(Random random);

    @Override
    default List<E> filter(final Predicate<E> predicate) {
        return foldLeft(ListBuilder.<E>builder(size()),
                        e -> builder -> predicate.test(e) ? builder.append(e): builder).build();
    }

    @Override
    default Pair<List<E>, List<E>> splitBy(final Predicate<E> predicate) {
        final var listFalse = ListBuilder.<E>builder(size());
        final var listTrue = ListBuilder.<E>builder(size());

        mapN((n, e) -> (predicate.test(e)
                        ? listTrue.append(e)
                        : listFalse.append(e)));

        return Pair.pair(listFalse.build(), listTrue.build());
    }

    boolean elementEquals(E[] elements);

    @SuppressWarnings("unchecked")
    static <T> List<T> from(final java.util.Collection<T> source) {
        return (List<T>) list(source.toArray());
    }

    @SuppressWarnings("unchecked")
    static <T> List<T> list() {
        return (List<T>) EMPTY_LIST;
    }

    static <E> CollectionBuilder<List<E>, E> builder() {
        return ListBuilder.builder(1);
    }

    //TODO: need more efficient implementation
    static <T> List<T> list(final T... elements) {
        return new List<>() {
            @Override
            public <R> List<R> mapN(final FN2<R, Integer, T> mapper) {
                return ListBuilder.<R>builder(size()).then(builder -> {
                    for (int i = 0; i < elements.length; i++) {
                        builder.append(mapper.apply(i, elements[i]));
                    }
                }).build();
            }

            @Override
            public <V> V foldRight(final V seed, final FN1<FN1<V, V>, T> function) {
                var result = seed;

                for (int i = elements.length - 1; i >= 0; i--) {
                    result = function.apply(elements[i])
                                     .apply(result);
                }

                return result;
            }

            @Override
            public <V> V foldLeft(final V seed, final FN1<FN1<V, V>, T> function) {
                var result = seed;

                for (int i = 0; i < elements.length; i++) {
                    result = function.apply(elements[i])
                                     .apply(result);
                }

                return result;
            }

            @Override
            public List<T> applyN(final BiConsumer<Integer, T> consumer) {
                for (int i = 0; i < elements.length; i++) {
                    consumer.accept(i, elements[i]);
                }
                return this;
            }

            @Override
            public Option<T> first() {
                return elements.length > 0 ? Option.option(elements[0]) : Option.empty();
            }

            @Override
            public Option<T> last() {
                return elements.length > 0 ? Option.option(elements[elements.length - 1]) : Option.empty();
            }

            @Override
            public List<T> first(final int n) {
                return list(Arrays.copyOf(elements, max(0, min(elements.length, n))));
            }

            @Override
            public List<T> append(final List<T> other) {
                return ListBuilder.builder(other.size(), elements)
                        .append(other).build();
            }

            @Override
            public Stream<T> stream() {
                return Arrays.stream(elements);
            }

            @Override
            public int size() {
                return elements.length;
            }

            @Override
            public boolean elementEquals(final T[] other) {
                return Arrays.equals(elements, other);
            }

            @Override
            public List<T> sort(final Comparator<T> comparator) {
                final var count = Arrays.copyOf(elements, elements.length);
                Arrays.sort(count, comparator);
                return list(count);
            }

            @Override
            public List<T> sort() {
                final var count = Arrays.copyOf(elements, elements.length);
                Arrays.sort(count, 0, count.length);
                return list(count);
            }

            @Override
            public List<T> shuffle(final Random random) {
                final var count = Arrays.copyOf(elements, elements.length);

                for (int i = 0; i < count.length; i++) {
                    final var pos = random.nextInt(count.length);
                    final var element = count[pos];
                    count[pos] = count[i];
                    count[i] = element;
                }
                return list(count);
            }

            @Override
            public int hashCode() {
                return Arrays.hashCode(elements);
            }

            @Override
            public boolean equals(final Object obj) {
                if (obj == this) {
                    return true;
                }

                if (obj instanceof List list) {
                    return list.size() == size() && list.elementEquals(elements);
                }

                return false;
            }

            @Override
            public String toString() {
                final StringJoiner joiner = new StringJoiner(",", "List(", ")");
                apply(e -> joiner.add(e.toString()));
                return joiner.toString();
            }
        };
    }

    final class ListBuilder<T> implements CollectionBuilder<List<T>, T> {
        private final ArrayList<T> values;

        private ListBuilder(final int capacity) {
            values = new ArrayList<>(capacity);
        }

        private ListBuilder(final int extraSize, final T[] elements) {
            values = new ArrayList<>(elements.length + extraSize);
            values.addAll(Arrays.asList(elements));
        }

        static <E> ListBuilder<E> builder(final int extraSize, final E[] elements) {
            return new ListBuilder<>(extraSize, elements);
        }

        static <E> ListBuilder<E> builder(final int capacity) {
            return new ListBuilder<>(capacity);
        }

        @Override
        public ListBuilder<T> append(final T e) {
            values.add(e);
            return this;
        }

        @Override
        public ListBuilder<T> append(final T[] elements) {
            values.addAll(Arrays.asList(elements));
            return this;
        }

        ListBuilder<T> then(final Consumer<ListBuilder<T>> consumer) {
            consumer.accept(this);
            return this;
        }

        ListBuilder<T> append(final List<T> other) {
            other.apply(values::add);
            return this;
        }

        @Override
        public List<T> build() {
            return from(values);
        }
    }

    Collection EMPTY_LIST = new List() {
        @Override
        public List mapN(final FN2 mapper) {
            return this;
        }

        @Override
        public List applyN(final BiConsumer consumer) {
            return this;
        }

        @Override
        public Option first() {
            return Option.empty();
        }

        @Override
        public Option last() {
            return Option.empty();
        }

        @Override
        public List first(final int n) {
            return this;
        }

        @Override
        public List append(final List other) {
            return other;
        }

        @Override
        public Object foldLeft(final Object seed, final FN1 function) {
            return seed;
        }

        @Override
        public Object foldRight(final Object seed, final FN1 function) {
            return seed;
        }

        @Override
        public Stream stream() {
            return Stream.empty();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean elementEquals(final Object[] elements) {
            return elements.length == 0;
        }

        @Override
        public List sort(final Comparator comparator) {
            return this;
        }

        @Override
        public List sort() {
            return this;
        }

        @Override
        public List shuffle(final Random random) {
            return this;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }

            return obj instanceof List list && list.size() == 0;
        }

        @Override
        public String toString() {
            return "List()";
        }
    };

    static <T> Collector<T, ListBuilder<T>, List<T>> toList() {
        return new Collector<>() {
            @Override
            public Supplier<ListBuilder<T>> supplier() {
                return () -> new ListBuilder<>(0);
            }

            @Override
            public BiConsumer<ListBuilder<T>, T> accumulator() {
                return ListBuilder::append;
            }

            @Override
            public BinaryOperator<ListBuilder<T>> combiner() {
                return (left, right) -> {
                    left.append(right.build());
                    return left;
                };
            }

            @Override
            public Function<ListBuilder<T>, List<T>> finisher() {
                return ListBuilder::build;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of();
            }
        };
    }
}

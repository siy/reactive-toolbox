package org.reactivetoolbox.core.lang.collection;

/*
 * Copyright (c) 2019 Sergiy Yevtushenko
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

import org.reactivetoolbox.core.lang.Pair;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Option;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Immutable Set
 */
//TODO: experimental, requires more considerations and, most likely, more efficient implementation
public interface Set<E> extends Collection<E> {
    Option<E> any();

    Set<E> any(final int n);

    Set<E> merge(final Set<E> other);

    @Override
    <R> Set<R> map(final FN1<R, E> mapper);

    @Override
    Set<E> apply(final Consumer<E> consumer);

    @Override
    Stream<E> stream();

    @Override
    int size();

    @Override
    Set<E> filter(final Predicate<E> predicate);

    @Override
    Pair<Set<E>, Set<E>> splitBy(final Predicate<E> predicate);

    //TODO: is it necessary?
    boolean equals(final E... elements);

//    @SuppressWarnings("unchecked")
//    static <T> Set<T> from(final java.util.Collection<T> source) {
//        return (Set<T>) set(source.toArray());
//    }
//
//    @SuppressWarnings("unchecked")
//    static <T> Set<T> set() {
//        return (Set<T>) EMPTY_SET;
//    }
//
//    static <T> Set<T> set(final T... elements) {
//        return new Set<T>() {
//            @Override
//            public int hashCode() {
//                return Arrays.hashCode(elements);
//            }
//
//            @SuppressWarnings("unchecked")
//            @Override
//            public boolean equals(final Object obj) {
//                if (obj == this) {
//                    return true;
//                }
//
//                if(obj instanceof Set) {
//                    final var list = (Set) obj;
//                    return list.size() == size() && list.equals(elements);
//                }
//
//                return false;
//            }
//
//            @Override
//            public String toString() {
//                final StringJoiner joiner = new StringJoiner(",", "List(", ")");
//                apply(e -> joiner.add(e.toString()));
//                return joiner.toString();
//            }
//        };
//    }
//
//    Set EMPTY_SET = new Set() {
//        @Override
//        public int hashCode() {
//            return 0;
//        }
//
//        @Override
//        public boolean equals(final Object obj) {
//            if (obj == this) {
//                return true;
//            }
//
//            if (obj instanceof Set && ((Set) obj).size() == 0) {
//                return true;
//            }
//
//            return false;
//        }
//
//        @Override
//        public String toString() {
//            return "List()";
//        }
//    };
//
//    static <T> Collector<T, ListBuilder<T>, Set<T>> toList() {
//        return new Collector<>() {
//            @Override
//            public Supplier<ListBuilder<T>> supplier() {
//                return () -> new ListBuilder<T>(16);
//            }
//
//            @Override
//            public BiConsumer<ListBuilder<T>, T> accumulator() {
//                return ListBuilder::append;
//            }
//
//            @Override
//            public BinaryOperator<ListBuilder<T>> combiner() {
//                return (left, right) -> {
//                    left.append(right.toList());
//                    return left;
//                };
//            }
//
//            @Override
//            public Function<ListBuilder<T>, Set<T>> finisher() {
//                return ListBuilder::toList;
//            }
//
//            @Override
//            public java.util.Set<Characteristics> characteristics() {
//                return java.util.Set.of();
//            }
//        };
//    }
}

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

import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.reactivetoolbox.core.lang.functional.Option.option;

public interface When {
    interface ExtendedMatcher<T> extends Matcher<T> {
        default void otherwise(final Consumer<T> consumer) {
            value().whenPresent(consumer);
        }

        default void otherwiseDo(final Runnable runnable) {
            value().whenPresent(__ -> runnable.run());
        }
    }

    interface Matcher<T> {
        Option<T> value();

        default <R extends T> Action<T, R> isA(final Class<R> clazz) {
            return apply(clazz::isInstance);
        }

        default Action<T, T> equal(final T otherValue) {
            return apply(v -> v.equals(otherValue));
        }

        @SuppressWarnings("unchecked")
        default <R extends Comparable<R>> Action<R, R> lt(final R otherValue) {
            return (Action<R, R>) apply(value -> ((R) value).compareTo(otherValue) < 0);
        }

        @SuppressWarnings("unchecked")
        default <R extends Comparable<R>> Action<R, R> gt(final R otherValue) {
            return (Action<R, R>) apply(value -> ((R) value).compareTo(otherValue) > 0);
        }

        @SuppressWarnings("unchecked")
        default <R extends Comparable<R>> Action<R, R> le(final R otherValue) {
            return (Action<R, R>) apply(value -> ((R) value).compareTo(otherValue) <= 0);
        }

        @SuppressWarnings("unchecked")
        default <R extends Comparable<R>> Action<R, R> ge(final R otherValue) {
            return (Action<R, R>) apply(value -> ((R) value).compareTo(otherValue) >= 0);
        }

        @SuppressWarnings("unchecked")
        default <R extends Comparable<R>> Action<R, R> eq(final R otherValue) {
            return (Action<R, R>) apply(value -> ((R) value).compareTo(otherValue) == 0);
        }

        @SuppressWarnings("unchecked")
        default <R extends Comparable<R>> Action<R, R> neq(final R otherValue) {
            return (Action<R, R>) apply(value -> ((R) value).compareTo(otherValue) != 0);
        }

        @SuppressWarnings("unchecked")
        private <R extends T> Action<T, R> run(final boolean result, final T value) {
            return result
                   ? consumer -> { consumer.accept((R) value); return Option::empty; }
                   : __ -> (ExtendedMatcher<T>) this;
        }

        @SuppressWarnings("unchecked")
        private <R extends T> Action<T, R> apply(final Predicate<T> checker) {
            return (Action<T, R>) value().map(v -> run(checker.test(v), v))
                                         .otherwise(__ -> (ExtendedMatcher<T>) this);
        }
    }

    interface Action<T, R> {
        ExtendedMatcher<T> then(Consumer<R> consumer);
    }

    static <T> Matcher<T> when(final T variable) {
        return (ExtendedMatcher<T>) () -> option(variable);
    }
}

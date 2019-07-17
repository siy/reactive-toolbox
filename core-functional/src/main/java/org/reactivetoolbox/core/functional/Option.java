package org.reactivetoolbox.core.functional;

import org.reactivetoolbox.core.functional.Functions.FN1;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

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

//TODO: Javadoc
public class Option<T> {
    private final T value;

    private Option(T value) {
        this.value = value;
    }

    public static <R> Option<R> empty() {
        return new Option<>(null);
    }

    public static <R> Option<R> of(final R value) {
        return new Option<>(value);
    }

    public T get() {
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    public boolean isEmpty() {
        return value == null;
    }

    public Option<T> filter(final Predicate<? super T> predicate) {
        return isEmpty() ? this : predicate.test(value) ? this : empty();
    }

    public <U> Option<U> map(final FN1<U, T> mapper) {
        return isEmpty() ? empty() : Option.of(mapper.apply(value));
    }

    public <U> Option<U> flatMap(final FN1<Option<U>, T> mapper) {
        return isEmpty() ? empty() : mapper.apply(value);
    }

    public Option<T> or(Supplier<? extends Option<T>> supplier) {
        return isPresent() ? this : supplier.get();
    }

    public T otherwise(T other) {
        return value != null ? value : other;
    }

    public T otherwise(Supplier<? extends T> supplier) {
        return value != null ? value : supplier.get();
    }

    public Stream<T> stream() {
        return isEmpty() ? Stream.empty() : Stream.of(value);
    }

    public T otherwiseThrow() {
        if (isPresent()) {
            return value;
        }

        throw new IllegalStateException("No value present");
    }

    public <X extends Throwable> T otherwiseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (isPresent()) {
            return value;
        }

        throw exceptionSupplier.get();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Option<?> option = (Option<?>) o;
        return Objects.equals(value, option.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return isPresent() ? new StringBuilder(Option.class.getSimpleName())
                .append('<').append(value.getClass().getSimpleName()).append('>')
                .append('(').append(value.toString()).append(')').toString()
                : EMPTY_TO_STRING;
    }
    private static final String EMPTY_TO_STRING = Option.class.getSimpleName() + ".empty";
}

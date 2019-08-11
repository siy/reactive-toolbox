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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Implementation of basic immutable container for value which may or may not be present
 *
 * @param <T>
 *        Type of contained value
 */
public class Option<T> {
    private final T value;

    private Option(final T value) {
        this.value = value;
    }

    /**
     * Create empty instance.
     *
     * @return Created instance
     */
    public static <R> Option<R> empty() {
        return new Option<>(null);
    }

    /**
     * Create a present or empty instance depending on the passed value.
     *
     * @param value
     *        Value to be stored in the created instance
     * @return Created instance
     */
    public static <R> Option<R> of(final R value) {
        return new Option<>(value);
    }

    /**
     * Get contained value without any checks or throwing exceptions.
     *
     * @return contained value, either <code>null</code> or <code>non-null</code>
     */
    public T get() {
        return value;
    }

    /**
     * Check if instance contains value.
     *
     * @return <code>true</code> if instance contains value and <code>false</code> otherwise
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * Check if instance is empty
     *
     * @return <code>true</code> if instance is empty (does not contain value) and <code>false</code> otherwise
     */
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * Transform instance according to results of testing of contained value with provided predicate.
     * If instance is empty, it remains empty. If instance contains value, this value is passed to predicate.
     * If predicate returns <code>true</code> then instance remains untouched. If predicate returns <code>false</code>
     * then empty instance is returned instead.
     *
     * @param predicate
     *        Predicate to test instance value.
     *
     * @return current instance if it is not empty and predicate returns <code>true</code> and empty instance otherwise
     */
    public Option<T> filter(final Predicate<? super T> predicate) {
        return isEmpty() ? this : predicate.test(value) ? this : empty();
    }

    /**
     * Convert instance into other instance of different type using provided mapping function. Empty instance is mapped
     * into empty instance of different type. Non-empty instance is converted to empty or non-empty instance depending
     * on results of execution of mapping function. Mapping function receives value contained in the current instance
     * only if current instance is not empty. Result of invocation of mapping function is wrapped into new instance.
     * If result of application of mapping function is <code>null</code>, then resulting instance will be empty.
     * <b>WARNING!</b> While it is highly discouraged to use this method with mapping functions which may return
     * <code>null</code>. Such a usage may result to subtle, hard to debug issues.
     * If such a behavior is actually necessary then use of {@link #flatMap(FN1)} provides clear and much less
     * error-prone way to achieve it.
     *
     * @param mapper
     *        Mapping function
     * @param <U>
     *        Type of new value
     * @return transformed instance
     */
    public <U> Option<U> map(final FN1<U, ? super T> mapper) {
        return isEmpty() ? empty() : Option.of(mapper.apply(value));
    }

    /**
     * Pass internal value to provided consumer in-line. Consumer is invoked only is current instance is not empty.
     * This is a convenience method which can be inserted at any point of fluent call chain. Note that provided
     * consumer should not change value in any way (for example, if contained value is mutable collection/map/array/etc.)
     * and should not throw any kind of exceptions.
     *
     * @param consumer
     *        Consumer to pass contained value to
     * @return this instance for fluent call chaining
     */
    public Option<T> consume(final Consumer<? super T> consumer) {
        if (isPresent()) {
            consumer.accept(value);
        }
        return this;
    }

    /**
     * Replace current non-empty instance with another one generated by applying provided mapper to value stored
     * in this instance. Empty instance is replaced with empty instance of new type matching type of provided mapping
     * function.
     *
     * @param mapper
     *        Mapping function
     * @param <U>
     *        New type
     * @return Instance of new type
     */
    @SuppressWarnings("unchecked")
    public <U> Option<U> flatMap(final FN1<? extends Option<? extends U>, ? super T> mapper) {
        return isEmpty() ? empty() : (Option<U>)mapper.apply(value);
    }

    /**
     * Logical <code>OR</code> between current instance and instance of same type provided by specified supplier.
     * First non-empty instance is returned. Note that if current instance is not empty then supplier is not invoked.
     *
     * @param supplier
     *        Supplier which provides new instance in case if current instance is empty
     * @return first non-empty instance, either current one or one returned by provided supplier
     */
    @SuppressWarnings("unchecked")
    public Option<T> or(final Supplier<? extends Option<? extends T>> supplier) {
        return isPresent() ? this : (Option<T>) supplier.get();
    }

    /**
     * Logical <code>OR</code> between current instance and provided instance of same type.
     * First non-empty instance is returned.
     *
     * @param replacement
     *        Replacement instance which is returned in case if current instance is empty
     * @return first non-empty instance, either current one or one returned by provided supplier
     */
    @SuppressWarnings("unchecked")
    public Option<T> or(final Option<? extends T> replacement) {
        return isPresent() ? this : (Option<T>) replacement;
    }

    /**
     * Return current value stored in current instance if current instance is non-empty. If current
     * instance is empty then return provided replacement value.
     *
     * @param replacement
     *        Replacement value returned in case if current instance is empty
     *
     * @return either value stored in current instance or provided replacement value if current instance is empty
     */
    public T otherwise(final T replacement) {
        return isPresent() ? value : replacement;
    }

    /**
     * Return current value stored in current instance if current instance is non-empty. If current
     * instance is empty then return value returned by provided supplier. If current instance is not empty then
     * supplier is not invoked.
     *
     * @param supplier
     *        Supplier for replacement value returned in case if current instance is empty
     *
     * @return either value stored in current instance or value returned by provided supplier if current instance
     * is empty
     */
    public T otherwiseGet(final Supplier<T> supplier) {
        return isPresent() ? value : supplier.get();
    }

    /**
     * Stream current instance. For empty instance empty stream is created. For non-empty instance the stream
     * with single element is returned. The element is the value stored in current instance.
     *
     * @return created stream
     */
    public Stream<T> stream() {
        return isEmpty() ? Stream.empty() : Stream.of(value);
    }

    /**
     * Returns current value if instance is not empty and throws exception if instance is empty.
     *
     * @deprecated
     * @return value stored in this instance
     * @throws IllegalStateException if instance is empty
     */
    @Deprecated
    public T otherwiseThrow() {
        if (isPresent()) {
            return value;
        }

        throw new IllegalStateException("No value present");
    }

    /**
     * Returns current value if instance is not empty and throws exception if instance is empty.
     *
     * @deprecated
     * @return value stored in this instance if instance is not empty or throws exception returned from provided
     * supplier if instance is empty
     */
    @Deprecated
    public <X extends Throwable> T otherwiseThrow(final Supplier<? extends X> exceptionSupplier) throws X {
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

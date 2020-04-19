package org.reactivetoolbox.core.lang.functional;

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

import java.util.function.Supplier;

/**
 * Various useful transformation functions for {@link Supplier}'s.
 */
//TODO: fix docs
public interface Suppliers {
    /**
     * Transform supplier into lazily instantiating supplier.
     * The given supplier (aka {@code factory}) is transformed into supplier
     * which calls {@code factory} only once, on first invocation and then
     * caches result.
     * This version of the method is safe for concurrent access from different
     * threads.
     *
     * @param factory
     *        Supplier to transform
     * @return lazily instantiating supplier
     */
    static <T> Supplier<T> concurrentMemoize(final Supplier<T> factory) {
        return new Supplier<T>() {
            private final Supplier<T> defaultDelegate = this::init;
            private volatile Supplier<T> delegate = defaultDelegate;

            private synchronized T init() {
                if (delegate != defaultDelegate) {
                    return delegate.get();
                }

                final T value = factory.get();
                delegate = () -> value;
                return delegate.get();
            }

            @Override
            public T get() {
                return delegate.get();
            }
        };
    }

    /**
     * Transform supplier into lazily instantiating supplier.
     * The given supplier (aka {@code factory}) is transformed into supplier
     * which calls {@code factory} only once, on first invocation and then
     * caches result.
     * This version of the method should be used only if access to created
     * supplier is limited to one thread at once.
     *
     * @param factory
     *        Supplier to transform
     * @return lazily instantiating supplier
     */
    static <T> Supplier<T> memoize(final Supplier<T> factory) {
        return new Supplier<T>() {
            private Supplier<T> delegate = () -> {
                final T value = factory.get();
                delegate = () -> value;
                return value;
            };

            @Override
            public T get() {
                return delegate.get();
            }
        };
    }
}

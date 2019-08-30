package org.reactivetoolbox.core.functional;

import java.util.function.Supplier;

/**
 * Various useful transformation functions for {@link Supplier}'s.
 */
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
    static <T> Supplier<T> concurrentLazy(final Supplier<T> factory) {
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
    static <T> Supplier<T> lazy(final Supplier<T> factory) {
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

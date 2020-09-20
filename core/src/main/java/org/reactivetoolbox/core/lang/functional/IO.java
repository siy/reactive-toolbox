package org.reactivetoolbox.core.lang.functional;

import org.reactivetoolbox.core.lang.functional.Functions.FN0;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;

import java.util.function.Consumer;

/**
 * Implementation of container (monad) for side effects (usually I/O operations).
 * <br>
 * The idea behind this monad is to postpone invocation of function which
 * produces side effects until {@link #run()} method is explicitly invoked.
 * <br>
 * Simple example:
 * <pre>
 *   IO.io(() -> "What is your name?")
 *       .apply(System.out::println)
 *       .map(__ -> System.console().readLine())
 *       .map(name -> String.format("Hello %s!", name))
 *       .apply(System.out::println)
 *       .run();
 * </pre>
 */
public final class IO<T> {
    private final FN0<T> effect;

    private IO(final FN0<T> effect) {
        this.effect = effect;
    }

    public static <T> IO<T> io(final FN0<T> effect) {
        return new IO<>(effect);
    }

    public T run() {
        return effect.apply();
    }

    public <R> IO<R> flatMap(final FN1<IO<R>, T> mapper) {
        return IO.io(() -> mapper.apply(effect.apply()).run());
    }

    public <R> IO<R> map(final FN1<R, T> mapper) {
        return io(() -> mapper.apply(effect.apply()));
    }

    public IO<Void> apply(final Consumer<T> consumer) {
        return map(value -> { consumer.accept(value); return null; });
    }
}

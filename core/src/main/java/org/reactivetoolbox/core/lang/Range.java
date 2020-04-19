package org.reactivetoolbox.core.lang;

import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Functions.FN2;

import java.util.Objects;
import java.util.StringJoiner;

public class Range<T extends Comparable> {
    private final T from;
    private final T to;

    private Range(final T from, final T to) {
        this.from = from;
        this.to = to;
    }

    public T from() {
        return from;
    }

    public T to() {
        return to;
    }

    public <R extends Comparable> Range<R> map(final FN1<R, T> mapper) {
        return new Range<>(mapper.apply(from), mapper.apply(to));
    }

    public <R> R map(final FN2<R, T, T> mapper) {
        return mapper.apply(from, to);
    }

    /**
     * Checks if provided value is within range bounds (inclusive).
     *
     * @param value
     *        Value to check
     * @return {@code true} if value is within range bounds.
     */
    @SuppressWarnings("unchecked")
    public boolean inclusiveWithin(final T value) {
        return from.compareTo(value) <= 0 && to.compareTo(value) >= 0;
    }

    /**
     * Checks if provided value is within range bounds (includes lower bound, excludes upper bound).
     *
     * @param value
     *        Value to check
     * @return {@code true} if value is within range bounds.
     */
    @SuppressWarnings("unchecked")
    public boolean openEndedWithin(final T value) {
        return from.compareTo(value) <= 0 && to.compareTo(value) > 0;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable> Range<T> range(final T from, final T to) {
        return (from.compareTo(to) <= 0) ? new Range<>(from, to)
                                         : new Range<>(to, from);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Range)) {
            return false;
        }
        final Range<?> range = (Range<?>) o;
        return from.equals(range.from) &&
               to.equals(range.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Range(", ")")
                       .add(from.toString())
                       .add(to.toString())
                       .toString();
    }
}

package org.reactivetoolbox.core.lang.functional;

public final class Unit {
    private Unit() {}

    private static final Unit UNIT = new Unit();

    public static Unit unit() {
        return UNIT;
    }

    public static <T> Unit unit(final T ignored) {
        return UNIT;
    }

    @Override
    public String toString() {
        return "()";
    }
}

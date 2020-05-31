package org.reactivetoolbox.core.lang.functional;

import java.util.StringJoiner;

public final class Unit {
    private Unit() {}

    public static final Unit UNIT = new Unit();

    @Override
    public String toString() {
        return "()";
    }
}

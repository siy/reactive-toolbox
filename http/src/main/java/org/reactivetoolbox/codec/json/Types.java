package org.reactivetoolbox.codec.json;

import java.util.Set;

public enum Types {
    STRING(Property.QUOTED),
    NUMBER(Property.START_WITH_DIGIT),
    INTEGER(Property.START_WITH_DIGIT),
    BOOLEAN(Property.LITERAL, Set.of("true", "false")),
    NULL(Property.LITERAL, Set.of("null"));

    private final Property property;
    private final Set<String> literals;

    Types(final Property property) {
        this(property, Set.of());
    }

    Types(final Property property, final Set<String> literals) {
        this.property = property;
        this.literals = literals;
    }

    public Property getProperty() {
        return property;
    }

    public Set<String> getLiterals() {
        return literals;
    }

    public enum Property {
        QUOTED,
        LITERAL,
        START_WITH_DIGIT
    }
}

package org.reactivetoolbox.web.server.parameter.conversion;

public class TypeDescription {
    private final String description;

    private TypeDescription(final String description) {
        this.description = description;
    }

    public static TypeDescription of(final String description) {
        return new TypeDescription(description);
    }

    public String description() {
        return description;
    }
}

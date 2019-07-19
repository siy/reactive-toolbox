package org.reactivetoolbox.web.server.parameter;

import org.reactivetoolbox.core.functional.Option;

/**
 * Container for request parameter documentation details
 */
public class ParameterDescription {
    private final String name;
    private final Option<TypeDescription> type;
    private final String description;
    private final boolean mandatory;

    private ParameterDescription(final String name, final Option<TypeDescription> type, final String description, final boolean mandatory) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.mandatory = mandatory;
    }

    public static ParameterDescription of(final String name, final Option<TypeDescription> type, final String description) {
        return new ParameterDescription(name, type, description, false);
    }

    public String name() {
        return name;
    }

    public Option<TypeDescription> type() {
        return type;
    }

    public String description() {
        return description;
    }

    public boolean mandatory() {
        return mandatory;
    }

    public ParameterDescription makeMandatory() {
        return new ParameterDescription(name, type, description, true);
    }
}

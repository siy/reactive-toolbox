package org.reactivetoolbox.value.conversion.var;

import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.value.conversion.TypeDescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Container for request parameter documentation details
 */
public class VarDescription {
    private final String name;
    private final Option<TypeDescription> type;
    private final List<String> validationComments = new ArrayList<>();
    private final String description;
    private final boolean mandatory;

    private VarDescription(final String name, final Option<TypeDescription> type, final String description, final boolean mandatory) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.mandatory = mandatory;
    }

    public static VarDescription of(final String name, final Option<TypeDescription> type, final String description) {
        return new VarDescription(name, type, description, false);
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

    public List<String> validationComments() {
        return Collections.unmodifiableList(validationComments);
    }

    public VarDescription addValidationComment(final String comment) {
        validationComments.add(comment);
        return this;
    }

    public VarDescription name(final String name) {
        return new VarDescription(name, type, description, mandatory);
    }

    public VarDescription type(final Option<TypeDescription> type) {
        return new VarDescription(name, type, description, mandatory);
    }

    public VarDescription mandatory(final boolean mandatory) {
        return new VarDescription(name, type, description, mandatory);
    }

    public VarDescription description(final String description) {
        return new VarDescription(name, type, description, mandatory);
    }
}

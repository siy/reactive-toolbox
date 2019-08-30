package org.reactivetoolbox.value.conversion;

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

import org.reactivetoolbox.core.functional.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for variable (parameter/field) documentation details
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
        return List.copyOf(validationComments);
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

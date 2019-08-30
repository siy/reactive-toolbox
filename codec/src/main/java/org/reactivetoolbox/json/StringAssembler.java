package org.reactivetoolbox.json;

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

import org.reactivetoolbox.core.functional.Functions.FN1;

import java.util.Collection;

//TODO: add escaping, JavaDoc
public class StringAssembler {
    private final StringBuilder builder;
    private final char suffix;

    private StringAssembler(final char prefix, final char suffix) {
        this.suffix = suffix;
        builder = new StringBuilder(256).append(prefix);
    }

    public static StringAssembler forObject() {
        return new StringAssembler('{', '}');
    }

    public static StringAssembler assembleWith(final char prefix, final char suffix) {
        return new StringAssembler(prefix, suffix);
    }

    public static String singleQuoted(final String value) {
        return new StringBuilder().append('"').append(value).append('"').toString();
    }

    public static <T> String toStringQuoted(final T value) {
        return new StringBuilder().append('"').append(value.toString()).append('"').toString();
    }

    public StringAssembler plain(final String value) {
        builder.append(value).append(',');
        return this;
    }

    public StringAssembler quoted(final String value) {
        builder.append('"').append(value).append('"').append(',');
        return this;
    }

    public StringAssembler quoted(final String name, final String value) {
        builder.append('"').append(name).append('"').append(':')
               .append('"').append(value).append('"').append(',');
        return this;
    }

    public <T> StringAssembler quoted(final String name, final Collection<T> values, final FN1<String, T> elementSerializer) {
        builder.append('"').append(name).append('"').append(':').append('[');

        boolean comma = false;
        for(final T value : values) {
            if (!comma) {
                comma = true;
            } else {
                builder.append(',');
            }

            builder.append(elementSerializer.apply(value));
        }
        builder.append(']').append(',');
        return this;
    }

    public StringAssembler quotedPlain(final String name, final String value) {
        builder.append('"').append(name).append('"').append(':')
               .append(value).append(',');
        return this;
    }

    @Override
    public String toString() {
        if (builder.length() > 1) {
            builder.setLength(builder.length() - 1);
        }
        return builder.append(suffix).toString();
    }
}

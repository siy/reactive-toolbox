package org.reactivetoolbox.json;

//TODO: add escaping
public class StringAssembler {
    private final StringBuilder builder;
    private final char suffix;

    private StringAssembler(final char prefix, final char suffix) {
        this.suffix = suffix;
        builder = new StringBuilder(256).append(prefix);
    }

    public static StringAssembler of(final char prefix, final char suffix) {
        return new StringAssembler(prefix, suffix);
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

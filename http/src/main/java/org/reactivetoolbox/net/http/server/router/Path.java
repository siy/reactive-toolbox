package org.reactivetoolbox.net.http.server.router;

import org.reactivetoolbox.core.lang.collection.List;
import org.reactivetoolbox.core.lang.Tuple;
import org.reactivetoolbox.core.lang.Tuple.Tuple3;
import org.reactivetoolbox.net.http.Method;
import org.reactivetoolbox.net.http.server.ParsingContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static org.reactivetoolbox.core.lang.Tuple.tuple;

public abstract class Path implements Prefixed<Path> {
    private final Method method;
    private final String prefix;

    private Path(final Method method, final String prefix) {
        this.method = method;
        this.prefix = prefix;
    }

    public Method method() {
        return method;
    }

    public String prefix() {
        return prefix;
    }

    public Path prefix(final String prefix) {
        return path(method, concat("/", prefix, "/", source()));
    }

    public abstract ParsingContext extract(final String uri);
    public abstract boolean matches(final String uri);
    public abstract boolean exact();

    protected abstract String source();

    public static Path path(final Method method, final String path) {
        final var normalizedPath = normalize(path);

        if (containsParameters(normalizedPath)) {
            final var arguments = parsePath(normalizedPath);

            return arguments.map((prefix, names, pattern) -> new Path(method, prefix) {
                private final List<String> parameterNames = names;
                private final Pattern parameterPattern = pattern;

                @Override
                public ParsingContext extract(final String uri) {
                    final var matcher = parameterPattern.matcher(normalize(uri));

                    if (!matcher.find()) {
                        return ParsingContext.emptyContext();
                    }

                    if (matcher.groupCount() != parameterNames.size()) {
                        return ParsingContext.emptyContext();
                    }

                    final var result = new HashMap<String, List<String>>();

                    // Every second group contains value
                    parameterNames.mapN((i, name) -> result.put(name, List.list(matcher.group(i + 1))));

                    return ParsingContext.context(result);
                }

                @Override
                public boolean matches(final String uri) {
                    return parameterPattern.matcher(uri).matches();
                }

                @Override
                protected String source() {
                    return normalizedPath;
                }

                @Override
                public boolean exact() {
                    return false;
                }
            });
        } else {
            return new Path(method, normalizedPath) {
                @Override
                public ParsingContext extract(final String uri) {
                    return ParsingContext.emptyContext();
                }

                @Override
                public boolean matches(final String uri) {
                    return normalizedPath.equals(uri);
                }

                @Override
                protected String source() {
                    return normalizedPath;
                }

                @Override
                public boolean exact() {
                    return true;
                }
            };
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ",  "Path(", ")")
                .add(method.name())
                .add("\"" + source() + "\"")
                .toString();
    }

    private static final Pattern PARAMETER_PATTERN = compile("(?:/)(\\{\\w+})");
    private static final Pattern MULTISLASH = compile("/+");

    private static boolean containsParameters(final String path) {
        return PARAMETER_PATTERN.matcher(path).find();
    }

    public static String normalize(final String path) {
        if (path == null || path.isBlank() || "/".equals(path)) {
            return "/";
        }

        final var stringPath = MULTISLASH.matcher("/" + path.strip()).replaceAll("/");
        final var index = stringPath.lastIndexOf('/');

        if (index < (stringPath.length() - 1)) {
            return stringPath + "/";
        } else {
            return stringPath;
        }
    }

    private static Tuple3<String, List<String>, Pattern> parsePath(final String path) {
        final var matcher = PARAMETER_PATTERN.matcher(path);
        final var names = new ArrayList<String>();

        matcher.find();

        var prefix = path.substring(0, matcher.start());

        do {
            names.add(path.substring(matcher.start() + 2, matcher.end() - 1));
        }
        while (matcher.find());

        return tuple(prefix, List.from(names), compile(matcher.reset()
                                                              .replaceAll("\\/(\\\\w+?)")));
    }

    public static String concat(final String ... elements) {
        final StringBuilder builder = new StringBuilder(256);
        for(final var element : elements) {
            builder.append(element);
        }

        return normalize(builder.toString());
    }
}

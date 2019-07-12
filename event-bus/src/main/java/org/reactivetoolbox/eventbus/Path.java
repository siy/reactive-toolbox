package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.functional.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * General purpose routing path. Modelled after URL paths and tries to obey similar rules:
 * <ul>
 *     <li>- Path contains zero or more <code>segments</code> and ends with <code>/</code></li>
 *     <li>- Each segment starts with <code>/</code> followed by one or more characters allowed in URL's and consisting segment body</li>
 *     <li>- Special segment body which starts with <code>{</code> followed by one or more alphanumeric characters and ending with
 *     <code>}</code> denotes named parameter. </li>
 * </ul>
 * Note: relative paths are not supported, each path is explicitly converted to absolute form (i.e. starting with <code>/</code>).
 */
public interface Path {
    Pattern PARAMETER_PATTERN = Pattern.compile("(?:\\/)(\\{\\w+\\})");

    boolean isExact();

    default boolean hasParams() {
        return !isExact();
    }

    String prefix();

    List<Pair<String, String>> extractParameters(final String source);

    List<String> parameterNames();

    boolean matches(final String input);

    static Path of(final String stringPath, PathKey key) {
        return of("/" + key.key() + normalize(stringPath));
    }

    static Path of(final String stringPath) {
        final var normalizedPath = normalize(stringPath);

        if (containsParameters(normalizedPath)) {
            return new ParametrizedPath(normalizedPath);
        } else {
            return new ExactPath(normalizedPath);
        }
    }

    private static String normalize(final String path) {
        if (path == null || path.isBlank() || "/".equals(path)) {
            return "/";
        }

        final var stripped = path.strip();
        final var stringPath = stripped.startsWith("/") ? stripped : "/" + stripped;
        final var index = stringPath.lastIndexOf('/');

        if (index < (stringPath.length() - 1)) {
            return stringPath + "/";
        } else {
            return stringPath;
        }
    }

    private static boolean containsParameters(final String stringPath) {
        return PARAMETER_PATTERN.matcher(stringPath).find();
    }

    final class ExactPath implements Path {
        private final String prefix;

        private ExactPath(final String prefix) {
            this.prefix = prefix;
        }

        @Override
        public boolean isExact() {
            return true;
        }

        @Override
        public String prefix() {
            return prefix;
        }

        @Override
        public List<Pair<String, String>> extractParameters(final String source) {
            return Collections.emptyList();
        }

        @Override
        public List<String> parameterNames() {
            return Collections.emptyList();
        }

        @Override
        public boolean matches(final String input) {
            return prefix.equals(input);
        }
    }

    final class ParametrizedPath implements Path {
        private final String prefix;
        private final List<String> parameterNames;
        private final Pattern pathPattern;

        private ParametrizedPath(final String pathPatternWithParameters) {
            final var matcher = PARAMETER_PATTERN.matcher(pathPatternWithParameters);
            final var names = new ArrayList<String>();

            if (!matcher.find()) {
                // Should never happen under normal circumstances, but better be safe than sorry
                throw new IllegalArgumentException("Path contains no parameters inside: " + pathPatternWithParameters);
            }

            prefix = pathPatternWithParameters.substring(0, matcher.start());

            do {
                names.add(pathPatternWithParameters.substring(matcher.start() + 2, matcher.end() - 1));
            }
            while (matcher.find());

            parameterNames = names;

            final String regex = matcher.reset().replaceAll("\\/(\\\\w+?)");
            pathPattern = Pattern.compile(regex);
        }

        @Override
        public boolean isExact() {
            return false;
        }

        @Override
        public String prefix() {
            return prefix;
        }

        @Override
        public List<Pair<String, String>> extractParameters(final String source) {
            final var matcher = pathPattern.matcher(Path.normalize(source));

            if (!matcher.find()) {
                return Collections.emptyList();
            }

            if (matcher.groupCount() != parameterNames.size()) {
                return Collections.emptyList();
            }

            final var result = new ArrayList<Pair<String, String>>();

            // Every second group contains value
            for(int i = 0; i < parameterNames.size(); i++) {
                result.add(Pair.of(parameterNames.get(i), matcher.group(i + 1)));
            }

            return result;
        }

        @Override
        public List<String> parameterNames() {
            return new ArrayList<>(parameterNames);
        }

        @Override
        public boolean matches(final String input) {
            return pathPattern.matcher(input).find();
        }
    }
}

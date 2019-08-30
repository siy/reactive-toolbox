package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.functional.Option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

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
    boolean isExact();

    default boolean hasParams() {
        return !isExact();
    }

    String prefix();

    RawParameters extractParameters(final String source);

    List<String> parameterNames();

    boolean matches(final String input);

    String source();

    PathKey key();

    default Path root(final Option<String> root) {
        return root.map(prefix -> of(prefix + "/" + source(), key())).otherwise(this);
    }

    static Path of(final String stringPath) {
        return of(stringPath, () -> "");
    }

    static Path of(final String stringPath, final PathKey key) {
        final var normalizedPath = normalize(stringPath);

        if (containsParameters(normalizedPath)) {
            return new ParametrizedPath(normalizedPath, key);
        } else {
            return new ExactPath(normalizedPath, key);
        }
    }

    static String normalize(final String path) {
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

    Pattern MULTISLASH = Pattern.compile("/+");
    //Pattern PARAMETER_PATTERN = Pattern.compile("(?:\\/)(\\{\\w+\\})");
    Pattern PARAMETER_PATTERN = Pattern.compile("(?:/)(\\{\\w+})");

    private static boolean containsParameters(final String stringPath) {
        return PARAMETER_PATTERN.matcher(stringPath).find();
    }

    final class ExactPath implements Path {
        private final String prefix;
        private final String source;
        private final PathKey key;

        private ExactPath(final String source, final PathKey key) {
            this.source = source;
            this.key = key;
            prefix = normalize("/" + key.key() + "/" + source);
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
        public RawParameters extractParameters(final String source) {
            return RawParameters.of();
        }

        @Override
        public List<String> parameterNames() {
            return List.of();
        }

        @Override
        public boolean matches(final String input) {
            return prefix.equals(input);
        }

        @Override
        public String source() {
            return source;
        }

        @Override
        public PathKey key() {
            return key;
        }
    }

    final class ParametrizedPath implements Path {
        private final String source;
        private final String prefix;
        private final List<String> parameterNames;
        private final Pattern pathPattern;
        private final PathKey key;

        private ParametrizedPath(final String source, final PathKey key) {
            this.key = key;
            this.source = source;
            final var normalizedSource = normalize("/" + key.key() + "/" + source);
            final var matcher = PARAMETER_PATTERN.matcher(normalizedSource);
            final var names = new ArrayList<String>();

            if (!matcher.find()) {
                // Should never happen under normal circumstances, but better be safe than sorry
                throw new IllegalArgumentException("Path contains no parameters inside: " + normalizedSource);
            }

            prefix = normalizedSource.substring(0, matcher.start());

            do {
                names.add(normalizedSource.substring(matcher.start() + 2, matcher.end() - 1));
            }
            while (matcher.find());

            parameterNames = names;
            pathPattern = Pattern.compile(matcher.reset().replaceAll("\\/(\\\\w+?)"));
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
        public RawParameters extractParameters(final String source) {
            final var matcher = pathPattern.matcher(normalize(source));

            if (!matcher.find()) {
                return RawParameters.of();
            }

            if (matcher.groupCount() != parameterNames.size()) {
                return RawParameters.of();
            }

            final var result = new HashMap<String, List<String>>();

            // Every second group contains value
            for(int i = 0; i < parameterNames.size(); i++) {
                result.put(parameterNames.get(i), List.of(matcher.group(i + 1)));
            }

            return RawParameters.of(result);
        }

        @Override
        public List<String> parameterNames() {
            return List.copyOf(parameterNames);
        }

        @Override
        public boolean matches(final String input) {
            return pathPattern.matcher(input).find();
        }

        @Override
        public String source() {
            return source;
        }

        @Override
        public PathKey key() {
            return key;
        }
    }
}

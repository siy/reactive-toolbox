package org.reactivetoolbox.net.http.server;

import org.reactivetoolbox.core.lang.collection.List;
import org.reactivetoolbox.core.lang.functional.Option;

import java.util.HashMap;
import java.util.Map;

import static org.reactivetoolbox.core.lang.collection.List.list;
import static org.reactivetoolbox.core.lang.functional.Option.empty;
import static org.reactivetoolbox.core.lang.functional.Option.option;

public interface ParsingContext {
    Option<String> content();
    Option<String> first(String name);
    List<String> all(String name);

    //TODO: this one might be necessary for JSON array parsing
    //List<String> elements();

    //TODO: use static instance
    static ParsingContext emptyContext() {
        return new ParsingContext() {
            @Override
            public Option<String> content() {
                return empty();
            }

            @Override
            public Option<String> first(final String name) {
                return empty();
            }

            @Override
            public List<String> all(final String name) {
                return list();
            }
        };
    }

    static ParsingContext context(final Map<String, List<String>> result) {
        return new ParsingContext() {
            @Override
            public Option<String> content() {
                return empty();
            }

            @Override
            public Option<String> first(final String name) {
                return all(name).first();
            }

            @Override
            public List<String> all(final String name) {
                return option(result.get(name)).otherwise(list());
            }
        };
    }
}

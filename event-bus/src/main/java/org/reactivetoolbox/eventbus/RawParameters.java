package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.functional.Option;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RawParameters {
    private final Map<String, List<String>> values;

    public RawParameters(final Map<String, List<String>> values) {
        this.values = values;
    }

    public static RawParameters of(final Map<String, List<String>> parameters) {
        return new RawParameters(parameters);
    }

    public static RawParameters of() {
        return new RawParameters(Collections.emptyMap());
    }

    public Option<String> first(final String name) {
        return Option.of(values.get(name))
                     .filter(list -> !list.isEmpty())
                     .map(list -> list.get(0));
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }
}

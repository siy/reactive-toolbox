package org.reactivetoolbox.net.http.server.router;

import org.reactivetoolbox.core.lang.collection.List;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface Utils {

    static <K, V> java.util.List<V> listCombiner(final K key, final java.util.List<V> oldValue, final V valueToAdd) {
        final var list = (oldValue == null) ? new ArrayList<V>() : oldValue;
        list.add(valueToAdd);
        return list;
    }

    static <K, V> Map<K, List<V>> transform(final Map<K, java.util.List<V>> original) {
        final var transformed = new HashMap<K, List<V>>();

        original.forEach((key, value) -> transformed.put(key, List.from(value)));

        return transformed;
    }

}

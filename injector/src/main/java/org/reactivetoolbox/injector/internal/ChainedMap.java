/*
 * Copyright (c) 2017 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.reactivetoolbox.injector.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Specialized map convenient for tree traversal. It enables convenient lookup for keys through subtree (from current
 * location up to root) while preventing searches between sibling subtrees.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class ChainedMap<K, V> {
    private final Map<K, V> values = new HashMap<>();
    private final ChainedMap<K, V> parent;
    private final List<ChainedMap<K, V>> children = new ArrayList<>();

    /**
     * Create an empty map
     */
    public ChainedMap() {
        this(null);
    }

    /**
     * Create empty map with provided parent map.
     *
     * @param parent
     *          Parent map.
     */
    public ChainedMap(ChainedMap<K, V> parent) {
        this.parent = parent;

        if (parent != null) {
            parent.addChild(this);
        }
    }

    /**
     * Retrieve value from this map instance or from parent, if this map does not contain requested key.
     *
     * @param key
     *          Key to retrieve value for.
     * @return Value associated with key or <code>null</code> if no such key present in the map.
     */
    public V get(K key) {
        V value = values.get(key);
        return value == null ? getFromParent(key) : value;
    }

    /**
     * Put value into map.
     *
     * @param key
     *          Key to associate value with.
     * @param value
     *          Value to associate with key
     * @return Source value for convenient call chaining (fluent syntax)
     */
    public V put(K key, V value) {
        values.put(key, value);
        return value;
    }

    /**
     * Invoke specified {@link BiConsumer} for all map entries and then invoke it for all child maps.
     *
     * @param biConsumer
     *          {@link BiConsumer} instance to invoke.
     */
    public void forEach(BiConsumer<K, V> biConsumer) {
        children.forEach(child -> child.forEach(biConsumer));
        values.forEach(biConsumer);
    }

    private void addChild(ChainedMap<K, V> child) {
        children.add(child);
    }

    private V getFromParent(K key) {
        return parent == null ? null : parent.get(key);
    }
}

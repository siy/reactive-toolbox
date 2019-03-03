package org.reactivetoolbox.map;

import java.util.Map;

/***
 * Helper class simulating a tuple of 2 elements in Scala
 * 
 * @author Roman Levenstein <romixlev@gmail.com>
 *
 * @param <K>
 * @param <V>
 */

//TODO: refactor/rework
public class Pair<K, V> implements Map.Entry<K, V> {

    final K k;
    final V v;

    Pair (K k, V v) {
        this.k = k;
        this.v = v;
    }

    @Override
    public K getKey () {
        return k;
    }

    @Override
    public V getValue () {
        return v;
    }

    @Override
    public V setValue (V value) {
        throw new RuntimeException ("Operation not supported");
    }

}

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

package org.reactivetoolbox.injector.core;

import org.reactivetoolbox.injector.Injector;
import org.reactivetoolbox.injector.Key;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Representation of single entry in dependency tree. Instances of this class are used as containers for collected data
 * during dependency tree traversal.
 */
public class Node {
    private final Key key;
    private final List<Key> dependencies;
    private final Constructor<?> constructor;
    private final Method method;

    private Node(Key key, List<Key> dependencies, Constructor<?> constructor, Method method) {
        this.key = key;
        this.dependencies = dependencies;
        this.constructor = constructor;
        this.method = method;
    }

    /**
     * Create instance of {@link Node} for constructor class dependency.
     *
     * @param key
     *          Key for which {@link Node} should be created.
     * @param dependencies
     *          List of {@link Key}'s on which this node depends.
     * @param constructor
     *          Constructor for class which should be created when key will be requested from
     *          {@link Injector}.
     */
    public Node(Key key, List<Key> dependencies, Constructor<?> constructor) {
        this(key, dependencies, constructor, null);
    }

    /**
     * Create instance of {@link Node} for method dependency.
     *
     * @param key
     *          Key for which {@link Node} should be created.
     * @param dependencies
     *          List of {@link Key}'s on which this node depends.
     * @param method
     *          Method which should be invoked in order to create instances when key will be requested from
     *          {@link Injector}
     */
    public Node(Key key, List<Key> dependencies, Method method) {
        this(key, dependencies, null, method);
    }

    /**
     * Key for whih {@link Node} is created.
     * @return
     */
    public Key key() {
        return key;
    }

    /**
     * {@link Node} instance dependencies.
     * @return List of dependencies (keys).
     */
    public List<Key> dependencies() {
        return dependencies;
    }

    /**
     * {@link Constructor} associated with node.
     *
     * @return Constructor if it was provided or <code>null</code> if no constructor was associated with node.
     */
    public Constructor<?> constructor() {
        return constructor;
    }

    /**
     * {@link Method} associated with node.
     *
     * @return Method if it was provided or <code>null</code> if no method was associated with node.
     */
    public Method method() {
        return method;
    }

    /**
     * @return <code>true</code> if this node was created for method and <code>false</code> otherwise.
     */
    public boolean isMethod() {
        return method != null;
    }
}

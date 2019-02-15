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

package org.reactivetoolbox.injector;

/**
 * This interface describes binding between key and other components which can be used to create instances.
 */
public interface Binding<T> {
    /**
     * Key for which binding is created.
     */
    Key key();

    /**
     * Target of the binding - class or supplier.
     */
    T binding();

    /**
     * Returns <code>true</code> if no further resolution of the dependencies is necessary and <code>false</code>
     * otherwise.
     */
    boolean isResolved();

    /**
     * Returns <code>true</code> if this binding has singleton target.
     */
    boolean isSingleton();

    /**
     * Returns <code>true</code> if this binding has eager singleton target.
     */
    boolean isEager();
}

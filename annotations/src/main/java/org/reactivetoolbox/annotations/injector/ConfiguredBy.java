/*
 * Copyright (c) 2019 Sergiy Yevtushenko
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
 *
 */

package org.reactivetoolbox.annotations.injector;

import java.lang.annotation.*;

/**
 * This annotation provides reference to class which can be used for configuration of annotated class. For example:
 * <pre>{@code
 *
 * @ConfiguredBy(MyModule.class)
 * public MyClass {
 *     ...
 * }
 * }</pre>
 *
 * In the example above class <code>MyModule</code> contains configuration information for the <code>MyClass</code>.
 * Configuration class can provide binding information in two ways:
 * <ol>
 *     <li>Via methods annotated with {@link Supplies} annotation</li>
 *     <li>Via implementing <code>org.reactivetoolbox.injector.Module</code> interface</li>
 * </ol>
 * These methods are not mutually exclusive, so they can be used together where necessary.
 * <br />
 * Instances of classes referenced by the annotation are created through injector, so they are handled just like any
 * other class - their dependencies will be injected. There is only one significant difference:
 * {@link ConfiguredBy} annotation is ignored for configuration class itself. In the example above class
 * <code>MyModule</code> will be loaded without taking into account {@link ConfiguredBy} annotation.
 *
 * @see Supplies
 * @see Module
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface ConfiguredBy {
    /**
     * @return Configuration class.
     */
    Class<?> value();
}

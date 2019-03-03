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

import org.reactivetoolbox.injector.annotations.*;
import org.reactivetoolbox.injector.internal.InjectorImpl;

import java.util.function.Supplier;

//TODO: more details about injector behavior - (constructor selection rules),
/**
 * <h3>Injector API</h3>
 * <p>
 * Injector is used to instantiate classes along with their dependencies (i.e. other classes necessary to instantiate
 * required class). Information about how to instantiate class and its dependencies collected from constructors,
 * special annotations and from dedicated configuration classes. Configuration classes can be provided to injector in
 * declarative way or passed directly to {@link #configure(Class[])} method.
 * <p>
 * Information about connection between dependency and its implementation called <b>binding</b>. Bindings can be one of
 * two categories - implicit and explicit. Implicit bindings are collected by analysing class constructor parameters.
 * Explicit bindings are provided via configuration.
 * <p>
 * In order to create instance of the particular class, appropriate constructor should be used. Selection of the
 * constructor is done automatically or with the help of {@link Inject} annotation.
 * Selection of the constructor is done as follows (highest precedence first):
 * <ol>
 *     <li>If constructor annotated with {@link Inject} is present then it is selected.</li>
 *     <li>If no constructor is annotated then:
 *      <ol>
 *          <li>If there is only one constructor, then this constructor is selected.</li>
 *          <li>If default constructor is present then it is used.</li>
 *      </ol>
 *     </li>
 * </ol>
 * <p>
 * If there are more than one non-default constructors and no default constructor, then {@link Injector} requires that
 * one of the constructors must be annotated with {@link Inject}.
 *
 * @see BindingAnnotation
 * @see ConfiguredBy
 * @see ImplementedBy
 * @see Supplies
 * @see Module
 * @see AbstractModule
 */
public interface Injector {
    /**
     * Get or create instance for specified class. Note that it might be instance of another class depending on the
     * configuration of particular binding. If no satisfying binding is found then Injector attempts to build dependency
     * tree and instantiate class. If dependencies cannot be resolved then
     * {@link InjectorException} is thrown.
     *
     * @param clazz
     *          Class for which instance should be created.
     * @return created instance
     */
    <T> T get(Class<T> clazz);

    /**
     * Get or create instance of class bound to given key. If no satisfying binding is found then Injector attempts to
     * build dependency tree and instantiate class. If dependencies cannot be resolved then
     * {@link InjectorException} is thrown.
     *
     * @param key
     *          Key for which instance should be created.
     * @return created instance
     */
    <T> T get(Key key);

    /**
     * Get or create instance of {@link Supplier} which returns instances for specified class. Note that it might be
     * instance of another class depending on the configuration of particular binding. If no satisfying binding is
     * found then Injector attempts to build dependency tree and instantiate class. If dependencies cannot be resolved
     * then {@link InjectorException} is thrown.

     * @param clazz
     *          Class for which {@link Supplier} should be provided.
     * @return supplier for class instances (or substituting class instances)
     */
    <T> Supplier<T> supplier(Class<T> clazz);

    /**
     * Get or create instance of {@link Supplier} bound to given key. If no satisfying binding is
     * found then Injector attempts to build dependency tree and create supplier. If dependencies cannot be resolved
     * then {@link InjectorException} is thrown.

     * @param key
     *          Key for which supplier should be returned.
     * @return supplier for class instances (or substituting class instances)
     */
    <T> Supplier<T> supplier(Key key);

    /**
     * Configure {@link Injector} with specified classes. Each class can provide two variants of configuration data:
     * class may implement {@link Module} interface (or be a subclass of {@link AbstractModule}) and/or describe
     * bindings as methods annotated with {@link Supplies} annotation. Example of both
     * types of configuration:
     *
     * <pre>{@code
     *  public class MyModule extends AbstractModule {
     *
     *      public void configure() {
     *          bind(MyInterface.class).toSingleton(MyImplementation.class);
     *          bind(MyOtherInterface.class).toEagerSingleton(MyOtherImplementation.class);
     *      }
     *
     *      @Supplies
     *      @MyAnnotation
     *      public List<String> names() {
     *          return Arrays.asList("John", "Pete");
     *      }
     *  }
     * }</pre>
     *
     * Note that if method has annotation which in turn is annotated with
     * {@link BindingAnnotation} then this annotation becomes part of the binding
     * and only requests for annotated instances will match this binding.
     *
     * @see BindingAnnotation
     * @param configurators
     *          Classes with configuration information.
     * @return <code>this</code> for call chaining (fluent syntax).
     */
    Injector configure(Class<?>... configurators);

    /**
     * Static factory for {@link Injector} instances.
     *
     * @return created injector.
     */
    static Injector create() {
        return new InjectorImpl();
    }

    /**
     * Static factory for {@link Injector} instances. Injector is returned pre-configured with specified configuration
     * classes.
     *
     * @return created and configured injector.
     */
    static Injector create(Class<?>...classes) {
        return new InjectorImpl().configure(classes);
    }
}

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

import org.reactivetoolbox.annotations.injector.BindingAnnotation;
import org.reactivetoolbox.annotations.injector.ConfiguredBy;
import org.reactivetoolbox.annotations.injector.Named;
import org.reactivetoolbox.injector.core.Bindings;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Convenient boilerplate for application modules. Using this class as a base class for module implementation
 * enables convenient injector configuration using fluent syntax. For example:
 * <pre>{@code
 *  public class MyModule extends AbstractModule {
 *
 *      public void configure() {
 *          bind(new TypeLiteral<List<String>>() {}).annotatedWith(MyAnnotation.class).to(NamesList.class);
 *          bind(MyInterface.class).toSingleton(MyImplementation.class);
 *          bind(MyOtherInterface.class).toEagerSingleton(MyOtherImplementation.class);
 *      }
 *  }
 * }</pre>
 *
 * Note that {@link ConfiguredBy} annotations present in classes bound inside
 * {@link #configure()} method are ignored and relevant modules are not loaded.
 *
 * @see ConfiguredBy
 */
public abstract class AbstractModule implements Module {
    private final List<Binding<?>> bindings = new ArrayList<>();

    /**
     * Method invoked by {@link Injector} in order to collect bindings.
     * @return
     */
    @Override
    public final List<Binding<?>> collectBindings() {
        configure();
        return bindings;
    }

    /**
     * Method which should be overridden by implementations.
     */
    protected abstract void configure();

    /**
     * Begin binding creation sequence for provided class.
     *
     * @param clazz
     *          Class to which binding will be performed.
     * @return {@link Builder} which will finish binding creation.
     */
    protected <T> Builder<T> bind(Class<T> clazz) {
        return new Builder<>(Key.of(clazz));
    }

    /**
     * Begin binding creation sequence for provided {@link TypeToken}.
     *
     * @param token
     *          Token which represents a type to which binding will be performed.
     * @return {@link Builder} which will finish binding creation.
     */
    protected <T> Builder<T> bind(TypeToken<T> token) {
        return new Builder<>(Key.of(token));
    }

    public class Builder<T> {
        private Key key;

        private Builder(Key key) {
            this.key = key;
        }

        /**
         * Terminate binding creation sequence by associating it with specified class.
         *
         * @param implementation
         *          Class to which binding should be created.
         */
        public void to(Class<? extends T> implementation) {
            bindings.add(Bindings.of(key, implementation, false, false));
        }

        /**
         * Terminate binding creation sequence by associating it with specified instance.
         *
         * @param instance
         *          Instance to which binding should be created.
         */
        public void toInstance(T instance) {
            bindings.add(Bindings.of(key, instance));
        }

        /**
         * Terminate binding creation sequence by associating it with specified {@link Supplier}.
         *
         * @param supplier
         *          Supplier to which binding should be created.
         */
        public void toSupplier(Supplier<? extends T> supplier) {
            bindings.add(Bindings.of(key, supplier));
        }

        /**
         * Terminate binding creation sequence by associating it with specified class. The class will be handled as
         * lazy singleton.
         *
         * @param implementation
         *          Class to which binding should be created.
         */
        public void toSingleton(Class<? extends T> implementation) {
            toLazySingleton(implementation);
        }

        /**
         * Terminate binding creation sequence by associating it with specified class. The class will be handled as
         * lazy singleton.
         *
         * @param implementation
         *          Class to which binding should be created.
         */
        public void toLazySingleton(Class<? extends T> implementation) {
            bindings.add(Bindings.of(key, implementation, true, false));
        }

        /**
         * Terminate binding creation sequence by associating it with specified class. The class will be handled as
         * eager singleton.
         *
         * @param implementation
         *          Class to which binding should be created.
         */
        public void toEagerSingleton(Class<? extends T> implementation) {
            bindings.add(Bindings.of(key, implementation, true, true));
        }

        /**
         * Add requirement for specified annotation for binding. Example:
         * <pre>{@code
         *  bind(MyInterface.class).annotatedWith(SomeAnnotation.class).to(SpecificImplementation.class);
         * }</pre>
         *
         * For the example above created binding will match parameters which request <code>MyInterface</code> annotated
         * with <code>SomeAnnotation</code> like this:
         *
         * <pre>{@code
         * public class MyOtherClass {
         *     ...
         *     public MyOtherClass(@SomeAnnotation MyInterface parameter) {
         *         ...
         *     }
         *     ...
         * }
         * }</pre>
         *
         * Note that this method is an intermediate step in binding creation sequence. In order to complete binding
         * one of terminating methods should be invoked.
         * <br />
         * Note that provided annotation class should be annotated with
         * {@link BindingAnnotation} in order to make this work.
         *
         * @param annotation
         *          Annotation class which should be present in order to match binding.
         * @return <code>this</code> for call chaining (fluent syntax).
         */
        public Builder<T> annotatedWith(Class<? extends Annotation> annotation) {
            key = key.with(annotation);
            return this;
        }

        /**
         * Specialised version of the {@link #annotatedWith(Class)} method which adds
         * {@link Named} annotation with specified value to built binding.
         * <br />
         * Note that this method is an intermediate step in binding creation sequence. In order to complete binding
         * one of terminating methods should be invoked.
         *
         * @param name
         *          Value for the {@link Named} annotation.
         * @return <code>this</code> for call chaining (fluent syntax).
         */
        public Builder<T> named(String name) {
            key = key.with(Naming.with(name));
            return this;
        }
    }
}

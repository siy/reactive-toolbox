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

package org.reactivetoolbox.injector;

import org.reactivetoolbox.injector.annotations.ImplementedBy;
import org.reactivetoolbox.injector.annotations.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Various utility methods used by injector.
 */
public final class Utils {
    private Utils() {}

    /**
     * Locate constructor for provided key. If class for which key is created has {@link ImplementedBy} annotation
     * then class referenced by annotation is searched for constructor. In either case constructor
     * selection rules are following: <br/>
     * <ul>
     *     <li>If class has constructor annotated with {@link Inject}, then this constructor is returned</li>
     *     <li>If class has single constructor, then this constructor is returned</li>
     *     <li>If class has default constructor, then default constructor is returned</li>
     * </ul>
     *
     * @param key
     *          Key to locate constructor for.
     * @return Found constructor. If no constructor is found then {@link InjectorException} is thrown.
     */
    public static Constructor<?> locateConstructor(Key key) {
        return locateConstructor(key.rawClass());
    }

    /**
     * Locate constructor for provided class. If class has {@link ImplementedBy} annotation then class referenced by
     * annotation is searched for constructor. In either case constructor selection rules are following: <br/>
     * <ul>
     *     <li>If class has constructor annotated with {@link Inject}, then this constructor is returned</li>
     *     <li>If class has single constructor, then this constructor is returned</li>
     *     <li>If class has default constructor, then default constructor is returned</li>
     * </ul>
     *
     * @param inputClass
     *          Key to locate constructor for.
     * @return Found constructor. If no constructor is found then {@link InjectorException} is thrown.
     */
    public static Constructor<?> locateConstructor(Class<?> inputClass) {
        ImplementedBy implementationClass = inputClass.getAnnotation(ImplementedBy.class);
        Class<?> clazz = implementationClass != null ? implementationClass.value() : inputClass;

        Constructor<?> instanceConstructor = null;
        Constructor<?> defaultConstructor = null;
        Constructor<?> singleConstructor = null;
        boolean singleConstructorIsPending = true;

        Constructor<?>[] constructors = clazz.getConstructors();

        if (constructors.length == 1) {
            return constructors[0];
        }

        for (Constructor<?> constructor : constructors) {
            if (!constructor.isAnnotationPresent(Inject.class)) {
                if (constructor.getParameterCount() == 0) {
                    defaultConstructor = constructor;
                    singleConstructorIsPending = false;
                    singleConstructor = null;
                } else {
                    if (singleConstructorIsPending) {
                        singleConstructor = constructor;
                        singleConstructorIsPending = false;
                    } else {
                        singleConstructor = null;
                    }
                }

                continue;
            }

            if (instanceConstructor != null) {
                throw new InjectorException("Class "
                     + clazz.getCanonicalName()
                     + " has more than one createConstructorSupplier annotated with @Inject");
            }

            instanceConstructor = constructor;
        }

        Constructor<?> constructor = instanceConstructor == null ? (defaultConstructor == null ? singleConstructor
                                                                                               : defaultConstructor)
                                                                 : instanceConstructor;

        if (constructor == null) {
            throw new InjectorException("Unable to locate suitable constructor for " + clazz);
        }

        return constructor;
    }

    public interface ThrowingSupplier<T> {
        T get() throws Throwable;
    }

    /**
     * Convenient wrapper which intercepts any exceptions coming from source supplier during invocation and wraps them
     * into {@link InjectorException}. Useful for stripping checked exceptions while invoking methods of external
     * classes.
     *
     * @param factory
     *          Source supplier, potentially throwing some exceptions.
     * @param messageSupplier
     *          Supplier for the message generated for exception. Invoked only in case of exception, so message can
     *          be complex and time consuming to build. Lazy evaluation enables providing as much information in
     *          exception as necessary to simplify nailing down the issue.
     *
     * @return Value returned by source supplier.
     */
    public static <T> T safeCall(ThrowingSupplier<T> factory, Supplier<String> messageSupplier) {
        try {
            return factory.get();
        } catch (Throwable  e) {
            throw new InjectorException("Error while invoking " + messageSupplier.get(), e);
        }
    }

    /**
     * Convenient method for invocation of methods of {@link Executable} instances ({@link Constructor} and
     * {@link java.lang.reflect.Method}).
     *
     * @param factory
     *      Source supplier which performs actual method invocation.
     * @param executable
     *      Instance of {@link Executable} which is used to build exception message if source supplier invocation
     *      throws an exception.
     *
     * @return Value returned by source supplier.
     */
    public static <T> T safeCall(ThrowingSupplier<T> factory, Executable executable) {
        return safeCall(factory, () -> Objects.toString(executable));
    }

    /**
     * Validate presence and number of parameters for given {@link Executable} instance ({@link Constructor} and
     * {@link java.lang.reflect.Method}). If any issues found then {@link InjectorException} is thrown.
     *
     * @param executable
     *          {@link Constructor} or {@link java.lang.reflect.Method} instance to check.
     * @param parameters
     *          Array of parameters which are going to be used to invoke the input executable.
     * @param extraParameters
     *          Number of extra parameters to take into account - 0 for constructors and 1 for instance methods.
     */
    public static void validateParameters(Executable executable, Supplier<?>[] parameters, int extraParameters) {
        validateNotNull(executable, parameters);

        if (parameters.length < executable.getParameterCount() + extraParameters) {
            throw new InjectorException("Passed number of parameters is incorrect for " + executable);
        }
    }

    /**
     * Validate presence and number of parameters for given {@link Executable} instance ({@link Constructor} and
     * {@link java.lang.reflect.Method}).
     *
     * @param executable
     *          {@link Constructor} or {@link java.lang.reflect.Method} instance to check.
     * @param parameters
     *          List of parameters which are going to be used to invoke the input executable.
     * @param extraParameters
     *          Number of extra parameters to take into account - 0 for constructors and 1 for instance methods.
     */
    public static void validateParameters(Executable executable, List<Supplier<?>> parameters, int extraParameters) {
        validateNotNull(executable, parameters);

        if (parameters.size() < executable.getParameterCount() + extraParameters) {
            throw new InjectorException("Passed number of parameters is incorrect for " + executable);
        }
    }

    /**
     * Validate that given parameter is not null. If provided parameter is <code>null</code> then
     * {@link InjectorException} is thrown.
     *
     * @param value
     *          Value to check.
     */
    public static<T> void validateNotNull(T value) {
        if (value == null) {
            throw new InjectorException("Null value passed as a parameter");
        }
    }

    /**
     * Validate that given parameters are not null. If any of provided parameters is <code>null</code> then
     * {@link InjectorException} is thrown.
     *
     * @param value1
     *          First value to check.
     * @param value2
     *          Second value to check.
     */
    public static<T1, T2> void validateNotNull(T1 value1, T2 value2) {
        if (value1 == null || value2 == null) {
            throw new InjectorException("Null value passed as a parameter");
        }
    }
}

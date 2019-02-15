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

package org.reactivetoolbox.injector.core.supplier;

import org.reactivetoolbox.annotations.injector.ComputationStyle;
import org.reactivetoolbox.annotations.injector.PostConstruct;
import org.reactivetoolbox.annotations.injector.Singleton;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class provides set of convenience methods which are used by injector. While they can be used as general purpose
 * utility methods, care should be taken in regard parameters.
 */
public final class SupplierFactory {
    private SupplierFactory() {}

    /**
     * Create instance supplier using constructor and list of parameters. Each parameter is represented as an instance
     * of {@link Supplier<T>} which provides instance of parameter at the invocation time.
     * This function builds a supplier taking into account all relevant annotations, i.e. it creates singleton supplier
     * for classes annotated with {@link Singleton} and, depending on the annotation value it creates eager or lazy
     * singleton. Also, if class has a method annotated with {@link PostConstruct} annotation, created supplier will
     * be wrapped into another supplier which will invoke method annotated with {@link PostConstruct} right after
     * instance creation.
     *
     * @param constructor
     *          Constructor of required class
     * @param parameters
     *          List of suppliers for constructor parameters. Values returned by suppliers must match constructor
     *          parameters' types and order. Note that there is check for parameter count, but no check for type.
     *          Incorrect parameter type will be reported only at run time, when created supplier will be invoked.
     * @return Supplier instance of same type as provided constructor.
     */
    public static <T> Supplier<T> createInstanceSupplier(Constructor<T> constructor, List<Supplier<?>> parameters) {
        Utils.validateParameters(constructor, parameters, 0);

        Supplier<T> factory = tryWrapWithPostConstruct(constructor,
                                                       Suppliers.enhancing(createConstructorSupplier(constructor, parameters),
                                                                           () -> LambdaFactory.create(constructor, parameters.toArray(new Supplier[parameters.size()]))));

        return tryBuildSingleton(factory, constructor.getDeclaringClass());
    }

    /**
     * Create instance supplier using constructor and list of parameters. Each parameter is represented as an instance
     * of {@link Supplier<T>} which provides instance of parameter at the invocation time.
     * This function builds a singleton supplier (eager or lazy, depending on parameters) and ignores
     * {@link Singleton} annotation even if it's present in class for which constructor is provided. If class has a
     * method annotated with {@link PostConstruct} annotation, created supplier will be wrapped into another supplier
     * which will invoke method annotated with {@link PostConstruct} right after instance creation.
     *
     * @param constructor
     *          Constructor of required class
     * @param parameters
     *          List of suppliers for constructor parameters. Values returned by suppliers must match constructor
     *          parameters' types and order. Note that there is check for parameter count, but no check for type.
     *          Incorrect parameter type will be reported only at run time, when created supplier will be invoked.
     * @return Supplier instance of same type as provided constructor.
     */
    public static <T> Supplier<T> createSingletonSupplier(Constructor<T> constructor, List<Supplier<?>> parameters, boolean eager) {
        Utils.validateParameters(constructor, parameters, 0);

        Supplier<T> factory = tryWrapWithPostConstruct(constructor,
                                                       createConstructorSupplier(constructor, parameters));

        return Suppliers.singleton(factory, eager);
    }

    /**
     * Create supplier from method of some class using method and list of parameters. Each parameter is represented as
     * an instance of {@link Supplier<T>} which provides instance of parameter at the invocation time. The first
     * parameter supplier must provide instance of the class from which {@link Method} is taken. Note that there are
     * checks for number of parameters, but no check for parameter types, even for first parameter. Any discrepancies
     * in types (except regular Java type conversion like boxing/unboxing) will result to run time exception.
     * <br />
     * If method is annotated with {@link Singleton} annotation, then singleton supplier will be created. Note
     * that in this case singleton computation style is ignored and lazy singleton is always created.
     *
     * @param method
     *          The method which will be used to create a supplier. Supplier will return value of exactly the same
     *          type as method return type.
     * @param parameters
     *          List of parameters provided as suppliers. The first parameter is an supplier for the instance of class
     *          for which method will be invoked. Remaining suppliers should provide instances of parameters required
     *          for method invocation. Total count of suppliers must be number of method parameters + 1.
     * @return Supplier instance of same type as return type of provided method.
     */
    @SuppressWarnings("unchecked")
    public static <T> Supplier<T> createMethodSupplier(Method method, List<Supplier<?>> parameters) {
        Utils.validateParameters(method, parameters, 1);

        Supplier<?>[] suppliers = parameters.toArray(new Supplier[parameters.size()]);
        Supplier<T> supplier = () -> Utils.safeCall(mapMethodParameters(method, suppliers), method);
        Singleton singleton = method.getAnnotation(Singleton.class);

        return singleton == null ? supplier : Suppliers.lazy(supplier);
    }

    private static <T> Supplier<T> tryWrapWithPostConstruct(Constructor<T> constructor, Supplier<T> factory) {
        MethodHandle methodHandle = LambdaFactory.locateFirstAnnotated(constructor.getDeclaringClass(), PostConstruct.class);

        if (methodHandle == null) {
            return factory;
        }

        return () -> {
            T instance = factory.get();

            Utils.safeCall(() -> methodHandle.invoke(instance), () -> "@PostConstruct method for " + constructor.getDeclaringClass());
            return instance;
        };
    }

    private static <T> Supplier<T> tryBuildSingleton(Supplier<T> instanceSupplier, Class<?> clazz) {
        Singleton singleton = clazz.getAnnotation(Singleton.class);

        if (singleton == null) {
            return instanceSupplier;
        }

        return Suppliers.singleton(instanceSupplier, singleton.value() == ComputationStyle.EAGER);
    }

    @SuppressWarnings("unchecked")
    private static <T> Utils.ThrowingSupplier<T> mapMethodParameters(Method method, Supplier<?>[] suppliers) {
        switch (method.getParameterCount()) {
            case  0: return () -> (T) method.invoke(suppliers[0].get());
            case  1: return () -> (T) method.invoke(suppliers[0].get(), suppliers[1].get());
            case  2: return () -> (T) method.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get());
            case  3: return () -> (T) method.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(), suppliers[3].get());
            case  4: return () -> (T) method.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(), suppliers[3].get(), suppliers[4].get());
            case  5: return () -> (T) method.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(), suppliers[3].get(), suppliers[4].get(), suppliers[5].get());
            case  6: return () -> (T) method.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(), suppliers[3].get(), suppliers[4].get(), suppliers[5].get(), suppliers[6].get());
            case  7: return () -> (T) method.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(), suppliers[3].get(), suppliers[4].get(), suppliers[5].get(), suppliers[6].get(), suppliers[7].get());
            case  8: return () -> (T) method.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(), suppliers[3].get(), suppliers[4].get(), suppliers[5].get(), suppliers[6].get(), suppliers[7].get(), suppliers[8].get());
            case  9: return () -> (T) method.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(), suppliers[3].get(), suppliers[4].get(), suppliers[5].get(), suppliers[6].get(), suppliers[7].get(), suppliers[8].get(), suppliers[9].get());
            case 10: return () -> (T) method.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(), suppliers[3].get(), suppliers[4].get(), suppliers[5].get(), suppliers[6].get(), suppliers[7].get(), suppliers[8].get(), suppliers[9].get(), suppliers[10].get());
            default:
                //Should not happen, limits are already checked
                return null;
        }
    }

    private static <T> Supplier<T> createConstructorSupplier(Constructor<T> constructor, List<Supplier<?>> parameters) {
        Utils.validateParameters(constructor, parameters, 0);
        Supplier<?>[] suppliers = parameters.toArray(new Supplier[parameters.size()]);

        return () -> Utils.safeCall(mapConstructorParameters(constructor, suppliers), constructor);
    }

    private static <T> Utils.ThrowingSupplier<T> mapConstructorParameters(Constructor<T> constructor, Supplier<?>[] suppliers) {
        switch (constructor.getParameterCount()) {
            case  0: return constructor::newInstance;
            case  1: return () -> constructor.newInstance(suppliers[0].get());
            case  2: return () -> constructor.newInstance(suppliers[0].get(), suppliers[1].get());
            case  3: return () -> constructor.newInstance(suppliers[0].get(), suppliers[1].get(), suppliers[2].get());
            case  4: return () -> constructor.newInstance(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(), suppliers[3].get());
            case  5: return () -> constructor.newInstance(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(), suppliers[3].get(), suppliers[4].get());
            case  6: return () -> constructor.newInstance(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(), suppliers[3].get(), suppliers[4].get(), suppliers[5].get());
            case  7: return () -> constructor.newInstance(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(), suppliers[3].get(), suppliers[4].get(), suppliers[5].get(), suppliers[6].get());
            case  8: return () -> constructor.newInstance(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(), suppliers[3].get(), suppliers[4].get(), suppliers[5].get(), suppliers[6].get(), suppliers[7].get());
            case  9: return () -> constructor.newInstance(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(), suppliers[3].get(), suppliers[4].get(), suppliers[5].get(), suppliers[6].get(), suppliers[7].get(), suppliers[8].get());
            case 10: return () -> constructor.newInstance(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(), suppliers[3].get(), suppliers[4].get(), suppliers[5].get(), suppliers[6].get(), suppliers[7].get(), suppliers[8].get(), suppliers[9].get());
            default:
                //Should not happen, limits are already checked
                return null;
        }
    }
}

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

import org.reactivetoolbox.injector.core.exception.InjectorException;

import java.lang.annotation.Annotation;
import java.lang.invoke.*;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is a convenient run-time generator of lambdas which can be used to createInstanceSupplier instances of objects or call
 * methods of classes. By convention all parameters required to call methods and constructors are passed as instances
 * of {@link Supplier} class. For constructors array of input suppliers corresponds to createConstructorSupplier parameters.
 * For methods array of suppliers consists of provider of instance for which method should be invoked and remaining
 * suppliers provide rest of method parameters.
 */
public final class LambdaFactory {
    private LambdaFactory() {}

    public static final Function<Executable, Parameter[]> getParametersFunction;

    private static final Class<?> INTERFACES[] = {
            Invocable0.class, Invocable1.class, Invocable2.class, Invocable3.class, Invocable4.class,
            Invocable5.class, Invocable6.class, Invocable7.class, Invocable8.class, Invocable9.class,
            Invocable10.class
    };

    private static final int MAX_PARAMETER_COUNT = INTERFACES.length - 1;
    private static final Lookup LOOKUP;
    private static final Module module = LambdaFactory.class.getModule();

    static {
        try {
            //TODO: rework?
//            Constructor<Lookup> constructor = Lookup.class.getDeclaredConstructor(Class.class, int.class);
//            constructor.setAccessible(true);
//
            //LOOKUP = constructor.newInstance(LambdaFactory.class, Lookup.PRIVATE);
            LOOKUP = MethodHandles.lookup();
            getParametersFunction = createParametersFunction();
        } catch (Exception e) {
            throw new InjectorException("Unable to createInstanceSupplier new MethodHandles.Lookup instance", e);
        }
    }

    /**
     * Locate method annotated with specific annotation in given class and convert it into {@link MethodHandle}.
     *
     * @param declaringClass
     *          Class to look into.
     * @param annotation
     *          Required annotation.
     * @return  Method handle for found method or null if no method was found.
     */
    public static MethodHandle locateFirstAnnotated(Class<?> declaringClass, Class<? extends Annotation> annotation) {
        for (Method method: declaringClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                return createHandle(method);
            }
        }
        return null;
    }

    /**
     * Create method handle for given method.
     *
     * @param method
     *          Method to convert into handle.
     * @return  Method handle
     */
    public static MethodHandle createHandle(Method method) {
        try {
            method.setAccessible(true);
            return LOOKUP.unreflect(method);
        } catch (Exception e) {
            throw new InjectorException("Unable to createInstanceSupplier method handle for " + method, e);
        }
    }

    /**
     * Create lambda for the provided method. In the array of parameter suppliers first parameter must be a
     * supplier of instances of class to which passed method belongs.
     *
     * @param method
     *          Method to convert into lambda.
     * @param suppliers
     *          Array of suppliers where first element provides instances of method class while remaining
     *          provide method parameters.
     *
     * @return  Created supplier.
     */
    public static <T> Supplier<T> create(Method method, Supplier<?>[] suppliers) {
        Utils.validateParameters(method, suppliers, 1);
        return Utils.safeCall(() -> internalCreate(method, suppliers, true), method);
    }

    /**
     * Create lambda for the provided createConstructorSupplier.
     *
     * @param constructor
     *          Constructor to convert to lambda
     * @param suppliers
     *          Array of suppliers of createConstructorSupplier parameters.
     *
     * @return  Created supplier.
     */
    public static <T> Supplier<T> create(Constructor<T> constructor, Supplier<?>[] suppliers) {
        Utils.validateParameters(constructor, suppliers, 0);
        return Utils.safeCall(() -> internalCreate(constructor, suppliers, false), constructor);
    }

    private static <T> Supplier<T> internalCreate(Executable constructor, Supplier<?>[] suppliers, boolean isMethod) throws Throwable {
        int parameterCount = constructor.getParameterCount() + (isMethod ? 1:0);
        return createLambdaSupplier(suppliers, createCallSite(constructor, parameterCount, isMethod), parameterCount);
    }

    private static Function<Executable,Parameter[]> createParametersFunction() {
        return  (Executable e) -> e.getParameters();
    }

    private static CallSite createCallSite(Executable executable, int parameterCount, boolean isMethod)
            throws ReflectiveOperationException, LambdaConversionException {

        if (parameterCount > MAX_PARAMETER_COUNT) {
            throw new InjectorException("More than " + (MAX_PARAMETER_COUNT + (isMethod ? 1 : 0))
                                        + " parameters are not supported in " + executable);
        }

        MethodHandle target = isMethod ? LOOKUP.unreflect((Method) executable)
                                       : LOOKUP.unreflectConstructor((Constructor<?>) executable);

        return LambdaMetafactory.metafactory(LOOKUP,
                                             "invoke",
                                             MethodType.methodType(INTERFACES[parameterCount]),
                                             target.type().generic(),
                                             target,
                                             target.type().wrap());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static<T> Supplier<T> createLambdaSupplier(Supplier<?>[] suppliers, CallSite callSite, int parameterCount) throws Throwable {
        switch (parameterCount) {
            case 0: {
                Invocable0<T> invocable = (Invocable0<T>) callSite.getTarget().invokeExact();
                return () -> (T) invocable.invoke();
            }
            case 1: {
                Invocable1<T, Object> invocable = (Invocable1<T, Object>) callSite.getTarget().invoke();
                return () -> (T) invocable.invoke(suppliers[0].get());
            }
            case 2: {
                Invocable2 invocable = (Invocable2) callSite.getTarget().invoke();
                return () -> (T) invocable.invoke(suppliers[0].get(), suppliers[1].get());
            }
            case 3: {
                Invocable3 invocable = (Invocable3) callSite.getTarget().invoke();
                return () -> (T) invocable.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get());
            }
            case 4: {
                Invocable4 invocable = (Invocable4) callSite.getTarget().invoke();
                return () -> (T) invocable.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(),
                                                  suppliers[3].get());
            }
            case 5: {
                Invocable5 invocable = (Invocable5) callSite.getTarget().invoke();
                return () -> (T) invocable.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(),
                                                  suppliers[3].get(), suppliers[4].get());
            }
            case 6: {
                Invocable6 invocable = (Invocable6) callSite.getTarget().invoke();
                return () -> (T) invocable.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(),
                                                  suppliers[3].get(), suppliers[4].get(), suppliers[5].get());
            }
            case 7: {
                Invocable7 invocable = (Invocable7) callSite.getTarget().invoke();
                return () -> (T) invocable.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(),
                                                  suppliers[3].get(), suppliers[4].get(), suppliers[5].get(),
                                                  suppliers[6].get());
            }
            case 8: {
                Invocable8 invocable = (Invocable8) callSite.getTarget().invoke();
                return () -> (T) invocable.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(),
                                                  suppliers[3].get(), suppliers[4].get(), suppliers[5].get(),
                                                  suppliers[6].get(), suppliers[7].get());
            }
            case 9: {
                Invocable9 invocable = (Invocable9) callSite.getTarget().invoke();
                return () -> (T) invocable.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(),
                                                  suppliers[3].get(), suppliers[4].get(), suppliers[5].get(),
                                                  suppliers[6].get(), suppliers[7].get(), suppliers[8].get());
            }
            case 10: {
                Invocable10 invocable = (Invocable10) callSite.getTarget().invoke();
                return () -> (T) invocable.invoke(suppliers[0].get(), suppliers[1].get(), suppliers[2].get(),
                                                  suppliers[3].get(), suppliers[4].get(), suppliers[5].get(),
                                                  suppliers[6].get(), suppliers[7].get(), suppliers[8].get(),
                                                  suppliers[9].get());
            }
            default:
                //Should not happen, limits are already checked
                return null;
        }
    }

    // Although these interfaces are declared public, they are internal for the LambdaFactory and not intended
    // for outside use.

    public interface Invocable0<T> {
        T invoke();
    }

    public interface Invocable1<T, P0> {
        T invoke(P0 p0);
    }

    public interface Invocable2<T, P0, P1> {
        T invoke(P0 p0, P1 p1);
    }

    public interface Invocable3<T, P0, P1, P2> {
        T invoke(P0 p0, P1 p1, P2 p2);
    }

    public interface Invocable4<T, P0, P1, P2, P3> {
        T invoke(P0 p0, P1 p1, P2 p2, P3 p3);
    }

    public interface Invocable5<T, P0, P1, P2, P3, P4> {
        T invoke(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4);
    }

    public interface Invocable6<T, P0, P1, P2, P3, P4, P5> {
        T invoke(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);
    }

    public interface Invocable7<T, P0, P1, P2, P3, P4, P5, P6> {
        T invoke(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);
    }

    public interface Invocable8<T, P0, P1, P2, P3, P4, P5, P6, P7> {
        T invoke(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);
    }

    public interface Invocable9<T, P0, P1, P2, P3, P4, P5, P6, P7, P8> {
        T invoke(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);
    }

    public interface Invocable10<T, P0, P1, P2, P3, P4, P5, P6, P7, P8, P9> {
        T invoke(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);
    }
}

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

/*
 * License: Apache License, Version 2.0
 * See the LICENSE file in the root directory or at <a href="http://www.apache.org/licenses/LICENSE-2">apache.org</a>.
 */
package org.reactivetoolbox.injector.internal.annotation;

import org.reactivetoolbox.injector.InjectorException;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

/**
 * An implementation of {@link Annotation} that mimics the behavior of normal annotations.
 * It is an {@link InvocationHandler}, meant to be used via {@link AnnotationFactory#create(Class, Map)}.
 * <p>
 * The constructorSupplier checks that the all the elements required by the annotation interface are provided
 * and that the types are compatible. If extra elements are provided, they are ignored.
 * If a value is of an incompatible type is provided or no value is provided for an element
 * without a default value, {@link InjectorException} is thrown.
 * </p>
 * <p>
 * Note: {@link #equals(Object)} and {@link #hashCode()} and implemented as specified
 * by {@link Annotation}, so instances are safeCreate to mix with normal annotations.
 *
 * Modified version of AnnotationInvocationHandler from https://github.com/leangen/geantyref
 *
 * @see Annotation
 */
class AnnotationInvocationHandler implements Annotation, InvocationHandler, Serializable {

    private static final long serialVersionUID = 8615044376674805680L;
    /**
     * Maps primitive {@code Class}es to their corresponding wrapper {@code Class}.
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS = new HashMap<>();

    static {
        PRIMITIVE_WRAPPERS.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_WRAPPERS.put(Byte.TYPE, Byte.class);
        PRIMITIVE_WRAPPERS.put(Character.TYPE, Character.class);
        PRIMITIVE_WRAPPERS.put(Short.TYPE, Short.class);
        PRIMITIVE_WRAPPERS.put(Integer.TYPE, Integer.class);
        PRIMITIVE_WRAPPERS.put(Long.TYPE, Long.class);
        PRIMITIVE_WRAPPERS.put(Double.TYPE, Double.class);
        PRIMITIVE_WRAPPERS.put(Float.TYPE, Float.class);
    }

    private final Class<? extends Annotation> annotationType;
    private final Map<String, Object> values;
    private final int hashCode;
    private volatile String stringValue;

    AnnotationInvocationHandler(Class<? extends Annotation> annotationType, Map<String, Object> values)
            throws  InjectorException {
        this.annotationType = validateType(annotationType);
        this.values = normalize(annotationType, values);
        this.hashCode = calculateHashCode();
    }

    private static Class<? extends Annotation> validateType(Class<? extends Annotation> annotationType) {
        Class<?>[] interfaces = annotationType.getInterfaces();

        if (!annotationType.isAnnotation() || interfaces.length != 1 || interfaces[0] != Annotation.class) {
            throw new InjectorException(annotationType.getName() + " is not an annotation type");
        }
        return annotationType;
    }

    private static Map<String, Object> normalize(Class<? extends Annotation> annotationType,
                                                 Map<String, Object> values)
            throws InjectorException {

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> source = new HashMap<>(values);

        for (Method method : annotationType.getDeclaredMethods()) {
            Object value = calculateValue(source, method);
            validateReturnType(method, value);
            result.put(method.getName(), value);
            source.remove(method.getName());
        }

        if (!source.isEmpty()) {
            throw new InjectorException("Incorrectly named values are provided : " + source);
        }

        return result;
    }

    private static Object calculateValue(Map<String, Object> values, Method method) {
        String name = method.getName();
        Object value = values.getOrDefault(name, method.getDefaultValue());

        if (value == null) {
            throw new InjectorException("Missing value for " + method);
        }

        return value;
    }

    private static void validateReturnType(Method method, Object value) {
        Class<?> returnType = method.getReturnType();

        if (returnType.isPrimitive()) {
            returnType = PRIMITIVE_WRAPPERS.get(returnType);
        }

        if (!returnType.isInstance(value)) {
            throw new InjectorException("Incompatible type " + value.getClass() + " provided for '" + method.getName() + "()' of " + method.getDeclaringClass());
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (values.containsKey(method.getName())) {
            return values.get(method.getName());
        }
        return method.invoke(this, args);
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return annotationType;
    }

    /**
     * Performs an equality check as described in {@link Annotation#equals(Object)}.
     *
     * @param other The object to compare
     * @return Whether the given object is equal to this annotation or not
     * @see Annotation#equals(Object)
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!annotationType.isInstance(other)) {
            return false;
        }

        Annotation that = annotationType.cast(other);

        //compare annotation member values
        for (Entry<String, Object> entry : values.entrySet()) {
            Object value = entry.getValue();
            Object otherValue;
            try {
                otherValue = that.annotationType().getMethod(entry.getKey()).invoke(that);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }

            if (!Objects.deepEquals(value, otherValue)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Calculates the hash code of this annotation as described in {@link Annotation#hashCode()}.
     *
     * @return The hash code of this annotation.
     * @see Annotation#hashCode()
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        if (stringValue == null) {
            synchronized (this) {
                if (stringValue == null) {
                    stringValue = calculateToStringValue();
                }
            }
        }
        return stringValue;
    }

    private String calculateToStringValue() {
        StringBuilder result = new StringBuilder().append('@').append(annotationType.getSimpleName()).append('(');

        for (String name : new TreeSet<>(values.keySet())) {
            result.append(name).append('=').append(values.get(name).toString()).append(", ");
        }

        if (!values.isEmpty()) {
            result.delete(result.length() - 2, result.length());
        }

        return result.append(")").toString();
    }

    private int calculateHashCode() {
        int hashCode = 0;

        for (Entry<String, Object> element : values.entrySet()) {
            hashCode += (127 * element.getKey().hashCode()) ^ calculateHashCode(element.getValue());
        }

        return hashCode;
    }

    private int calculateHashCode(Object element) {
        if (!element.getClass().isArray()) {
            return element.hashCode();
        }
        if (element instanceof Object[]) {
            return Arrays.hashCode((Object[]) element);
        }
        if (element instanceof byte[]) {
            return Arrays.hashCode((byte[]) element);
        }
        if (element instanceof short[]) {
            return Arrays.hashCode((short[]) element);
        }
        if (element instanceof int[]) {
            return Arrays.hashCode((int[]) element);
        }
        if (element instanceof long[]) {
            return Arrays.hashCode((long[]) element);
        }
        if (element instanceof char[]) {
            return Arrays.hashCode((char[]) element);
        }
        if (element instanceof float[]) {
            return Arrays.hashCode((float[]) element);
        }
        if (element instanceof double[]) {
            return Arrays.hashCode((double[]) element);
        }
        if (element instanceof boolean[]) {
            return Arrays.hashCode((boolean[]) element);
        }

        return Objects.hashCode(element);
    }
}

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

package org.reactivetoolbox.introspector.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static org.reactivetoolbox.injector.Suppliers.*;

public class BeanDetails {
    private final Class<?> clazz;
    private final Supplier<List<Method>> methods;
    private final Supplier<List<Field>> fields;
    private final Supplier<List<Annotation>> annotations;
    private final ConcurrentHashMap<Method, List<Parameter>> methodParameters = new ConcurrentHashMap<>();

    public BeanDetails(final Class<?> clazz) {
        this.clazz = clazz;
        this.methods = lazy(() -> collectMethods());
        this.fields =  lazy(() -> collectFields());
        this.annotations =  lazy(() -> collectAnnotations());
    }

    private List<Annotation> collectAnnotations() {
        return List.of(clazz.getAnnotations());
    }

    private List<Field> collectFields() {
        List<Field> result = new ArrayList<>();
        Class<?> current = clazz;

        while (!current.isAssignableFrom(Object.class)) {
            result.addAll(List.of(current.getDeclaredFields()));
            current = current.getSuperclass();
        }

        return result;
    }

    private List<Method> collectMethods() {
        List<Method> result = new ArrayList<>();
        Class<?> current = clazz;

        while (!current.isAssignableFrom(Object.class)) {
            result.addAll(List.of(current.getDeclaredMethods()));
            current = current.getSuperclass();
        }

        return result;
    }

    public static BeanDetails of(Class<?> clazz) {
        return new BeanDetails(clazz);
    }

    public List<Method> methods() {
        return methods.get();
    }

    public List<Field> fields() {
        return fields.get();
    }

    public List<Annotation> annotations() {
        return annotations.get();
    }

    public List<Parameter> parameters(final Method method) {
        return methodParameters.computeIfAbsent(method, m -> List.of(m.getParameters()));
    }
}

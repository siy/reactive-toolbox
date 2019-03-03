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

import org.reactivetoolbox.introspector.Introspector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class IntrospectorImpl implements Introspector {
    private final ConcurrentHashMap<Class<?>, BeanDetails> beans = new ConcurrentHashMap<>();

    @Override
    public List<Method> annotatedPublicMethods(final Class<?> clazz, final Class<? extends Annotation> annotation) {
        return getBeanDetails(clazz).methods()
                                    .stream()
                                    .filter(method -> Modifier.isPublic(method.getModifiers()))
                                    .filter(method -> method.getAnnotation(annotation) != null)
                                    .filter(method -> !method.isBridge())
                                    .filter(method -> !method.isSynthetic())
                                    .filter(method -> !method.isVarArgs())
                                    .collect(Collectors.toList());
    }

    private BeanDetails getBeanDetails(final Class<?> clazz) {
        return beans.computeIfAbsent(clazz, BeanDetails::of);
    }

    @Override
    public List<Class<?>> annotatedClasses(final Class<? extends Annotation> annotation) {
        return beans.keySet()
                    .stream()
                    .filter(c -> c.isAnnotationPresent(annotation))
                    .filter(c -> !c.isPrimitive())
                    .collect(Collectors.toList());
    }

    @Override
    public List<Parameter> parameters(final Method method) {
        return getBeanDetails(method.getDeclaringClass()).parameters(method);
    }

    @Override
    public Introspector introspect(final List<Class> clases) {
        clases.forEach(this::getBeanDetails);
        return this;
    }
}

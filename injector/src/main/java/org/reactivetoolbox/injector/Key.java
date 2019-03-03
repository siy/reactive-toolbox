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

import org.reactivetoolbox.injector.annotations.BindingAnnotation;
import org.reactivetoolbox.injector.internal.annotation.AnnotationFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Injection point key class.
 * <br />
 * This class represents details of the single injection dependency - type and optional annotation. Note that annotation
 * held by key must be itself annotated with {@link BindingAnnotation} annotation.
 *
 * @see BindingAnnotation
 */
public class Key {
    private final Annotation annotation;
    private final Type type;
    private final Class<?> clazz;
    private final boolean supplier;

    private Key(Type type, boolean supplier, Annotation... annotations) {
        this(type, supplier, lookupClass(type), findBindingAnnotation(annotations));
    }

    private Key(Type type, boolean supplier) {
        this(type, supplier, lookupClass(type), null);
    }

    private Key(Type type, boolean supplier, Class<?> clazz, Annotation annotation) {
        this.type = type;
        this.clazz = clazz;
        this.annotation = annotation;
        this.supplier = supplier;
    }

    /**
     * Create key for single method/constructor parameter.
     *
     * @param parameter
     *          Parameter to build key for.
     * @return Created key.
     */
    public static Key of(Parameter parameter) {
        Annotation[] annotations = parameter.getAnnotations();

        if (!parameter.getType().isAssignableFrom(Supplier.class)) {
            return new Key(parameter.getParameterizedType(), false,
                           parameter.getType(), findBindingAnnotation(annotations));
        }

        Type type = parameter.getParameterizedType();

        if (type instanceof ParameterizedType) {
            Type[] args = ((ParameterizedType) type).getActualTypeArguments();

            if (args.length > 0 && args[0] instanceof Class) {
                return Key.of(args[0], true, annotations);
            }
        }

        throw new InjectorException("Unable to determine parameter type for " + parameter);
    }

    /**
     * Create key for specified {@link Type}.
     *
     * @param type
     *          Type to create key for.
     * @return Created key
     */
    public static Key of(Type type) {
        return new Key(type, false);
    }

    /**
     * Create key for specified {@link Type} and annotations. Note that among provided at most one annotation will be
     * used to describe key. All provided annotations are checked and first one which is annotated with
     * {@link BindingAnnotation} will be selected. If no such annotations exists among provided, then resulting key
     * will have no annotations at all.
     *
     * @param type
     *          Type to create key for.
     * @param annotations
     *          Annotations to choose from.
     * @return Created key
     */
    public static Key of(Type type, Annotation... annotations) {
        return new Key(type, false, annotations);
    }

    /**
     * Create key for specified {@link Type} and annotations. Note that among provided at most one annotation will be
     * used to describe key. All provided annotations are checked and first one which is annotated with
     * {@link BindingAnnotation} will be selected. If no such annotations exists among provided, then resulting key
     * will have no annotations at all.
     *
     * @param type
     *          Type to create key for.
     * @param isSupplier
     *          Mark key as related to injection of {@link Supplier} instead of regular injection.
     * @param annotations
     *          Annotations to choose from.
     * @return Created key
     */
    public static Key of(Type type, boolean isSupplier, Annotation... annotations) {
        return new Key(type, isSupplier, annotations);
    }

    /**
     * Create key for specified {@link TypeToken}. Since {@link TypeToken} allows to capture full type information,
     * resulting key will also describe precise injection point type.
     *
     * @param token
     *          Type token to create key for.
     * @return Created key
     */
    public static <T> Key of(TypeToken<T> token) {
        return new Key(token.type(), false);
    }

    /**
     * Create key for specified {@link TypeToken}. Since {@link TypeToken} allows to capture full type information,
     * resulting key will also describe precise injection point type. Note that among provided at most one annotation
     * will be used to describe key. All provided annotations are checked and first one which is annotated with
     * {@link BindingAnnotation} will be selected. If no such annotations exists among provided, then resulting key
     * will have no annotations at all.
     *
     * @param token
     *          Type token to create key for.
     * @param annotations
     *          Annotations to choose from.
     * @return Created key
     */
    public static <T> Key of(TypeToken<T> token, Annotation... annotations) {
        return new Key(token.type(), false, annotations);
    }

    @Override
    public int hashCode() {
        return type.hashCode() ^ (annotation == null ? 0x55555555: annotation.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Key)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        Key key = (Key) obj;
        return  type.equals(key.type) && Objects.equals(annotation, key.annotation);
    }

    /**
     * Get retrieved information about raw class behind key type information. For example, key for
     * <code>List&lt;String&gt></code> has raw class <code>List</code>.
     *
     * @return Raw class of the key type.
     */
    public Class<?> rawClass() {
        return clazz;
    }

    /**
     * Get key type.
     *
     * @return Key type.
     */
    public Type type() {
        return type;
    }

    /**
     * Get annotation associated with key.
     *
     * @return Annotation for key or <code>null</code> if no such annotation present.
     */
    public Annotation annotation() {
        return annotation;
    }

    /**
     * @return <code>true</code> if key is created for {@link Supplier} injection point and <code>false</code> otherwise.
     */
    public boolean isSupplier() {
        return supplier;
    }

    /**
     * Create new key from existing with added or replaced annotation.
     *
     * @param annotation
     *          Type of the new annotation for key.
     * @return Created key.
     */
    public Key with(Class<? extends Annotation> annotation) {
        if (!annotation.isAnnotationPresent(BindingAnnotation.class)) {
            throw new InjectorException("Annotation "
                                        + annotation.getSimpleName()
                                        + " must be annotated with @"
                                        + BindingAnnotation.class.getSimpleName());
        }

        return new Key(type, supplier, clazz, AnnotationFactory.create(annotation));
    }

    /**
     * Create new key from existing with added or replaced annotation.
     *
     * @param annotation
     *          New annotation for key.
     * @return Created key.
     */
    public Key with(Annotation annotation) {
        if (!annotation.annotationType().isAnnotationPresent(BindingAnnotation.class)) {
            throw new InjectorException("Annotation "
                                        + annotation.annotationType().getSimpleName()
                                        + " must be annotated with @"
                                        + BindingAnnotation.class.getSimpleName());
        }

        return new Key(type, supplier, clazz, annotation);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");

        builder.append(type.getTypeName());

        if (annotation != null) {
            builder.append(" @").append(annotation.annotationType().getTypeName());
        }

        return builder.append('}').toString();
    }

    private static Annotation findBindingAnnotation(Annotation[] annotations) {
        for(Annotation annotation: annotations) {
            if(annotation.annotationType().isAnnotationPresent(BindingAnnotation.class)) {
                return annotation;
            }
        }
        return null;
    }

    private static Class<?> lookupClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }

        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();

            if (rawType instanceof Class)
                return (Class<?>) rawType;
        }

        throw new InjectorException("Unable to determine base class for "
                                    + ((type == null) ? "null" : ("type " + type)));
    }
}

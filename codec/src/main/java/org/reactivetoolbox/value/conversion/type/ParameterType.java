package org.reactivetoolbox.value.conversion.type;

/*
 * Copyright (c) 2017-2019 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.core.functional.Pair;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Parameter type representation.
 */
public class ParameterType {
    private final Type type;
    private final Class<?> rawClass;
    private final Class<?> elementClass;

    private ParameterType(final Type type, final Class<?> rawClass, final Class<?> elementClass) {
        this.type = type;
        this.rawClass = rawClass;
        this.elementClass = elementClass;
    }

    /**
     * Represent parameter type for specified {@link Type}.
     *
     * @param type
     *        Type to represent.
     * @return Created parameter type
     */
    public static Either<? extends BaseError, ParameterType> represent(final Type type) {
        return lookupClass(type)
                .mapSuccess(types -> new ParameterType(type, types.left(), types.right()));
    }

    /**
     * Represent parameter type for specified {@link TypeToken}.
     *
     * @param token
     *        Type token to represent.
     * @return Created parameter type
     */
    public static <T> Either<? extends BaseError, ParameterType> represent(final TypeToken<T> token) {
        return token.type()
                    .flatMap(ParameterType::represent);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ParameterType)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        return  type.equals(((ParameterType) obj).type);
    }

    /**
     * Get retrieved information about raw class behind parameter type information. For example,
     * <code>List&lt;String&gt></code> parameter has raw class type <code>List</code>.
     *
     * @return Raw class of the parameter type.
     */
    public Class<?> base() {
        return rawClass;
    }

    /**
     * Get retrieved information about inner element type of parameter type information. For example,
     * <code>List&lt;String&gt></code> parameter has element class type <code>String</code>. If parameter has no
     * element type, returned value will be empty
     *
     * @return Raw class of the parameter element type.
     */
    public Option<Class<?>> element() {
        return Option.of(elementClass);
    }

    /**
     * Get parameter type.
     *
     * @return Parameter type.
     */
    public Type type() {
        return type;
    }

    @Override
    public String toString() {
        return new StringBuilder("{").append(type.getTypeName())
                                     .append('}')
                                     .toString();
    }

    //WARNING!!! Current implementation does not look deeper than class and it's first type argument, also
    //it fails to handle properly GenericArrayType types at any position of type specification
    private static Either<? extends BaseError, Pair<Class<?>, Class<?>>> lookupClass(final Type type) {
        final Class<?> baseType = lookupClassRecursively(type);
        final Class<?> elementType = (type instanceof ParameterizedType)
                                     ? lookupClassRecursively(((ParameterizedType) type).getActualTypeArguments()[0])
                                     : null;

        if (baseType != null) {
            return Either.success(Pair.of(baseType, elementType));
        }

        return TypeError.UNABLE_TO_RECOGNIZE_TYPE.asFailure();
    }

    private static Class<?> lookupClassRecursively(final Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }

        if (type instanceof ParameterizedType) {
            final Type rawType = ((ParameterizedType) type).getRawType();

            if (rawType instanceof Class) {
                return (Class<?>) rawType;
            }
        }

        return null;
    }
}

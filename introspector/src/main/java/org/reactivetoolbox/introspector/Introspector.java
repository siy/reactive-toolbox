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

package org.reactivetoolbox.introspector;

import org.reactivetoolbox.injector.annotations.ImplementedBy;
import org.reactivetoolbox.introspector.internal.IntrospectorImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

@ImplementedBy(IntrospectorImpl.class)
public interface Introspector {

    List<Method> annotatedPublicMethods(Class<?> clazz, Class<? extends Annotation> annotation);

    List<Class<?>> annotatedClasses(Class<? extends Annotation> annotation);
    List<Parameter> parameters(Method method);

    Introspector introspect(List<Class> clases);

    public static Introspector instance() {
        return new IntrospectorImpl();
    }
}

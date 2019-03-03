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

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.injector.annotations.Singleton;
import org.reactivetoolbox.introspector.beans.ClassWithAnnotatedMethods;
import org.reactivetoolbox.introspector.beans.ClassWithInheritedAnnotatedMethods;
import org.reactivetoolbox.introspector.beans.ClassWithOverriddenAnnotatedMethodsWithoutAnnotation;
import org.reactivetoolbox.introspector.beans.ClassWithoutAnnotatedMethods;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class IntrospectorTest {
    @Test
    void shouldFindAnnotatedPublicMethods() {
        final List<Method> result = Introspector.instance()
                                                .annotatedPublicMethods(ClassWithAnnotatedMethods.class,
                                                                        Singleton.class);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("method2");
    }

    @Test
    void shouldFindInheritedAnnotatedPublicMethods() {
        final List<Method> result = Introspector.instance()
                                                .annotatedPublicMethods(ClassWithInheritedAnnotatedMethods.class,
                                                                        Singleton.class);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("method6");
        assertThat(result.get(1).getName()).isEqualTo("method2");
    }

    @Test
    void shouldFindAnnotatedInheritedPublicMethods() {
        final List<Method> result = Introspector.instance()
                                                .annotatedPublicMethods(
                                                        ClassWithOverriddenAnnotatedMethodsWithoutAnnotation.class,
                                                        Singleton.class);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("method2");
    }

    @Test
    void shouldNotFindAnnotatedPublicMethods() {
        final List<Method> result = Introspector.instance()
                                                .annotatedPublicMethods(
                                                        ClassWithoutAnnotatedMethods.class,
                                                        Singleton.class);

        assertThat(result).isEmpty();
    }
}
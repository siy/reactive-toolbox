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

package org.reactivetoolbox.injector.core;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.annotations.injector.BindingAnnotation;
import org.reactivetoolbox.injector.Key;
import org.reactivetoolbox.injector.TypeToken;
import org.reactivetoolbox.injector.core.annotation.AnnotationFactory;
import org.reactivetoolbox.injector.core.beans.ClassWithDefaultConstructor;
import org.reactivetoolbox.injector.core.exception.InjectorException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class KeyTest {
    @Test
    public void shouldCreateKeyForSimpleClass() throws Exception {
        Key key = Key.of(ClassWithDefaultConstructor.class);

        assertThat(key).isNotNull();
        assertThat(key.rawClass()).isEqualTo(ClassWithDefaultConstructor.class);
        assertThat(key.annotation()).isNull();
        assertThat(key.type().getTypeName()).isEqualTo("org.reactivetoolbox.injector.core.beans.ClassWithDefaultConstructor");
        assertThat(key.toString()).isEqualTo("{org.reactivetoolbox.injector.core.beans.ClassWithDefaultConstructor}");
    }

    @Test
    public void shouldCreateKeyForSimpleClassWithAnnotation() throws Exception {
        Key key = Key.of(ClassWithDefaultConstructor.class, AnnotationFactory.create(TestAnnotation.class));

        assertThat(key).isNotNull();
        assertThat(key.rawClass()).isEqualTo(ClassWithDefaultConstructor.class);
        assertThat(key.annotation()).isInstanceOf(TestAnnotation.class);
        assertThat(key.toString()).isEqualTo("{org.reactivetoolbox.injector.core.beans.ClassWithDefaultConstructor @org.reactivetoolbox.injector.core.KeyTest$TestAnnotation}");
    }

    @Test
    public void shouldIgnoreNonBindingAnnotation() throws Exception {
        Key key = Key.of(ClassWithDefaultConstructor.class, AnnotationFactory.create(TestNonBindingAnnotation.class), AnnotationFactory.create(TestAnnotation.class));

        assertThat(key).isNotNull();
        assertThat(key.rawClass()).isEqualTo(ClassWithDefaultConstructor.class);
        assertThat(key.annotation()).isInstanceOf(TestAnnotation.class);
        assertThat(key.toString()).isEqualTo("{org.reactivetoolbox.injector.core.beans.ClassWithDefaultConstructor @org.reactivetoolbox.injector.core.KeyTest$TestAnnotation}");
    }

    @Test
    public void keyFollowsEqualsContract() throws Exception {
        Key key1 = Key.of(new TypeToken<List<Supplier<String>>>() {});
        Key key2 = Key.of(new TypeToken<List<Supplier<String>>>() {});

        assertThat(key1).isEqualTo(key2);
        assertThat(key2).isEqualTo(key1);

        assertThat(key1).isEqualTo(key1);
        assertThat(key2).isEqualTo(key2);

        assertThat(key1.equals("")).isFalse();
        assertThat(key2.equals("")).isFalse();
    }

    @Test
    public void shouldCreateKeyForTypeToken() throws Exception {
        Key key = Key.of(new TypeToken<List<Supplier<String>>>() {});

        assertThat(key).isNotNull();
        assertThat(key.rawClass()).isEqualTo(List.class);
        assertThat(key.annotation()).isNull();
        assertThat(key.type().toString()).isEqualTo("java.util.List<java.util.function.Supplier<java.lang.String>>");
        assertThat(key.toString()).isEqualTo("{java.util.List<java.util.function.Supplier<java.lang.String>>}");
    }

    @Test
    public void shouldCreateKeyForTypeTokenWithAnnotation() throws Exception {
        Key key = Key.of(new TypeToken<List<Supplier<String>>>() {}).with(TestAnnotation.class);

        assertThat(key).isNotNull();
        assertThat(key.rawClass()).isEqualTo(List.class);
        assertThat(key.annotation()).isInstanceOf(TestAnnotation.class);
        assertThat(key.type().toString()).isEqualTo("java.util.List<java.util.function.Supplier<java.lang.String>>");
        assertThat(key.toString()).isEqualTo("{java.util.List<java.util.function.Supplier<java.lang.String>> @org.reactivetoolbox.injector.core.KeyTest$TestAnnotation}");
    }

    @Test
    public void shouldDistinguishAnnotatedAndNotAnnotatedKeys() throws Exception {
        Key key1 = Key.of(new TypeToken<List<Supplier<String>>>() {});
        Key key2 = key1.with(TestAnnotation.class);

        assertThat(key1).isNotNull();
        assertThat(key2).isNotNull();
        assertThat(key1.rawClass()).isEqualTo(key2.rawClass());
        assertThat(key1).isNotEqualTo(key2);
    }

    @Test
    public void shouldCreateSameKeyForTypeTokenRegardlessFromTheWay() throws Exception {
        Key key1 = Key.of(new TypeToken<List<Supplier<String>>>() {}).with(TestAnnotation.class);
        Key key2 = Key.of(new TypeToken<List<Supplier<String>>>() {}, AnnotationFactory.create(TestAnnotation.class));

        assertThat(key1).isNotNull();
        assertThat(key2).isNotNull();
        assertThat(key1).isEqualTo(key2);
    }

    @Test
    public void shouldCreateKeyForSimpleParameter() throws Exception {
        Parameter parameter = getClass().getDeclaredMethod("method1", List.class).getParameters()[0];
        Key key = Key.of(parameter);

        assertThat(key).isNotNull();
        assertThat(key.toString()).isEqualTo("{java.util.List<java.lang.String>}");
    }

    @Test
    public void shouldCreateKeyForArrayType() throws Exception {
        Key key = Key.of(Supplier[].class);

        assertThat(key).isNotNull();
        assertThat(key.toString()).isEqualTo("{java.util.function.Supplier[]}");
    }

    @Test
    public void shouldCreateKeyForGenericArrayType() throws Exception {
        Key key = Key.of(new TypeToken<List<Supplier[]>>() {});

        assertThat(key).isNotNull();
        assertThat(key.toString()).isEqualTo("{java.util.List<java.util.function.Supplier[]>}");
    }

    @Test
    public void shouldCreateKeyForWildcardType() throws Exception {
        Key key = Key.of(new TypeToken<Supplier<?>>() {});

        assertThat(key).isNotNull();
        assertThat(key.toString()).isEqualTo("{java.util.function.Supplier<?>}");
    }

    public String method1(List<String> p0) {
        return (p0 == null || p0.isEmpty()) ? "empty" : p0.get(0);
    }

    @Test
    public void shouldThrowExceptionIfNonBindingAnnotationIsPassedToWith() throws Exception {
        assertThrows(InjectorException.class, () -> Key.of(String.class).with(TestNonBindingAnnotation.class));
    }

    @Test
    public void shouldThrowExceptionForNullType() throws Exception {
        assertThrows(InjectorException.class, () -> Key.of((Type) null));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @BindingAnnotation
    @Target({ElementType.TYPE, ElementType.PARAMETER})
    public @interface TestAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.PARAMETER})
    public @interface TestNonBindingAnnotation {
    }
}
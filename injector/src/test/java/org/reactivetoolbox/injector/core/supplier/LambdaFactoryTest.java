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

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.injector.core.beans.*;
import org.reactivetoolbox.injector.core.exception.InjectorException;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LambdaFactoryTest {
    private final Supplier<?>[] suppliers = new Supplier<?>[] {
            () -> 1234L,
            () -> "098",
            () -> 10321,
            () -> 2345L,
            () -> "987",
            () -> 10432,
            () -> 3456L,
            () -> "876",
            () -> 10543,
            () -> 3456L,
            () -> "765"
            };

    @Test
    public void shouldCreateBeanWith0Parameters() throws Exception {
        Supplier<ClassWithDefaultConstructor> result = LambdaFactory.create(constructor(ClassWithDefaultConstructor.class), suppliers);

        assertThat(result).isNotNull();
        assertThat(result.get()).isInstanceOf(ClassWithDefaultConstructor.class);
    }

    @Test
    public void shouldCreateBeanWith1Parameters() throws Exception {
        Supplier<ClassWith1ParameterConstructor> result = LambdaFactory.create(constructor(ClassWith1ParameterConstructor.class), suppliers);

        assertThat(result).isNotNull();
        assertThat(result.get()).isInstanceOf(ClassWith1ParameterConstructor.class);
        assertThat(result.get().p0()).isEqualTo(1234L);
    }

    @Test
    public void shouldCreateBeanWith2Parameters() throws Exception {
        Supplier<ClassWith2ParametersConstructor> result = LambdaFactory.create(constructor(ClassWith2ParametersConstructor.class), suppliers);

        assertThat(result).isNotNull();
        assertThat(result.get()).isInstanceOf(ClassWith2ParametersConstructor.class);
        assertThat(result.get().p0()).isEqualTo(1234L);
        assertThat(result.get().p1()).isEqualTo("098");
    }

    @Test
    public void shouldCreateBeanWith3Parameters() throws Exception {
        Supplier<ClassWith3ParametersConstructor> result = LambdaFactory.create(constructor(ClassWith3ParametersConstructor.class), suppliers);

        assertThat(result).isNotNull();
        assertThat(result.get()).isInstanceOf(ClassWith3ParametersConstructor.class);
        assertThat(result.get().p0()).isEqualTo(1234L);
        assertThat(result.get().p1()).isEqualTo("098");
        assertThat(result.get().p2()).isEqualTo(10321);
    }

    @Test
    public void shouldCreateBeanWith4Parameters() throws Exception {
        Supplier<ClassWith4ParametersConstructor> result = LambdaFactory.create(constructor(ClassWith4ParametersConstructor.class), suppliers);

        assertThat(result).isNotNull();
        assertThat(result.get()).isInstanceOf(ClassWith4ParametersConstructor.class);
        assertThat(result.get().p0()).isEqualTo(1234L);
        assertThat(result.get().p1()).isEqualTo("098");
        assertThat(result.get().p2()).isEqualTo(10321);
        assertThat(result.get().p3()).isEqualTo(2345L);
    }

    @Test
    public void shouldCreateBeanWith5Parameters() throws Exception {
        Supplier<ClassWith5ParametersConstructor> result = LambdaFactory.create(constructor(ClassWith5ParametersConstructor.class), suppliers);

        assertThat(result).isNotNull();
        assertThat(result.get()).isInstanceOf(ClassWith5ParametersConstructor.class);
        assertThat(result.get().p0()).isEqualTo(1234L);
        assertThat(result.get().p1()).isEqualTo("098");
        assertThat(result.get().p2()).isEqualTo(10321);
        assertThat(result.get().p3()).isEqualTo(2345L);
        assertThat(result.get().p4()).isEqualTo("987");
    }

    @Test
    public void shouldCreateBeanWith6Parameters() throws Exception {
        Supplier<ClassWith6ParametersConstructor> result = LambdaFactory.create(constructor(ClassWith6ParametersConstructor.class), suppliers);

        assertThat(result).isNotNull();
        assertThat(result.get()).isInstanceOf(ClassWith6ParametersConstructor.class);
        assertThat(result.get().p0()).isEqualTo(1234L);
        assertThat(result.get().p1()).isEqualTo("098");
        assertThat(result.get().p2()).isEqualTo(10321);
        assertThat(result.get().p3()).isEqualTo(2345L);
        assertThat(result.get().p4()).isEqualTo("987");
        assertThat(result.get().p5()).isEqualTo(10432);
    }

    @Test
    public void shouldCreateBeanWith7Parameters() throws Exception {
        Supplier<ClassWith7ParametersConstructor> result = LambdaFactory.create(constructor(ClassWith7ParametersConstructor.class), suppliers);

        assertThat(result).isNotNull();
        assertThat(result.get()).isInstanceOf(ClassWith7ParametersConstructor.class);
        assertThat(result.get().p0()).isEqualTo(1234L);
        assertThat(result.get().p1()).isEqualTo("098");
        assertThat(result.get().p2()).isEqualTo(10321);
        assertThat(result.get().p3()).isEqualTo(2345L);
        assertThat(result.get().p4()).isEqualTo("987");
        assertThat(result.get().p5()).isEqualTo(10432);
        assertThat(result.get().p6()).isEqualTo(3456L);
    }

    @Test
    public void shouldCreateBeanWith8Parameters() throws Exception {
        Supplier<ClassWith8ParametersConstructor> result = LambdaFactory.create(constructor(ClassWith8ParametersConstructor.class), suppliers);

        assertThat(result).isNotNull();
        assertThat(result.get()).isInstanceOf(ClassWith8ParametersConstructor.class);
        assertThat(result.get().p0()).isEqualTo(1234L);
        assertThat(result.get().p1()).isEqualTo("098");
        assertThat(result.get().p2()).isEqualTo(10321);
        assertThat(result.get().p3()).isEqualTo(2345L);
        assertThat(result.get().p4()).isEqualTo("987");
        assertThat(result.get().p5()).isEqualTo(10432);
        assertThat(result.get().p6()).isEqualTo(3456L);
        assertThat(result.get().p7()).isEqualTo("876");
    }

    @Test
    public void shouldCreateBeanWith9Parameters() throws Exception {
        Supplier<ClassWith9ParametersConstructor> result = LambdaFactory.create(constructor(ClassWith9ParametersConstructor.class), suppliers);

        assertThat(result).isNotNull();
        assertThat(result.get()).isInstanceOf(ClassWith9ParametersConstructor.class);
        assertThat(result.get().p0()).isEqualTo(1234L);
        assertThat(result.get().p1()).isEqualTo("098");
        assertThat(result.get().p2()).isEqualTo(10321);
        assertThat(result.get().p3()).isEqualTo(2345L);
        assertThat(result.get().p4()).isEqualTo("987");
        assertThat(result.get().p5()).isEqualTo(10432);
        assertThat(result.get().p6()).isEqualTo(3456L);
        assertThat(result.get().p7()).isEqualTo("876");
        assertThat(result.get().p8()).isEqualTo(10543);
    }

    @Test
    public void shouldCreateBeanWith10Parameters() throws Exception {
        Supplier<ClassWith10ParametersConstructor> result = LambdaFactory.create(constructor(ClassWith10ParametersConstructor.class), suppliers);

        assertThat(result).isNotNull();
        assertThat(result.get()).isInstanceOf(ClassWith10ParametersConstructor.class);
        assertThat(result.get().p0()).isEqualTo(1234L);
        assertThat(result.get().p1()).isEqualTo("098");
        assertThat(result.get().p2()).isEqualTo(10321);
        assertThat(result.get().p3()).isEqualTo(2345L);
        assertThat(result.get().p4()).isEqualTo("987");
        assertThat(result.get().p5()).isEqualTo(10432);
        assertThat(result.get().p6()).isEqualTo(3456L);
        assertThat(result.get().p7()).isEqualTo("876");
        assertThat(result.get().p8()).isEqualTo(10543);
        assertThat(result.get().p9()).isEqualTo(3456L);
    }

    @Test
    public void shouldFailToCreateSupplierForBeanWith11Parameters() throws Exception {
        assertThrows(InjectorException.class, () -> LambdaFactory.create(constructor(ClassWith11ParametersConstructor.class), suppliers));
    }

    @Test
    public void shouldFailToCreateSupplierWithNotEnoughParameters() throws Exception {
        Supplier<?>[] parameters = new Supplier[1];
        parameters[0] = () -> 12345L;
        assertThrows(InjectorException.class, () -> LambdaFactory.create(constructor(ClassWith2ParametersConstructor.class), parameters));
    }

    @Test
    public void shouldFailToCreateSupplierForNullClass() throws Exception {
        assertThrows(InjectorException.class, () -> LambdaFactory.create((Constructor<?>) null, suppliers));
    }

    @Test
    public void shouldFailToCreateSupplierForNullArguments() throws Exception {
        assertThrows(InjectorException.class, () -> LambdaFactory.create(constructor(ClassWithDefaultConstructor.class), null));
    }

    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> constructor(Class<T> clazz) {
        return (Constructor<T>) clazz.getDeclaredConstructors()[0];
    }
}
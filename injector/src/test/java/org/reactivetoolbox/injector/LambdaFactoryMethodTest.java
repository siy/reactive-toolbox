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

package org.reactivetoolbox.injector;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.injector.InjectorException;
import org.reactivetoolbox.injector.LambdaFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LambdaFactoryMethodTest {
    private final Supplier<?>[] parameters = new Supplier[]{
            () -> null,
            () -> 123,
            () -> 234L,
            () -> "s1",
            () -> 345,
            () -> 456L,
            () -> "s2",
            () -> 456,
            () -> 567L,
            () -> "s3",
    };

    private final Class<?>[] types = new Class[]{
            int.class,
            long.class,
            String.class,
            int.class,
            long.class,
            String.class,
            int.class,
            long.class,
            String.class,
            int.class,
            };

    public LambdaFactoryMethodTest() {
        parameters[0] = () -> this;
    }

    @Test
    public void shouldCallMethodWith0Parameters() throws Throwable {
        Object result = createAndCallLambda("method0", new Supplier<?>[]{() -> this});

        assertThat(result).isInstanceOf(String.class);
        assertThat(result).isEqualTo("some value");
    }

    @Test
    public void shouldCallMethodWith1Parameter() throws Throwable {
        createAndInvokeMethod(1);
    }

    @Test
    public void shouldCallMethodWith2Parameters() throws Throwable {
        createAndInvokeMethod(2);
    }

    @Test
    public void shouldCallMethodWith3Parameters() throws Throwable {
        createAndInvokeMethod(3);
    }

    @Test
    public void shouldCallMethodWith4Parameters() throws Throwable {
        createAndInvokeMethod(4);
    }

    @Test
    public void shouldCallMethodWith5Parameters() throws Throwable {
        createAndInvokeMethod(5);
    }

    @Test
    public void shouldCallMethodWith6Parameters() throws Throwable {
        createAndInvokeMethod(6);
    }

    @Test
    public void shouldCallMethodWith7Parameters() throws Throwable {
        createAndInvokeMethod(7);
    }

    @Test
    public void shouldCallMethodWith8Parameters() throws Throwable {
        createAndInvokeMethod(8);
    }

    @Test
    public void shouldCallMethodWith9Parameters() throws Throwable {
        createAndInvokeMethod(9);
    }

    @Test
    public void shouldFailToCreateLambdaForMethodWith10Parameters() throws Throwable {
        assertThrows(InjectorException.class, () -> createAndInvokeMethod(10));
    }

    @Test
    public void shouldFailToCreateLambdaForNullMethod() throws Throwable {
        assertThrows(InjectorException.class, () -> LambdaFactory.create((Method) null, parameters));
    }

    @Test
    public void shouldFailToCreateLambdaForNullParameters() throws Throwable {
        Method method0 = getClass().getMethod("method0");
        assertThat(method0).isNotNull();

        assertThrows(InjectorException.class, () -> LambdaFactory.create(method0, null));
    }

    @Test
    public void shouldFailToCreateLambdaIfNotEnoughParameters() throws Throwable {
        Method method0 = getClass().getMethod("method2", int.class, long.class);
        assertThat(method0).isNotNull();

        assertThrows(InjectorException.class, () -> LambdaFactory.create(method0, new Supplier[1]));
    }

    private void createAndInvokeMethod(int size) throws Throwable {
        Supplier<?>[] params = Arrays.copyOf(parameters, size + 1);
        Class<?>[] paramTypes = Arrays.copyOf(types, size);

        Object result = createAndCallLambda("method" + size, params, paramTypes);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Object[].class);

        Object[] values = (Object[]) result;

        for (int i = 0; i < size; i++) {
            assertThat(values[i]).isEqualTo(params[i + 1].get());
        }
    }

    public String method0() {
        return "some value";
    }

    public Object[] method1(int val0) {
        return new Object[]{val0};
    }

    public Object[] method2(int val0, long val1) {
        return new Object[]{val0, val1};
    }

    public Object[] method3(int val0, long val1, String val2) {
        return new Object[]{val0, val1, val2};
    }

    public Object[] method4(int val0, long val1, String val2, int val3) {
        return new Object[]{val0, val1, val2, val3};
    }

    public Object[] method5(int val0, long val1, String val2, int val3, long val4) {
        return new Object[]{val0, val1, val2, val3, val4};
    }

    public Object[] method6(int val0, long val1, String val2, int val3, long val4,
                            String val5) {
        return new Object[]{val0, val1, val2, val3, val4, val5};
    }

    public Object[] method7(int val0, long val1, String val2, int val3, long val4,
                            String val5, int val6) {
        return new Object[]{val0, val1, val2, val3, val4, val5, val6};
    }

    public Object[] method8(int val0, long val1, String val2, int val3, long val4,
                            String val5, int val6, long val7) {
        return new Object[]{val0, val1, val2, val3, val4, val5, val6, val7};
    }

    public Object[] method9(int val0, long val1, String val2, int val3, long val4,
                            String val5, int val6, long val7, String val8) {
        return new Object[]{val0, val1, val2, val3, val4, val5, val6, val7, val8};
    }

    public Object[] method10(int val0, long val1, String val2, int val3, long val4,
                             String val5, int val6, long val7, String val8, int val9) {
        return new Object[]{val0, val1, val2, val3, val4, val5, val6, val7, val8, val9};
    }

    private Object createAndCallLambda(String name, Supplier<?>[] suppliers, Class<?>... types) throws Throwable {
        Method method = getClass().getDeclaredMethod(name, types);
        return LambdaFactory.create(method, suppliers).get();
    }
}
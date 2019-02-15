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
import org.reactivetoolbox.annotations.injector.Inject;
import org.reactivetoolbox.injector.Key;
import org.reactivetoolbox.injector.core.beans.AnnotatedConstructorClass;
import org.reactivetoolbox.injector.core.beans.DefaultConstructorClass;
import org.reactivetoolbox.injector.core.beans.MultipleConstructorClass;
import org.reactivetoolbox.injector.core.beans.SingleConstructorClass;
import org.reactivetoolbox.injector.core.exception.InjectorException;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UtilsTest {
    @Test
    public void shouldLocateAnnotatedConstructor() throws Exception {
        Constructor<?> result = Utils.locateConstructor(Key.of(AnnotatedConstructorClass.class));

        assertThat(result).isNotNull();
        assertThat(result.isAnnotationPresent(Inject.class)).isTrue();
    }

    @Test
    public void shouldLocateDefaultConstructor() throws Exception {
        Constructor<?> result = Utils.locateConstructor(Key.of(DefaultConstructorClass.class));

        assertThat(result).isNotNull();
        assertThat(result.getParameterCount()).isEqualTo(0);
    }

    @Test
    public void shouldLocateSingleConstructor() throws Exception {
        Constructor<?> result = Utils.locateConstructor(Key.of(SingleConstructorClass.class));

        assertThat(result).isNotNull();
        assertThat(result.getParameterCount()).isEqualTo(1);
    }

    @Test
    public void shouldThrowExceptionWhenMultipleNonDefaultConstructorsAreEncountered() throws Exception {
        assertThrows(InjectorException.class, () -> Utils.locateConstructor(Key.of(MultipleConstructorClass.class)));
    }
}
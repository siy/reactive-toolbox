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

package org.reactivetoolbox.injector.core.beans;

import org.reactivetoolbox.annotations.injector.PostConstruct;

import java.util.function.Consumer;

public class ClassWithThrowingPostConstruct {
    private final Consumer<Integer> consumer;

    public ClassWithThrowingPostConstruct(Consumer<Integer> consumer) {
        this.consumer = consumer;
        consumer.accept(1);
    }

    @PostConstruct
    public void postConstruct() {
        consumer.accept(2);
        throw new RuntimeException("Something wrong");
    }
}

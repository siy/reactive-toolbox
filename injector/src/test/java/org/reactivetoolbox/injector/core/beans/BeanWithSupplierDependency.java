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

import org.reactivetoolbox.injector.core.beans.tree.Bar;
import org.reactivetoolbox.injector.core.beans.tree.Ber;

import java.util.function.Supplier;

public class BeanWithSupplierDependency {
    private final Supplier<Bar> bar;
    private final Ber ber;

    public BeanWithSupplierDependency(Supplier<Bar> bar, Ber ber) {
        this.bar = bar;
        this.ber = ber;
    }

    public Bar bar() {
        return bar.get();
    }

    public Ber ber() {
        return ber;
    }
}

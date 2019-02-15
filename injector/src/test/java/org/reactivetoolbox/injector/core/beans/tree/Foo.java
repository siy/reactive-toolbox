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

package org.reactivetoolbox.injector.core.beans.tree;

import org.reactivetoolbox.annotations.injector.Inject;
import org.reactivetoolbox.annotations.injector.Singleton;

@Singleton
public class Foo {
    private final Bar bar;
    private final Foe foe;

    @Inject
    public Foo(Bar bar, Foe foe) {
        this.bar = bar;
        this.foe = foe;
    }

    public Bar bar() {
        return bar;
    }

    public Foe foe() {
        return foe;
    }
}

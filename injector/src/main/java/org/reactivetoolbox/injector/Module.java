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

package org.reactivetoolbox.injector;

import org.reactivetoolbox.injector.annotations.ConfiguredBy;

import java.util.List;

/**
 * Base module API.
 * <br />
 * Modules are used to configure {@link Injector}. The {@link #collectBindings()} method is invoked at run time to
 * obtain binding information. Usually it more convenient to inherit {@link AbstractModule} and use fluent style
 * binding builder API.
 * <br />
 * Note that bindings to classes returned by {@link #collectBindings()} method are handled differently than
 * other dependencies. During loading of these classes {@link ConfiguredBy} annotation
 * is ignored.
 *
 * @see AbstractModule
 */
public interface Module {
    /**
     * Return information about bindings.
     *
     * @return List of bindings.
     */
    List<Binding<?>> collectBindings();
}

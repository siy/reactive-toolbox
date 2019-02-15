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

package org.reactivetoolbox.injector.core.examples.annotated.impl;

import org.reactivetoolbox.annotations.injector.Singleton;
import org.reactivetoolbox.injector.Injector;
import org.reactivetoolbox.injector.core.examples.annotated.Service;
import org.reactivetoolbox.injector.core.examples.annotated.ServiceConfig;
import org.reactivetoolbox.injector.core.examples.annotated.ServiceFactory;

@Singleton
public class ServiceFactoryImpl implements ServiceFactory {
    private final Injector injector;

    public ServiceFactoryImpl(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Service create(ServiceConfig config) {
        return injector.get(config.implementation());
    }
}

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
import org.reactivetoolbox.injector.core.examples.annotated.ApplicationConfig;
import org.reactivetoolbox.injector.core.examples.annotated.ServiceConfig;
import org.reactivetoolbox.injector.core.examples.annotated.services.ListService;
import org.reactivetoolbox.injector.core.examples.annotated.services.SetService;

import java.util.Arrays;
import java.util.List;

@Singleton
public class ApplicationConfigImpl implements ApplicationConfig {
    private final List<ServiceConfig> serviceConfiguration = Arrays.asList(
        ServiceConfig.of("ListService", ListService.class),
        ServiceConfig.of("SetService", SetService.class)
    );

    @Override
    public List<ServiceConfig> services() {
        return serviceConfiguration;
    }
}

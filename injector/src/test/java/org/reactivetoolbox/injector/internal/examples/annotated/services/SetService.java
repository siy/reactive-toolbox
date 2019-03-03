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

package org.reactivetoolbox.injector.internal.examples.annotated.services;

import org.reactivetoolbox.injector.internal.examples.annotated.Service;

public class SetService implements Service {
    @Override
    public void start() {
        System.out.println("starting " + name() + "...");
    }

    @Override
    public void stop() {
        System.out.println("stopping " + name() + "...");
    }
}

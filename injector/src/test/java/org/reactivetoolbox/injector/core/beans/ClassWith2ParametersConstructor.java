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

public class ClassWith2ParametersConstructor {
    private final Long p0;
    private final String p1;

    public ClassWith2ParametersConstructor(Long p0, String p1) {
        this.p0 = p0;
        this.p1 = p1;
    }

    public Long p0() {
        return p0;
    }

    public String p1() {
        return p1;
    }
}

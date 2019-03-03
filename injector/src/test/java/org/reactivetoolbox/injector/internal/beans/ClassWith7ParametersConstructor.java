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

package org.reactivetoolbox.injector.internal.beans;

public class ClassWith7ParametersConstructor {
    private final Long p0;
    private final String p1;
    private final int p2;
    private final Long p3;
    private final String p4;
    private final int p5;
    private final Long p6;

    public ClassWith7ParametersConstructor(Long p0, String p1, int p2, Long p3, String p4, int p5, Long p6) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.p5 = p5;
        this.p6 = p6;
    }

    public Long p0() {
        return p0;
    }

    public String p1() {
        return p1;
    }

    public int p2() {
        return p2;
    }

    public Long p3() {
        return p3;
    }

    public String p4() {
        return p4;
    }

    public int p5() {
        return p5;
    }

    public Long p6() {
        return p6;
    }
}

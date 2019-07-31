package org.reactivetoolbox.build;

/*
 * Copyright (c) 2017-2019 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.reactivetoolbox.build.HttpParameterAssembler.PB0;
import org.reactivetoolbox.build.HttpParameterAssembler.PB1;
import org.reactivetoolbox.build.HttpParameterAssembler.PB2;
import org.reactivetoolbox.build.HttpParameterAssembler.PB3;
import org.reactivetoolbox.build.HttpParameterAssembler.PB4;
import org.reactivetoolbox.build.HttpParameterAssembler.PB5;
import org.reactivetoolbox.build.HttpParameterAssembler.PB6;
import org.reactivetoolbox.build.HttpParameterAssembler.PB7;
import org.reactivetoolbox.build.HttpParameterAssembler.PB8;
import org.reactivetoolbox.build.HttpParameterAssembler.PB9;
import org.reactivetoolbox.core.functional.Tuples;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.web.server.HttpMethod;
import org.reactivetoolbox.web.server.parameter.Parameters.P;

/**
 * Root class for fluent HTTP route assembling.
 */
public class HttpRouteAssembler {
    private final Path path;
    private final String description;

    private HttpRouteAssembler(final Path path, final String description) {
        this.path = path;
        this.description = description;
    }

    public HttpRouteAssembler description(final String description) {
        return new HttpRouteAssembler(path, description);
    }

    public static HttpRouteAssembler when(final HttpMethod method, final String path) {
        return new HttpRouteAssembler(Path.of(path, method), "(no description)");
    }

    public PB0 withoutParameters() {
        return new PB0(path, description);
    }

    public <T1> PB1<T1> with(final P<T1> param1) {
        return new PB1<>(path, description, Tuples.of(param1));
    }

    public <T1, T2> PB2<T1, T2> with(final P<T1> param1,
                                     final P<T2> param2) {
        return new PB2<>(path, description, Tuples.of(param1, param2));
    }

    public <T1, T2, T3> PB3<T1, T2, T3> with(final P<T1> param1,
                                             final P<T2> param2,
                                             final P<T3> param3) {
        return new PB3<>(path, description, Tuples.of(param1, param2, param3));
    }

    public <T1, T2, T3, T4> PB4<T1, T2, T3, T4> with(final P<T1> param1,
                                                     final P<T2> param2,
                                                     final P<T3> param3,
                                                     final P<T4> param4) {
        return new PB4<>(path, description, Tuples.of(param1, param2, param3, param4));
    }

    public <T1, T2, T3, T4, T5> PB5<T1, T2, T3, T4, T5> with(final P<T1> param1,
                                                             final P<T2> param2,
                                                             final P<T3> param3,
                                                             final P<T4> param4,
                                                             final P<T5> param5) {
        return new PB5<>(path, description, Tuples.of(param1, param2, param3, param4, param5));
    }

    public <T1, T2, T3, T4, T5, T6> PB6<T1, T2, T3, T4, T5, T6> with(final P<T1> param1,
                                                                     final P<T2> param2,
                                                                     final P<T3> param3,
                                                                     final P<T4> param4,
                                                                     final P<T5> param5,
                                                                     final P<T6> param6) {
        return new PB6<>(path, description, Tuples.of(param1, param2, param3, param4, param5, param6));
    }

    public <T1, T2, T3, T4, T5, T6, T7> PB7<T1, T2, T3, T4, T5, T6, T7> with(final P<T1> param1,
                                                                             final P<T2> param2,
                                                                             final P<T3> param3,
                                                                             final P<T4> param4,
                                                                             final P<T5> param5,
                                                                             final P<T6> param6,
                                                                             final P<T7> param7) {
        return new PB7<>(path, description, Tuples.of(param1, param2, param3, param4, param5, param6, param7));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8> PB8<T1, T2, T3, T4, T5, T6, T7, T8> with(final P<T1> param1,
                                                                                     final P<T2> param2,
                                                                                     final P<T3> param3,
                                                                                     final P<T4> param4,
                                                                                     final P<T5> param5,
                                                                                     final P<T6> param6,
                                                                                     final P<T7> param7,
                                                                                     final P<T8> param8) {
        return new PB8<>(path, description, Tuples.of(param1, param2, param3, param4, param5, param6, param7, param8));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> PB9<T1, T2, T3, T4, T5, T6, T7, T8, T9> with(final P<T1> param1,
                                                                                             final P<T2> param2,
                                                                                             final P<T3> param3,
                                                                                             final P<T4> param4,
                                                                                             final P<T5> param5,
                                                                                             final P<T6> param6,
                                                                                             final P<T7> param7,
                                                                                             final P<T8> param8,
                                                                                             final P<T9> param9) {
        return new PB9<>(path,
                         description,
                         Tuples.of(param1, param2, param3, param4, param5, param6, param7, param8, param9));
    }
}

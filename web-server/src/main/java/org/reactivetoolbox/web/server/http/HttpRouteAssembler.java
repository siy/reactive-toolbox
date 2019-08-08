package org.reactivetoolbox.web.server.http;

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

import org.reactivetoolbox.build.DescribedPath;
import org.reactivetoolbox.eventbus.Path;
import org.reactivetoolbox.web.server.http.HttpParameters.PB0;
import org.reactivetoolbox.web.server.http.HttpParameters.PB1;
import org.reactivetoolbox.web.server.http.HttpParameters.PB2;
import org.reactivetoolbox.web.server.http.HttpParameters.PB3;
import org.reactivetoolbox.web.server.http.HttpParameters.PB4;
import org.reactivetoolbox.web.server.http.HttpParameters.PB5;
import org.reactivetoolbox.web.server.http.HttpParameters.PB6;
import org.reactivetoolbox.web.server.http.HttpParameters.PB7;
import org.reactivetoolbox.web.server.http.HttpParameters.PB8;
import org.reactivetoolbox.web.server.http.HttpParameters.PB9;
import org.reactivetoolbox.web.server.parameter.conversion.var.Var;
import org.reactivetoolbox.web.server.parameter.validation.Validator;

import static org.reactivetoolbox.core.functional.Tuples.of;

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

    public PB0 withNoParameters() {
        return new PB0(DescribedPath.of(path, description));
    }

    public <T1> PB1<T1> with(final Var<T1> param1) {
        return new PB1<>(DescribedPath.of(path, description), of(param1));
    }

    public <T1, T2> PB2<T1, T2> with(final Var<T1> param1,
                                     final Var<T2> param2) {
        return new PB2<>(DescribedPath.of(path, description), of(param1, param2), Validator::valid);
    }

    public <T1, T2, T3> PB3<T1, T2, T3> with(final Var<T1> param1,
                                             final Var<T2> param2,
                                             final Var<T3> param3) {
        return new PB3<>(DescribedPath.of(path, description), of(param1, param2, param3), Validator::valid);
    }

    public <T1, T2, T3, T4> PB4<T1, T2, T3, T4> with(final Var<T1> param1,
                                                     final Var<T2> param2,
                                                     final Var<T3> param3,
                                                     final Var<T4> param4) {
        return new PB4<>(DescribedPath.of(path, description), of(param1, param2, param3, param4), Validator::valid);
    }

    public <T1, T2, T3, T4, T5> PB5<T1, T2, T3, T4, T5> with(final Var<T1> param1,
                                                             final Var<T2> param2,
                                                             final Var<T3> param3,
                                                             final Var<T4> param4,
                                                             final Var<T5> param5) {
        return new PB5<>(DescribedPath.of(path, description), of(param1, param2, param3, param4, param5), Validator::valid);
    }

    public <T1, T2, T3, T4, T5, T6> PB6<T1, T2, T3, T4, T5, T6> with(final Var<T1> param1,
                                                                     final Var<T2> param2,
                                                                     final Var<T3> param3,
                                                                     final Var<T4> param4,
                                                                     final Var<T5> param5,
                                                                     final Var<T6> param6) {
        return new PB6<>(DescribedPath.of(path, description), of(param1, param2, param3, param4, param5, param6), Validator::valid);
    }

    public <T1, T2, T3, T4, T5, T6, T7> PB7<T1, T2, T3, T4, T5, T6, T7> with(final Var<T1> param1,
                                                                             final Var<T2> param2,
                                                                             final Var<T3> param3,
                                                                             final Var<T4> param4,
                                                                             final Var<T5> param5,
                                                                             final Var<T6> param6,
                                                                             final Var<T7> param7) {
        return new PB7<>(DescribedPath.of(path, description), of(param1, param2, param3, param4, param5, param6, param7), Validator::valid);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8> PB8<T1, T2, T3, T4, T5, T6, T7, T8> with(final Var<T1> param1,
                                                                                     final Var<T2> param2,
                                                                                     final Var<T3> param3,
                                                                                     final Var<T4> param4,
                                                                                     final Var<T5> param5,
                                                                                     final Var<T6> param6,
                                                                                     final Var<T7> param7,
                                                                                     final Var<T8> param8) {
        return new PB8<>(DescribedPath.of(path, description), of(param1, param2, param3, param4, param5, param6, param7, param8), Validator::valid);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> PB9<T1, T2, T3, T4, T5, T6, T7, T8, T9> with(final Var<T1> param1,
                                                                                             final Var<T2> param2,
                                                                                             final Var<T3> param3,
                                                                                             final Var<T4> param4,
                                                                                             final Var<T5> param5,
                                                                                             final Var<T6> param6,
                                                                                             final Var<T7> param7,
                                                                                             final Var<T8> param8,
                                                                                             final Var<T9> param9) {
        return new PB9<>(DescribedPath.of(path, description),
                         of(param1, param2, param3, param4, param5, param6, param7, param8, param9), Validator::valid);
    }
}

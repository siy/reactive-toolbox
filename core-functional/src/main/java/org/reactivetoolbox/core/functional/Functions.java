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

package org.reactivetoolbox.core.functional;

public interface Functions {
    interface FN0<R> {
        R apply();
    }

    interface FN1<R, T1> {
        R apply(T1 param1);
    }

    interface FN2<R, T1, T2> {
        R apply(T1 param1, T2 param2);
    }

    interface FN3<R, T1, T2, T3> {
        R apply(T1 param1, T2 param2, T3 param3);
    }

    interface FN4<R, T1, T2, T3, T4> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4);
    }

    interface FN5<R, T1, T2, T3, T4, T5> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4, T5 param5);
    }

    interface FN6<R, T1, T2, T3, T4, T5, T6> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4, T5 param5, T6 param6);
    }

    interface FN7<R, T1, T2, T3, T4, T5, T6, T7> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4, T5 param5, T6 param6, T7 param7);
    }

    interface FN8<R, T1, T2, T3, T4, T5, T6, T7, T8> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4, T5 param5, T6 param6, T7 param7, T8 param8);
    }

    interface FN9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
        R apply(T1 param1, T2 param2, T3 param3, T4 param4, T5 param5, T6 param6, T7 param7, T8 param8, T9 param9);
    }
}

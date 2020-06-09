package org.reactivetoolbox.core.lang;

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

import org.reactivetoolbox.core.lang.functional.Functions.FN0;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Functions.FN2;
import org.reactivetoolbox.core.lang.functional.Functions.FN3;
import org.reactivetoolbox.core.lang.functional.Functions.FN4;
import org.reactivetoolbox.core.lang.functional.Functions.FN5;
import org.reactivetoolbox.core.lang.functional.Functions.FN6;
import org.reactivetoolbox.core.lang.functional.Functions.FN7;
import org.reactivetoolbox.core.lang.functional.Functions.FN8;
import org.reactivetoolbox.core.lang.functional.Functions.FN9;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Tuples with various size and convenient static factories for tuple creation.<br/>
 */
public interface Tuple<S extends Tuple> {
    interface Tuple0 extends Tuple<Tuple0> {
        <T> T map(FN0<T> mapper);

        default <T1> Tuple1<T1> append(final T1 value) {
            return tuple(value);
        }

        default <T1> Tuple1<T1> prepend(final T1 value) {
            return tuple(value);
        }

        static int size() {
            return 0;
        }
    }

    interface Tuple1<T1> extends Tuple<Tuple1<T1>> {
        <T> T map(FN1<T, T1> mapper);

        default <T2> Tuple2<T1, T2> append(final T2 value) {
            return map(v1 -> tuple(v1, value));
        }

        default <T2> Tuple2<T2, T1> prepend(final T2 value) {
            return map(v1 -> tuple(value, v1));
        }

        static int size() {
            return 1;
        }
    }

    interface Tuple2<T1, T2> extends Tuple {
        <T> T map(FN2<T, T1, T2> mapper);

        default <NT1, NT2> Tuple2<NT1, NT2> map(final FN1<NT1, T1> mapper1, final FN1<NT2, T2> mapper2) {
            return map((v1, v2) -> tuple(mapper1.apply(v1), mapper2.apply(v2)));
        }

        default <T3> Tuple3<T1, T2, T3> append(final T3 value) {
            return map((v1, v2) -> tuple(v1, v2, value));
        }

        default <T3> Tuple3<T3, T1, T2> prepend(final T3 value) {
            return map((v1, v2) -> tuple(value, v1, v2));
        }

        static int size() {
            return 2;
        }
    }

    interface Tuple3<T1, T2, T3> extends Tuple {
        <T> T map(FN3<T, T1, T2, T3> mapper);

        default <NT1, NT2, NT3> Tuple3<NT1, NT2, NT3> map(final FN1<NT1, T1> mapper1,
                                                          final FN1<NT2, T2> mapper2,
                                                          final FN1<NT3, T3> mapper3) {
            return map((v1, v2, v3) -> tuple(mapper1.apply(v1),
                                             mapper2.apply(v2),
                                             mapper3.apply(v3)));
        }

        default <T4> Tuple4<T1, T2, T3, T4> append(final T4 value) {
            return map((v1, v2, v3) -> tuple(v1, v2, v3, value));
        }

        default <T4> Tuple4<T4, T1, T2, T3> prepend(final T4 value) {
            return map((v1, v2, v3) -> tuple(value, v1, v2, v3));
        }

        static int size() {
            return 3;
        }
    }

    interface Tuple4<T1, T2, T3, T4> extends Tuple {
        <T> T map(FN4<T, T1, T2, T3, T4> mapper);

        default <NT1, NT2, NT3, NT4> Tuple4<NT1, NT2, NT3, NT4> map(final FN1<NT1, T1> mapper1,
                                                                    final FN1<NT2, T2> mapper2,
                                                                    final FN1<NT3, T3> mapper3,
                                                                    final FN1<NT4, T4> mapper4) {
            return map((v1, v2, v3, v4) -> tuple(mapper1.apply(v1),
                                                 mapper2.apply(v2),
                                                 mapper3.apply(v3),
                                                 mapper4.apply(v4)));
        }

        default <T5> Tuple5<T1, T2, T3, T4, T5> append(final T5 value) {
            return map((v1, v2, v3, v4) -> tuple(v1, v2, v3, v4, value));
        }

        default <T5> Tuple5<T5, T1, T2, T3, T4> prepend(final T5 value) {
            return map((v1, v2, v3, v4) -> tuple(value, v1, v2, v3, v4));
        }

        static int size() {
            return 4;
        }
    }

    interface Tuple5<T1, T2, T3, T4, T5> extends Tuple {
        <T> T map(FN5<T, T1, T2, T3, T4, T5> mapper);

        default <NT1, NT2, NT3, NT4, NT5> Tuple5<NT1, NT2, NT3, NT4, NT5> map(final FN1<NT1, T1> mapper1,
                                                                              final FN1<NT2, T2> mapper2,
                                                                              final FN1<NT3, T3> mapper3,
                                                                              final FN1<NT4, T4> mapper4,
                                                                              final FN1<NT5, T5> mapper5) {
            return map((v1, v2, v3, v4, v5) -> tuple(mapper1.apply(v1),
                                                     mapper2.apply(v2),
                                                     mapper3.apply(v3),
                                                     mapper4.apply(v4),
                                                     mapper5.apply(v5)));
        }

        default <T6> Tuple6<T1, T2, T3, T4, T5, T6> append(final T6 value) {
            return map((v1, v2, v3, v4, v5) -> tuple(v1, v2, v3, v4, v5, value));
        }

        default <T6> Tuple6<T6, T1, T2, T3, T4, T5> prepend(final T6 value) {
            return map((v1, v2, v3, v4, v5) -> tuple(value, v1, v2, v3, v4, v5));
        }

        static int size() {
            return 5;
        }
    }

    interface Tuple6<T1, T2, T3, T4, T5, T6> extends Tuple {
        <T> T map(FN6<T, T1, T2, T3, T4, T5, T6> mapper);

        default <NT1, NT2, NT3, NT4, NT5, NT6> Tuple6<NT1, NT2, NT3, NT4, NT5, NT6> map(final FN1<NT1, T1> mapper1,
                                                                                        final FN1<NT2, T2> mapper2,
                                                                                        final FN1<NT3, T3> mapper3,
                                                                                        final FN1<NT4, T4> mapper4,
                                                                                        final FN1<NT5, T5> mapper5,
                                                                                        final FN1<NT6, T6> mapper6) {
            return map((v1, v2, v3, v4, v5, v6) -> tuple(mapper1.apply(v1),
                                                         mapper2.apply(v2),
                                                         mapper3.apply(v3),
                                                         mapper4.apply(v4),
                                                         mapper5.apply(v5),
                                                         mapper6.apply(v6)));
        }

        default <T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> append(final T7 value) {
            return map((v1, v2, v3, v4, v5, v6) -> tuple(v1, v2, v3, v4, v5, v6, value));
        }

        default <T7> Tuple7<T7, T1, T2, T3, T4, T5, T6> prepend(final T7 value) {
            return map((v1, v2, v3, v4, v5, v6) -> tuple(value, v1, v2, v3, v4, v5, v6));
        }

        static int size() {
            return 6;
        }
    }

    interface Tuple7<T1, T2, T3, T4, T5, T6, T7> extends Tuple {
        <T> T map(FN7<T, T1, T2, T3, T4, T5, T6, T7> mapper);

        default <NT1, NT2, NT3, NT4, NT5, NT6, NT7> Tuple7<NT1, NT2, NT3, NT4, NT5, NT6, NT7> map(final FN1<NT1, T1> mapper1,
                                                                                                  final FN1<NT2, T2> mapper2,
                                                                                                  final FN1<NT3, T3> mapper3,
                                                                                                  final FN1<NT4, T4> mapper4,
                                                                                                  final FN1<NT5, T5> mapper5,
                                                                                                  final FN1<NT6, T6> mapper6,
                                                                                                  final FN1<NT7, T7> mapper7) {
            return map((v1, v2, v3, v4, v5, v6, v7) -> tuple(mapper1.apply(v1),
                                                             mapper2.apply(v2),
                                                             mapper3.apply(v3),
                                                             mapper4.apply(v4),
                                                             mapper5.apply(v5),
                                                             mapper6.apply(v6),
                                                             mapper7.apply(v7)));
        }

        default <T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> append(final T8 value) {
            return map((v1, v2, v3, v4, v5, v6, v7) -> tuple(v1, v2, v3, v4, v5, v6, v7, value));
        }

        default <T8> Tuple8<T8, T1, T2, T3, T4, T5, T6, T7> prepend(final T8 value) {
            return map((v1, v2, v3, v4, v5, v6, v7) -> tuple(value, v1, v2, v3, v4, v5, v6, v7));
        }

        static int size() {
            return 7;
        }
    }

    interface Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> extends Tuple {
        <T> T map(FN8<T, T1, T2, T3, T4, T5, T6, T7, T8> mapper);

        default <NT1, NT2, NT3, NT4, NT5, NT6, NT7, NT8>
        Tuple8<NT1, NT2, NT3, NT4, NT5, NT6, NT7, NT8> map(final FN1<NT1, T1> mapper1,
                                                           final FN1<NT2, T2> mapper2,
                                                           final FN1<NT3, T3> mapper3,
                                                           final FN1<NT4, T4> mapper4,
                                                           final FN1<NT5, T5> mapper5,
                                                           final FN1<NT6, T6> mapper6,
                                                           final FN1<NT7, T7> mapper7,
                                                           final FN1<NT8, T8> mapper8) {
            return map((v1, v2, v3, v4, v5, v6, v7, v8) -> tuple(mapper1.apply(v1),
                                                                 mapper2.apply(v2),
                                                                 mapper3.apply(v3),
                                                                 mapper4.apply(v4),
                                                                 mapper5.apply(v5),
                                                                 mapper6.apply(v6),
                                                                 mapper7.apply(v7),
                                                                 mapper8.apply(v8)));
        }

        default <T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> append(final T9 value) {
            return map((v1, v2, v3, v4, v5, v6, v7, v8) -> tuple(v1, v2, v3, v4, v5, v6, v7, v8, value));
        }

        default <T9> Tuple9<T9, T1, T2, T3, T4, T5, T6, T7, T8> prepend(final T9 value) {
            return map((v1, v2, v3, v4, v5, v6, v7, v8) -> tuple(value, v1, v2, v3, v4, v5, v6, v7, v8));
        }

        static int size() {
            return 8;
        }
    }

    interface Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends Tuple {
        <T> T map(FN9<T, T1, T2, T3, T4, T5, T6, T7, T8, T9> mapper);

        default <NT1, NT2, NT3, NT4, NT5, NT6, NT7, NT8, NT9>
        Tuple9<NT1, NT2, NT3, NT4, NT5, NT6, NT7, NT8, NT9> map(final FN1<NT1, T1> mapper1,
                                                                final FN1<NT2, T2> mapper2,
                                                                final FN1<NT3, T3> mapper3,
                                                                final FN1<NT4, T4> mapper4,
                                                                final FN1<NT5, T5> mapper5,
                                                                final FN1<NT6, T6> mapper6,
                                                                final FN1<NT7, T7> mapper7,
                                                                final FN1<NT8, T8> mapper8,
                                                                final FN1<NT9, T9> mapper9) {
            return map((v1, v2, v3, v4, v5, v6, v7, v8, v9) -> tuple(mapper1.apply(v1),
                                                                     mapper2.apply(v2),
                                                                     mapper3.apply(v3),
                                                                     mapper4.apply(v4),
                                                                     mapper5.apply(v5),
                                                                     mapper6.apply(v6),
                                                                     mapper7.apply(v7),
                                                                     mapper8.apply(v8),
                                                                     mapper9.apply(v9)));
        }

        static int size() {
            return 9;
        }
    }

    Tuple0 TUPLE_0 = new Tuple0() {
        @Override
        public <T> T map(final FN0<T> mapper) {
            return mapper.apply();
        }

        @Override
        public boolean equals(final Object obj) {
            return this == obj || obj instanceof Tuple0;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public String toString() {
            return "Tuple0()";
        }
    };

    static Tuple0 tuple() {
        return TUPLE_0;
    }

    static <T1> Tuple1<T1> tuple(final T1 param1) {
        return new Tuple1<>() {
            @Override
            public <T> T map(final FN1<T, T1> mapper) {
                return mapper.apply(param1);
            }

            @Override
            public boolean equals(final Object obj) {
                if (this == obj) {
                    return true;
                }

                return (obj instanceof Tuple1<?> tuple1)
                       ? tuple1.map(v1 -> Objects.equals(v1, param1))
                       : false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(param1);
            }

            @Override
            public String toString() {
                return new StringJoiner(", ", "Tuple(", ")")
                        .add(param1.toString())
                        .toString();
            }
        };
    }

    static <T1, T2> Tuple2<T1, T2> tuple(final T1 param1, final T2 param2) {
        return new Tuple2<>() {
            @Override
            public <T> T map(final FN2<T, T1, T2> mapper) {
                return mapper.apply(param1, param2);
            }

            @Override
            public boolean equals(final Object obj) {
                if (this == obj) {
                    return true;
                }

                return (obj instanceof Tuple2<?, ?> tuple2)
                       ? tuple2.map((v1, v2) -> Objects.equals(v1, param1)
                                                && Objects.equals(v2, param2))
                       : false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(param1, param2);
            }

            @Override
            public String toString() {
                return new StringJoiner(", ", "Tuple(", ")")
                        .add(param1.toString())
                        .add(param2.toString())
                        .toString();
            }
        };
    }

    static <T1, T2, T3> Tuple3<T1, T2, T3> tuple(final T1 param1, final T2 param2, final T3 param3) {
        return new Tuple3<>() {
            @Override
            public <T> T map(final FN3<T, T1, T2, T3> mapper) {
                return mapper.apply(param1, param2, param3);
            }

            @Override
            public boolean equals(final Object obj) {
                if (this == obj) {
                    return true;
                }

                return (obj instanceof Tuple3<?, ?, ?> tuple3)
                       ? tuple3.map((v1, v2, v3) -> Objects.equals(v1, param1)
                                                    && Objects.equals(v2, param2)
                                                    && Objects.equals(v3, param3))
                       : false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(param1, param2, param3);
            }

            @Override
            public String toString() {
                return new StringJoiner(", ", "Tuple(", ")")
                        .add(param1.toString())
                        .add(param2.toString())
                        .add(param3.toString())
                        .toString();
            }
        };
    }

    static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> tuple(final T1 param1, final T2 param2, final T3 param3,
                                                         final T4 param4) {
        return new Tuple4<>() {
            @Override
            public <T> T map(final FN4<T, T1, T2, T3, T4> mapper) {
                return mapper.apply(param1, param2, param3, param4);
            }

            @Override
            public boolean equals(final Object obj) {
                if (this == obj) {
                    return true;
                }

                return (obj instanceof Tuple4<?, ?, ?, ?> tuple4)
                       ? tuple4.map((v1, v2, v3, v4) -> Objects.equals(v1, param1)
                                                        && Objects.equals(v2, param2)
                                                        && Objects.equals(v3, param3)
                                                        && Objects.equals(v4, param4))
                       : false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(param1, param2, param3, param4);
            }

            @Override
            public String toString() {
                return new StringJoiner(", ", "Tuple(", ")")
                        .add(param1.toString())
                        .add(param2.toString())
                        .add(param3.toString())
                        .add(param4.toString())
                        .toString();
            }
        };
    }

    static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> tuple(final T1 param1, final T2 param2, final T3 param3,
                                                                 final T4 param4, final T5 param5) {
        return new Tuple5<>() {
            @Override
            public <T> T map(final FN5<T, T1, T2, T3, T4, T5> mapper) {
                return mapper.apply(param1, param2, param3, param4, param5);
            }

            @Override
            public boolean equals(final Object obj) {
                if (this == obj) {
                    return true;
                }

                return (obj instanceof Tuple5<?, ?, ?, ?, ?> tuple5)
                       ? tuple5.map((v1, v2, v3, v4, v5) -> Objects.equals(v1, param1)
                                                            && Objects.equals(v2, param2)
                                                            && Objects.equals(v3, param3)
                                                            && Objects.equals(v4, param4)
                                                            && Objects.equals(v5, param5))
                       : false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(param1, param2, param3, param4, param5);
            }

            @Override
            public String toString() {
                return new StringJoiner(", ", "Tuple(", ")")
                        .add(param1.toString())
                        .add(param2.toString())
                        .add(param3.toString())
                        .add(param4.toString())
                        .add(param5.toString())
                        .toString();
            }
        };
    }

    static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> tuple(final T1 param1,
                                                                         final T2 param2,
                                                                         final T3 param3,
                                                                         final T4 param4,
                                                                         final T5 param5,
                                                                         final T6 param6) {
        return new Tuple6<>() {
            @Override
            public <T> T map(final FN6<T, T1, T2, T3, T4, T5, T6> mapper) {
                return mapper.apply(param1, param2, param3, param4, param5, param6);
            }

            @Override
            public boolean equals(final Object obj) {
                if (this == obj) {
                    return true;
                }

                return (obj instanceof Tuple6<?, ?, ?, ?, ?, ?> tuple6)
                       ? tuple6.map((v1, v2, v3, v4, v5, v6) -> Objects.equals(v1, param1)
                                                                && Objects.equals(v2, param2)
                                                                && Objects.equals(v3, param3)
                                                                && Objects.equals(v4, param4)
                                                                && Objects.equals(v5, param5)
                                                                && Objects.equals(v6, param6))
                       : false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(param1, param2, param3, param4, param5, param6);
            }

            @Override
            public String toString() {
                return new StringJoiner(", ", "Tuple(", ")")
                        .add(param1.toString())
                        .add(param2.toString())
                        .add(param3.toString())
                        .add(param4.toString())
                        .add(param5.toString())
                        .add(param6.toString())
                        .toString();
            }
        };
    }

    static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple(final T1 param1,
                                                                                 final T2 param2,
                                                                                 final T3 param3,
                                                                                 final T4 param4,
                                                                                 final T5 param5,
                                                                                 final T6 param6,
                                                                                 final T7 param7) {
        return new Tuple7<>() {
            @Override
            public <T> T map(final FN7<T, T1, T2, T3, T4, T5, T6, T7> mapper) {
                return mapper.apply(param1, param2, param3, param4, param5, param6, param7);
            }

            @Override
            public boolean equals(final Object obj) {
                if (this == obj) {
                    return true;
                }

                return (obj instanceof Tuple7<?, ?, ?, ?, ?, ?, ?> tuple7)
                       ? tuple7.map((v1, v2, v3, v4, v5, v6, v7) -> Objects.equals(v1, param1)
                                                                    && Objects.equals(v2, param2)
                                                                    && Objects.equals(v3, param3)
                                                                    && Objects.equals(v4, param4)
                                                                    && Objects.equals(v5, param5)
                                                                    && Objects.equals(v6, param6)
                                                                    && Objects.equals(v7, param7))
                       : false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(param1, param2, param3, param4, param5, param6, param7);
            }

            @Override
            public String toString() {
                return new StringJoiner(", ", "Tuple(", ")")
                        .add(param1.toString())
                        .add(param2.toString())
                        .add(param3.toString())
                        .add(param4.toString())
                        .add(param5.toString())
                        .add(param6.toString())
                        .add(param7.toString())
                        .toString();
            }
        };
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> tuple(final T1 param1,
                                                                                         final T2 param2,
                                                                                         final T3 param3,
                                                                                         final T4 param4,
                                                                                         final T5 param5,
                                                                                         final T6 param6,
                                                                                         final T7 param7,
                                                                                         final T8 param8) {
        return new Tuple8<>() {
            @Override
            public <T> T map(final FN8<T, T1, T2, T3, T4, T5, T6, T7, T8> mapper) {
                return mapper.apply(param1, param2, param3, param4, param5, param6, param7, param8);
            }

            @Override
            public boolean equals(final Object obj) {
                if (this == obj) {
                    return true;
                }

                return (obj instanceof Tuple8<?, ?, ?, ?, ?, ?, ?, ?> tuple8)
                       ? tuple8.map((v1, v2, v3, v4, v5, v6, v7, v8) -> Objects.equals(v1, param1)
                                                                        && Objects.equals(v2, param2)
                                                                        && Objects.equals(v3, param3)
                                                                        && Objects.equals(v4, param4)
                                                                        && Objects.equals(v5, param5)
                                                                        && Objects.equals(v6, param6)
                                                                        && Objects.equals(v7, param7)
                                                                        && Objects.equals(v8, param8))
                       : false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(param1, param2, param3, param4, param5, param6, param7, param8);
            }

            @Override
            public String toString() {
                return new StringJoiner(", ", "Tuple(", ")")
                        .add(param1.toString())
                        .add(param2.toString())
                        .add(param3.toString())
                        .add(param4.toString())
                        .add(param5.toString())
                        .add(param6.toString())
                        .add(param7.toString())
                        .add(param8.toString())
                        .toString();
            }
        };
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple(final T1 param1,
                                                                                                 final T2 param2,
                                                                                                 final T3 param3,
                                                                                                 final T4 param4,
                                                                                                 final T5 param5,
                                                                                                 final T6 param6,
                                                                                                 final T7 param7,
                                                                                                 final T8 param8,
                                                                                                 final T9 param9) {
        return new Tuple9<>() {
            @Override
            public <T> T map(final FN9<T, T1, T2, T3, T4, T5, T6, T7, T8, T9> mapper) {
                return mapper.apply(param1, param2, param3, param4, param5, param6, param7, param8, param9);
            }

            @Override
            public boolean equals(final Object obj) {
                if (this == obj) {
                    return true;
                }

                return (obj instanceof Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?> tuple9)
                       ? tuple9.map((v1, v2, v3, v4, v5, v6, v7, v8, v9) -> Objects.equals(v1, param1)
                                                                            && Objects.equals(v2, param2)
                                                                            && Objects.equals(v3, param3)
                                                                            && Objects.equals(v4, param4)
                                                                            && Objects.equals(v5, param5)
                                                                            && Objects.equals(v6, param6)
                                                                            && Objects.equals(v7, param7)
                                                                            && Objects.equals(v8, param8)
                                                                            && Objects.equals(v9, param9))
                       : false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(param1, param2, param3, param4, param5, param6, param7, param8, param9);
            }

            @Override
            public String toString() {
                return new StringJoiner(", ", "Tuple(", ")")
                        .add(param1.toString())
                        .add(param2.toString())
                        .add(param3.toString())
                        .add(param4.toString())
                        .add(param5.toString())
                        .add(param6.toString())
                        .add(param7.toString())
                        .add(param8.toString())
                        .add(param9.toString())
                        .toString();
            }
        };
    }

    static <A, B> Tuple2<Tuple2<A, A>, Tuple2<B, B>> rotate(final Tuple2<Tuple2<A, B>, Tuple2<A, B>> input) {
        return input.map((t1, t2) -> t1.map((a1, b1) -> t2.map((a2, b2) -> tuple(tuple(a1, a2),
                                                                                 tuple(b1, b2)))));
    }

    static <A, B, C> Tuple3<Tuple3<A, A, A>, Tuple3<B, B, B>, Tuple3<C, C, C>> rotate(final Tuple3<Tuple3<A, B, C>, Tuple3<A, B, C>, Tuple3<A, B, C>> input) {
        return input.map((t1, t2, t3) ->
                                 t1.map((a1, b1, c1) ->
                                                t2.map((a2, b2, c2) ->
                                                               t3.map((a3, b3, c3) ->
                                                                              tuple(tuple(a1, a2, a3),
                                                                                    tuple(b1, b2, b3),
                                                                                    tuple(c1, c2, c3))))));
    }
    //TODO: remaining versions for Tuple4-Tuple9
}

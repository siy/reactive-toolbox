package org.reactivetoolbox.json;

import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN1;
import org.reactivetoolbox.core.functional.Functions.FN2;
import org.reactivetoolbox.core.functional.Functions.FN3;
import org.reactivetoolbox.core.functional.Functions.FN4;
import org.reactivetoolbox.core.functional.Functions.FN5;
import org.reactivetoolbox.core.functional.Functions.FN6;
import org.reactivetoolbox.core.functional.Functions.FN7;
import org.reactivetoolbox.core.functional.Functions.FN8;
import org.reactivetoolbox.core.functional.Functions.FN9;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.core.functional.Tuples.Tuple1;
import org.reactivetoolbox.core.functional.Tuples.Tuple2;
import org.reactivetoolbox.core.functional.Tuples.Tuple3;
import org.reactivetoolbox.core.functional.Tuples.Tuple4;
import org.reactivetoolbox.core.functional.Tuples.Tuple5;
import org.reactivetoolbox.core.functional.Tuples.Tuple6;
import org.reactivetoolbox.core.functional.Tuples.Tuple7;
import org.reactivetoolbox.core.functional.Tuples.Tuple8;
import org.reactivetoolbox.core.functional.Tuples.Tuple9;
import org.reactivetoolbox.value.conversion.Converter;
import org.reactivetoolbox.value.conversion.Var;

import java.util.Collection;

import static org.reactivetoolbox.core.functional.Tuples.of;
import static org.reactivetoolbox.value.conversion.Extractors.extract1;
import static org.reactivetoolbox.value.conversion.Extractors.extract2;
import static org.reactivetoolbox.value.conversion.Extractors.extract3;
import static org.reactivetoolbox.value.conversion.Extractors.extract4;
import static org.reactivetoolbox.value.conversion.Extractors.extract5;
import static org.reactivetoolbox.value.conversion.Extractors.extract6;
import static org.reactivetoolbox.value.conversion.Extractors.extract7;
import static org.reactivetoolbox.value.conversion.Extractors.extract8;
import static org.reactivetoolbox.value.conversion.Extractors.extract9;

public interface ObjectAssembler {
    static <T, T1> FB1<T, T1> fields(final Class<T> type, final Var<T1> field1) {
        return new FB1<>(field1);
    }

    static <T, T1, T2> FB2<T, T1, T2> fields(final Class<T> type, final Var<T1> field1, final Var<T2> field2) {
        return new FB2<>(field1, field2);
    }

    static <T, T1, T2, T3> FB3<T, T1, T2, T3> fields(final Class<T> type,
                                                     final Var<T1> field1,
                                                     final Var<T2> field2,
                                                     final Var<T3> field3) {
        return new FB3<>(field1, field2, field3);
    }

    static <T, T1, T2, T3, T4> FB4<T, T1, T2, T3, T4> fields(final Class<T> type, final Var<T1> field1,
                                                             final Var<T2> field2, final Var<T3> field3,
                                                             final Var<T4> field4) {
        return new FB4<>(field1, field2, field3, field4);
    }

    static <T, T1, T2, T3, T4, T5> FB5<T, T1, T2, T3, T4, T5> fields(final Class<T> type, final Var<T1> field1,
                                                                     final Var<T2> field2, final Var<T3> field3,
                                                                     final Var<T4> field4, final Var<T5> field5) {
        return new FB5<>(field1, field2, field3, field4, field5);
    }

    static <T, T1, T2, T3, T4, T5, T6> FB6<T, T1, T2, T3, T4, T5, T6> fields(final Class<T> type, final Var<T1> field1,
                                                                             final Var<T2> field2, final Var<T3> field3,
                                                                             final Var<T4> field4, final Var<T5> field5,
                                                                             final Var<T6> field6) {
        return new FB6<>(field1, field2, field3, field4, field5, field6);
    }

    static <T, T1, T2, T3, T4, T5, T6, T7> FB7<T, T1, T2, T3, T4, T5, T6, T7> fields(final Class<T> type,
                                                                                     final Var<T1> field1,
                                                                                     final Var<T2> field2,
                                                                                     final Var<T3> field3,
                                                                                     final Var<T4> field4,
                                                                                     final Var<T5> field5,
                                                                                     final Var<T6> field6,
                                                                                     final Var<T7> field7) {
        return new FB7<>(field1, field2, field3, field4, field5, field6, field7);
    }

    static <T, T1, T2, T3, T4, T5, T6, T7, T8> FB8<T, T1, T2, T3, T4, T5, T6, T7, T8> fields(final Class<T> type,
                                                                                             final Var<T1> field1,
                                                                                             final Var<T2> field2,
                                                                                             final Var<T3> field3,
                                                                                             final Var<T4> field4,
                                                                                             final Var<T5> field5,
                                                                                             final Var<T6> field6,
                                                                                             final Var<T7> field7,
                                                                                             final Var<T8> field8) {
        return new FB8<>(field1, field2, field3, field4, field5, field6, field7, field8);
    }

    static <T, T1, T2, T3, T4, T5, T6, T7, T8, T9> FB9<T, T1, T2, T3, T4, T5, T6, T7, T8, T9> fields(final Class<T> type,
                                                                                                     final Var<T1> field1,
                                                                                                     final Var<T2> field2,
                                                                                                     final Var<T3> field3,
                                                                                                     final Var<T4> field4,
                                                                                                     final Var<T5> field5,
                                                                                                     final Var<T6> field6,
                                                                                                     final Var<T7> field7,
                                                                                                     final Var<T8> field8,
                                                                                                     final Var<T9> field9) {
        return new FB9<>(field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    static <T> Var<Option<T>> field(final Class<T> type, final String name) {
        return Var.of(contextValue(type, name), name);
    }

    static <T, C extends Collection<T>> Var<Option<C>> field(final Class<C> containerType,
                                                             final Class<T> elementType,
                                                             final String name) {
        return Var.of(contextValue(containerType, elementType, name), name);
    }

    static <T> Converter<Option<T>> contextValue(final Class<T> type, final String name) {
        return (context) -> Either.success(context.first(name))
                                  .flatMap((value) -> context.valueConverter(type).apply(value));
    }

    //TODO: may be we need better approach for collection type handling
    static <T, C extends Collection<T>> Converter<Option<C>> contextValue(final Class<C> containerType,
                                                                          final Class<T> elementType,
                                                                          final String name) {
        return (context) -> Either.success(context.all(name))
                                  .flatMap((value) -> context.valueConverter(containerType, elementType).apply(value));
    }

    class FB1<T, T1> {
        private final Tuple1<Var<T1>> fields;

        private FB1(final Var<T1> fields) {
            this.fields = of(fields);
        }

        public Deserializer<T> deserializer(final FN1<T, T1> factory) {
            return (context) -> fields.map(extract1(context))
                                      .mapSuccess(v -> v.map(factory));
        }
    }

    class FB2<T, T1, T2> {
        private final Tuple2<Var<T1>, Var<T2>> fields;

        private FB2(final Var<T1> field1, final Var<T2> field2) {
            fields = of(field1, field2);
        }

        public Deserializer<T> deserializer(final FN2<T, T1, T2> factory) {
            return (context) -> fields.map(extract2(context))
                                      .mapSuccess(v -> v.map(factory));
        }
    }

    class FB3<T, T1, T2, T3> {
        private final Tuple3<Var<T1>, Var<T2>, Var<T3>> fields;

        private FB3(final Var<T1> field1, final Var<T2> field2, final Var<T3> field3) {
            fields = of(field1, field2, field3);
        }

        public Deserializer<T> deserializer(final FN3<T, T1, T2, T3> factory) {
            return (context) -> fields.map(extract3(context))
                                      .mapSuccess(v -> v.map(factory));
        }
    }

    class FB4<T, T1, T2, T3, T4> {
        private final Tuple4<Var<T1>, Var<T2>, Var<T3>, Var<T4>> fields;

        private FB4(final Var<T1> field1, final Var<T2> field2, final Var<T3> field3, final Var<T4> field4) {
            fields = of(field1, field2, field3, field4);
        }

        public Deserializer<T> deserializer(final FN4<T, T1, T2, T3, T4> factory) {
            return (context) -> fields.map(extract4(context))
                                      .mapSuccess(v -> v.map(factory));
        }
    }

    class FB5<T, T1, T2, T3, T4, T5> {
        private final Tuple5<Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>> fields;

        private FB5(final Var<T1> field1, final Var<T2> field2, final Var<T3> field3, final Var<T4> field4,
                    final Var<T5> field5) {
            fields = of(field1, field2, field3, field4, field5);
        }

        public Deserializer<T> deserializer(final FN5<T, T1, T2, T3, T4, T5> factory) {
            return (context) -> fields.map(extract5(context))
                                      .mapSuccess(v -> v.map(factory));
        }
    }

    class FB6<T, T1, T2, T3, T4, T5, T6> {
        private final Tuple6<Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>, Var<T6>> fields;

        private FB6(final Var<T1> field1, final Var<T2> field2, final Var<T3> field3, final Var<T4> field4,
                    final Var<T5> field5, final Var<T6> field6) {
            fields = of(field1, field2, field3, field4, field5, field6);
        }

        public Deserializer<T> deserializer(final FN6<T, T1, T2, T3, T4, T5, T6> factory) {
            return (context) -> fields.map(extract6(context))
                                      .mapSuccess(v -> v.map(factory));
        }
    }

    class FB7<T, T1, T2, T3, T4, T5, T6, T7> {
        private final Tuple7<Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>, Var<T6>, Var<T7>> fields;

        private FB7(final Var<T1> field1, final Var<T2> field2, final Var<T3> field3, final Var<T4> field4,
                    final Var<T5> field5, final Var<T6> field6, final Var<T7> field7) {
            fields = of(field1, field2, field3, field4, field5, field6, field7);
        }

        public Deserializer<T> deserializer(final FN7<T, T1, T2, T3, T4, T5, T6, T7> factory) {
            return (context) -> fields.map(extract7(context))
                                      .mapSuccess(v -> v.map(factory));
        }
    }

    class FB8<T, T1, T2, T3, T4, T5, T6, T7, T8> {
        private final Tuple8<Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>, Var<T6>, Var<T7>, Var<T8>> fields;

        private FB8(final Var<T1> field1, final Var<T2> field2, final Var<T3> field3, final Var<T4> field4,
                    final Var<T5> field5, final Var<T6> field6, final Var<T7> field7, final Var<T8> field8) {
            fields = of(field1, field2, field3, field4, field5, field6, field7, field8);
        }

        public Deserializer<T> deserializer(final FN8<T, T1, T2, T3, T4, T5, T6, T7, T8> factory) {
            return (context) -> fields.map(extract8(context))
                                      .mapSuccess(v -> v.map(factory));
        }
    }

    class FB9<T, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
        private final Tuple9<Var<T1>, Var<T2>, Var<T3>, Var<T4>, Var<T5>, Var<T6>, Var<T7>, Var<T8>, Var<T9>> fields;

        private FB9(final Var<T1> field1, final Var<T2> field2, final Var<T3> field3, final Var<T4> field4,
                    final Var<T5> field5, final Var<T6> field6, final Var<T7> field7, final Var<T8> field8,
                    final Var<T9> field9) {
            fields = of(field1, field2, field3, field4, field5, field6, field7, field8, field9);
        }

        public Deserializer<T> deserializer(final FN9<T, T1, T2, T3, T4, T5, T6, T7, T8, T9> factory) {
            return (context) -> fields.map(extract9(context))
                                      .mapSuccess(v -> v.map(factory));
        }
    }
}

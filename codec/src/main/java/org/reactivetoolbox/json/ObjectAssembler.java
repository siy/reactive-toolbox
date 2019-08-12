package org.reactivetoolbox.json;

import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.core.functional.Tuples;
import org.reactivetoolbox.core.functional.Tuples.Tuple1;
import org.reactivetoolbox.core.functional.Tuples.Tuple2;
import org.reactivetoolbox.value.conversion.Converter;
import org.reactivetoolbox.value.conversion.Var;

import java.util.Collection;

public interface ObjectAssembler {
    static <T, T1> FB1<T, T1> fields(final Class<T> type, final Var<T1> field1) {
        return new  FB1<>(field1);
    }

    static <T, T1, T2> FB2<T, T1, T2> fields(final Class<T> type, final Var<T1> field1, final Var<T2> field2) {
        return new FB2<>(field1, field2);
    }

    static <T> Var<Option<T>> field(final Class<T> type, final String name) {
        return Var.of(contextValue(type, name), name);
    }

    static <T, C extends Collection<T>> Var<Option<C>> field(final Class<C> containerType, final Class<T> elementType, final String name) {
        return Var.of(contextValue(containerType, elementType, name), name);
    }

    static <T> Converter<Option<T>> contextValue(final Class<T> type, final String name) {
        return (context) -> Either.success(context.first(name))
                                  .flatMap((value) -> context.valueConverter(type).apply(value));
    }

    static <T, C extends Collection<T>> Converter<Option<C>> contextValue(final Class<C> containerType,
                                                                          final Class<T> elementType,
                                                                          final String name) {
        return (context) -> Either.success(context.all(name))
                                  .flatMap((value) -> context.valueConverter(containerType, elementType).apply(value));
    }

    class FB1<T, T1> {
        private final Tuple1<Var<T1>> fields;

        private FB1(final Var<T1> fields) {
            this.fields = Tuples.of(fields);
        }

        public Deserializer<T> deserializer() {
            return null;
        }
    }

    class FB2<T, T1, T2> {
        private final Tuple2<Var<T1>, Var<T2>> fields;

        private FB2(final Var<T1> field1, final Var<T2> field2) {
            fields = Tuples.of(field1, field2);
        }

        public Deserializer<T> deserializer() {
            return null;
        }
    }
}

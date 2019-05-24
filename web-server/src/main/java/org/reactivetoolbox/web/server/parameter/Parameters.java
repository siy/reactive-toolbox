package org.reactivetoolbox.web.server.parameter;

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
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.parameter.conversion.Converter;
import org.reactivetoolbox.web.server.parameter.conversion.ConverterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class Parameters {
    private static final ConverterFactory FACTORY = ServiceLoader.load(ConverterFactory.class)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Unable to find suitable ConverterFactory"));

    public static <T> Parameter<T> inPath(final Class<T> type, final String name) {
        return new Parameter<>(FACTORY.get(type, name));
    }

    public static <T> Parameter<T> inQuery(final Class<T> type, final String name) {
        return new Parameter<>(FACTORY.get(type, name));
    }

    public static <T> Parameter<T> inBody(final Class<T> type) {
        return new Parameter<>(FACTORY.get(type));
    }

    public static <T> Parameter<T> internal(final Class<T> type) {
        return new Parameter<>(FACTORY.get(type));
    }

    public static class Parameter<T> {
        private final List<FN1<Either<?, T>, T>> validators = new ArrayList<>();
        private final Converter<T> converter;

        private Parameter(final Converter<T> converter) {
            this.converter = converter;
        }

        public final Either<?, T> convert(final RequestContext context) {
            return converter.convert(context).map(this::runValidators);
        }

        private Either<?, T> runValidators(final Object leftValue, final T rightValue) {
            if (leftValue != null) {
                return Either.failure(leftValue);
            }

            for (final FN1<Either<?, T>, T> validator : validators) {
                final Either<?, T> result = validator.apply(rightValue);

                if (result.isLeft()) {
                    return result;
                }
            }

            return Either.success(rightValue);
        }

        public Parameter<T> validate(final FN1<Either<?, T>, T> validator) {
            validators.add(validator);
            return this;
        }

        public <T1> Parameter<T> validate(final FN2<Either<?, T>, T, T1> validator, final T1 param1) {
            validators.add((input) -> validator.apply(input, param1));
            return this;
        }

        public <T1, T2> Parameter<T> validate(final FN3<Either<?, T>, T, T1, T2> validator, final T1 param1,
                                              final T2 param2) {
            validators.add((input) -> validator.apply(input, param1, param2));
            return this;
        }

        public <T1, T2, T3> Parameter<T> validate(final FN4<Either<?, T>, T, T1, T2, T3> validator, final T1 param1,
                                                  final T2 param2, final T3 param3) {
            validators.add((input) -> validator.apply(input, param1, param2, param3));
            return this;
        }

        public <T1, T2, T3, T4> Parameter<T> validate(final FN5<Either<?, T>, T, T1, T2, T3, T4> validator,
                                                      final T1 param1, final T2 param2, final T3 param3, final T4 param4) {
            validators.add((input) -> validator.apply(input, param1, param2, param3, param4));
            return this;
        }

        public <T1, T2, T3, T4, T5> Parameter<T> validate(final FN6<Either<?, T>, T, T1, T2, T3, T4, T5> validator,
                                                          final T1 param1, final T2 param2, final T3 param3, final T4 param4,
                                                          final T5 param5) {
            validators.add((input) -> validator.apply(input, param1, param2, param3, param4, param5));
            return this;
        }

        public <T1, T2, T3, T4, T5, T6> Parameter<T>
               validate(final FN7<Either<?, T>, T, T1, T2, T3, T4, T5, T6> validator, final T1 param1, final T2 param2,
                        final T3 param3, final T4 param4, final T5 param5, final T6 param6) {
            validators.add((input) -> validator.apply(input, param1, param2, param3, param4, param5, param6));
            return this;
        }

        public <T1, T2, T3, T4, T5, T6, T7> Parameter<T>
               validate(final FN8<Either<?, T>, T, T1, T2, T3, T4, T5, T6, T7> validator, final T1 param1,
                        final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6, final T7 param7) {
            validators.add((input) -> validator.apply(input, param1, param2, param3, param4, param5, param6, param7));
            return this;
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8> Parameter<T>
               validate(final FN9<Either<?, T>, T, T1, T2, T3, T4, T5, T6, T7, T8> validator, final T1 param1,
                        final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6, final T7 param7, final T8 param8) {
            validators.add((input) -> validator.apply(input, param1, param2, param3, param4, param5, param6, param7,
                                                      param8));
            return this;
        }
    }
}

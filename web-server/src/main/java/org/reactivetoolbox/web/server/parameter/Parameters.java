package org.reactivetoolbox.web.server.parameter;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.*;
import org.reactivetoolbox.web.server.RequestContext;
import org.reactivetoolbox.web.server.auth.AuthHeader;
import org.reactivetoolbox.web.server.auth.Authentication;
import org.reactivetoolbox.web.server.parameter.conversion.Converter;
import org.reactivetoolbox.web.server.parameter.conversion.ConverterFactory;
import org.reactivetoolbox.web.server.parameter.validation.Validator;

import java.util.ArrayList;
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

    public static <T> Parameter<T> inBody(final Class<T> type, final String name) {
        return new Parameter<>(FACTORY.get(type));
    }

    public static <T> Parameter<T> internal(final Class<T> type) {
        return new Parameter<>(FACTORY.get(type));
    }

    public static Parameter<Authentication> inAuthHeader(final AuthHeader header) {
        return new Parameter<>(FACTORY.get(Authentication.class, header.name()));
    }

    public static class Parameter<T> {
        private final Converter<T> converter;
        private Validator<T> validator = Either::success;

        private Parameter(final Converter<T> converter) {
            this.converter = converter;
        }

        public Either<? extends BaseError, T> extract(final RequestContext context) {
            return converter.apply(context).flatMap(validator::apply);
        }

        public Parameter<T> validate(final FN1<Either<? extends BaseError, T>, T> validator) {
            this.validator = validator::apply;
            return this;
        }

        public <T1> Parameter<T> validate(final FN2<Either<? extends BaseError, T>, T, T1> validator, final T1 param1) {
            this.validator = input -> validator.apply(input, param1);
            return this;
        }

        public <T1, T2> Parameter<T> validate(final FN3<Either<? extends BaseError, T>, T, T1, T2> validator, final T1 param1,
                                              final T2 param2) {
            this.validator = input -> validator.apply(input, param1, param2);
            return this;
        }

        public <T1, T2, T3> Parameter<T> validate(final FN4<Either<? extends BaseError, T>, T, T1, T2, T3> validator, final T1 param1,
                                                  final T2 param2, final T3 param3) {
            this.validator = input -> validator.apply(input, param1, param2, param3);
            return this;
        }

        public <T1, T2, T3, T4> Parameter<T> validate(final FN5<Either<? extends BaseError, T>, T, T1, T2, T3, T4> validator,
                                                      final T1 param1, final T2 param2, final T3 param3, final T4 param4) {
            this.validator = input -> validator.apply(input, param1, param2, param3, param4);
            return this;
        }

        public <T1, T2, T3, T4, T5> Parameter<T> validate(final FN6<Either<? extends BaseError, T>, T, T1, T2, T3, T4, T5> validator,
                                                          final T1 param1, final T2 param2, final T3 param3, final T4 param4,
                                                          final T5 param5) {
            this.validator = input -> validator.apply(input, param1, param2, param3, param4, param5);
            return this;
        }

        public <T1, T2, T3, T4, T5, T6> Parameter<T>
               validate(final FN7<Either<? extends BaseError, T>, T, T1, T2, T3, T4, T5, T6> validator, final T1 param1, final T2 param2,
                        final T3 param3, final T4 param4, final T5 param5, final T6 param6) {
            this.validator = input -> validator.apply(input, param1, param2, param3, param4, param5, param6);
            return this;
        }

        public <T1, T2, T3, T4, T5, T6, T7> Parameter<T>
               validate(final FN8<Either<? extends BaseError, T>, T, T1, T2, T3, T4, T5, T6, T7> validator, final T1 param1,
                        final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6, final T7 param7) {
            this.validator = input -> validator.apply(input, param1, param2, param3, param4, param5, param6, param7);
            return this;
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8> Parameter<T>
               validate(final FN9<Either<? extends BaseError, T>, T, T1, T2, T3, T4, T5, T6, T7, T8> validator, final T1 param1,
                        final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6, final T7 param7, final T8 param8) {
            this.validator = input -> validator.apply(input, param1, param2, param3, param4, param5, param6, param7, param8);
            return this;
        }
    }
}

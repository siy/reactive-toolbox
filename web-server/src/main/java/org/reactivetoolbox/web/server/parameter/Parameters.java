package org.reactivetoolbox.web.server.parameter;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Functions.FN2;
import org.reactivetoolbox.core.functional.Functions.FN3;
import org.reactivetoolbox.core.functional.Functions.FN4;
import org.reactivetoolbox.core.functional.Functions.FN5;
import org.reactivetoolbox.core.functional.Functions.FN6;
import org.reactivetoolbox.core.functional.Functions.FN7;
import org.reactivetoolbox.core.functional.Functions.FN8;
import org.reactivetoolbox.core.functional.Functions.FN9;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.web.server.parameter.auth.AuthHeader;
import org.reactivetoolbox.web.server.parameter.auth.Authentication;
import org.reactivetoolbox.web.server.parameter.conversion.Converter;
import org.reactivetoolbox.web.server.parameter.conversion.ConverterFactory;
import org.reactivetoolbox.web.server.parameter.validation.Is.Validator;

import java.util.ServiceLoader;

public class Parameters {
    private static final ConverterFactory FACTORY = ServiceLoader.load(ConverterFactory.class)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Unable to find suitable ConverterFactory"));

    public static <T> Parameter<Option<T>> inPath(final Class<T> type, final String name) {
        return new Parameter<>(FACTORY.getParameterConverter(type, name));
    }

    public static <T> Parameter<Option<T>> inQuery(final Class<T> type, final String name) {
        return new Parameter<>(FACTORY.getParameterConverter(type, name));
    }

    public static <T> Parameter<Option<T>> inBody(final Class<T> type, final String name) {
        return new Parameter<>(FACTORY.getBodyValueConverter(type));
    }

    public static <T> Parameter<Option<T>> inContext(final Class<T> type) {
        return new Parameter<>(FACTORY.getContextConverter(type));
    }

    public static Parameter<Option<Authentication>> inAuthHeader(final AuthHeader header) {
        return new Parameter<>(FACTORY.getHeaderConverter(Authentication.class, header.name()));
    }

    public static class Parameter<T> {
        private final Converter<T> converter;

        private Parameter(final Converter<T> converter) {
            this.converter = converter;
        }

        public Converter<T> converter() {
            return converter;
        }

        public <R> Parameter<R> and(final Validator<R, T> validator) {
            return new Parameter<>((context) -> converter.apply(context).flatMap(validator::apply));
        }

        public <R, T1> Parameter<R> and(final FN2<Either<? extends BaseError, R>, T, T1> validator, final T1 param1) {
            return and(input -> validator.apply(input, param1));
        }

        public <R, T1, T2> Parameter<R> and(final FN3<Either<? extends BaseError, R>, T, T1, T2> validator, final T1 param1, final T2 param2) {
            return and(input -> validator.apply(input, param1, param2));
        }

        public <R, T1, T2, T3> Parameter<R> and(final FN4<Either<? extends BaseError, R>, T, T1, T2, T3> validator, final T1 param1, final T2 param2, final T3 param3) {
            return and(input -> validator.apply(input, param1, param2, param3));
        }

        public <R, T1, T2, T3, T4> Parameter<R> and(final FN5<Either<? extends BaseError, R>, T, T1, T2, T3, T4> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4) {
            return and(input -> validator.apply(input, param1, param2, param3, param4));
        }

        public <R, T1, T2, T3, T4, T5> Parameter<R> and(final FN6<Either<? extends BaseError, R>, T, T1, T2, T3, T4, T5> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5) {
            return and(input -> validator.apply(input, param1, param2, param3, param4, param5));
        }

        public <R, T1, T2, T3, T4, T5, T6> Parameter<R> and(final FN7<Either<? extends BaseError, R>, T, T1, T2, T3, T4, T5, T6> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6) {
            return and(input -> validator.apply(input, param1, param2, param3, param4, param5, param6));
        }

        public <R, T1, T2, T3, T4, T5, T6, T7> Parameter<R> and(final FN8<Either<? extends BaseError, R>, T, T1, T2, T3, T4, T5, T6, T7> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6, final T7 param7) {
            return and(input -> validator.apply(input, param1, param2, param3, param4, param5, param6, param7));
        }

        public <R, T1, T2, T3, T4, T5, T6, T7, T8> Parameter<R> and(final FN9<Either<? extends BaseError, R>, T, T1, T2, T3, T4, T5, T6, T7, T8> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6, final T7 param7, final T8 param8) {
            return and(input -> validator.apply(input, param1, param2, param3, param4, param5, param6, param7, param8));
        }
    }
}

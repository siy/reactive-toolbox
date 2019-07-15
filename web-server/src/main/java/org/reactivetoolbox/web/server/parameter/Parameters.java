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
import org.reactivetoolbox.web.server.auth.AuthHeader;
import org.reactivetoolbox.web.server.auth.Authentication;
import org.reactivetoolbox.web.server.parameter.conversion.Converter;
import org.reactivetoolbox.web.server.parameter.conversion.ConverterFactory;
import org.reactivetoolbox.web.server.parameter.validation.Validators;
import org.reactivetoolbox.web.server.parameter.validation.Validators.Validator;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Consumer;

public class Parameters {
    private static final ConverterFactory FACTORY = ServiceLoader.load(ConverterFactory.class)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Unable to find suitable ConverterFactory"));

    public static <T> Parameter<Optional<T>> inPath(final Class<T> type, final String name) {
        return new Parameter<>(FACTORY.get(type, name));
    }

    public static <T> Parameter<Optional<T>> inQuery(final Class<T> type, final String name) {
        return new Parameter<>(FACTORY.get(type, name));
    }

    public static <T> Parameter<Optional<T>> inBody(final Class<T> type, final String name) {
        return new Parameter<>(FACTORY.get(type));
    }

    public static <T> Parameter<Optional<T>> internal(final Class<T> type) {
        return new Parameter<>(FACTORY.get(type));
    }

    public static Parameter<Optional<Authentication>> inAuthHeader(final AuthHeader header) {
        return new Parameter<>(FACTORY.get(Authentication.class, header.name()));
    }

    public static class Parameter<T> {
        private final Converter<T> converter;

        private Parameter(final Converter<T> converter) {
            this.converter = converter;
        }

        public Converter<T> converter() {
            return converter;
        }

        public <R> Parameter<R> validate(final Validator<R, T> validator) {
            return new Parameter<>((context) -> converter.apply(context).flatMap(validator::apply));
        }

        public <R, T1> Parameter<R> validate(final FN2<Either<? extends BaseError, R>, T, T1> validator, final T1 param1) {
            return validate(input -> validator.apply(input, param1));
        }

        public <R, T1, T2> Parameter<R> validate(final FN3<Either<? extends BaseError, R>, T, T1, T2> validator, final T1 param1, final T2 param2) {
            return validate(input -> validator.apply(input, param1, param2));
        }

        public <R, T1, T2, T3> Parameter<R> validate(final FN4<Either<? extends BaseError, R>, T, T1, T2, T3> validator, final T1 param1, final T2 param2, final T3 param3) {
            return validate(input -> validator.apply(input, param1, param2, param3));
        }

        public <R, T1, T2, T3, T4> Parameter<R> validate(final FN5<Either<? extends BaseError, R>, T, T1, T2, T3, T4> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4) {
            return validate(input -> validator.apply(input, param1, param2, param3, param4));
        }

        public <R, T1, T2, T3, T4, T5> Parameter<R> validate(final FN6<Either<? extends BaseError, R>, T, T1, T2, T3, T4, T5> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5) {
            return validate(input -> validator.apply(input, param1, param2, param3, param4, param5));
        }

        public <R, T1, T2, T3, T4, T5, T6> Parameter<R> validate(final FN7<Either<? extends BaseError, R>, T, T1, T2, T3, T4, T5, T6> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6) {
            return validate(input -> validator.apply(input, param1, param2, param3, param4, param5, param6));
        }

        public <R, T1, T2, T3, T4, T5, T6, T7> Parameter<R> validate(final FN8<Either<? extends BaseError, R>, T, T1, T2, T3, T4, T5, T6, T7> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6, final T7 param7) {
            return validate(input -> validator.apply(input, param1, param2, param3, param4, param5, param6, param7));
        }

        public <R, T1, T2, T3, T4, T5, T6, T7, T8> Parameter<R> validate(final FN9<Either<? extends BaseError, R>, T, T1, T2, T3, T4, T5, T6, T7, T8> validator, final T1 param1, final T2 param2, final T3 param3, final T4 param4, final T5 param5, final T6 param6, final T7 param7, final T8 param8) {
            return validate(input -> validator.apply(input, param1, param2, param3, param4, param5, param6, param7, param8));
        }
    }
}

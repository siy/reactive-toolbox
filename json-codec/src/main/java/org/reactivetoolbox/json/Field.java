package org.reactivetoolbox.json;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.web.server.parameter.validation.Validator;

public class Field<T> {
    private final String name;
    private final Class<?> type;
    private final Validator<T, ?> validator;

    private Field(final String name,
                  final Class<?> type,
                  final Validator<T, ?> validator) {
        this.name = name;
        this.type = type;
        this.validator = validator;
    }

    @SuppressWarnings("unchecked")
    public static <T> Field<Option<T>> field(final String name, final Class<T> type) {
        return new Field<Option<T>>(name, type, Field::defaultValid);
    }

    public <R, T> Field<R> and(final Validator<R, T> validator) {
        return new Field<>(name, type, validator);
    }

    private static <T> Either<? extends BaseError, Option<T>> defaultValid(final Option<T> value) {
        return Either.success(value);
    }
}

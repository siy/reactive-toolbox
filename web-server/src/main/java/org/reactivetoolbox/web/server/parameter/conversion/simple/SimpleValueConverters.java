package org.reactivetoolbox.web.server.parameter.conversion.simple;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Option;

import java.util.UUID;

/**
 * Set of parsing routines for some base types
 */
public class SimpleValueConverters {
    private SimpleValueConverters() {}

    public static Either<? extends BaseError, Option<String>> toString(final Option<String> value) {
        return Either.success(value);
    }

    public static Either<? extends BaseError, Option<Integer>> toInteger(final Option<String> value) {
        return value.map(SimpleValueConverters::integerValueOf)
                    .otherwise(Either.success(Option.empty()));
    }

    public static Either<? extends BaseError, Option<Long>> toLong(final Option<String> value) {
        return value.map(SimpleValueConverters::longValueOf)
                    .otherwise(Either.success(Option.empty()));
    }

    public static Either<? extends BaseError, Option<Double>> toDouble(final Option<String> value) {
        return value.map(SimpleValueConverters::doubleValueOf)
                    .otherwise(Either.success(Option.empty()));
    }

    public static Either<? extends BaseError, Option<UUID>> toUUID(final Option<String> value) {
        return value.map(SimpleValueConverters::uuidValueOf)
                    .otherwise(Either.success(Option.empty()));
    }

    private static Either<? extends BaseError, Option<UUID>> uuidValueOf(final String s) {
        try {
            return Either.success(Option.of(UUID.fromString(s)));
        } catch (final IllegalArgumentException e) {
            return Either.failure(ConversionError.NOT_A_VALID_UUID);
        }
    }

    private static Either<? extends BaseError, Option<Integer>> integerValueOf(final String s) {
        try {
            return Either.success(Option.of(Integer.parseInt(s)));
        } catch (final NumberFormatException e) {
            return Either.failure(ConversionError.NOT_AN_INTEGER_NUMBER);
        }
    }

    private static Either<? extends BaseError, Option<Long>> longValueOf(final String s) {
        try {
            return Either.success(Option.of(Long.parseLong(s)));
        } catch (final NumberFormatException e) {
            return Either.failure(ConversionError.NOT_A_LONG_INTEGER_NUMBER);
        }
    }

    private static Either<? extends BaseError, Option<Double>> doubleValueOf(final String s) {
        try {
            return Either.success(Option.of(Double.parseDouble(s)));
        } catch (final NumberFormatException e) {
            return Either.failure(ConversionError.NOT_A_DOUBLE_NUMBER);
        }
    }
}

package org.reactivetoolbox.value.conversion.simple;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Option;

import java.util.Set;
import java.util.UUID;

import static org.reactivetoolbox.value.conversion.simple.ConversionError.NOT_AN_INTEGER_NUMBER;
import static org.reactivetoolbox.value.conversion.simple.ConversionError.NOT_A_BOOLEAN;
import static org.reactivetoolbox.value.conversion.simple.ConversionError.NOT_A_DOUBLE_NUMBER;
import static org.reactivetoolbox.value.conversion.simple.ConversionError.NOT_A_LONG_INTEGER_NUMBER;
import static org.reactivetoolbox.value.conversion.simple.ConversionError.NOT_A_VALID_UUID;

/**
 * Set of parsing routines for some base types
 */
public class CoreValueConverters {
    private CoreValueConverters() {}

    private static <T> Either<? extends BaseError, Option<T>> empty() {
        return Either.success(Option.empty());
    }

    public static Either<? extends BaseError, Option<Boolean>> toBoolean(final Option<String> value) {
        return value.map(String::toLowerCase)
                    .map(CoreValueConverters::booleanValueOf)
                    .otherwise(empty());
    }

    public static Either<? extends BaseError, Option<String>> toString(final Option<String> value) {
        return Either.success(value);
    }

    public static Either<? extends BaseError, Option<Integer>> toInteger(final Option<String> value) {
        return value.map(CoreValueConverters::integerValueOf)
                    .otherwise(empty());
    }

    public static Either<? extends BaseError, Option<Long>> toLong(final Option<String> value) {
        return value.map(CoreValueConverters::longValueOf)
                    .otherwise(empty());
    }

    public static Either<? extends BaseError, Option<Double>> toDouble(final Option<String> value) {
        return value.map(CoreValueConverters::doubleValueOf)
                    .otherwise(empty());
    }

    public static Either<? extends BaseError, Option<UUID>> toUUID(final Option<String> value) {
        return value.map(CoreValueConverters::uuidValueOf)
                    .otherwise(empty());
    }

    private static final Set<String> TRUE_VALUES = Set.of("true", "yes", "1");
    private static final Set<String> FALSE_VALUES = Set.of("false", "no", "0");

    private static Either<? extends BaseError, Option<Boolean>> booleanValueOf(final String value) {
        return Option.of(TRUE_VALUES.contains(value))
                     .or(() -> Option.of(FALSE_VALUES.contains(value)))
                     .map(v -> Either.<ConversionError, Option<Boolean>>success(Option.of(v)))
                     .otherwise(NOT_A_BOOLEAN.asFailure());
    }

    private static Either<? extends BaseError, Option<UUID>> uuidValueOf(final String value) {
        try {
            return Either.success(Option.of(UUID.fromString(value)));
        } catch (final IllegalArgumentException e) {
            return NOT_A_VALID_UUID.asFailure();
        }
    }

    private static Either<? extends BaseError, Option<Integer>> integerValueOf(final String value) {
        try {
            return Either.success(Option.of(Integer.parseInt(value)));
        } catch (final NumberFormatException e) {
            return NOT_AN_INTEGER_NUMBER.asFailure();
        }
    }

    private static Either<? extends BaseError, Option<Long>> longValueOf(final String s) {
        try {
            return Either.success(Option.of(Long.parseLong(s)));
        } catch (final NumberFormatException e) {
            return NOT_A_LONG_INTEGER_NUMBER.asFailure();
        }
    }

    private static Either<? extends BaseError, Option<Double>> doubleValueOf(final String value) {
        try {
            return Either.success(Option.of(Double.parseDouble(value)));
        } catch (final NumberFormatException e) {
            return NOT_A_DOUBLE_NUMBER.asFailure();
        }
    }
}

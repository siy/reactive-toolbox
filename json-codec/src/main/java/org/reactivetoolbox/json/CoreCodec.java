package org.reactivetoolbox.json;

import org.reactivetoolbox.core.async.BaseError;
import org.reactivetoolbox.core.functional.Either;
import org.reactivetoolbox.core.functional.Option;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public class CoreCodec {
    public static Either<? extends BaseError, String> stringEncoder(final String value) {
        return null;
    }

    public static Either<? extends BaseError, Option<String>> stringDecoder(final JsonObject jsonObject) {
        return null;
    }

    public static Either<? extends BaseError, String> booleanEncoder(final Boolean value) {
        return null;
    }

    public static Either<? extends BaseError, Option<Boolean>> booleanDecoder(final JsonObject jsonObject) {
        return null;
    }

    public static Either<? extends BaseError, String> integerEncoder(final Integer value) {
        return null;
    }

    public static Either<? extends BaseError, Option<Integer>> integerDecoder(final JsonObject jsonObject) {
        return null;
    }

    public static Either<? extends BaseError, String> longEncoder(final Long value) {
        return null;
    }

    public static Either<? extends BaseError, Option<Long>> longDecoder(final JsonObject jsonObject) {
        return null;
    }

    public static Either<? extends BaseError, String> floatEncoder(final Float value) {
        return null;
    }

    public static Either<? extends BaseError, Option<Float>> floatDecoder(final JsonObject jsonObject) {
        return null;
    }

    public static Either<? extends BaseError, String> doubleEncoder(final Double value) {
        return null;
    }

    public static Either<? extends BaseError, Option<Double>> doubleDecoder(final JsonObject jsonObject) {
        return null;
    }

    public static Either<? extends BaseError, String> bigDecimalEncoder(final BigDecimal value) {
        return null;
    }

    public static Either<? extends BaseError, Option<BigDecimal>> bigDecimalDecoder(final JsonObject jsonObject) {
        return null;
    }

    public static Either<? extends BaseError, String> uuidEncoder(final UUID value) {
        return null;
    }

    public static Either<? extends BaseError, Option<UUID>> uuidDecoder(final JsonObject jsonObject) {
        return null;
    }

    public static Either<? extends BaseError, String> localDateTimeEncoder(final LocalDateTime value) {
        return null;
    }

    public static Either<? extends BaseError, Option<LocalDateTime>> localDateTimeDecoder(final JsonObject jsonObject) {
        return null;
    }

    public static Either<? extends BaseError, String> zonedDateTimeEncoder(final ZonedDateTime value) {
        return null;
    }

    public static Either<? extends BaseError, Option<ZonedDateTime>> zonedDateTimeDecoder(final JsonObject jsonObject) {
        return null;
    }

    public static Either<? extends BaseError, String> instantEncoder(final Instant value) {
        return null;
    }

    public static Either<? extends BaseError, Option<Instant>> instantDecoder(final JsonObject jsonObject) {
        return null;
    }

    public static Either<? extends BaseError, String> optionEncoder(final Option<?> value) {
        return null;
    }

    public static Either<? extends BaseError, Option<Option>> optionDecoder(final JsonObject jsonObject) {
        return Either.failure(TypeConversionError.INCORRECT_USE_OF_OPTION);
    }

    public static Either<? extends BaseError, String> optionalEncoder(final Optional<?> value) {
        return null;
    }

    public static Either<? extends BaseError, Option<Optional>> optionalDecoder(final JsonObject jsonObject) {
        return Either.failure(TypeConversionError.INCORRECT_USE_OF_OPTIONAL);
    }
}

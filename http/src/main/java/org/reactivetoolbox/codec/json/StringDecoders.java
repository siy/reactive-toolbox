package org.reactivetoolbox.codec.json;

import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.lang.support.KSUID;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

import static org.reactivetoolbox.codec.json.CodecError.error;
import static org.reactivetoolbox.core.lang.functional.Result.ok;

public interface StringDecoders {
    static <T> Result<Option<T>> answer(final T value) {
        return ok(Option.option(value));
    }

    static <T> Result<Option<T>> answerEmpty() {
        return answer(null);
    }

    static <T> Result<Option<T>> nullDecoder(String literal) {
        return "null".equals(literal) ? answerEmpty()
                                      : error("Unrecognized literal {0}", literal);
    }

    static Result<Option<Boolean>> boolDecoder(String literal) {
        return "null".equals(literal) ? answerEmpty() :
               "true".equals(literal) ? answer(true) :
               "false".equals(literal) ? answer(false) :
               error("Unknown value for boolean literal {0}", literal);
    }

    static <T extends Number> Result<Option<T>> integerDecoder(final String string,
                                                               final long minValue,
                                                               final long maxValue,
                                                               final FN1<T, Long> mapper) {
        return longDecoder(string).flatMap(v -> boundsChecker(v, minValue, maxValue))
                                  .map(o -> o.map(mapper));
    }

    static Result<Option<Long>> boundsChecker(final Option<Long> value, final long minValue, final long maxValue) {
        return value.fold($ -> Result.ok(value),
                         val -> (val >= minValue && val <= maxValue) ? answer(val)
                                                                     : error("Value is out of bounds of the given type, value={0}, min={1}, max={2}",
                                                                             val, minValue, maxValue));

    }

    static Result<Option<Long>> longDecoder(final String string) {
        try {
            return answer(Long.parseLong(string));
        } catch (final NumberFormatException e) {
            return error("Invalid integer value {0}", string);
        }
    }

    static Result<Option<Float>> floatDecoder(String string) {
        try {
            return answer(Float.valueOf(string));
        } catch (final NumberFormatException e) {
            return error("Invalid float value {0}", string);
        }
    }

    static Result<Option<Double>> doubleDecoder(String string) {
        try {
            return answer(Double.valueOf(string));
        } catch (final NumberFormatException e) {
            return error("Invalid double value {0}", string);
        }
    }

    static Result<Option<BigDecimal>> bigDecimalDecoder(String string) {
        try {
            return answer(new BigDecimal(string));
        } catch (final NumberFormatException e) {
            return error("Invalid bid decimal value {0}", string);
        }
    }

    static Result<Option<LocalDate>> localDateDecoder(String string) {
        try {
            return answer(LocalDate.parse(string));
        } catch (final DateTimeParseException e) {
            return error("Invalid local date value {0}", string);
        }
    }

    static Result<Option<LocalDateTime>> localDateTimeDecoder(String string) {
        try {
            return answer(LocalDateTime.parse(string));
        } catch (final DateTimeParseException e) {
            return error("Invalid local date value {0}", string);
        }
    }

    static Result<Option<ZonedDateTime>> zonedDateTimeDecoder(String string) {
        try {
            return answer(ZonedDateTime.parse(string));
        } catch (final DateTimeParseException e) {
            return error("Invalid local date value {0}", string);
        }
    }

    static Result<Option<UUID>> uuidDecoder(String string) {
        try {
            return answer(UUID.fromString(string));
        } catch (final IllegalArgumentException e) {
            return error("Invalid UUID string {0}", string);
        }
    }

    static Result<Option<KSUID>> ksuidDecoder(String string) {
        return KSUID.fromString(string).map(Option::option);
    }
}

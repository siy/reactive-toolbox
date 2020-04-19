package org.reactivetoolbox.codec.json;

import org.reactivetoolbox.codec.json.Token.TokenType;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.lang.support.KSUID;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.reactivetoolbox.codec.json.CodecError.error;
import static org.reactivetoolbox.codec.json.Token.TokenType.INTEGER;
import static org.reactivetoolbox.codec.json.Token.TokenType.LITERAL;
import static org.reactivetoolbox.codec.json.Token.TokenType.NUMBER;
import static org.reactivetoolbox.codec.json.Token.TokenType.STRING;

public interface TokenDecoders {
    static <T> Result<Option<T>> expect(final Token token, final TokenType type, final FN1<Result<Option<T>>, String> decoder) {
        return token.type() == type ? decoder.apply(token.text()) :
               token.type() == LITERAL ? StringDecoders.nullDecoder(token.text()) :
               error("Unexpected token {0}", token.text());
    }

    static Result<Option<Boolean>> bool(final Token input) {
        return expect(input, LITERAL, StringDecoders::boolDecoder);
    }

    static Result<Option<String>> string(final Token input) {
        return expect(input, STRING, StringDecoders::answer);
    }

    static Result<Option<Byte>> byteInt(final Token input) {
        return expect(input, INTEGER,
                      string -> StringDecoders.integerDecoder(string, Byte.MIN_VALUE, Byte.MAX_VALUE, Long::byteValue));
    }

    static Result<Option<Short>> shortInt(final Token input) {
        return expect(input, INTEGER,
                      string -> StringDecoders.integerDecoder(string, Short.MIN_VALUE, Short.MAX_VALUE, Long::shortValue));
    }

    static Result<Option<Integer>> regularInt(final Token input) {
        return expect(input, INTEGER,
                      string -> StringDecoders.integerDecoder(string, Integer.MIN_VALUE, Integer.MAX_VALUE, Long::intValue));
    }

    static Result<Option<Long>> longInt(final Token input) {
        return expect(input, INTEGER,
                      string -> StringDecoders.integerDecoder(string, Long.MIN_VALUE, Long.MAX_VALUE, Long::longValue));
    }

    static Result<Option<Float>> floatNumber(final Token input) {
        return expect(input, NUMBER, StringDecoders::floatDecoder);
    }

    static Result<Option<Double>> doubleNumber(final Token input) {
        return expect(input, NUMBER, StringDecoders::doubleDecoder);
    }

    static Result<Option<BigDecimal>> bigDecimal(final Token input) {
        return expect(input, NUMBER, StringDecoders::bigDecimalDecoder);
    }

    static Result<Option<LocalDate>> localDate(final Token input) {
        return expect(input, STRING, StringDecoders::localDateDecoder);
    }

    static Result<Option<LocalDateTime>> localDateTime(final Token input) {
        return expect(input, STRING, StringDecoders::localDateTimeDecoder);
    }

    static Result<Option<ZonedDateTime>> zonedDateTime(final Token input) {
        return expect(input, STRING, StringDecoders::zonedDateTimeDecoder);
    }

    static Result<Option<UUID>> uuid(final Token input) {
        return expect(input, STRING, StringDecoders::uuidDecoder);
    }

    static Result<Option<KSUID>> ksuid(final Token input) {
        return expect(input, STRING, StringDecoders::ksuidDecoder);
    }

}

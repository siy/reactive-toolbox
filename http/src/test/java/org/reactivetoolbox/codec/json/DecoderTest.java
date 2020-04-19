package org.reactivetoolbox.codec.json;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.lang.support.KSUID;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.reactivetoolbox.codec.json.Decoder.decoder;
import static org.reactivetoolbox.core.lang.functional.Option.empty;
import static org.reactivetoolbox.core.lang.functional.Option.option;

class DecoderTest {
    @Test
    void canReadBooleanPrimitives() {
        decoder("true").read(boolean.class)
                       .onSuccess(v -> assertEquals(option(true), v))
                       .onFailure(f -> fail());
        decoder("false").read(boolean.class)
                        .onSuccess(v -> assertEquals(option(false), v))
                        .onFailure(f -> fail());
        decoder("null").read(boolean.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());

        decoder("true").read(Boolean.class)
                       .onSuccess(v -> assertEquals(option(true), v))
                       .onFailure(f -> fail());
        decoder("false").read(Boolean.class)
                        .onSuccess(v -> assertEquals(option(false), v))
                        .onFailure(f -> fail());
        decoder("null").read(Boolean.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());
    }

    @Test
    void canReadStringPrimitives() {
        decoder("\"one\"").read(String.class)
                          .onSuccess(v -> assertEquals(option("one"), v))
                          .onFailure(f -> fail());
        decoder("\"\"").read(String.class)
                       .onSuccess(v -> assertEquals(option(""), v))
                       .onFailure(f -> fail());
        decoder("null").read(String.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());
    }

    @Test
    void canReadBytePrimitives() {
        decoder("3").read(byte.class)
                    .onSuccess(v -> assertEquals(option((byte) 3), v))
                    .onFailure(f -> fail());
        decoder("null").read(byte.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());

        decoder("3").read(Byte.class)
                    .onSuccess(v -> assertEquals(option((byte) 3), v))
                    .onFailure(f -> fail());
        decoder("null").read(Byte.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());

        decoder("128").read(byte.class)
                      .onSuccess(v -> fail())
                      .onFailure(f -> assertEquals("Value is out of bounds", f.message().substring(0, 22)));
        decoder("-129").read(byte.class)
                       .onSuccess(v -> fail())
                       .onFailure(f -> assertEquals("Value is out of bounds", f.message().substring(0, 22)));
    }

    @Test
    void canReadShortPrimitives() {
        decoder("456").read(short.class)
                      .onSuccess(v -> assertEquals(option((short) 456), v))
                      .onFailure(f -> fail());
        decoder("null").read(short.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());

        decoder("789").read(Short.class)
                      .onSuccess(v -> assertEquals(option((short) 789), v))
                      .onFailure(f -> fail());
        decoder("null").read(Short.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());

        decoder("32768").read(Short.class)
                        .onSuccess(v -> fail())
                        .onFailure(f -> assertEquals("Value is out of bounds", f.message().substring(0, 22)));
        decoder("-32769").read(Short.class)
                         .onSuccess(v -> fail())
                         .onFailure(f -> assertEquals("Value is out of bounds", f.message().substring(0, 22)));
    }

    @Test
    void canReadIntPrimitives() {
        decoder("4563456").read(int.class)
                          .onSuccess(v -> assertEquals(option(4563456), v))
                          .onFailure(f -> fail());
        decoder("null").read(int.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());

        decoder("7892343").read(Integer.class)
                          .onSuccess(v -> assertEquals(option(7892343), v))
                          .onFailure(f -> fail());
        decoder("null").read(Integer.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());

        decoder("2147483648").read(Integer.class)
                             .onSuccess(v -> fail())
                             .onFailure(f -> assertEquals("Value is out of bounds", f.message().substring(0, 22)));
        decoder("-2147483649").read(Integer.class)
                              .onSuccess(v -> fail())
                              .onFailure(f -> assertEquals("Value is out of bounds", f.message().substring(0, 22)));
    }

    @Test
    void canReadLongPrimitives() {
        decoder("1234563456").read(long.class)
                             .onSuccess(v -> assertEquals(option(1234563456L), v))
                             .onFailure(f -> fail());
        decoder("null").read(long.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());

        decoder("1237892343").read(Long.class)
                             .onSuccess(v -> assertEquals(option(1237892343L), v))
                             .onFailure(f -> fail());
        decoder("null").read(Long.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());
    }

    @Test
    void canReadFloatPrimitives() {
        decoder("1234.563456").read(float.class)
                              .onSuccess(v -> assertEquals(option(1234.563456F), v))
                              .onFailure(f -> fail());
        decoder("null").read(float.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());

        decoder("1237.892343").read(Float.class)
                              .onSuccess(v -> assertEquals(option(1237.892343F), v))
                              .onFailure(f -> fail());
        decoder("null").read(Float.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());
    }

    @Test
    void canReadDoublePrimitives() {
        decoder("1234.563456e2").read(double.class)
                                .onSuccess(v -> assertEquals(option(123456.3456), v))
                                .onFailure(f -> fail());
        decoder("null").read(double.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());

        decoder("1237.892343e-2").read(Double.class)
                                 .onSuccess(v -> assertEquals(option(12.37892343), v))
                                 .onFailure(f -> fail());
        decoder("null").read(Double.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());
    }

    @Test
    void canReadBigDecimalPrimitives() {
        decoder("1237.892343e-2").read(BigDecimal.class)
                                 .onSuccess(v -> assertEquals(option(BigDecimal.valueOf(12.37892343)), v))
                                 .onFailure(f -> fail());
        decoder("null").read(BigDecimal.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());
    }

    @Test
    void canReadUUID() {
        decoder("\"e80b3c6e-37d6-406e-ac7e-9e82e5f03cc1\"").read(UUID.class)
                                                           .onSuccess(v -> assertEquals(option(UUID.fromString("e80b3c6e-37d6-406e-ac7e-9e82e5f03cc1")),
                                                                                        v))
                                                           .onFailure(f -> fail());
        decoder("null").read(UUID.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());
    }

    @Test
    void canReadKSUID() {
        KSUID id = KSUID.create();
        decoder("\"" + id.encoded() + "\"").read(KSUID.class)
                                           .onSuccess(v -> assertEquals(option(id), v))
                                           .onFailure(f -> fail());
        decoder("null").read(KSUID.class)
                       .onSuccess(v -> assertEquals(empty(), v))
                       .onFailure(f -> fail());
    }
    //TODO: test parsing dates

}
package org.reactivetoolbox.json;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.functional.Option;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonCodecTest {
    @Test
    void nullValueIsSerializedCorrectly() {
        assertEquals(Option.of("null"), JsonCodec.serialize(null));
    }

    @Test
    void stringValueIsSerializedCorrectly() {
        assertEquals(Option.of("\"null\""), JsonCodec.serialize("null"));
    }

    @Test
    void booleanValueIsSerializedCorrectly() {
        assertEquals(Option.of("true"), JsonCodec.serialize(Boolean.TRUE));
        assertEquals(Option.of("false"), JsonCodec.serialize(Boolean.FALSE));
    }

    @Test
    void integerValueIsSerializedCorrectly() {
        assertEquals(Option.of("123"), JsonCodec.serialize(123));
        assertEquals(Option.of("0"), JsonCodec.serialize(0));
        assertEquals(Option.of("-123"), JsonCodec.serialize(-123));
    }

    @Test
    void longValueIsSerializedCorrectly() {
        assertEquals(Option.of("234"), JsonCodec.serialize(234L));
        assertEquals(Option.of("0"), JsonCodec.serialize(0));
        assertEquals(Option.of("-234"), JsonCodec.serialize(-234L));
    }
    //TODO: remaining simple types test
}
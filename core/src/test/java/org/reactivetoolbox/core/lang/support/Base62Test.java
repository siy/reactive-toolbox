package org.reactivetoolbox.core.lang.support;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62Test {
    @Test
    void base10ToBase62AndBack() {
        final var number = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        final var encoded = Base62.encode(number);
        final var result = Base62.decode(encoded);

        assertArrayEquals(number, result);
    }

    @Test
    void base256ToBase62AndBack() {
        final var number = new byte[]{-1, -2, -3, -4, -5, 127, 126, 125};
        final var encoded = Base62.encode(number);
        final var result = Base62.decode(encoded);

        assertArrayEquals(number, result);
    }

    @Test
    void textEncodeAndDecode() {
        final var text = "hello world".getBytes(US_ASCII);
        final var encoded = Base62.encode(text);
        final var result = Base62.decode(encoded);

        assertArrayEquals(text, result);
    }

    @Test
    void validateLexicographicalOrder() {
        final var texts = new String[128];

        for (int i = 0; i < texts.length; i++) {
            final var encoded = Base62.encode(new byte[]{0, (byte) i});
            texts[i] = "0".repeat(2 - encoded.length) + new String(encoded, US_ASCII);
        }

        final var textsCopy = Arrays.copyOf(texts, texts.length);

        Arrays.sort(textsCopy);

        assertArrayEquals(texts, textsCopy);
    }

    @Test
    void predefinedSequencesDecodedCorrectly() {
        final byte[][] inputs = {"0uk1Hbc9dQ9pxyTqJ93IUrfhdGq".getBytes(US_ASCII),
                                 "0uk1HdCJ6hUZKDgcxhpJwUl5ZEI".getBytes(US_ASCII),
                                 "0uk1HcdvF0p8C20KtTfdRSB9XIm".getBytes(US_ASCII),
                                 "0uk1Ha7hGJ1Q9Xbnkt0yZgNwg3g".getBytes(US_ASCII)};

        final byte[][] payloads = {{(byte) 0x98,(byte) 0x50,(byte) 0xEE,(byte) 0xEC,(byte) 0x19,(byte) 0x1B,(byte) 0xF4,(byte) 0xFF,(byte) 0x26,(byte) 0xF9,(byte) 0x93,(byte) 0x15,(byte) 0xCE,(byte) 0x43,(byte) 0xB0,(byte) 0xC8},
                                   {(byte) 0xCC,(byte) 0x55,(byte) 0x07,(byte) 0x25,(byte) 0x55,(byte) 0x31,(byte) 0x6F,(byte) 0x45,(byte) 0xB8,(byte) 0xCA,(byte) 0x2D,(byte) 0x29,(byte) 0x79,(byte) 0xD3,(byte) 0xED,(byte) 0x0A},
                                   {(byte) 0xBA,(byte) 0x1C,(byte) 0x20,(byte) 0x5D,(byte) 0x61,(byte) 0x77,(byte) 0xF0,(byte) 0x99,(byte) 0x2D,(byte) 0x15,(byte) 0xEE,(byte) 0x60,(byte) 0x6A,(byte) 0xE3,(byte) 0x22,(byte) 0x38},
                                   {(byte) 0x67,(byte) 0x51,(byte) 0x7B,(byte) 0xA3,(byte) 0x09,(byte) 0xEA,(byte) 0x62,(byte) 0xAE,(byte) 0x79,(byte) 0x91,(byte) 0xB2,(byte) 0x7B,(byte) 0xB6,(byte) 0xF2,(byte) 0xFC,(byte) 0xAC}};

        final int timestamp = 107611700;

        for (int i = 0; i < inputs.length; i++) {
            final var decoded = Base62.decode(inputs[i]);
            final var buf = ByteBuffer.wrap(decoded);
            final int ts = buf.getInt();
            final var payload = new byte[16];
            buf.get(payload);

            assertEquals(timestamp, ts);
            assertArrayEquals(payloads[i], payload);
        }

    }
/*
    { "timestamp": "107611700", "payload": "9850EEEC191BF4FF26F99315CE43B0C8", "ksuid": "0uk1Hbc9dQ9pxyTqJ93IUrfhdGq"}
    { "timestamp": "107611700", "payload": "CC55072555316F45B8CA2D2979D3ED0A", "ksuid": "0uk1HdCJ6hUZKDgcxhpJwUl5ZEI"}
    { "timestamp": "107611700", "payload": "BA1C205D6177F0992D15EE606AE32238", "ksuid": "0uk1HcdvF0p8C20KtTfdRSB9XIm"}
    { "timestamp": "107611700", "payload": "67517BA309EA62AE7991B27BB6F2FCAC", "ksuid": "0uk1Ha7hGJ1Q9Xbnkt0yZgNwg3g"}
*/
}

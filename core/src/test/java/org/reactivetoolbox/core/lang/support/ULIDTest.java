/*
 * MIT License
 *
 * Copyright (c) 2016 Azamshul Azizy
 * Copyright (c) 2020 Sergiy Yevtushenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.reactivetoolbox.core.lang.support;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.lang.collection.List;

import java.util.Random;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.reactivetoolbox.core.lang.functional.Option.option;
import static org.reactivetoolbox.core.lang.support.ULIDTest.TestParam.testParam;

/**
 * Test class for {@link ULID}
 *
 * @author azam
 * @since 0.0.1
 */
class ULIDTest {

    @Test
    void testRandom() {
        final var value = ULID.random();

        assertNotNull(value, "Generated ULID must not be null");
        assertEquals(26, value.length(), "Generated ULID length must be 26");
        assertTrue(value.matches("[0123456789ABCDEFGHJKMNPQRSTVWXYZ]{26}"),
                   "Generated ULID characters must only include [0123456789ABCDEFGHJKMNPQRSTVWXYZ]");
    }

    @Test
    void testGenerateRandom() {
        final var entropy = new byte[10];
        final var random = new Random();
        random.nextBytes(entropy);
        final var value = ULID.generate(System.currentTimeMillis(), entropy);

        assertNotNull(value,
                      "Generated ULID must not be null");
        assertEquals(26, value.length(),
                     "Generated ULID length must be 26, but returned " + value.length() + " instead");
        assertTrue(value.matches("[0123456789ABCDEFGHJKMNPQRSTVWXYZ]{26}"),
                   "Generated ULID characters must only include [0123456789ABCDEFGHJKMNPQRSTVWXYZ], but returned " + value + " instead");
    }

    @Test
    void testGenerateFixedValues() {
        TEST_PARAMETERS.apply(params -> {
            boolean hasIllegalArgumentException = false;

            try {
                final var value = ULID.generate(params.timestamp, params.entropy);
                assertEquals(params.value, value,
                             "Generated ULID must be equal to \"" + params.value + "\" for " + params.reproducer
                             + " , but returned \"" + value + "\" instead");
                assertNotNull(value, "Generated ULID must not be null");
                assertEquals(26, value.length(),
                             "Generated ULID length must be 26, but returned " + value.length() + " instead");
                assertTrue(value.matches("[0123456789ABCDEFGHJKMNPQRSTVWXYZ]{26}"),
                           "Generated ULID characters must only include [0123456789ABCDEFGHJKMNPQRSTVWXYZ], but returned "
                           + value + " instead");
            } catch (IllegalArgumentException e) {
                hasIllegalArgumentException = true;
            }
            if (params.isIllegalArgument) {
                assertTrue(hasIllegalArgumentException,
                           "IllegalArgumentException is expected for " + params.reproducer);
            } else {
                assertFalse(hasIllegalArgumentException,
                            "IllegalArgumentException is not expected for " + params.reproducer);
            }
        });
    }

    @Test
    void testIsValidNegative() {
        final var invalidUlids = List.list(
                null,
                "",
                "0",
                "000000000000000000000000000",
                "-0000000000000000000000000",
                "0000000000000000000000000U",
                "0000000000000000000000000/u3042",
                "0000000000000000000000000#");

        invalidUlids.apply(ulid -> {
            assertFalse(ULID.isValid(ulid), "ULID \"" + ulid + "\" should be invalid");
        });
    }

    @Test
    void testIsValidFixedValues() {
        TEST_PARAMETERS.filter(param -> !param.isIllegalArgument)
                       .apply(params -> assertTrue(ULID.isValid(params.value),
                                                   "ULID string is valid"));
    }

    @Test
    void testGetTimestampFixedValues() {
        TEST_PARAMETERS.filter(param -> !param.isIllegalArgument)
                       .apply(params -> assertEquals(params.timestamp,
                                                     ULID.getTimestamp(params.value),
                                                     "ULID timestamp is different"));

    }

    @Test
    void testGetEntropyFixedValues() {
        TEST_PARAMETERS.filter(param -> !param.isIllegalArgument)
                       .apply(params -> assertArrayEquals(params.entropy,
                                                          ULID.getEntropy(params.value),
                                                          "ULID entropy is different"));
    }

    private static final byte[] ZERO_ENTROPY = new byte[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};

    static class TestParam {
        public final long timestamp;
        public final byte[] entropy;
        public final String value;
        public final boolean isIllegalArgument;
        public final String reproducer;

        private TestParam(final long timestamp, final byte[] entropy, final String value, final boolean isIllegalArgument) {
            this.timestamp = timestamp;
            this.entropy = entropy;
            this.value = value;
            this.isIllegalArgument = isIllegalArgument;
            this.reproducer = assembleReproducer(timestamp, entropy);
        }

        private String assembleReproducer(final long timestamp, final byte[] entropy) {
            return "ULID.generate(" + timestamp + "L," + option(entropy).map(this::bytesToString)
                                                                        .otherwise("null") + ")";
        }

        private String bytesToString(final byte[] entropy) {
            final var joiner = new StringJoiner(", ", "new byte[]{", "}");
            for (final byte val : entropy) {
                joiner.add(String.format("0x%02X", val));
            }
            return joiner.toString();
        }

        public static TestParam testParam(final long timestamp, final byte[] entropy, final String value, final boolean isIllegalArgument) {
            return new TestParam(timestamp, entropy, value, isIllegalArgument);
        }
    }

    private static final List<TestParam> TEST_PARAMETERS = List.list(
            testParam(ULID.MIN_TIME, ZERO_ENTROPY, "00000000000000000000000000", false),
            testParam(ULID.MAX_TIME, ZERO_ENTROPY, "7ZZZZZZZZZ0000000000000000", false),
            testParam(0x00000001L, ZERO_ENTROPY, "00000000010000000000000000", false),
            testParam(0x0000000fL, ZERO_ENTROPY, "000000000F0000000000000000", false),
            testParam(0x00000010L, ZERO_ENTROPY, "000000000G0000000000000000", false),
            testParam(0x00000011L, ZERO_ENTROPY, "000000000H0000000000000000", false),
            testParam(0x0000001fL, ZERO_ENTROPY, "000000000Z0000000000000000", false),
            testParam(0x00000020L, ZERO_ENTROPY, "00000000100000000000000000", false),
            testParam(0x00000021L, ZERO_ENTROPY, "00000000110000000000000000", false),
            testParam(0x0000002fL, ZERO_ENTROPY, "000000001F0000000000000000", false),
            testParam(0x00000030L, ZERO_ENTROPY, "000000001G0000000000000000", false),
            testParam(0x00000031L, ZERO_ENTROPY, "000000001H0000000000000000", false),
            testParam(0x0000003fL, ZERO_ENTROPY, "000000001Z0000000000000000", false),
            testParam(0x00000040L, ZERO_ENTROPY, "00000000200000000000000000", false),
            testParam(0x000000f0L, ZERO_ENTROPY, "000000007G0000000000000000", false),
            testParam(0x000000ffL, ZERO_ENTROPY, "000000007Z0000000000000000", false),
            testParam(0x00000100L, ZERO_ENTROPY, "00000000800000000000000000", false),
            testParam(0x00000101L, ZERO_ENTROPY, "00000000810000000000000000", false),
            testParam(0x000001ffL, ZERO_ENTROPY, "00000000FZ0000000000000000", false),
            testParam(0x00000200L, ZERO_ENTROPY, "00000000G00000000000000000", false),
            testParam(0x00000201L, ZERO_ENTROPY, "00000000G10000000000000000", false),
            testParam(0x000002ffL, ZERO_ENTROPY, "00000000QZ0000000000000000", false),
            testParam(0x00000300L, ZERO_ENTROPY, "00000000R00000000000000000", false),
            testParam(0x00000301L, ZERO_ENTROPY, "00000000R10000000000000000", false),
            testParam(0x000003ffL, ZERO_ENTROPY, "00000000ZZ0000000000000000", false),
            testParam(0x00000400L, ZERO_ENTROPY, "00000001000000000000000000", false),
            testParam(0x00000401L, ZERO_ENTROPY, "00000001010000000000000000", false),
            testParam(0x000007ffL, ZERO_ENTROPY, "00000001ZZ0000000000000000", false),
            testParam(0x00000800L, ZERO_ENTROPY, "00000002000000000000000000", false),
            testParam(0x00007fffL, ZERO_ENTROPY, "0000000ZZZ0000000000000000", false),
            testParam(ULID.MIN_TIME, new byte[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x01}, "00000000000000000000000001", false),
            testParam(ULID.MIN_TIME, new byte[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0f}, "0000000000000000000000000F", false),
            testParam(ULID.MIN_TIME, new byte[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x10}, "0000000000000000000000000G", false),
            testParam(ULID.MIN_TIME, new byte[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1f}, "0000000000000000000000000Z", false),
            testParam(ULID.MIN_TIME, new byte[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x20}, "00000000000000000000000010", false),
            testParam(ULID.MIN_TIME, new byte[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x21}, "00000000000000000000000011", false),
            testParam(ULID.MIN_TIME, new byte[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x2f}, "0000000000000000000000001F", false),
            testParam(ULID.MIN_TIME, new byte[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x30}, "0000000000000000000000001G", false),
            testParam(ULID.MIN_TIME, new byte[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x3f}, "0000000000000000000000001Z", false)
    );
}
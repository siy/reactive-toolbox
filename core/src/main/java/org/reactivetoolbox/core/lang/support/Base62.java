package org.reactivetoolbox.core.lang.support;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * A Base62 encoder/decoder.
 *
 * <p>
 * Comparing to original this version has some performance improvements and cleanups.
 * Some unnecessary functionality is removed since main use case is conversion to/from KSUID.
 * The API is switched to set of static methods, since only one alphabet is used.
 * </p>
 *
 * @author Sebastian Ruhleder, sebastian@seruco.io
 * @author Sergiy Yevtushenko
 */
public class Base62 {
    private static final int STANDARD_BASE = 256;
    private static final int TARGET_BASE = 62;
    private static final int SCALE_STD_TO_TARGET = 2;
    private static final int SCALE_TARGET_TO_STD = 1;

    private static final byte[] ALPHABET = {
            (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
            (byte) '8', (byte) '9', (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F',
            (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
            (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V',
            (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd',
            (byte) 'e', (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l',
            (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't',
            (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z'
    };

    private static final byte[] LOOKUP = new byte[256];

    static {
        for (int i = 0; i < ALPHABET.length; i++) {
            LOOKUP[ALPHABET[i]] = (byte) (i & 0xFF);
        }
    }

    /**
     * Encodes a sequence of bytes in Base62 encoding.
     *
     * @param message
     *         a byte sequence.
     *
     * @return a sequence of Base62-encoded bytes.
     */
    public static byte[] encode(final byte[] message) {
        final byte[] indices = convert(message, STANDARD_BASE, TARGET_BASE, SCALE_STD_TO_TARGET);

        return translate(indices, ALPHABET);
    }

    /**
     * Decodes a sequence of Base62-encoded bytes.
     *
     * @param encoded
     *         a sequence of Base62-encoded bytes.
     *
     * @return a byte sequence.
     */
    public static byte[] decode(final byte[] encoded) {
        final byte[] prepared = translate(encoded, LOOKUP);

        return convert(prepared, TARGET_BASE, STANDARD_BASE, SCALE_TARGET_TO_STD);
    }

    /**
     * Uses the elements of a byte array as indices to a dictionary and returns the corresponding values
     * in form of a byte array.
     */
    private static byte[] translate(final byte[] indices, final byte[] dictionary) {
        final byte[] translation = new byte[indices.length];

        for (int i = 0; i < indices.length; i++) {
            translation[i] = dictionary[indices[i]];
        }

        return translation;
    }

    /**
     * Converts a byte array from a source base to a target base using the alphabet.
     */
    private static byte[] convert(final byte[] message, final int sourceBase, final int targetBase, final int scale) {
        /**
         * This algorithm is inspired by: http://codegolf.stackexchange.com/a/21672
         */

        final int estimatedLength = message.length * scale;

        final ByteArrayOutputStream out = new ByteArrayOutputStream(estimatedLength);

        byte[] source = message;
        final byte[] buffer = new byte[message.length];
        final ByteBuffer quotient = ByteBuffer.wrap(buffer);

        while (source.length > 0) {
            quotient.rewind();

            int remainder = 0;

            for (int i = 0; i < source.length; i++) {
                final int accumulator = (source[i] & 0xFF) + remainder * sourceBase;
                final int digit = (accumulator - (accumulator % targetBase)) / targetBase;

                remainder = accumulator % targetBase;

                if (quotient.position() > 0 || digit > 0) {
                    quotient.put((byte) digit);
                }
            }

            out.write(remainder);

            source = Arrays.copyOf(quotient.array(), quotient.position());
        }

        return reverse(out.toByteArray());
    }

    private static byte[] reverse(final byte[] arr) {
        final int length = arr.length;

        final byte[] reversed = new byte[length];

        for (int i = 0; i < length; i++) {
            reversed[length - i - 1] = arr[i];
        }

        return reversed;
    }

    private static byte[] createLookupTable(final byte[] alphabet) {
        final var lookup = new byte[256];

        for (int i = 0; i < alphabet.length; i++) {
            lookup[alphabet[i]] = (byte) (i & 0xFF);
        }
        return lookup;
    }
}

package org.reactivetoolbox.core.lang.support;

import org.reactivetoolbox.core.Errors;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.lang.functional.Suppliers;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static org.reactivetoolbox.core.lang.functional.Result.ok;

public final class KSUID implements Comparable<KSUID> {
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final long EPOCH = 1_400_000_000L;
    private static final int PAYLOAD_LENGTH = 16;
    private static final int DECODED_LENGTH = PAYLOAD_LENGTH + 4;
    private static final int MAX_ENCODED_LENGTH = 27;
    private static final SecureRandom random = new SecureRandom();
    private static final Pattern validator = Pattern.compile("^[0-9a-zA-Z]+$");

    private final String representation;
    private final Supplier<Internals> decoded;

    private KSUID(final String representation) {
        this.representation = representation;
        decoded = Suppliers.memoize(this::parse);
    }

    public static Result<KSUID> fromString(final String input) {
        return validate(input).map(KSUID::new);
    }

    public static KSUID create() {
        return new KSUID(generate());
    }

    public String encoded() {
        return representation;
    }

    public ZonedDateTime dateTime() {
        return decoded.get().dateTime;
    }

    public int timestamp() {
        return decoded.get().timestamp;
    }

    public byte[] payload() {
        return decoded.get().payload;
    }

    @Override
    public String toString() {
        return "KSUID(" + representation + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof KSUID) {
            return representation.equals(((KSUID) o).representation);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return representation.hashCode();
    }

    private static Result<String> validate(final String input) {
        return (lengthValid(input) && validator.matcher(input).find())
               ? ok(input)
               : Errors.NOT_VALID("Not a valid KSUID {0}", valueOrNull(input)).asResult();
    }

    private static String valueOrNull(final String input) {
        return input == null ? "null" : input;
    }

    private static boolean lengthValid(final String input) {
        return input != null &&
               input.length() > 0 &&
               input.length() <= MAX_ENCODED_LENGTH;
    }

    private static String generate() {
        final var decoded = ByteBuffer.wrap(new byte[DECODED_LENGTH]);
        final var utc = ZonedDateTime.now(UTC).toInstant().toEpochMilli() / 1000;
        final var timestamp = (int) (utc - EPOCH);

        decoded.putInt(timestamp);
        decoded.putInt(random.nextInt());
        decoded.putInt(random.nextInt());
        decoded.putInt(random.nextInt());
        decoded.putInt(random.nextInt());

        return new String(Base62.encode(decoded.array()), StandardCharsets.US_ASCII).substring(0, MAX_ENCODED_LENGTH);
    }

    private Internals parse() {
        final var decoded = ByteBuffer.wrap(Base62.decode(representation.getBytes(StandardCharsets.US_ASCII)));
        final var timestamp = decoded.getInt();
        final var payload = new byte[PAYLOAD_LENGTH];
        decoded.get(payload);

        return new Internals(timestamp, payload);
    }

    @Override
    public int compareTo(final KSUID o) {
        return representation.compareTo(o.encoded());
    }

    private static class Internals {
        private final int timestamp;
        private final ZonedDateTime dateTime;
        private final byte[] payload;

        private Internals(final int timestamp, final byte[] payload) {
            this.timestamp = timestamp;
            dateTime = Instant.ofEpochSecond(timestamp + EPOCH).atZone(UTC);
            this.payload = payload;
        }
    }
}

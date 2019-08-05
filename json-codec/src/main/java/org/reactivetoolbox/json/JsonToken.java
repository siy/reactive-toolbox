package org.reactivetoolbox.json;

public class JsonToken {
    private final TokenType type;
    private final String value;

    private JsonToken(final TokenType type, final String value) {
        this.type = type;
        this.value = value;
    }

    public static JsonToken of(final TokenType type, final String value) {
        return new JsonToken(type, value);
    }

    public TokenType type() {
        return type;
    }
}

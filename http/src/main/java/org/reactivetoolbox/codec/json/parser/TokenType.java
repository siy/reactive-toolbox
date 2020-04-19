package org.reactivetoolbox.codec.json.parser;

import java.nio.charset.StandardCharsets;

public enum TokenType {
    PRIMITIVE(false),
    ESCAPED_STRING(false),
    OBJECT(true),
    ARRAY(true),
    STRING(false) {
        @Override
        public String asText(final byte[] js, final int offset, final int length) {
            //TODO: implement recovering escapes
            return super.asText(js, offset, length);
        }
    };

    private final boolean complex;

    TokenType(final boolean complex) {
        this.complex = complex;
    }

    public boolean isComplex() {
        return complex;
    }

    public String asText(final byte[] js, final int offset, final int length) {
        return new String(js, offset, length, StandardCharsets.UTF_8);
    }
}

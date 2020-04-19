package org.reactivetoolbox.codec.json.parser.impl;

import java.util.Arrays;

public class TokenPool {
    private final Token[] array;
    private int next = 0;

    public TokenPool(final int size) {
        array = new Token[size];
    }

    private TokenPool(final TokenPool other) {
        array = Arrays.copyOf(other.array, other.array.length * 2);
        next = other.next;
    }

    public TokenPool alloc(final TokenType type, final int start, final int end, final int tokSuper) {
        final var token = new Token(type, start, end, tokSuper);

        if (next == array.length) {
            return new TokenPool(this).alloc(type, start, end, tokSuper);
        }

        array[next++] = token;
        return this;
    }

    public TokenType increment(final int index) {
        array[index].increment();
        return array[index].type();
    }

    public int count() {
        return next;
    }

    public int last() {
        return next - 1;
    }

    public ErrorCode validate() {
        for (int i = 0; i < next; i++) {
            if (array[i].endIsNotSet()) {
                return ErrorCode.ERR;
            }
        }
        setNextSibling(0);

        return ErrorCode.OK;
    }

    private int setNextSibling(final int pos) {
        final Token token = array[pos];

        if (token.type() == TokenType.OBJECT) {
            int newPos = pos + 1;

            for (int i = 0; i < token.size(); i++) {
                //Key
                final int keyPos = newPos;
                newPos = setNextSibling(newPos);

                //Value
                newPos = setNextSibling(newPos);
                array[keyPos].nextSibling(newPos);
            }

            token.nextSibling(newPos);
            return newPos;
        } else if (token.type() == TokenType.ARRAY) {
            int newPos = pos + 1;

            for (int i = 0; i < token.size(); i++) {
                newPos = setNextSibling(newPos);
            }

            token.nextSibling(newPos);
            return newPos;
        } else {
            token.nextSibling(pos + 1);
        }
        return pos + 1;
    }

    public int getParent(final int tokenSuper) {
        if (!array[tokenSuper].type().isComplex()) {
            return array[tokenSuper].parent();
        }
        return tokenSuper;
    }

    public Token get(final int idx) {
        return array[idx];
    }

    public Token[] array() {
        return array;
    }

    public boolean isKey(final int tokenSuper) {
        return array[tokenSuper].type() == TokenType.OBJECT ||
               array[tokenSuper].type() == TokenType.STRING && array[tokenSuper].size() != 0;
    }
}

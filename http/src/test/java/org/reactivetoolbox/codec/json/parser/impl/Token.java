package org.reactivetoolbox.codec.json.parser.impl;

import java.util.StringJoiner;

public class Token {
    private final TokenType type;
    private int parent = -1;
    private int start = -1;
    private int end = -1;
    private int nextSibling;
    private int size;

    public Token(final TokenType type, final int start, final int end, final int tokSuper) {
        this.type = type;
        this.start = start;
        this.end = end;
        parent = tokSuper;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "(", ")")
                .add("" + type)
                .add("parent:" + parent)
                .add("start:" + start)
                .add("end:" + end)
                .add("size:" + size)
                .add("next:" + nextSibling)
                .toString();
    }

    public int parent() {
        return parent;
    }

    public boolean endIsNotSet() {
        return start != -1 && end == -1;
    }

    public void end(final int pos) {
        end = pos;
    }

    public void increment() {
        size++;
    }

    public TokenType type() {
        return type;
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }

    public int byteCount() {
        return end - start;
    }

    public String text(final byte[] js) {
        return type.asText(js, start, end - start);
    }

    public int nextSibling() {
        return nextSibling;
    }

    public void nextSibling(int pos) {
        nextSibling = pos;
    }

    public int size() {
        return size;
    }
}

package org.reactivetoolbox.codec.json;

import org.reactivetoolbox.core.lang.functional.Result;

import java.util.StringJoiner;

import static org.reactivetoolbox.core.lang.functional.Result.ok;

public class Token {
    public static final Result<Token> LeftBracket = ok(token(TokenType.LB, "["));
    public static final Result<Token> LeftCurlyBracket = ok(token(TokenType.LCB, "{"));
    public static final Result<Token> RightBracket = ok(token(TokenType.RB, "]"));
    public static final Result<Token> RightCurlyBracket = ok(token(TokenType.RCB, "}"));
    public static final Result<Token> Comma = ok(token(TokenType.COMMA, ","));
    public static final Result<Token> Semicolon = ok(token(TokenType.SEMICOLON, ":"));
    public static final Result<Token> EOF = ok(token(TokenType.EOF, ""));

    private final TokenType tokenType;
    private final String text;

    private Token(final TokenType tokenType, final String text) {
        this.tokenType = tokenType;
        this.text = text;
    }

    public static Token token(final TokenType tokenType, final String text) {
        return new Token(tokenType, text);
    }

    public TokenType type() {
        return tokenType;
    }

    public String text() {
        return text;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Token(", ")")
                .add(tokenType.name())
                .add("'" + text + "'")
                .toString();
    }

    public enum TokenType { LB, RB, LCB, RCB, COMMA, SEMICOLON, EOF, STRING, NUMBER, INTEGER, LITERAL}
}

package org.reactivetoolbox.codec.json;

import org.reactivetoolbox.codec.json.Token.TokenType;
import org.reactivetoolbox.core.lang.functional.Result;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.reactivetoolbox.codec.json.CodecError.error;
import static org.reactivetoolbox.codec.json.Token.token;
import static org.reactivetoolbox.core.lang.functional.Result.ok;

public class Scanner {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[-+]?[0-9]+(\\.[0-9]+)?([eE][-+]?[0-9]+)?$");
    private static final Set<CharType> VALID_IN_NUMBER = EnumSet.of(CharType.DOT, CharType.DIGIT, CharType.PLUS, CharType.MINUS);

    private final CharReader reader;

    private Scanner(final char[] input) {
        reader = new CharReader(input);
    }

    public static Scanner scanner(final String input) {
        return new Scanner(input.toCharArray());
    }

    public Result<Token> next() {
        skipWs();

        switch (reader.underCursor()) {
            case LCB:
                reader.skip();
                return Token.LeftCurlyBracket;
            case RCB:
                reader.skip();
                return Token.RightCurlyBracket;
            case LB:
                reader.skip();
                return Token.LeftBracket;
            case RB:
                reader.skip();
                return Token.RightBracket;
            case SEMICOLON:
                reader.skip();
                return Token.Semicolon;
            case COMMA:
                reader.skip();
                return Token.Comma;
            case QUOTE:
                return nextString();
            case ALPHA:
                return nextLiteral();
            case MINUS:
            case PLUS:
            case DIGIT:
                return nextNumber();

            default:
                return Token.EOF;
        }
    }

    private Result<Token> nextNumber() {
        final var text = new StringBuilder(64);

        boolean hasDot = false;

        while (true) {
            text.append(reader.current());
            reader.skip();

            final var currentType = reader.underCursor();

            if (VALID_IN_NUMBER.contains(currentType)) {
                if (currentType == CharType.DOT) {
                    hasDot = true;
                }
                continue;
            }

            if (currentType == CharType.ALPHA && reader.current() == 'e' || reader.current() == 'E') {
                continue;
            }

            final var input = text.toString();

            if (NUMBER_PATTERN.matcher(input).matches()) {
                return ok(token(hasDot ? TokenType.NUMBER : TokenType.INTEGER, input));
            } else {
                return error("Invalid number {0}", input);
            }
        }
    }

    private Result<Token> nextLiteral() {
        final var text = new StringBuilder(32);

        do {
            text.append(reader.current());
            reader.skip();
        } while (reader.underCursor() == CharType.ALPHA);

        return ok(token(TokenType.LITERAL, text.toString()));
    }

    private Result<Token> nextString() {
        final var text = new StringBuilder(256);

        //Skip leading quote
        reader.skip();

        while (true) {
            final var current = reader.underCursor();

            if (current == CharType.QUOTE) {
                break;
            }

            text.append(reader.skip());

            if (current == CharType.EOF) {
                return error("Premature EOF");
            }
        }

        //Skip trailing quote
        reader.skip();

        return ok(token(TokenType.STRING, text.toString()));
    }

    private void skipWs() {
        while (reader.isWs()) {
            reader.skip();
        }
    }

    private static class CharReader {
        private final char[] input;
        private int pos = 0;

        public CharReader(final char[] input) {
            this.input = input;
        }

        public boolean isWs() {
            return (pos < input.length) && Character.isWhitespace(input[pos]);
        }

        public char skip() {
            final char result = current();

            if (pos < input.length) {
                pos++;
            }

            return result;
        }

        public CharType underCursor() {
            if (pos >= input.length) {
                return CharType.EOF;
            }

            //TODO: potential location for optimization
            final char chr = input[pos];

            switch (chr) {
                case '[':
                    return CharType.LB;
                case ']':
                    return CharType.RB;
                case '{':
                    return CharType.LCB;
                case '}':
                    return CharType.RCB;
                case '"':
                    return CharType.QUOTE;
                case ',':
                    return CharType.COMMA;
                case ':':
                    return CharType.SEMICOLON;
                case '.':
                    return CharType.DOT;
                case '+':
                    return CharType.PLUS;
                case '-':
                    return CharType.MINUS;
            }

            if (Character.isDigit(chr)) {
                return CharType.DIGIT;
            }

            if (Character.isWhitespace(chr)) {
                return CharType.WS;
            }

            return CharType.ALPHA;
        }

        public char current() {
            if (pos < input.length) {
                return input[pos];
            }
            return 0;
        }
    }
}

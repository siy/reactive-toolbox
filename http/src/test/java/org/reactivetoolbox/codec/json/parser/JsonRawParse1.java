package org.reactivetoolbox.codec.json.parser;

import org.reactivetoolbox.codec.json.parser.impl.ErrorCode;
import org.reactivetoolbox.codec.json.parser.impl.ParsedJson;
import org.reactivetoolbox.codec.json.parser.impl.TokenPool;
import org.reactivetoolbox.codec.json.parser.impl.TokenType;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class JsonRawParse1 {
    public static ParsedJson parse(final String json) {
        return new Parser(json.getBytes(StandardCharsets.UTF_8)).parse();
    }

    private static class Parser {
        private int pos;
        private int tokenSuper = -1;
        private TokenPool pool;
        private final byte[] js;

        private Parser(final byte[] js) {
            this.js = js;
            // Empirical guess for initial token pool size
            pool = new TokenPool(js.length / 16 + 2);
        }

        private ErrorCode doParse() {
            for (; pos < js.length; pos++) {
                final byte c = js[pos];
                ErrorCode err;

                switch (c) {
                    case '{':
                    case '[':
                        err = handleOpen(c);
                        break;

                    case '}':
                    case ']':
                        err = handleClose(c);
                        break;

                    case '\"':
                        err = handleString();
                        break;

                    case '-':
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                    case 't':
                    case 'f':
                    case 'n':
                        /* And they must not be keys of the object */
                        err = handlePrimitive();
                        break;

                    default:
                        return ErrorCode.ERR;

                    case '\t':
                    case '\r':
                    case '\n':
                    case ' ':
                        continue;

                    case ':':
                        tokenSuper = pool.last();
                        continue;

                    case ',':
                        if (tokenSuper != -1) {
                            tokenSuper = pool.getParent(tokenSuper);
                        }
                        continue;
                }

                if (err != ErrorCode.OK) {
                    return err;
                }
            }

            return pool.validate();
        }

        private ErrorCode handlePrimitive() {
            if (tokenSuper != -1 && pool.isKey(tokenSuper)) {
                return ErrorCode.ERR;
            }

            final int start = pos++;

            for (; pos < js.length; pos++) {
                switch (js[pos]) {
                    case '\t':
                    case '\r':
                    case '\n':
                    case ' ':
                    case ',':
                    case ']':
                    case '}':
                        pool = pool.alloc(TokenType.PRIMITIVE, start, pos, tokenSuper);
                        pos--;

                        if (tokenSuper != -1) {
                            pool.increment(tokenSuper);
                        }
                        return ErrorCode.OK;

                    default:
                        //Covers also case of js[pos] < 0, i.e. upper half of the ASCII table
                        if (js[pos] < 32) {
                            return ErrorCode.ERR;
                        }
                        break;
                }
            }
            return ErrorCode.ERR;
        }

        private ErrorCode handleClose(final byte c) {
            final TokenType type;
            type = (c == '}' ? TokenType.OBJECT : TokenType.ARRAY);

            if (pool.count() < 1) {
                return ErrorCode.ERR;
            }

            int ndx = pool.last();
            final var array = pool.array();

            for (; ; ) {
                if (array[ndx].endIsNotSet()) {
                    if (array[ndx].type() != type) {
                        return ErrorCode.ERR;
                    }
                    array[ndx].end(pos);
                    tokenSuper = array[ndx].parent();
                    break;
                }

                if (array[ndx].parent() == -1) {
                    if (array[ndx].type() != type || tokenSuper == -1) {
                        return ErrorCode.ERR;
                    }
                    break;
                }
                ndx = array[ndx].parent();
            }
            return ErrorCode.OK;
        }

        private ErrorCode handleOpen(final byte c) {
            pool = pool.alloc((c == '{' ? TokenType.OBJECT : TokenType.ARRAY), pos, -1, tokenSuper);

            if (tokenSuper != -1 && pool.increment(tokenSuper) == TokenType.OBJECT) {
                return ErrorCode.ERR;
            }

            tokenSuper = pool.last();
            return ErrorCode.OK;
        }

        private ErrorCode handleString() {
            final int start = pos++;
            boolean escaped = false;

            for (; pos < js.length; pos++) {
                final byte c = js[pos];

                if (c == '"') {
                    pool = pool.alloc(escaped ? TokenType.ESCAPED_STRING : TokenType.STRING, start + 1, pos, tokenSuper);

                    if (tokenSuper != -1) {
                        pool.increment(tokenSuper);
                    }

                    return ErrorCode.OK;
                }

                if (c == '\\' && pos + 1 < js.length) {
                    escaped = true;
                    pos++;

                    switch (js[pos]) {
                        case '\"':
                        case '/':
                        case '\\':
                        case 'b':
                        case 'f':
                        case 'r':
                        case 'n':
                        case 't':
                            break;

                        case 'u':
                            pos++;
                            for (int i = 0; i < 4 && pos < js.length; i++) {
                                if (!((js[pos] >= 48 && js[pos] <= 57) ||   /* 0-9 */
                                      (js[pos] >= 65 && js[pos] <= 70) ||   /* A-F */
                                      (js[pos] >= 97 && js[pos] <= 102))) { /* a-f */
                                    return ErrorCode.ERR;
                                }
                                pos++;
                            }
                            pos--;
                            break;
                        default:
                            return ErrorCode.ERR;
                    }
                }
            }
            return ErrorCode.ERR;
        }

        private ParsedJson parse() {
            final var result = doParse();

            if (result != ErrorCode.OK) {
                //TODO: fix it
                throw new IllegalArgumentException("Can't parse input");
            }

            return new ParsedJson(Arrays.copyOf(pool.array(), pool.count()), js);
        }
    }

    public static void main(final String[] args) {
        final String toParse = BenchData.dataLong;

        final var parsedJson = new Parser(toParse.getBytes()).parse();

        System.out.println("Count: " + parsedJson.count());

        parsedJson.dump(System.out);
        final var array = parsedJson.root();
    }
}

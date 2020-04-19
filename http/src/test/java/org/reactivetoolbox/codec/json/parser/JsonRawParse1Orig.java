package org.reactivetoolbox.codec.json.parser;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.StringJoiner;

public class JsonRawParse1Orig {
    public static ParsedJson parse(final String json) {
        return new Parser(json.getBytes(StandardCharsets.UTF_8)).parse();
    }

    public static class ParsedJson {
        private final TokenPool pool;
        private final byte[] js;

        private ParsedJson(final TokenPool pool, final byte[] js) {
            this.pool = pool;
            this.js = js;
        }

        public JsonComponent root() {
            return new ParsedJsonComplexComponent(this, 0);
        }

        public void dump(final PrintStream out) {
            dump(out, 0, 0);
        }

        private int dump(final PrintStream out, final int pos, final int indent) {
            final Token token = pool.get(pos);

            out.print("  ".repeat(indent) + token);
            if (token.type == TokenType.OBJECT) {
                out.println(" idx " + pos + " :");
                int newPos = pos + 1;

                for (int i = 0; i < token.size; i++) {
                    //Key
                    newPos = dump(out, newPos, indent + 1);
                    //Value
                    newPos = dump(out, newPos, indent + 1);
                }

                return newPos;
            } else if (token.type == TokenType.ARRAY) {
                out.println(" idx " + pos + " :");
                int newPos = pos + 1;

                for (int i = 0; i < token.size; i++) {
                    newPos = dump(out, newPos, indent + 1);
                }
                return newPos;
            } else {
                final var value = new String(js, token.start, token.end - token.start, StandardCharsets.UTF_8);
                out.println("=> |" + value + "|");
            }
            return pos + 1;
        }

        public int count() {
            return pool.count();
        }

        public interface JsonComponent {
            TokenType type();

            int size();

            String text();
        }

        public interface JsonComplexComponent extends JsonComponent {
            TokenType componentType(int idx);

            JsonComponent component(int idx);
        }

        private static class ParsedJsonComplexComponent implements JsonComplexComponent {
            private final int[] offsets;
            private final ParsedJson parsedJson;
            private final int root;
            private final Token rootToken;

            public ParsedJsonComplexComponent(final ParsedJson parsedJson, final int root) {
                this.parsedJson = parsedJson;
                this.root = root;
                rootToken = parsedJson.pool.get(root);

                offsets = new int[rootToken.size];

                int pos = root + 1;
                for (int i = 0; i < rootToken.size; i++) {
                    offsets[i] = pos;
                    pos = parsedJson.pool.array[pos].nextSibling;
                }
            }

            @Override
            public TokenType type() {
                return rootToken.type;
            }

            @Override
            public int size() {
                return offsets.length;
            }

            @Override
            public String text() {
                return rootToken.type.asText(parsedJson.js, rootToken.start, rootToken.end - rootToken.start);
            }

            @Override
            public TokenType componentType(final int idx) {
                return parsedJson.pool.array[offsets[idx]].type;
            }

            @Override
            public JsonComponent component(final int idx) {
                //TODO: finish it
                return null;
            }
        }
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
            ErrorCode err;
            TokenType type;

            for (; pos < js.length; pos++) {
                final byte c = js[pos];

                switch (c) {
                    case '{':
                    case '[':
                        pool = pool.alloc((c == '{' ? TokenType.OBJECT : TokenType.ARRAY), pos, -1, tokenSuper);

                        if (tokenSuper != -1 && pool.increment(tokenSuper) == TokenType.OBJECT) {
                            return ErrorCode.ERR_INVALID;
                        }

                        tokenSuper = pool.count() - 1;
                        break;

                    case '}':
                    case ']':
                        type = (c == '}' ? TokenType.OBJECT : TokenType.ARRAY);

                        if (pool.count() < 1) {
                            return ErrorCode.ERR_INVALID;
                        }

                        {
                            int ndx = pool.next - 1;
                            final var array = pool.array;

                            for (; ; ) {
                                if (array[ndx].endIsNotSet()) {
                                    if (array[ndx].type != type) {
                                        return ErrorCode.ERR_INVALID;
                                    }
                                    array[ndx].end = pos;
                                    tokenSuper = array[ndx].parent;
                                    break;
                                }

                                if (array[ndx].parent == -1) {
                                    if (array[ndx].type != type || tokenSuper == -1) {
                                        return ErrorCode.ERR_INVALID;
                                    }
                                    break;
                                }
                                ndx = array[ndx].parent;
                            }
                        }
                        break;

                    case '\"':
                        err = parseString();

                        if (err != ErrorCode.OK) {
                            return err;
                        }

                        if (tokenSuper != -1) {
                            pool.increment(tokenSuper);
                        }
                        break;

                    case '\t':
                    case '\r':
                    case '\n':
                    case ' ':
                        break;

                    case ':':
                        tokenSuper = pool.count() - 1;
                        break;

                    case ',':
                        if (tokenSuper != -1) {
                            tokenSuper = pool.getParent(tokenSuper);
                        }
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
                        if (tokenSuper != -1) {
                            final var array = pool.array;

                            if (array[tokenSuper].type == TokenType.OBJECT ||
                                (array[tokenSuper].type == TokenType.STRING && array[tokenSuper].size != 0)) {
                                return ErrorCode.ERR_INVALID;
                            }
                        }

                        primitive: {
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
                                        break primitive;

                                    default:
                                        //Covers also case of js[pos] < 0, i.e. upper half of the ASCII table
                                        if (js[pos] < 32) {
                                            pos = start;
                                            return ErrorCode.ERR_INVALID;
                                        }
                                        break;
                                }
                            }
                            pos = start;
                            return ErrorCode.ERR_PART;
                        }

                        if (tokenSuper != -1) {
                            pool.increment(tokenSuper);
                        }
                        break;

                    default:
                        return ErrorCode.ERR_INVALID;
                }
            }

            return pool.validate();
        }

        private ErrorCode parseString() {
            final int start = pos++;
            boolean escaped = false;

            for (; pos < js.length; pos++) {
                final byte c = js[pos];

                if (c == '"') {
                    pool = pool.alloc(escaped ? TokenType.ESCAPED_STRING : TokenType.STRING, start + 1, pos, tokenSuper);
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
                                    pos = start;
                                    return ErrorCode.ERR_INVALID;
                                }
                                pos++;
                            }
                            pos--;
                            break;
                        default:
                            pos = start;
                            return ErrorCode.ERR_INVALID;
                    }
                }
            }
            pos = start;
            return ErrorCode.ERR_PART;
        }

        private ParsedJson parse() {
            final var result = doParse();

            if (result != ErrorCode.OK) {
                return new ParsedJson(TokenPool.EMPTY, js);
            }

            return new ParsedJson(pool, js);
        }
    }

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

    private enum ErrorCode {
        OK,
        ERR_PART,
        ERR_INVALID,
    }

    private static class Token {
        private final TokenType type;
        private int parent = -1;
        private int start = -1;
        private int end = -1;
        private int nextSibling;
        private int size;

        private Token(final TokenType type, final int start, final int end, final int tokSuper) {
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
    }

    private static class TokenPool {
        private final Token[] array;
        private int next = 0;

        private static final TokenPool EMPTY = new TokenPool(0);

        private TokenPool(final int size) {
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
            return array[index].type;
        }

        public int count() {
            return next;
        }

        public ErrorCode validate() {
            for (int i = 0; i < next; i++) {
                if (array[i].endIsNotSet()) {
                    return ErrorCode.ERR_PART;
                }
            }
            setNextSibling(0);

            return ErrorCode.OK;
        }

        private int setNextSibling(final int pos) {
            final Token token = array[pos];

            if (token.type == TokenType.OBJECT) {
                int newPos = pos + 1;

                for (int i = 0; i < token.size; i++) {
                    //Key
                    final int keyPos = newPos;
                    newPos = setNextSibling(newPos);

                    //Value
                    newPos = setNextSibling(newPos);
                    array[keyPos].nextSibling = newPos;
                }

                token.nextSibling = newPos;
                return newPos;
            } else if (token.type == TokenType.ARRAY) {
                int newPos = pos + 1;

                for (int i = 0; i < token.size; i++) {
                    newPos = setNextSibling(newPos);
                }

                token.nextSibling = newPos;
                return newPos;
            } else {
                token.nextSibling = pos + 1;
            }
            return pos + 1;
        }

        public int getParent(final int tokenSuper) {
            if (!array[tokenSuper].type.isComplex()) {
                return array[tokenSuper].parent();
            }
            return tokenSuper;
        }

        public Token get(final int idx) {
            return array[idx];
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

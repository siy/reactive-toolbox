package org.reactivetoolbox.codec.json.parser;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.StringJoiner;

public class JsonRawParse2 {
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

        public static class Metric {
            private long time;
            private long start;
            private int count;

            public void start() {
                start = System.nanoTime();
                count++;
            }

            public void end() {
                time += System.nanoTime() - start;
            }

            public long time() {
                return time;
            }

            public int count() {
                return count;
            }
        }

        public static class Metrics<E extends Enum<E>> {
            public static boolean printStats = true;

            private final EnumMap<E, Metric> metrics;
            private long start;
            private long totalTime;

            public Metrics(final Class<E> type) {
                metrics = new EnumMap<>(type);
            }

            public Metric of(final E key) {
                return metrics.computeIfAbsent(key, (e) -> new Metric());
            }

            void dump() {
                if (totalTime == 0) {
                    end();
                }

                if (!printStats) {
                    return;
                }

                metrics.entrySet()
                       .stream()
                       .sorted((e1, e2) -> (int) (e2.getValue().time() - e1.getValue().time()))
                       .forEach(e -> System.out.printf("%12s : total %dns %d times, avg %dns\n",
                                                       e.getKey(),
                                                       e.getValue().time(),
                                                       e.getValue().count(),
                                                       e.getValue().time() / e.getValue().count()));
                final var time = metrics.entrySet().stream().mapToLong(e -> e.getValue().time()).sum();

                System.out.printf("Total time %d (%d, other tasks: %d)\n", totalTime, time, totalTime - time);
            }

            public Metrics<E> start() {
                start = System.nanoTime();
                return this;
            }

            public void end() {
                totalTime = System.nanoTime() - start;
            }
        }

        enum ParserDetails {
            CLOSE_PAREN, STRING, WS, COLON, COMMA, PRIMITIVE, NEXT, INCREMENT, OPEN_PAREN
        }

        private ErrorCode doParse() {
            ErrorCode err;
            TokenType type;
            final var metrics = new Metrics<>(ParserDetails.class).start();

            Arrays.stream(ParserDetails.values()).forEach(metrics::of);

            for (; pos < js.length; pos++) {
                metrics.of(ParserDetails.NEXT).start();
                final byte c = js[pos];
                metrics.of(ParserDetails.NEXT).end();

                switch (c) {
                    case '{':
                    case '[':
                        //Local safe
                        metrics.of(ParserDetails.OPEN_PAREN).start();
                        pool = pool.alloc((c == '{' ? TokenType.OBJECT : TokenType.ARRAY), pos, -1, tokenSuper);

                        if (tokenSuper != -1) {
                            metrics.of(ParserDetails.INCREMENT).start();
                            if (pool.increment(tokenSuper) == TokenType.OBJECT) {
                                return ErrorCode.ERR_INVALID;
                            }
                            metrics.of(ParserDetails.INCREMENT).end();
                        }

                        tokenSuper = pool.count() - 1;
                        metrics.of(ParserDetails.OPEN_PAREN).end();
                        break;

                    case '}':
                    case ']':
                        //Local safe
                        metrics.of(ParserDetails.CLOSE_PAREN).start();

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
                        metrics.of(ParserDetails.CLOSE_PAREN).end();
                        break;

                    case '\"':
                        //Local unsafe
                        metrics.of(ParserDetails.STRING).start();
                        err = parseString();

                        if (err != ErrorCode.OK) {
                            return err;
                        }

                        if (tokenSuper != -1) {
                            metrics.of(ParserDetails.INCREMENT).start();
                            pool.increment(tokenSuper);
                            metrics.of(ParserDetails.INCREMENT).end();
                        }
                        metrics.of(ParserDetails.STRING).end();
                        break;

                    case '\t':
                    case '\r':
                    case '\n':
                    case ' ':
                        //Local safe
                        metrics.of(ParserDetails.WS).start();
                        metrics.of(ParserDetails.WS).end();
                        break;

                    case ':':
                        //Local safe
                        metrics.of(ParserDetails.COLON).start();
                        tokenSuper = pool.count() - 1;
                        metrics.of(ParserDetails.COLON).end();
                        break;

                    case ',':
                        //Local safe
                        metrics.of(ParserDetails.COMMA).start();
                        if (tokenSuper != -1) {
                            tokenSuper = pool.getParent(tokenSuper);
                        }
                        metrics.of(ParserDetails.COMMA).end();
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
                        //Local unsafe
                        metrics.of(ParserDetails.PRIMITIVE).start();
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
                            metrics.of(ParserDetails.INCREMENT).start();
                            pool.increment(tokenSuper);
                            metrics.of(ParserDetails.INCREMENT).end();
                        }
                        metrics.of(ParserDetails.PRIMITIVE).end();
                        break;

                    default:
                        return ErrorCode.ERR_INVALID;
                }
            }

            metrics.end();

            metrics.dump();

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
        final String toParse = "[\n" +
                               "  {\n" +
                               "    \"_id\": \"5e774a6f8b46d3387a22844c\",\n" +
                               "    \"index\": 0,\n" +
                               "    \"guid\": \"83595bde-5bd5-4e65-9977-5584b9c70fa3\",\n" +
                               "    \"isActive\": false,\n" +
                               "    \"balance\": \"$3,545.34\",\n" +
                               "    \"picture\": \"http://placehold.it/32x32\",\n" +
                               "    \"age\": 29,\n" +
                               "    \"eyeColor\": \"green\",\n" +
                               "    \"name\": {\n" +
                               "      \"first\": \"Cheri\",\n" +
                               "      \"last\": \"Newman\"\n" +
                               "    },\n" +
                               "    \"company\": \"DAISU\",\n" +
                               "    \"email\": \"cheri.newman@daisu.io\",\n" +
                               "    \"phone\": \"+1 (845) 532-3497\",\n" +
                               "    \"address\": \"195 Orient Avenue, Avoca, West Virginia, 2041\",\n" +
                               "    \"about\": \"Nostrud commodo labore reprehenderit duis dolore dolor fugiat nisi irure irure anim. Aliqua exercitation labore incididunt laborum consequat mollit. Reprehenderit magna duis et in dolor nisi dolore ex velit nulla reprehenderit sunt. Anim sint aute cupidatat in consectetur veniam ipsum nostrud aute. Voluptate adipisicing in sint velit cillum cupidatat incididunt amet ullamco nisi velit elit. Fugiat fugiat eu veniam incididunt minim Lorem adipisicing duis esse. Quis exercitation ea veniam velit eiusmod tempor velit minim elit anim aliqua ex cillum.\",\n" +
                               "    \"registered\": \"Monday, September 8, 2014 4:15 AM\",\n" +
                               "    \"latitude\": \"-49.9884\",\n" +
                               "    \"longitude\": \"-125.680761\",\n" +
                               "    \"tags\": [\n" +
                               "      \"fugiat\",\n" +
                               "      \"do\",\n" +
                               "      \"deserunt\",\n" +
                               "      \"minim\",\n" +
                               "      \"cupidatat\"\n" +
                               "    ],\n" +
                               "    \"range\": [\n" +
                               "      0,\n" +
                               "      1,\n" +
                               "      2,\n" +
                               "      3,\n" +
                               "      4,\n" +
                               "      5,\n" +
                               "      6,\n" +
                               "      7,\n" +
                               "      8,\n" +
                               "      9\n" +
                               "    ],\n" +
                               "    \"friends\": [\n" +
                               "      {\n" +
                               "        \"id\": 0,\n" +
                               "        \"name\": \"Lea Williams\"\n" +
                               "      },\n" +
                               "      {\n" +
                               "        \"id\": 1,\n" +
                               "        \"name\": \"Arlene Clay\"\n" +
                               "      },\n" +
                               "      {\n" +
                               "        \"id\": 2,\n" +
                               "        \"name\": \"Tamika Sharpe\"\n" +
                               "      }\n" +
                               "    ],\n" +
                               "    \"greeting\": \"Hello, Cheri! You have 8 unread messages.\",\n" +
                               "    \"favoriteFruit\": \"banana\"\n" +
                               "  },\n" +
                               "  {\n" +
                               "    \"_id\": \"5e774a6fbd08087cdf3a1e77\",\n" +
                               "    \"index\": 1,\n" +
                               "    \"guid\": \"6e8b5c5c-35c3-4e29-bce2-86106d8fa06d\",\n" +
                               "    \"isActive\": true,\n" +
                               "    \"balance\": \"$1,008.22\",\n" +
                               "    \"picture\": \"http://placehold.it/32x32\",\n" +
                               "    \"age\": 23,\n" +
                               "    \"eyeColor\": \"brown\",\n" +
                               "    \"name\": {\n" +
                               "      \"first\": \"Watts\",\n" +
                               "      \"last\": \"Bailey\"\n" +
                               "    },\n" +
                               "    \"company\": \"ZEDALIS\",\n" +
                               "    \"email\": \"watts.bailey@zedalis.info\",\n" +
                               "    \"phone\": \"+1 (985) 450-2403\",\n" +
                               "    \"address\": \"310 Dakota Place, Sanders, Arizona, 7999\",\n" +
                               "    \"about\": \"Tempor dolore qui aute consectetur. Enim reprehenderit esse nisi tempor nulla ea quis incididunt voluptate do ex. Eu ex ullamco dolore aliqua sit ullamco ipsum ipsum laboris deserunt laboris nostrud.\",\n" +
                               "    \"registered\": \"Thursday, January 2, 2020 5:08 PM\",\n" +
                               "    \"latitude\": \"-37.312644\",\n" +
                               "    \"longitude\": \"11.163169\",\n" +
                               "    \"tags\": [\n" +
                               "      \"reprehenderit\",\n" +
                               "      \"cupidatat\",\n" +
                               "      \"minim\",\n" +
                               "      \"dolor\",\n" +
                               "      \"ut\"\n" +
                               "    ],\n" +
                               "    \"range\": [\n" +
                               "      0,\n" +
                               "      1,\n" +
                               "      2,\n" +
                               "      3,\n" +
                               "      4,\n" +
                               "      5,\n" +
                               "      6,\n" +
                               "      7,\n" +
                               "      8,\n" +
                               "      9\n" +
                               "    ],\n" +
                               "    \"friends\": [\n" +
                               "      {\n" +
                               "        \"id\": 0,\n" +
                               "        \"name\": \"Cooley Boone\"\n" +
                               "      },\n" +
                               "      {\n" +
                               "        \"id\": 1,\n" +
                               "        \"name\": \"Kara Sims\"\n" +
                               "      },\n" +
                               "      {\n" +
                               "        \"id\": 2,\n" +
                               "        \"name\": \"Marlene Hess\"\n" +
                               "      }\n" +
                               "    ],\n" +
                               "    \"greeting\": \"Hello, Watts! You have 10 unread messages.\",\n" +
                               "    \"favoriteFruit\": \"strawberry\"\n" +
                               "  },\n" +
                               "  {\n" +
                               "    \"_id\": \"5e774a6fd7de03adcfb0e8b8\",\n" +
                               "    \"index\": 2,\n" +
                               "    \"guid\": \"b0a6de28-74d9-4842-bacd-fde00629ddb1\",\n" +
                               "    \"isActive\": false,\n" +
                               "    \"balance\": \"$3,699.28\",\n" +
                               "    \"picture\": \"http://placehold.it/32x32\",\n" +
                               "    \"age\": 29,\n" +
                               "    \"eyeColor\": \"green\",\n" +
                               "    \"name\": {\n" +
                               "      \"first\": \"Melanie\",\n" +
                               "      \"last\": \"Dejesus\"\n" +
                               "    },\n" +
                               "    \"company\": \"MULTRON\",\n" +
                               "    \"email\": \"melanie.dejesus@multron.ca\",\n" +
                               "    \"phone\": \"+1 (941) 439-2713\",\n" +
                               "    \"address\": \"497 Polhemus Place, Tivoli, Alabama, 4814\",\n" +
                               "    \"about\": \"Occaecat reprehenderit ullamco  et amet enim ex sunt. Officia anim nostrud consequat ex fugiat exercitation. Culpa exercitation sint aliquip cupidatat anim excepteur. Laboris ex sit excepteur nostrud aute quis nostrud tempor Lorem voluptate amet.\",\n" +
                               "    \"registered\": \"Sunday, July 10, 2016 12:10 PM\",\n" +
                               "    \"latitude\": \"-18.951673\",\n" +
                               "    \"longitude\": \"178.39119\",\n" +
                               "    \"tags\": [\n" +
                               "      \"ullamco\",\n" +
                               "      \"ea\",\n" +
                               "      \"non\",\n" +
                               "      \"adipisicing\",\n" +
                               "      \"exercitation\"\n" +
                               "    ],\n" +
                               "    \"range\": [\n" +
                               "      0,\n" +
                               "      1,\n" +
                               "      2,\n" +
                               "      3,\n" +
                               "      4,\n" +
                               "      5,\n" +
                               "      6,\n" +
                               "      7,\n" +
                               "      8,\n" +
                               "      9\n" +
                               "    ],\n" +
                               "    \"friends\": [\n" +
                               "      {\n" +
                               "        \"id\": 0,\n" +
                               "        \"name\": \"Lynch Maddox\"\n" +
                               "      },\n" +
                               "      {\n" +
                               "        \"id\": 1,\n" +
                               "        \"name\": \"Maddox Bernard\"\n" +
                               "      },\n" +
                               "      {\n" +
                               "        \"id\": 2,\n" +
                               "        \"name\": \"Sharon Best\"\n" +
                               "      }\n" +
                               "    ],\n" +
                               "    \"greeting\": \"Hello, Melanie! You have 7 unread messages.\",\n" +
                               "    \"favoriteFruit\": \"banana\"\n" +
                               "  },\n" +
                               "  {\n" +
                               "    \"_id\": \"5e774a6f84e25446dcce32f8\",\n" +
                               "    \"index\": 3,\n" +
                               "    \"guid\": \"f495afe7-626e-4404-91f1-de41c352f9d7\",\n" +
                               "    \"isActive\": false,\n" +
                               "    \"balance\": \"$2,889.92\",\n" +
                               "    \"picture\": \"http://placehold.it/32x32\",\n" +
                               "    \"age\": 26,\n" +
                               "    \"eyeColor\": \"green\",\n" +
                               "    \"name\": {\n" +
                               "      \"first\": \"Paige\",\n" +
                               "      \"last\": \"Franco\"\n" +
                               "    },\n" +
                               "    \"company\": \"EMOLTRA\",\n" +
                               "    \"email\": \"paige.franco@emoltra.biz\",\n" +
                               "    \"phone\": \"+1 (982) 491-2193\",\n" +
                               "    \"address\": \"270 Hendrickson Place, Bowie, Arkansas, 3720\",\n" +
                               "    \"about\": \"Sit do minim amet voluptate eiusmod pariatur id incididunt velit deserunt aliquip. Labore sunt ut ad est ex et ipsum quis non. Laborum est incididunt id ad incididunt laborum sit tempor nostrud exercitation velit.\",\n" +
                               "    \"registered\": \"Sunday, June 3, 2018 7:26 AM\",\n" +
                               "    \"latitude\": \"31.230443\",\n" +
                               "    \"longitude\": \"68.506268\",\n" +
                               "    \"tags\": [\n" +
                               "      \"duis\",\n" +
                               "      \"do\",\n" +
                               "      \"aliquip\",\n" +
                               "      \"ipsum\",\n" +
                               "      \"aliquip\"\n" +
                               "    ],\n" +
                               "    \"range\": [\n" +
                               "      0,\n" +
                               "      1,\n" +
                               "      2,\n" +
                               "      3,\n" +
                               "      4,\n" +
                               "      5,\n" +
                               "      6,\n" +
                               "      7,\n" +
                               "      8,\n" +
                               "      9\n" +
                               "    ],\n" +
                               "    \"friends\": [\n" +
                               "      {\n" +
                               "        \"id\": 0,\n" +
                               "        \"name\": \"Ericka Beach\"\n" +
                               "      },\n" +
                               "      {\n" +
                               "        \"id\": 1,\n" +
                               "        \"name\": \"Rollins Mack\"\n" +
                               "      },\n" +
                               "      {\n" +
                               "        \"id\": 2,\n" +
                               "        \"name\": \"Viola Mcgowan\"\n" +
                               "      }\n" +
                               "    ],\n" +
                               "    \"greeting\": \"Hello, Paige! You have 7 unread messages.\",\n" +
                               "    \"favoriteFruit\": \"banana\"\n" +
                               "  },\n" +
                               "  {\n" +
                               "    \"_id\": \"5e774a6fd40797dc80a9311d\",\n" +
                               "    \"index\": 4,\n" +
                               "    \"guid\": \"70332b7b-cd8c-4306-9caf-ef16f6f1b03d\",\n" +
                               "    \"isActive\": true,\n" +
                               "    \"balance\": \"$3,750.04\",\n" +
                               "    \"picture\": \"http://placehold.it/32x32\",\n" +
                               "    \"age\": 40,\n" +
                               "    \"eyeColor\": \"green\",\n" +
                               "    \"name\": {\n" +
                               "      \"first\": \"Katie\",\n" +
                               "      \"last\": \"Velazquez\"\n" +
                               "    },\n" +
                               "    \"company\": \"ZAJ\",\n" +
                               "    \"email\": \"katie.velazquez@zaj.me\",\n" +
                               "    \"phone\": \"+1 (848) 579-2943\",\n" +
                               "    \"address\": \"150 Dekoven Court, Gardners, Indiana, 1720\",\n" +
                               "    \"about\": \"Minim tempor enim non commodo duis tempor laborum aliquip aliquip culpa. Eiusmod in velit occaecat mollit nulla eu tempor. Non pariatur velit ipsum officia labore. Laborum qui magna duis id adipisicing labore amet velit cupidatat dolore nulla magna est reprehenderit. Pariatur enim commodo culpa occaecat quis ullamco nostrud deserunt Lorem consectetur voluptate minim proident. Consequat ut consectetur et qui aute quis et labore est consequat laborum.\",\n" +
                               "    \"registered\": \"Monday, June 16, 2014 2:52 PM\",\n" +
                               "    \"latitude\": \"-6.580683\",\n" +
                               "    \"longitude\": \"-125.549775\",\n" +
                               "    \"tags\": [\n" +
                               "      \"quis\",\n" +
                               "      \"irure\",\n" +
                               "      \"veniam\",\n" +
                               "      \"mollit\",\n" +
                               "      \"aute\"\n" +
                               "    ],\n" +
                               "    \"range\": [\n" +
                               "      0,\n" +
                               "      1,\n" +
                               "      2,\n" +
                               "      3,\n" +
                               "      4,\n" +
                               "      5,\n" +
                               "      6,\n" +
                               "      7,\n" +
                               "      8,\n" +
                               "      9\n" +
                               "    ],\n" +
                               "    \"friends\": [\n" +
                               "      {\n" +
                               "        \"id\": 0,\n" +
                               "        \"name\": \"Kirsten Horne\"\n" +
                               "      },\n" +
                               "      {\n" +
                               "        \"id\": 1,\n" +
                               "        \"name\": \"Benson Bradley\"\n" +
                               "      },\n" +
                               "      {\n" +
                               "        \"id\": 2,\n" +
                               "        \"name\": \"Sanchez Schroeder\"\n" +
                               "      }\n" +
                               "    ],\n" +
                               "    \"greeting\": \"Hello, Katie! You have 7 unread messages.\",\n" +
                               "    \"favoriteFruit\": \"apple\"\n" +
                               "  }\n" +
                               "]";

        final var bytes = toParse.getBytes();
        System.out.println("Working...");

        final var totalRuns = 200_000;
        for (int i = 0; i < totalRuns; i++) {
            Parser.Metrics.printStats = i == (totalRuns - 1);
            new Parser(bytes).parse();
        }

//        final var parsedJson = new Parser(bytes).parse();
//
//        System.out.println("Count: " + parsedJson.count());
//        parsedJson.dump(System.out);
        //final var array = parsedJson.root();
    }
}

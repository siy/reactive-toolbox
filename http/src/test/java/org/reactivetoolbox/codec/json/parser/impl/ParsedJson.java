package org.reactivetoolbox.codec.json.parser.impl;

import java.io.PrintStream;

public class ParsedJson {
    private final Token[] array;
    private final byte[] js;

    public ParsedJson(final Token[] array, final byte[] js) {
        this.array = array;
        this.js = js;
    }

    public JsonComplexComponent root() {
        return array[0].type() == TokenType.ARRAY
               ? new JsonArray(this, 0)
               : new JsonObject(this, 0);
    }

    private int size(final int root) {
        return array[root].size();
    }

    public int count() {
        return array.length;
    }

    private TokenType type(final int root) {
        return array[root].type();
    }

    private String text(final int root) {
        return array[root].text(js);
    }

    //--------------------------------------------------------------------------------------
    // Representation
    //--------------------------------------------------------------------------------------

    public interface JsonComponent {
        TokenType type();
        String text();
    }

    public interface JsonComplexComponent extends JsonComponent {
        int size();
    }

    public static class JsonObject implements JsonComplexComponent {
        private final ParsedJson parsedJson;
        private final int root;

        //TODO: collect info about fields
        private JsonObject(final ParsedJson parsedJson, final int root) {
            this.parsedJson = parsedJson;
            this.root = root;
        }

        @Override
        public TokenType type() {
            return parsedJson.type(root);
        }

        @Override
        public String text() {
            return parsedJson.text(root);
        }

        @Override
        public int size() {
            return parsedJson.size(root);
        }
    }

    public static class JsonArray implements JsonComplexComponent {
        private final ParsedJson parsedJson;
        private final int root;

        private JsonArray(final ParsedJson parsedJson, final int root) {
            this.parsedJson = parsedJson;
            this.root = root;
        }

        @Override
        public TokenType type() {
            return parsedJson.type(root);
        }

        @Override
        public String text() {
            return parsedJson.text(root);
        }

        @Override
        public int size() {
            return parsedJson.size(root);
        }

        //TODO: convenient way to retrieve elements
    }

    // Utility

    public void dump(final PrintStream out) {
        dump(out, 0, 0);
    }

    private int dump(final PrintStream out, final int pos, final int indent) {
        final Token token = array[pos];

        out.print("  ".repeat(indent) + token);
        if (token.type() == TokenType.OBJECT) {
            out.println(" idx " + pos + " :");
            int newPos = pos + 1;

            for (int i = 0; i < token.size(); i++) {
                //Key
                newPos = dump(out, newPos, indent + 1);
                //Value
                newPos = dump(out, newPos, indent + 1);
            }

            return newPos;
        } else if (token.type() == TokenType.ARRAY) {
            out.println(" idx " + pos + " :");
            int newPos = pos + 1;

            for (int i = 0; i < token.size(); i++) {
                newPos = dump(out, newPos, indent + 1);
            }
            return newPos;
        } else {
            final var value = token.text(js);
            out.println("=> |" + value + "|");
        }
        return pos + 1;
    }
}

package org.reactivetoolbox.codec.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.reactivetoolbox.codec.json.Token.TokenType;

class ScannerTest {
    @Test
    void testParsingEmpty() {
        final var scanner = Scanner.scanner("");

        checkEof(scanner);
    }

    @Test
    void testParsingLiteral0() {
        final var scanner = Scanner.scanner("null");

        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.LITERAL, token.type()))
               .onSuccess(token -> assertEquals("null", token.text()))
               .onFailure(failure -> fail());
        checkEof(scanner);
    }

    @Test
    void testParsingLiteral1() {
        final var scanner = Scanner.scanner("true");

        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.LITERAL, token.type()))
               .onSuccess(token -> assertEquals("true", token.text()))
               .onFailure(failure -> fail());
        checkEof(scanner);
    }

    @Test
    void testParsingLiteral2() {
        final var scanner = Scanner.scanner("false");

        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.LITERAL, token.type()))
               .onSuccess(token -> assertEquals("false", token.text()))
               .onFailure(failure -> fail());
        checkEof(scanner);
    }

    @Test
    void testParsingInteger0() {
        final var scanner = Scanner.scanner("9");

        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.INTEGER, token.type()))
               .onSuccess(token -> assertEquals("9", token.text()))
               .onFailure(failure -> fail());
        checkEof(scanner);
    }

    @Test
    void testParsingInteger1() {
        final var scanner = Scanner.scanner("1324234242345");

        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.INTEGER, token.type()))
               .onSuccess(token -> assertEquals("1324234242345", token.text()))
               .onFailure(failure -> fail());
        checkEof(scanner);
    }

    @Test
    void testParsingNumber0() {
        final var scanner = Scanner.scanner("-9.0");

        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.NUMBER, token.type()))
               .onSuccess(token -> assertEquals("-9.0", token.text()))
               .onFailure(failure -> fail());
        checkEof(scanner);
    }

    @Test
    void testParsingNumber1() {
        final var scanner = Scanner.scanner("   345345345435.345345345345345  \n   ");

        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.NUMBER, token.type()))
               .onSuccess(token -> assertEquals("345345345435.345345345345345", token.text()))
               .onFailure(failure -> fail());
        checkEof(scanner);
    }

    @Test
    void testParsingNumber2() {
        final var scanner = Scanner.scanner("-9.0e1");

        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.NUMBER, token.type()))
               .onSuccess(token -> assertEquals("-9.0e1", token.text()))
               .onFailure(failure -> fail());
        checkEof(scanner);
    }

    @Test
    void testParsingNumber3() {
        final var scanner = Scanner.scanner("+9.0E+5");

        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.NUMBER, token.type()))
               .onSuccess(token -> assertEquals("+9.0E+5", token.text()))
               .onFailure(failure -> fail());
        checkEof(scanner);
    }

    @Test
    void testParsingNumber4() {
        final var scanner = Scanner.scanner("- 3");

        scanner.next()
               .onSuccess(token -> fail());
    }

    @Test
    void testParsingNumber5() {
        final var scanner = Scanner.scanner("+3.");

        scanner.next()
               .onSuccess(token -> fail());
    }

    @Test
    void testParsingNumber6() {
        final var scanner = Scanner.scanner("-5.0e");

        scanner.next()
               .onSuccess(token -> fail());
    }

    @Test
    void testParsingNumber7() {
        final var scanner = Scanner.scanner("-5.0e+");

        scanner.next()
               .onSuccess(token -> fail());
    }

    @Test
    void testParsingNumber8() {
        final var scanner = Scanner.scanner("-5.0e-1e");

        scanner.next()
               .onSuccess(token -> fail());
    }

    @Test
    void testParsingNumber9() {
        final var scanner = Scanner.scanner("-5.0e-1e3");

        scanner.next()
               .onSuccess(token -> fail());
    }

    @Test
    void testParsingString0() {
        final var scanner = Scanner.scanner("   \"\"   ");

        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.STRING, token.type()))
               .onSuccess(token -> assertEquals("", token.text()))
               .onFailure(failure -> fail());
        checkEof(scanner);
    }

    @Test
    void testParsingString1() {
        final var scanner = Scanner.scanner("\"  123  \"");

        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.STRING, token.type()))
               .onSuccess(token -> assertEquals("  123  ", token.text()))
               .onFailure(failure -> fail());
        checkEof(scanner);
    }

    @Test
    void testParsingString2() {
        final var scanner = Scanner.scanner("\"  123  ");

        scanner.next()
               .onSuccess(token -> fail());
        checkEof(scanner);
    }

    @Test
    void testParsingObject0() {
        final var scanner = Scanner.scanner("{}");

        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.LCB, token.type()))
               .onFailure(failure -> fail());
        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.RCB, token.type()))
               .onFailure(failure -> fail());
        checkEof(scanner);
    }

    @Test
    void testParsingObject1() {
        final var scanner = Scanner.scanner("  { \"one\" :     \n false }   ");

        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.LCB, token.type()))
               .onFailure(failure -> fail());
        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.STRING, token.type()))
               .onSuccess(token -> assertEquals("one", token.text()))
               .onFailure(failure -> fail());
        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.SEMICOLON, token.type()))
               .onFailure(failure -> fail());
        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.LITERAL, token.type()))
               .onSuccess(token -> assertEquals("false", token.text()))
               .onFailure(failure -> fail());
        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.RCB, token.type()))
               .onFailure(failure -> fail());
        checkEof(scanner);
    }

    private void checkEof(final Scanner scanner) {
        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.EOF, token.type()))
               .onFailure(failure -> fail());
        //EOF is always returned since then
        scanner.next()
               .onSuccess(token -> assertEquals(TokenType.EOF, token.type()))
               .onFailure(failure -> fail());
    }
}
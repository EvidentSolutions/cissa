/*
 * Copyright (c) 2011 Evident Solutions Oy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package fi.evident.cissa.parser;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Scanners;

import static org.codehaus.jparsec.Parsers.*;
import static org.codehaus.jparsec.Scanners.isChar;
import static org.codehaus.jparsec.pattern.Patterns.regex;

final class CissaLexer {
    static final Parser<?> optSpaces =
        or(Scanners.WHITESPACES, Scanners.JAVA_LINE_COMMENT, Scanners.JAVA_BLOCK_COMMENT).many_();
    static final Parser<?> colon = token(':');
    static final Parser<?> comma = token(',');
    static final Parser<?> semicolon = token(';');
    static final Parser<?> openingBrace = token('{');
    static final Parser<?> closingBrace = token('}');
    static final Parser<?> openingParen = token('(');
    static final Parser<?> closingParen = token(')');
    static final Parser<?> openingBracket = token('[');
    static final Parser<?> closingBracket = token(']');

    static final Parser<String> identifier =
        Scanners.pattern(regex("-?[a-zA-Z][-a-zA-Z0-9_]*"), "identifier").source();


    static Parser<String> variable() {
        return token(sequence(isChar('@'), identifier)).label("variable");
    }

    static <T> Parser<T> inBraces(Parser<T> p) {
        return between(openingBrace, p, closingBrace);
    }

    static <T> Parser<T> inParens(Parser<T> p) {
        return between(openingParen, p, closingParen);
    }

    static <T> Parser<T> token(Parser<T> p) {
        return p.followedBy(optSpaces);
    }

    static Parser<?> token(char c) {
        return token(isChar(c));
    }

    static Parser<String> token(String s) {
        return token(Scanners.string(s).source());
    }
}

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

import fi.evident.cissa.model.CSSColor;
import fi.evident.cissa.model.CSSValue;
import fi.evident.cissa.model.Dimension;
import fi.evident.cissa.model.DimensionUnit;
import fi.evident.cissa.template.SourceRange;
import fi.evident.cissa.template.ValueExpression;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import static java.lang.Character.isWhitespace;
import static java.lang.Math.min;

final class Lexer {
    private final String source;
    private int position = 0;

    public Lexer(String source) {
        this.source = source;
    }

    public void skipSpaces() {
        while (hasMore()) {
            if (isWhitespace(current())) {
                position += 1;
            } else if (consumeTokenIf("//")) {
                while (nextCharacterIsNot('\n'))
                    position += 1;
            } else if (consumeTokenIf("/*")) {
                while (hasMore() && !startsWith("*/"))
                    position += 1;

                consumeToken("*/");
            } else {
                break;
            }
        }
    }

    public char read() {
        char c = current();
        position += 1;
        return c;
    }

    private char current() {
        return source.charAt(position);
    }

    private boolean hasMore() {
        return position < source.length();
    }

    public boolean nextCharacterIs(char c) {
        return hasMore() && current() == c;
    }

    public boolean nextCharacterIsNot(char c) {
        return hasMore() && current() != c;
    }

    public ParseException parseError(String expected) {
        String currentSource = source.substring(position, min(source.length(), position + 30));
        return new ParseException("expected: '" + expected + "', but got: " + currentSource);
    }

    private void assume(String s) {
        if (startsWith(s))
            position += s.length();
        else
            throw parseError(s);
    }

    public boolean consumeTokenIf(String s) {
        if (startsWith(s)) {
            consumeToken(s);
            return true;
        } else
            return false;
    }
    
    public void consumeToken(String s) {
        assume(s);
        skipSpaces();
    }

    public String readString() {
        String quote = (current() == '"') ? "\"" : "'";

        StringBuilder sb = new StringBuilder();

        assume(quote);
        while (hasMore() && !startsWith(quote))
            sb.append(read());
        consumeToken(quote);

        return sb.toString();
    }

    private boolean startsWith(String s) {
        return source.regionMatches(position, s, 0, s.length());
    }

    public boolean nextCharacterIsDigit() {
        return hasMore() && isDigit(current());
    }

    private boolean nextCharacterIsHexDigit() {
        return hasMore() && "01234567890abcdefABCDEF".indexOf(current()) != -1;
    }

    private boolean nextCharacterIsLetter() {
        return hasMore() && isLetter(current());
    }

    public int savePosition() {
        return position;
    }

    public void restorePosition(int position) {
        this.position = position;
    }

    public Token<String> parseVariable() {
        int start = position;
        assume("@");
        return parseIdentifierInternal(start);
    }

    public String parseIdentifier() {
        return parseIdentifierInternal(position).getValue();
    }
    
    private Token<String> parseIdentifierInternal(int start) {
        StringBuilder sb = new StringBuilder();

        while (hasMore() && isIdentifierChar(current()))
            sb.append(read());

        SourceRange range = rangeFrom(start);

        skipSpaces();

        String name = sb.toString();
        if (name.isEmpty() || isDigit(name.charAt(0)))
            throw parseError("identifier");

        return new Token<String>(name, range);
    }

    private SourceRange rangeFrom(int start) {
        return new SourceRange(start, position, source.substring(start, position));
    }

    private static boolean isIdentifierChar(char ch) {
        return isLetter(ch) || isDigit(ch) || "-_".indexOf(ch) != -1;
    }

    public Dimension parseDimension() {
        StringBuilder num = new StringBuilder();

        while (hasMore() && "0123456789.".indexOf(current()) != -1)
            num.append(read());

        StringBuilder unitName = new StringBuilder();
        while (nextCharacterIs('%') || nextCharacterIsLetter())
            unitName.append(read());

        skipSpaces();

        DimensionUnit unit = (unitName.length() != 0) ? DimensionUnit.forName(unitName.toString()) : DimensionUnit.EMPTY;

        return Dimension.dimension(num.toString(), unit);
    }

    public CSSValue parseHexColor() {
        assume("#");
        StringBuilder sb = new StringBuilder("#");
        while (nextCharacterIsHexDigit())
            sb.append(read());
        skipSpaces();
        return CSSColor.parse(sb.toString());
    }
}

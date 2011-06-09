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

import javax.swing.text.Position;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import static java.lang.Character.isWhitespace;
import static java.lang.Math.min;

final class Lexer {
    private final SourceReader reader;

    public Lexer(String source) {
        this.reader = new SourceReader(source);
    }

    public void skipSpaces() {
        while (reader.hasMore()) {
            if (isWhitespace(reader.peek())) {
                reader.read();
            } else if (consumeTokenIf("//")) {
                consumeUntil("\n", false);
            } else if (consumeTokenIf("/*")) {
                consumeUntil("*/", true);
            } else {
                break;
            }
        }
    }

    public char read() {
        return reader.read();
    }

    public boolean nextCharacterIs(char c) {
        return reader.hasMore() && reader.peek() == c;
    }

    public boolean nextCharacterIsNot(char c) {
        return reader.hasMore() && reader.peek() != c;
    }

    public ParseException parseError(String expected) {
        // TODO: add a location of the source
        return new ParseException("expected: '" + expected);
    }

    private void assume(String s) {
        if (reader.startsWith(s))
            reader.skip(s.length());
        else
            throw parseError(s);
    }

    public boolean consumeTokenIf(String s) {
        if (reader.startsWith(s)) {
            reader.skip(s.length());
            skipSpaces();
            return true;
        } else
            return false;
    }

    public SourceRange consumeTokenWithSource(String s) {
        if (reader.startsWith(s)) {
            int start = reader.position();
            reader.skip(s.length());
            SourceRange range = reader.rangeFrom(start);
            skipSpaces();
            return range;
        } else {
            throw parseError(s);
        }
    }
    
    public void consumeToken(String s) {
        assume(s);
        skipSpaces();
    }

    private void consumeUntil(String end, boolean requireEnd) {
        while (reader.hasMore() && !reader.startsWith(end))
            reader.read();

        if (requireEnd || reader.hasMore())
            consumeToken(end);
    }

    public String readString() {
        String quote = (reader.peek() == '"') ? "\"" : "'";

        StringBuilder sb = new StringBuilder();

        assume(quote);
        while (reader.hasMore() && !reader.startsWith(quote))
            sb.append(reader.read());
        consumeToken(quote);

        return sb.toString();
    }

    public boolean nextCharacterIsDigit() {
        return reader.hasMore() && isDigit(reader.peek());
    }

    private boolean nextCharacterIsHexDigit() {
        return reader.hasMore() && "01234567890abcdefABCDEF".indexOf(reader.peek()) != -1;
    }

    private boolean nextCharacterIsLetter() {
        return reader.hasMore() && isLetter(reader.peek());
    }

    public int savePosition() {
        return reader.position();
    }

    public void restorePosition(int position) {
        reader.restorePosition(position);
    }

    public Token<String> parseVariable() {
        int start = reader.position();
        assume("@");
        return parseIdentifierInternal(start);
    }

    public String parseIdentifier() {
        return parseIdentifierInternal(reader.position()).getValue();
    }
    
    private Token<String> parseIdentifierInternal(int start) {
        StringBuilder sb = new StringBuilder();

        while (reader.hasMore() && isIdentifierChar(reader.peek()))
            sb.append(reader.read());

        SourceRange range = reader.rangeFrom(start);

        skipSpaces();

        String name = sb.toString();
        if (name.isEmpty() || isDigit(name.charAt(0)))
            throw parseError("identifier");

        return new Token<String>(name, range);
    }

    private static boolean isIdentifierChar(char ch) {
        return isLetter(ch) || isDigit(ch) || "-_".indexOf(ch) != -1;
    }

    public Dimension parseDimension() {
        StringBuilder num = new StringBuilder();

        while (reader.hasMore() && "0123456789.".indexOf(reader.peek()) != -1)
            num.append(reader.read());

        StringBuilder unitName = new StringBuilder();
        while (nextCharacterIs('%') || nextCharacterIsLetter())
            unitName.append(reader.read());

        skipSpaces();

        DimensionUnit unit = (unitName.length() != 0) ? DimensionUnit.forName(unitName.toString()) : DimensionUnit.EMPTY;

        return Dimension.dimension(num.toString(), unit);
    }

    public CSSValue parseHexColor() {
        assume("#");
        StringBuilder sb = new StringBuilder("#");
        while (nextCharacterIsHexDigit())
            sb.append(reader.read());
        skipSpaces();
        return CSSColor.parse(sb.toString());
    }
}

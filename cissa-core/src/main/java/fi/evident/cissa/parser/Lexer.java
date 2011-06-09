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

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import static java.lang.Character.isWhitespace;
import static java.lang.Math.min;

final class Lexer {
    private final SourceReader reader;

    private static final CharacterClass NOT_NEW_LINE =
            CharacterClass.NEWLINE.complement();

    private static final CharacterClass IDENTIFIER_CHAR =
            CharacterClass.or(CharacterClass.LETTER, CharacterClass.DIGIT, CharacterClass.anyOf("-_"));

    private static final CharacterClass UNIT_NAME_CHAR =
            CharacterClass.or(CharacterClass.LETTER, CharacterClass.character('%'));

    public Lexer(String source) {
        this.reader = new SourceReader(source);
    }

    public void skipSpaces() {
        while (reader.hasMore()) {
            if (reader.nextCharacterIs(CharacterClass.WHITESPACE)) {
                reader.skipWhile(CharacterClass.WHITESPACE);

            } else if (reader.startsWith("//")) {
                reader.skipWhile(NOT_NEW_LINE);

            } else if (reader.startsWith("/*")) {
                assume('/');
                assume('*');
                while (reader.hasMore() && !reader.startsWith("*/"))
                    reader.read();
                assume('*');
                assume('/');

            } else {
                break;
            }
        }
    }

    public Token<String> parseVariable() {
        int start = reader.position();
        assume('@');
        return parseIdentifierInternal(start);
    }

    public String parseIdentifier() {
        return parseIdentifierInternal(reader.position()).getValue();
    }

    private Token<String> parseIdentifierInternal(int start) {
        String name = reader.readWhile(IDENTIFIER_CHAR);

        SourceRange range = reader.rangeFrom(start);

        skipSpaces();

        if (name.isEmpty() || isDigit(name.charAt(0)))
            throw parseError("identifier");

        return new Token<String>(name, range);
    }

    public Dimension parseDimension() {
        String num = reader.readWhile(CharacterClass.DECIMAL_NUMBER_CHAR);
        String unitName = reader.readWhile(UNIT_NAME_CHAR);
        skipSpaces();

        DimensionUnit unit = unitName.isEmpty() ? DimensionUnit.EMPTY : DimensionUnit.forName(unitName);

        return Dimension.dimension(num, unit);
    }

    public CSSValue parseHexColor() {
        assume('#');
        String digits = reader.readWhile(CharacterClass.HEX_DIGIT);
        skipSpaces();

        return CSSColor.parse("#" + digits);
    }

    public String parseString() {
        char quote = reader.read();
        if (CharacterClass.QUOTE.contains(quote)) {
            String str = reader.readWhile(CharacterClass.character(quote).complement());
            assumeToken(quote);
            return str;
        } else
            throw parseError(quote);
    }

    public char read() {
        return reader.read();
    }

    public boolean nextCharacterIs(char c) {
        return reader.nextCharacterIs(c);
    }

    public boolean nextCharacterIs(CharacterClass cc) {
        return reader.nextCharacterIs(cc);
    }

    public ParseException parseError(String expected) {
        // TODO: add a location of the source
        return new ParseException("expected: '" + expected);
    }

    private ParseException parseError(char expected) {
        return parseError(String.valueOf(expected));
    }

    public boolean consumeTokenIf(char c) {
        if (reader.nextCharacterIs(c)) {
            assumeToken(c);
            return true;
        } else {
            return false;
        }
    }

    public SourceRange assumeTokenWithSource(char c) {
        int start = reader.position();
        assume(c);
        SourceRange range = reader.rangeFrom(start);
        skipSpaces();
        return range;
    }

    public void assumeToken(char c) {
        assume(c);
        skipSpaces();
    }

    private void assume(char c) {
        if (reader.read() != c)
            throw parseError(c);
    }
    
    public int savePosition() {
        return reader.position();
    }

    public void restorePosition(int position) {
        reader.restorePosition(position);
    }
}

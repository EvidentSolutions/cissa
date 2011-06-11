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

abstract class CharacterClass {

    public abstract boolean contains(char c);

    public CharacterClass complement() {
        final CharacterClass original = this;
        return new CharacterClass() {
            @Override
            public boolean contains(char c) {
                return !original.contains(c);
            }
        };
    }

    public static final CharacterClass DECIMAL_NUMBER_CHAR = anyOf("01234567890.");
    public static final CharacterClass HEX_DIGIT = anyOf("01234567890abcdefABCDEF");
    public static final CharacterClass DIGIT = anyOf("0123456789");
    public static final CharacterClass QUOTE = anyOf("\"'");
    public static final CharacterClass NEWLINE = anyOf("\n\r");

    public static final CharacterClass LETTER = new CharacterClass() {
        @Override
        public boolean contains(char c) {
            return Character.isLetter(c);
        }
    };

    public static final CharacterClass WHITESPACE = new CharacterClass() {
        @Override
        public boolean contains(char c) {
            return Character.isWhitespace(c);
        }
    };

    public static CharacterClass noneOf(String characters) {
        return anyOf(characters).complement();
    }

    public static CharacterClass anyOf(final String members) {
        return new CharacterClass() {
            @Override
            public boolean contains(char c) {
                return members.indexOf(c) != -1;
            }
        };
    }

    public static CharacterClass character(final char ch) {
        return new CharacterClass() {
            @Override
            public boolean contains(char c) {
                return c == ch;
            }
        };
    }

    public static CharacterClass or(final CharacterClass... ccs) {
        return new CharacterClass() {
            @Override
            public boolean contains(char c) {
                for (CharacterClass cc : ccs)
                    if (cc.contains(c))
                        return true;
                return false;
            }
        };
    }
}

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

import fi.evident.cissa.template.SourceRange;

final class SourceReader {

    private final String source;
    private int position = 0;

    SourceReader(String source) {
        this.source = source;
    }

    public char read() {
        char c = peek();
        position += 1;
        return c;
    }

    public char peek() {
        return source.charAt(position);
    }

    public boolean hasMore() {
        return position < source.length();
    }

    public boolean startsWith(String s) {
        return source.regionMatches(position, s, 0, s.length());
    }

    public SourceRange rangeFrom(int start) {
        return new SourceRange(start, position, source.substring(start, position));
    }

    public void skip(int length) {
        position += length;
    }

    public int position() {
        return position;
    }

    public void restorePosition(int position) {
        this.position = position;
    }
}

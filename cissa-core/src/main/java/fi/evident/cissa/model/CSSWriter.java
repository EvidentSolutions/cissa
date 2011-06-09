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

package fi.evident.cissa.model;

import fi.evident.cissa.utils.Require;

import java.util.Iterator;
import java.util.List;

final class CSSWriter {
    private final StringBuilder sb = new StringBuilder();
    private static final String hexDigits = "0123456789abcdef";

    public CSSWriter write(char c) {
        sb.append(c);
        return this;
    }
    
    public CSSWriter write(CharSequence cs) {
        Require.argumentNotNull("cs", cs);
        
        sb.append(cs);
        return this;
    }

    public CSSWriter writeSeparated(List<? extends CSSNode> list, String separator) {
        for (Iterator<? extends CSSNode> it = list.iterator(); it.hasNext(); ) {
            it.next().writeTo(this);
            if (it.hasNext())
                write(separator);
        }
        return this;
    }

    public CSSWriter writeHexByte(int b) {
        sb.append(hexDigits.charAt((b >> 4) & 15)).append(hexDigits.charAt(b & 15));
        return this;
    }

    public void writeAll(Iterable<String> ss) {
        for (String s : ss)
            write(s);
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}

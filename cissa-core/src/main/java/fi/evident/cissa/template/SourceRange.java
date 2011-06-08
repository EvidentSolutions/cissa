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

package fi.evident.cissa.template;

import com.sun.tools.corba.se.idl.toJavaPortable.StringGen;
import fi.evident.cissa.utils.Require;

import javax.xml.stream.events.EndDocument;

public final class SourceRange {

    private final int start;
    private final int end;
    private final String sourceFragment;

    public SourceRange(int start, int end, String sourceFragment) {
        Require.argumentNotNull("sourceFragment", sourceFragment);

        this.start = start;
        this.end = end;
        this.sourceFragment = sourceFragment;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getSourceFragment() {
        return sourceFragment;
    }

    @Override
    public String toString() {
        return start + "-" + end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o instanceof SourceRange) {
            SourceRange rhs = (SourceRange) o;
            return start == rhs.start
                && end == rhs.end
                && sourceFragment.equals(rhs.sourceFragment);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return start * 79 + end;
    }
}

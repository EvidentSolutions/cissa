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

import java.util.ArrayList;
import java.util.List;

public final class Attribute extends CSSNode {
    private final String name;
    private final List<CSSValue> values = new ArrayList<CSSValue>();
    private final boolean important;

    public Attribute(String name, boolean important) {
        this.name = name;
        this.important = important;
    }

    public void addValue(CSSValue value) {
        Require.argumentNotNull("value", value);
        
        values.add(value);
    }

    @Override
    public void writeTo(CSSWriter writer) {
        writer.write(name).write(": ").writeSeparated(values, " ");

        if (important)
            writer.write(" !important");
    }
}

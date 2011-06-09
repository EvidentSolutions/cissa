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

import fi.evident.cissa.model.*;

import java.util.List;

enum RGBFunction implements Function {

    INSTANCE;

    public CSSColor apply(List<CSSValue> args) {
        if (args.size() != 3) throw new IllegalArgumentException("expected 3 arguments, but got: " + args.size());

        int r = intValue(args.get(0), "red");
        int g = intValue(args.get(1), "green");
        int b = intValue(args.get(2), "blue");

        return new CSSColor(r, g, b);
    }

    private static int intValue(CSSValue value, String name) {
        if (value instanceof CSSAmount) {
            Dimension dim = ((CSSAmount) value).getValue();
            if (dim.getUnit() == DimensionUnit.EMPTY)
                return dim.getValue().intValue();
            else
                throw new IllegalArgumentException(
                        "argument '" + name + "' is invalid; expected number without unit, but got unit '" + dim.getUnit() + "'");
        } else {
            throw new IllegalArgumentException(
                    "argument '" + name + "' is invalid; expected integer but got '" + value + "'");
        }
    }
}

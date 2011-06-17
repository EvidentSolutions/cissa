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

public final class DimensionUnit {

    public static final DimensionUnit EMPTY = new DimensionUnit("");
    private final String name;

    private DimensionUnit(String name) {
        Require.argumentNotNull("name", name);

        this.name = name;
    }

    public DimensionUnit add(DimensionUnit unit) {
        return mergeAdditive(unit);
    }

    public DimensionUnit subtract(DimensionUnit unit) {
        return mergeAdditive(unit);
    }

    private DimensionUnit mergeAdditive(DimensionUnit unit) {
        Require.argumentNotNull("unit", unit);

        if (this == EMPTY || this.equals(unit)) return unit;
        if (unit == EMPTY) return this;

        throw new IncompatibleUnitsException();
    }

    public DimensionUnit multiply(DimensionUnit unit) {
        Require.argumentNotNull("unit", unit);

        if (this == EMPTY) return unit;
        if (unit == EMPTY) return this;

        throw new IncompatibleUnitsException();
    }

    public DimensionUnit divide(DimensionUnit unit) {
        Require.argumentNotNull("unit", unit);

        if (unit == EMPTY) return this;
        if (unit.equals(this)) return EMPTY;

        throw new IncompatibleUnitsException();
    }

    public static DimensionUnit forName(String name) {
        Require.argumentNotNullOrEmpty("name", name);

        return new DimensionUnit(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (o instanceof DimensionUnit) {
            DimensionUnit rhs = (DimensionUnit) o;
            return name.equals(rhs.name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

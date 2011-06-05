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

import java.math.BigDecimal;

public final class Dimension {

    private final BigDecimal value;
    private final DimensionUnit unit;

    public static final Dimension ZERO = new Dimension(BigDecimal.ZERO, DimensionUnit.EMPTY);

    private Dimension(BigDecimal value, DimensionUnit unit) {
        Require.argumentNotNull("value", value);
        Require.argumentNotNull("unit", unit);

        this.value = value;
        this.unit = unit;
    }

    public BigDecimal getValue() {
        return value;
    }

    public DimensionUnit getUnit() {
        return unit;
    }

    public static Dimension dimension(long value) {
        return dimension(value, DimensionUnit.EMPTY);
    }

    public static Dimension dimension(long value, DimensionUnit unit) {
        return dimension(new BigDecimal(value), unit);
    }

    public static Dimension dimension(String value) {
        return dimension(value, DimensionUnit.EMPTY);
    }

    public static Dimension dimension(String value, DimensionUnit unit) {
        return dimension(new BigDecimal(value), unit);
    }

    public static Dimension dimension(BigDecimal value) {
        return dimension(value, DimensionUnit.EMPTY);
    }

    public static Dimension dimension(BigDecimal value, DimensionUnit unit) {
        return new Dimension(value, unit);
    }

    public Dimension add(Dimension rhs) {
        return dimension(value.add(rhs.value), unit.add(rhs.unit));
    }

    public Dimension subtract(Dimension rhs) {
        return dimension(value.subtract(rhs.value), unit.subtract(rhs.unit));
    }

    public Dimension multiply(Dimension rhs) {
        return dimension(value.multiply(rhs.value), unit.multiply(rhs.unit));
    }

    public Dimension divide(Dimension rhs) {
        return dimension(value.divide(rhs.value), unit.divide(rhs.unit));
    }

    @Override
    public String toString() {
        return value.toString() + unit.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o instanceof Dimension) {
            Dimension rhs = (Dimension) o;
            return value.equals(rhs.value);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

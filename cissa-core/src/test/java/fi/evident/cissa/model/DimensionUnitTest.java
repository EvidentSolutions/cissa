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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class DimensionUnitTest {

    private final DimensionUnit e = DimensionUnit.EMPTY;
    private final DimensionUnit m = DimensionUnit.forName("m");
    private final DimensionUnit s = DimensionUnit.forName("s");

    @Test
    public void toStringUsesNameOfUnit() {
        assertEquals("foo", DimensionUnit.forName("foo").toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyNameIsNotAllowed() {
        DimensionUnit.forName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullNameIsNotAllowed() {
        DimensionUnit.forName(null);
    }

    @Test
    public void equalityIsDefinedInTermsOfName() {
        assertThat("equal with same name", DimensionUnit.forName("foo"), equalTo(DimensionUnit.forName("foo")));
        assertThat("not equal with different name", DimensionUnit.forName("foo"), not(equalTo(DimensionUnit.forName("bar"))));
        assertThat("not equal with null", DimensionUnit.forName("foo"), not(equalTo(null)));
        assertFalse("not equal with other type", DimensionUnit.forName("foo").equals("foo"));
    }

    @Test
    public void hashCodeIsConsistentWithEquality() {
        assertEquals(DimensionUnit.forName("foo").hashCode(), DimensionUnit.forName("foo").hashCode());
    }

    @Test
    public void addingUnits() {
        assertEquals(e, e.add(e));
        assertEquals(m, e.add(m));
        assertEquals(m, m.add(e));
        assertEquals(m, m.add(m));
    }

    @Test(expected = IncompatibleUnitsException.class)
    public void incompatibleUnitsCantBeAddedTogether() {
        s.add(m);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullUnitCantBeAdded() {
        e.add(null);
    }

    @Test
    public void subtractingUnits() {
        assertEquals(e, e.subtract(e));
        assertEquals(m, e.subtract(m));
        assertEquals(m, m.subtract(e));
        assertEquals(m, m.subtract(m));
    }

    @Test(expected = IncompatibleUnitsException.class)
    public void incompatibleUnitsCantBeSubtractedFromEachOthers() {
        s.subtract(m);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullUnitCantBeSubtracted() {
        e.subtract(null);
    }

    @Test
    public void multiplyingUnits() {
        assertEquals("untyped multiplication", e, e.multiply(e));
        assertEquals("multiplication by scalar", m, e.multiply(m));
        assertEquals("multiplication by scalar", m, m.multiply(e));
    }

    @Test(expected = IncompatibleUnitsException.class)
    public void multiplicationWithSelfIsNotSupported() {
        m.multiply(m);
    }

    @Test(expected = IncompatibleUnitsException.class)
    public void multiplicationOfDifferentTypesIsNotSupported() {
        s.multiply(m);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplicationByNullIsNotAllowed() {
        e.multiply(null);
    }

    @Test
    public void dividingUnits() {
        assertEquals("untyped division", e, e.divide(e));
        assertEquals("division by scalar", m, m.divide(e));
        assertEquals("division by same type", e, m.divide(m));
    }

    @Test(expected = IncompatibleUnitsException.class)
    public void dividingByUnitIsNotSupported() {
        e.divide(m);
    }

    @Test(expected = IncompatibleUnitsException.class)
    public void dividingDifferentUnitsIsNotSupported() {
        s.divide(m);
    }

    @Test(expected = IllegalArgumentException.class)
    public void dividingByNullIsNotSupported() {
        s.divide(null);
    }
}

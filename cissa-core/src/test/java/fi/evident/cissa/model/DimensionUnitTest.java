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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class DimensionUnitTest {

    private final DimensionUnit e = DimensionUnit.EMPTY;
    private final DimensionUnit m = DimensionUnit.forName("m");
    private final DimensionUnit s = DimensionUnit.forName("s");

    @Test
    public void toStringUsesNameOfUnit() {
        assertThat(DimensionUnit.forName("foo").toString(), is("foo"));
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
        assertThat("equal with same name", DimensionUnit.forName("foo"), is(DimensionUnit.forName("foo")));
        assertThat("not equal with different name", DimensionUnit.forName("foo"), is(not(DimensionUnit.forName("bar"))));
        assertThat("not equal with null", DimensionUnit.forName("foo"), is(not(equalTo(null))));
        assertThat("not equal with other type", DimensionUnit.forName("foo").equals("foo"), is(false));
    }

    @Test
    public void hashCodeIsConsistentWithEquality() {
        assertThat(DimensionUnit.forName("foo").hashCode(), is(DimensionUnit.forName("foo").hashCode()));
    }

    @Test
    public void addingUnits() {
        assertThat(e.add(e), is(e));
        assertThat(e.add(m), is(m));
        assertThat(m.add(e), is(m));
        assertThat(m.add(m), is(m));
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
        assertThat(e.subtract(e), is(e));
        assertThat(e.subtract(m), is(m));
        assertThat(m.subtract(e), is(m));
        assertThat(m.subtract(m), is(m));
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
        assertThat("untyped multiplication",   e.multiply(e), is(e));
        assertThat("multiplication by scalar", e.multiply(m), is(m));
        assertThat("multiplication by scalar", m.multiply(e), is(m));
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
        assertThat("untyped division",      e.divide(e), is(e));
        assertThat("division by scalar",    m.divide(e), is(m));
        assertThat("division by same type", m.divide(m), is(e));
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

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

import java.math.BigDecimal;

import static fi.evident.cissa.model.Dimension.dimension;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class DimensionTest {

    @Test
    public void shouldHaveSensibleStringRepresentation() {
        assertThat(dimension(8).toString(), is("8"));
        assertThat(dimension("5").toString(), is("5"));
        assertThat(dimension("5.123").toString(), is("5.123"));
        assertThat(dimension("-1.2").toString(), is("-1.2"));
    }

    @Test
    public void stringRepresentationShouldIncludeUnit() {
        DimensionUnit m = DimensionUnit.forName("m");
        assertThat(dimension(8, m).toString(), is("8m"));
    }

    @Test(expected = Exception.class)
    public void shouldNotBeConstructableFromNull() {
        dimension((BigDecimal) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeConstructableFromEmptyString() {
        dimension("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeConstructableFromStringNotRepresentingNumber() {
        dimension("foo");
    }

    @Test
    public void shouldDefineEquality() {
        assertThat("equal with same value", dimension("8.424"), is(dimension("8.424")));
        assertThat("not equal with different value", dimension("8.4"), is(not(dimension("4.5"))));
        assertThat("not equal with null", dimension("123"), is(not(equalTo(null))));
        assertThat("not equal with other type", dimension("42").equals("foo"), is(false));
    }

    @Test
    public void definesConsistentHashCode() {
        assertThat(dimension("5.4").hashCode(), is(dimension("5.4").hashCode()));
    }

    @Test
    public void shouldSupportBasicArithmetic() {
        assertThat(dimension("6.1").add(dimension("2.5")),      is(dimension("8.6")));
        assertThat(dimension("6.1").subtract(dimension("2.5")), is(dimension("3.6")));
        assertThat(dimension("6.1").multiply(dimension("2.5")), is(dimension("15.25")));
        assertThat(dimension("6.1").divide(dimension("2.5")),   is(dimension("2.44")));
        assertThat(dimension("5").divide(dimension("2")),       is(dimension("2.5")));
    }

    @Test
    public void byDefaultThereIsNoUnit() {
        assertThat(dimension("5.5").getUnit(), is(DimensionUnit.EMPTY));
    }

    @Test
    public void shouldSupportExtractingValueAndUnit() {
        BigDecimal value = new BigDecimal("24.5");
        DimensionUnit unit = DimensionUnit.forName("foo");

        Dimension dim = dimension(value, unit);
        assertThat(dim.getValue(), is(value));
        assertThat(dim.getUnit(), is(unit));
    }

    @Test
    public void shouldCalculateCorrectUnits() {
        DimensionUnit m = DimensionUnit.forName("m");

        assertThat(dimension(5, m).add(dimension(5, m)), is(dimension(10, m)));
        assertThat(dimension(5).add(dimension(5, m)), is(dimension(10, m)));
        assertThat(dimension(5, m).add(dimension(5)), is(dimension(10, m)));
    }
}

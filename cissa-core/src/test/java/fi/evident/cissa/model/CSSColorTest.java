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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class CSSColorTest {

    @Test
    public void constructorParametersAreSavedToFields() {
        CSSColor color = new CSSColor(42, 123, 54);

        assertThat(color.getR(), is(42));
        assertThat(color.getG(), is(123));
        assertThat(color.getB(), is(54));
    }

    @Test
    public void hashedRepresentationIsUsedForToString() {
        CSSColor color = new CSSColor(42, 123, 54);

        assertThat(color.toString(), is("#2a7b36"));
    }

    @Test
    public void colorsCanBeAdded() {
        CSSColor color1 = new CSSColor(0, 100, 200);
        CSSColor color2 = new CSSColor(4, 35, 42);

        assertThat(color1.add(color2), is(new CSSColor(4, 135, 242)));
    }

    @Test
    public void addingCanOverflowColor() {
        CSSColor color1 = new CSSColor(0, 100, 200);
        CSSColor color2 = new CSSColor(4, 35, 142);

        assertThat(color1.add(color2), is(new CSSColor(4, 135, 342)));
    }

    @Test
    public void overflowedComponentsAreClampedInStringRepresentation() {
        CSSColor color = new CSSColor(42, 423, 54);

        assertThat(color.toString(), is("#2aff36"));
    }

    @Test
    public void colorsCanBeParsedFromLongHexRepresentation() {
        CSSColor color = CSSColor.parse("#2a7b36");

        assertThat(color, is(new CSSColor(42, 123, 54)));
    }

    @Test
    public void colorsCanBeParsedFromShortHexRepresentation() {
        CSSColor color = CSSColor.parse("#2f8");

        assertThat(color, is(new CSSColor(34, 255, 136)));
    }

    @Test
    public void colorsImplementEquality() {
        CSSColor color1 = new CSSColor(42, 52, 20.4);

        assertThat(color1, is(equalTo(new CSSColor(42, 52, 20.4))));
        assertThat(color1, is(not(equalTo(new CSSColor(42, 52, 20.3)))));
        assertThat(color1, is(not(equalTo(null))));
    }

    @Test
    public void colorsImplementHashCode() {
        CSSColor color1 = new CSSColor(42, 52, 20.4);
        CSSColor color2 = new CSSColor(42, 52, 20.4);

        assertThat(color1.hashCode(), is(color2.hashCode()));
    }

    @Test
    public void colorsCanBeMultipliedByScalars() {
        CSSColor color = new CSSColor(15, 60, 42);

        assertThat(color.multiply(2.2), is(new CSSColor(33, 132, 92.4)));
    }
}

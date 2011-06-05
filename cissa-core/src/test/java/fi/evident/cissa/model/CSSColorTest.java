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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CSSColorTest {

    @Test
    public void constructorParametersAreSavedToFields() {
        CSSColor color = new CSSColor(42, 123, 54);

        assertThat(color.r, is(42));
        assertThat(color.g, is(123));
        assertThat(color.b, is(54));
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

        CSSColor color3 = color1.add(color2);

        assertThat(color3.r, is(4));
        assertThat(color3.g, is(135));
        assertThat(color3.b, is(242));
    }

    @Test
    public void addingCanOverflowColor() {
        CSSColor color1 = new CSSColor(0, 100, 200);
        CSSColor color2 = new CSSColor(4, 35, 142);

        CSSColor color3 = color1.add(color2);
        
        assertThat(color3.r, is(4));
        assertThat(color3.g, is(135));
        assertThat(color3.b, is(342));
    }

    @Test
    public void overflowedComponentsAreClampedInStringRepresentation() {
        CSSColor color = new CSSColor(42, 423, 54);

        assertThat(color.toString(), is("#2aff36"));
    }

    @Test
    public void colorsCanBeParsedFromLongHexRepresentation() {
        CSSColor color = CSSColor.parse("#2a7b36");

        assertThat(color.r, is(42));
        assertThat(color.g, is(123));
        assertThat(color.b, is(54));
    }

    @Test
    public void colorsCanBeParsedFromShortHexRepresentation() {
        CSSColor color = CSSColor.parse("#2f8");

        assertThat(color.r, is(34));
        assertThat(color.g, is(255));
        assertThat(color.b, is(136));
    }
}

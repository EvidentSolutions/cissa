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

import fi.evident.cissa.model.CSSColor;
import fi.evident.cissa.model.CSSValue;
import fi.evident.cissa.model.DimensionUnit;
import org.junit.Test;

import static fi.evident.cissa.model.Dimension.dimension;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RGBFunctionTest {

    private final RGBFunction func = RGBFunction.INSTANCE;
    private final CSSValue zero = intAmount(0);

    @Test
    public void callWithProperArgumentsProducesColor() {
        CSSColor color = func.apply(asList(intAmount(42), intAmount(56), intAmount(125)));
        assertThat(color.getR(), is(42));
        assertThat(color.getG(), is(56));
        assertThat(color.getB(), is(125));
    }

    @Test(expected = IllegalArgumentException.class)
    public void callWithTooFewArgumentsThrowsException() {
        func.apply(asList(zero));
    }

    @Test(expected = IllegalArgumentException.class)
    public void callWithTooManyArgumentsThrowsException() {
        func.apply(asList(zero, zero, zero, zero));
    }

    @Test(expected = IllegalArgumentException.class)
    public void callWithNonIntegerThrowsException() {
        func.apply(asList(zero, zero, CSSValue.string("foo")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void callWithNonEmptyUnitThrowsException() {
        CSSValue meters = CSSValue.amount(dimension(4, DimensionUnit.forName("m")));
        func.apply(asList(meters, meters, meters));
    }

    private static CSSValue intAmount(int i) {
        return CSSValue.amount(dimension(i));
    }
}

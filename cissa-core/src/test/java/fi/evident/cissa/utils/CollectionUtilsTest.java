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

package fi.evident.cissa.utils;

import org.junit.Test;

import java.util.ArrayList;

import static fi.evident.cissa.utils.CollectionUtils.join;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class CollectionUtilsTest {

    @Test
    public void joiningWillUseSeparatorBetweenItems() {
        assertEquals("foo, bar, baz", join(asList("foo", "bar", "baz"), ", "));
    }

    @Test
    public void joiningSingleItem() {
        assertEquals("foo", join(asList("foo"), ", "));
    }

    @Test
    public void joiningEmptyCollectionWillReturnEmptyString() {
        assertEquals("", join(new ArrayList<Object>(), "foo"));
    }

    @Test
    public void joiningWithNullsWillPrintNull() {
        assertEquals("foo, null, baz", join(asList("foo", null, "baz"), ", "));
    }
}

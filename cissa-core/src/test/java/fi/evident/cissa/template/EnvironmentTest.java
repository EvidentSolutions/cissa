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

import fi.evident.cissa.model.CSSValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EnvironmentTest {

    private Environment parent = new Environment();
    private Environment env = new Environment(parent);

    @Test(expected = UnboundVariableException.class)
    public void lookingUpUnboundVariableThrowsException() {
        env.lookup("foo");
    }

    @Test
    public void extendingEnvironmentMakesVariableAvailable() {
        CSSValue value = CSSValue.token("bar");
        env.bind("foo", value);

        assertEquals(value, env.lookup("foo"));
    }

    @Test
    public void lookupsAreDelegatedToParentIfBindingIsNotFound() {
        CSSValue value = CSSValue.token("bar");
        parent.bind("foo", value);

        assertEquals(value, env.lookup("foo"));
    }

    @Test
    public void childBindingsArePreferredToParent() {
        CSSValue bar = CSSValue.token("bar");
        CSSValue baz = CSSValue.token("baz");

        parent.bind("foo", bar);
        env.bind("foo", baz);

        assertEquals(baz, env.lookup("foo"));
    }
}

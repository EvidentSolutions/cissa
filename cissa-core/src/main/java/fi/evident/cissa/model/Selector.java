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

import java.util.ArrayList;
import java.util.List;

import static fi.evident.cissa.utils.CollectionUtils.join;

public abstract class Selector {

    public static Selector simple(String selector, List<String> specs) {
        return new SimpleSelector(selector, specs);
    }

    public static Selector compound(Selector left, String combinator, Selector right) {
        return new CompoundSelector(left, combinator, right);
    }
}

final class SimpleSelector extends Selector {
    private final String elementName;
    private final List<String> specs;

    SimpleSelector(String elementName, List<String> specs) {
        Require.argumentNotNull("elementName", elementName);

        this.elementName = elementName;
        this.specs = new ArrayList<String>(specs);
    }

    @Override
    public String toString() {
        return elementName + join(specs);
    }
}

final class CompoundSelector extends Selector {
    private final Selector left;
    private final String combinator;
    private final Selector right;

    CompoundSelector(Selector left, String combinator, Selector right) {
        Require.argumentNotNull("left", left);
        Require.argumentNotNull("combinator", combinator);
        Require.argumentNotNull("right", right);

        this.combinator = combinator;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return combinator.isEmpty()
            ? left + " " + right
            : left + " " + combinator + " " + right;
    }
}

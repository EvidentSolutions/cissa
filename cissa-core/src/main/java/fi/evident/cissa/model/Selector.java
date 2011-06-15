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

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public abstract class Selector extends CSSNode {

    public static Selector simple(String selector, String... specs) {
        return simple(selector, asList(specs));
    }

    public static Selector simple(String selector, List<String> specs) {
        return new SimpleSelector(selector, specs);
    }

    public static Selector compound(Selector left, Selector right) {
        return compound(left, "", right);
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
        this.specs = unmodifiableList(new ArrayList<String>(specs));
    }

    @Override
    void writeTo(CSSWriter writer) {
        writer.write(elementName).writeAll(specs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o instanceof SimpleSelector) {
            SimpleSelector rhs = (SimpleSelector) o;

            return elementName.equals(rhs.elementName)
                && specs.equals(rhs.specs);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return elementName.hashCode() * 79 + specs.hashCode();
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
    void writeTo(CSSWriter writer) {
        left.writeTo(writer);
        writer.write(" ");
        
        if (!combinator.isEmpty())
            writer.write(combinator).write(" ");

        right.writeTo(writer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o instanceof CompoundSelector) {
            CompoundSelector rhs = (CompoundSelector) o;

            return combinator.equals(rhs.combinator)
                && left.equals(rhs.left)
                && right.equals(rhs.right);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (left.hashCode() * 79 + combinator.hashCode()) * 79 + right.hashCode();
    }
}

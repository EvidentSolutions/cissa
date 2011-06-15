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

package fi.evident.cissa.parser;

import fi.evident.cissa.model.Selector;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static fi.evident.cissa.model.Selector.compound;
import static fi.evident.cissa.model.Selector.simple;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.fail;

public class SelectorParserTest {

    @Test
    public void simpleSelector() {
        assertThatInput("*", producesSelectors(simple("*")));
        assertThatInput("foo", producesSelectors(simple("foo")));
    }

    @Test
    public void simpleSelectorWithSpecs() {
        assertThatInput("foo.bar:hover#id", producesSelectors(simple("foo", ".bar", ":hover", "#id")));
        assertThatInput("foo[id=4]:hover[class=bar]",
                producesSelectors(simple("foo", "[id=4]", ":hover", "[class=bar]")));
    }

    @Test
    public void simpleSelectorWithoutElementName() {
        assertThatInput(".foo", producesSelectors(simple("", ".foo")));
        assertThatInput(".foo:hover", producesSelectors(simple("", ".foo", ":hover")));
    }

    @Test
    public void compoundSelectorWithoutCombinator() {
        assertThatInput("foo bar", producesSelectors(compound(simple("foo"), simple("bar"))));
    }

    @Test
    public void compoundSelectorWithCombinator() {
        assertThatInput("foo > bar", producesSelectors(compound(simple("foo"), ">", simple("bar"))));
    }

    @Test
    public void multipleSimpleSelectors() {
        assertThatInput("foo, bar", producesSelectors(simple("foo"), simple("bar")));
        assertThatInput("foo, bar, baz", producesSelectors(simple("foo"), simple("bar"), simple("baz")));
    }

    @Test
    public void complexSelectors() {
        assertThatInput("foo, bar > baz quux, xyzzy",
                producesSelectors(
                        simple("foo"),
                        compound(simple("bar"), ">", compound(simple("baz"), simple("quux"))),
                        simple("xyzzy")));
    }

    @Test
    public void invalidSelectors() {
        assertThatMarkupGeneratesParseFailure("foo,");
        assertThatMarkupGeneratesParseFailure(",");
        assertThatMarkupGeneratesParseFailure(", foo");
    }

    private static void assertThatInput(String input, Matcher<List<Selector>> matcher) {
        List<Selector> selectors = parse(input);
        Assert.assertThat(selectors, is(matcher));
    }

    private static Matcher<List<Selector>> producesSelectors(Selector... selectors) {
        return is(asList(selectors));
    }

    private static void assertThatMarkupGeneratesParseFailure(String markup) {
        try {
            List<Selector> selectors = parse(markup);
            fail("Expected ParserException for selectors '" + markup + "', got '" + selectors + "'");
        } catch (ParseException e) {
        }
    }

    private static List<Selector> parse(String input) {
        return new SelectorParser(new Lexer(input)).parseSelectors();
    }
}

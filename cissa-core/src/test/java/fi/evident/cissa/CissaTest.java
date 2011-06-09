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

package fi.evident.cissa;

import fi.evident.cissa.parser.ParseException;
import fi.evident.cissa.template.EvaluationException;
import fi.evident.cissa.template.SourceRange;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class CissaTest {

    @Test
    public void prettyPrintValidCSS() {
        assertThatMarkup("h1 { color: red }", generatesCSS("h1 { color: red }"));
    }

    @Test
    public void supportNumericAttributeValues() {
        assertThatMarkupGeneratesIdenticalCSS("h1 { width: 10 }");
    }

    @Test
    public void supportAttributeValuesWithUnits() {
        assertThatMarkupGeneratesIdenticalCSS("h1 { width: 10px }");
    }

    @Test
    public void supportVariables() {
        assertThatMarkup("h1 { @myColor: blue; color: @myColor; }", generatesCSS("h1 { color: blue }"));
        assertThatMarkup("h1 { @x: 2pt; @y: @x; font-size: @y; }", generatesCSS("h1 { font-size: 2pt }"));
    }

    @Test
    public void supportGlobalVariables() {
        assertThatMarkup("@foo: 42px; h1 { width: @foo }", generatesCSS("h1 { width: 42px }"));
    }

    @Test
    public void supportAddingDimensions() {
        assertThatMarkup("h1 { width: 10   + 20 }", generatesCSS("h1 { width: 30 }"));
        assertThatMarkup("h1 { width: 10pt + 20   }", generatesCSS("h1 { width: 30pt }"));
        assertThatMarkup("h1 { width: 10   + 20pt }", generatesCSS("h1 { width: 30pt }"));
        assertThatMarkup("h1 { width: 10pt + 20pt }", generatesCSS("h1 { width: 30pt }"));
    }

    @Test
    public void supportMultiplication() {
        assertThatMarkup("h1 { width: 6   * 8   }", generatesCSS("h1 { width: 48 }"));
        assertThatMarkup("h1 { width: 6pt * 8   }", generatesCSS("h1 { width: 48pt }"));
        assertThatMarkup("h1 { width: 6   * 8pt }", generatesCSS("h1 { width: 48pt }"));
    }

    @Test
    public void precedenceForArithmeticOperations() {
        assertThatMarkup("h1 { width: (2+3) * 4 }", generatesCSS("h1 { width: 20 }"));
        assertThatMarkup("h1 { width: 2+3*4 }"    , generatesCSS("h1 { width: 14 }"));
        assertThatMarkup("h1 { width: 2*3+4 }"    , generatesCSS("h1 { width: 10 }"));
        assertThatMarkup("h1 { width: 10-1-2 }"   , generatesCSS("h1 { width: 7 }"));
        assertThatMarkup("h1 { width: 80/4/2 }"   , generatesCSS("h1 { width: 10 }"));
        assertThatMarkup("h1 { width: -4 }"       , generatesCSS("h1 { width: -4 }"));
        assertThatMarkup("h1 { width: -4 * -3 }"  , generatesCSS("h1 { width: 12 }"));
    }

    @Test
    public void extraWhitespaceIsAllowed() {
        assertThatMarkup(" h1 { width : ( 2 + 3 ) * 4 } ", generatesCSS("h1 { width: 20 }"));
    }

    @Test
    public void localVariablesShouldShadowGlobals() {
        assertThatMarkup("@foo: 12px; h1 { @foo: 13px; width: @foo }", generatesCSS("h1 { width: 13px }"));
    }

    @Test
    public void outerVariableIsInScopeForInitializationOfLocalVariable() {
        assertThatMarkup("@foo: 12px; h1 { @foo: @foo+2; width: @foo }", generatesCSS("h1 { width: 14px }"));
    }

    @Test
    public void supportDoubleSlashComments() {
        assertThatMarkup("h1 { // width: 10pt; \n height: 20pt }", generatesCSS("h1 { height: 20pt }"));
        assertThatMarkup("h1 { height: 20pt } // comment on the last line", generatesCSS("h1 { height: 20pt }"));
    }

    @Test
    public void supportMultiLineComments() {
        assertThatMarkup("h1 { /* width: 10; */ height: 20pt }", generatesCSS("h1 { height: 20pt }"));
    }

    @Test
    public void supportMultipleSelectorsPerRuleSet() {
        assertThatMarkupGeneratesIdenticalCSS("h1, h2, h3 { color: red }");
    }

    @Test
    public void supportMultipleValues() {
        assertThatMarkupGeneratesIdenticalCSS("h1 { margin: 1 2 3 4 }");
    }

    @Test
    public void supportCompoundSelectors() {
        assertThatMarkupGeneratesIdenticalCSS("h1 h2 { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS("h1 h2 h3 { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS("h1 > h2 { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS("h1 + h2 { width: 10pt }");
    }

    @Test
    public void supportStarsAsElementNames() {
        assertThatMarkupGeneratesIdenticalCSS("* { width: 10pt }");
    }

    @Test
    public void supportHashesInSelectors() {
        assertThatMarkupGeneratesIdenticalCSS("h1#foo { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS("#foo { width: 10pt }");
    }

    @Test
    public void supportClassesInSelectors() {
        assertThatMarkupGeneratesIdenticalCSS("h1.foo { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS(".foo { width: 10pt }");
    }

    @Test
    public void supportAttributesInSelectors() {
        assertThatMarkupGeneratesIdenticalCSS("h1[id] { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS("h1[id=foo] { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS("[id] { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS("[id=foo] { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS("[id~=foo] { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS("[id!=foo] { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS("[id=] { width: 10pt }");
    }

    @Test
    public void supportPseudoClassesInSelectors() {
        assertThatMarkupGeneratesIdenticalCSS("a:link { color: red }");
        assertThatMarkupGeneratesIdenticalCSS(":link { color: red }");
    }

    @Test
    public void supportMultipleSpecificationsInSelectors() {
        assertThatMarkupGeneratesIdenticalCSS("a.foo.bar#baz:active[class=quux] { width: 10pt }");
    }

    @Test
    public void supportMultipleSuccessiveSelectors() {
        assertThatMarkupGeneratesIdenticalCSS(".foo .bar { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS(".foo.bar { width: 10pt }");

        assertThatMarkupGeneratesIdenticalCSS("#foo.bar { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS("#foo .bar { width: 10pt }");
    }

    @Test
    public void allowUnderscoredInClassAndIdNames() {
        assertThatMarkup(".foo_bar { color: red }", generatesCSS(".foo_bar { color: red }"));
        assertThatMarkup("#foo_bar { color: red }", generatesCSS("#foo_bar { color: red }"));
    }

    @Test
    @Ignore
    public void supportStringsAsAttributeValues() {
        assertThatMarkupGeneratesIdenticalCSS("[id='foo'] { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS("[id=\"foo\"] { width: 10pt }");
    }

    @Test
    public void supportUrlAttributeValues() {
        assertThatMarkupGeneratesIdenticalCSS("h1 { background: url(\"foo.png\") }");
    }

    @Test
    public void supportColors() {
        assertThatMarkup("h1 { color: #9fa2a4 }", generatesCSS("h1 { color: #9fa2a4 }"));
        assertThatMarkup("h1 { color: #9FA2A4 }", generatesCSS("h1 { color: #9fa2a4 }"));
        assertThatMarkup("h1 { color: #9FA2A4 }", generatesCSS("h1 { color: #9fa2a4 }"));

        assertThatMarkup("h1 { color: #abc }", generatesCSS("h1 { color: #aabbcc }"));
        //assertThatMarkup("h1 { color: rgb(0, 0, 0) }", generatesCSS("h1 { color: #000000 }"));
    }

    @Test
    public void supportAddingColors() {
        assertThatMarkup("h1 { color: #112233 + #111100 }", generatesCSS("h1 { color: #223333 }"));
    }

    @Test
    public void allowLineBreaksInsideMultiLineComments() {
        assertThatMarkup("/* \n */ h1 { color: red }", generatesCSS("h1 { color: red }"));
    }

    @Test
    public void supportNestedRuleSets() {
        assertThatMarkup("h1 { h2 { color: red } }", generatesCSS("h1 h2 { color: red }"));
        assertThatMarkup("h1, h2 { h3 { color: red } }", generatesCSS("h1 h3, h2 h3 { color: red }"));
    }

    @Test
    public void supportImportantRules() {
        assertThatMarkup("h1 { color: red !important }", generatesCSS("h1 { color: red !important }"));
    }

    @Test
    public void listOfValuesAreSupported() {
        assertThatMarkupGeneratesIdenticalCSS("h1 { font-family: Arial, sans-serif }");
    }

    @Test
    public void supportIdentifiersWithDashes() {
        assertThatMarkupGeneratesIdenticalCSS("h1 { font-family: sans-serif }");
    }

    @Test
    public void nestedRulesWithColonSelectorsAreHandledCorrectly() {
        assertThatMarkup("h1 { foo:hover { color: red } }", generatesCSS("h1 foo:hover { color: red }"));
    }

    @Test
    public void evaluationErrorsProduceEvaluationException() {
        assertThatMarkupGeneratesEvaluationException("h1 { width: 1px + 2pt }");
        assertThatMarkupGeneratesEvaluationException("h1 { width: @foo }");
    }

    @Test
    public void unboundVariableErrorsKnowTheirSourceLocation() {
        assertThatMarkupGeneratesEvaluationExceptionWhereRange("h1 { width: @foo }", isRange(12, 16, "@foo"));
    }

    @Test
    public void arithmeticErrorsKnowTheirSourceLocation() {
        assertThatMarkupGeneratesEvaluationExceptionWhereRange("h1 { width: 1px + 2pt }", isRange(16, 17, "+"));
    }

    @Test
    public void divideByZeroErrorsKnowTheirSourceLocation() {
        assertThatMarkupGeneratesEvaluationExceptionWhereRange("h1 { width: 42 / 0 }", isRange(15, 16, "/"));
    }

    // support detecting overflow
    // support nested comments
    // support value functions
    // support rule-set functions
    // support selector functions
    // check expression types
    // comments are ignored inside string literals

    private static void assertThatMarkup(String markup, Matcher<String> matcher) {
        try {
            String css = Cissa.generate(markup);
            assertThat(css, matcher);
        } catch (ParseException e) {
            fail("Failed to parse markup '" + markup + "'\n  error: " + e);
        }
    }

    private static void assertThatMarkupGeneratesEvaluationException(String markup) {
        assertThatMarkupGeneratesEvaluationExceptionWhereRange(markup, is(CoreMatchers.<SourceRange>anything()));
    }

    private static void assertThatMarkupGeneratesEvaluationExceptionWhereRange(String markup, Matcher<SourceRange> rangeMatcher) {
        try {
            Cissa.generate(markup);
            fail("Expected evaluation exception");
        } catch (EvaluationException e) {
            assertThat(e.getRange(), rangeMatcher);
        }
    }

    private static Matcher<String> generatesCSS(final String expected) {
        return CoreMatchers.equalTo(expected);
    }

    private static void assertThatMarkupGeneratesIdenticalCSS(String markup) {
        assertThatMarkup(markup, generatesCSS(markup));
    }

    private static Matcher<SourceRange> isRange(final int start, final int end, final String sourceFragment) {
        return is(new SourceRange(start, end, sourceFragment));
    }
}

package fi.evident.cissa;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class CissaTest {

    @Test
    public void prettyPrintValidCSS() {
        assertThatMarkup("h1 { }", generatesCSS("h1 {  }"));
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
    @Ignore
    public void supportVariables() {
        assertThatMarkup("h1 { @myColor: blue; color: @myColor; }", generatesCSS("h1 { color: blue }"));
        assertThatMarkup("h1 { @x: 2pt; @y: @x; font-size: @y; }", generatesCSS("h1 { font-size: 2pt }"));
    }

    @Test
    @Ignore
    public void supportGlobalVariables() {
        assertThatMarkup("@foo: 42px; h1 { width: @foo }", generatesCSS("h1 { width: 42px }"));
    }

    @Test
    @Ignore
    public void supportAddingDimensions() {
        assertThatMarkup("h1 { width: 10   + 20   }", generatesCSS("h1 { width: 30 }"));
        assertThatMarkup("h1 { width: 10pt + 20   }", generatesCSS("h1 { width: 30pt }"));
        assertThatMarkup("h1 { width: 10   + 20pt }", generatesCSS("h1 { width: 30pt }"));
        assertThatMarkup("h1 { width: 10pt + 20pt }", generatesCSS("h1 { width: 30pt }"));
    }

    @Test
    @Ignore
    public void supportMultiplication() {
        assertThatMarkup("h1 { width: 6   * 8   }", generatesCSS("h1 { width: 48 }"));
        assertThatMarkup("h1 { width: 6pt * 8   }", generatesCSS("h1 { width: 48pt }"));
        assertThatMarkup("h1 { width: 6   * 8pt }", generatesCSS("h1 { width: 48pt }"));
    }

    @Test
    @Ignore
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
    @Ignore
    public void extraWhitespaceIsAllowed() {
        assertThatMarkup(" h1 { width : ( 2 + 3 ) * 4 } ", generatesCSS("h1 { width: 20 }"));
    }

    @Test
    @Ignore
    public void localVariablesShouldShadowGlobals() {
        assertThatMarkup("@foo: 12px; h1 { @foo: 13px; width: @foo }", generatesCSS("h1 { width: 13px }"));
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
    @Ignore
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
    @Ignore
    public void supportHashesInSelectors() {
        assertThatMarkupGeneratesIdenticalCSS("h1#foo { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS("#foo { width: 10pt }");
    }

    @Test
    @Ignore
    public void supportClassesInSelectors() {
        assertThatMarkupGeneratesIdenticalCSS("h1.foo { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS(".foo { width: 10pt }");
    }

    @Test
    @Ignore
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
    @Ignore
    public void supportStringsAsAttributeValues() {
        assertThatMarkupGeneratesIdenticalCSS("[id='foo'] { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS("[id=\"foo\"] { width: 10pt }");
    }

    @Test
    @Ignore
    public void supportPseudoClassesInSelectors() {
        assertThatMarkupGeneratesIdenticalCSS("a:link { color: red }");
        assertThatMarkupGeneratesIdenticalCSS(":link { color: red }");
    }

    @Test
    @Ignore
    public void supportMultipleSpecificationsInSelectors() {
        assertThatMarkupGeneratesIdenticalCSS("a.foo.bar#baz:active[class=quux] { width: 10pt }");
    }

    @Test
    @Ignore
    public void supportMultipleSuccessiveSelectors() {
        assertThatMarkupGeneratesIdenticalCSS(".foo .bar { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS(".foo.bar { width: 10pt }");

        assertThatMarkupGeneratesIdenticalCSS("#foo.bar { width: 10pt }");
        assertThatMarkupGeneratesIdenticalCSS("#foo .bar { width: 10pt }");
    }

    @Test
    @Ignore
    public void supportUrlAttributeValues() {
        assertThatMarkupGeneratesIdenticalCSS("h1 { background: url(\"foo.png\") }");
    }

    @Test
    @Ignore
    public void supportColors() {
        assertThatMarkup("h1 { color: #9fa2a4 }", generatesCSS("h1 { color: #9fa2a4 }"));
        assertThatMarkup("h1 { color: #9FA2A4 }", generatesCSS("h1 { color: #9fa2a4 }"));
        assertThatMarkup("h1 { color: #9FA2A4 }", generatesCSS("h1 { color: #9fa2a4 }"));

        assertThatMarkup("h1 { color: #abc }", generatesCSS("h1 { color: #aabbcc }"));
        //assertThatMarkup("h1 { color: rgb(0, 0, 0) }", generatesCSS("h1 { color: #000000 }"));
    }

    @Test
    @Ignore
    public void allowLineBreaksInsideMultiLineComments() {
        assertThatMarkup("/* \n */ h1 { color: red }", generatesCSS("h1 { color: red }"));
    }

    @Test
    @Ignore
    public void supportNestedRuleSets() {
        assertThatMarkup("h1 { h2 { color: red } }", generatesCSS("h1 h2 { color: red }"));
        assertThatMarkup("h1, h2 { h3 { color: red } }", generatesCSS("h1 h3, h2 h3 { color: red }"));
    }

    @Test
    @Ignore
    public void supportAddingColors() {
        assertThatMarkup("h1 { color: #112233 + #111100 }", generatesCSS("h1 { color: #223333 }"));
    }

    @Test
    @Ignore
    public void allowUnderscoredInClassAndIdNames() {
        assertThatMarkup(".foo_bar { color: red }", generatesCSS(".foo_bar { color: red }"));
        assertThatMarkup("#foo_bar { color: red }", generatesCSS("#foo_bar { color: red }"));
    }

    @Test
    @Ignore
    public void supportImportantRules() {
        assertThatMarkup("h1 { color: red !important }", generatesCSS("h1 { color: red !important }"));
    }

    @Test
    @Ignore
    public void listOfValuesAreSupported() {
        assertThatMarkupGeneratesIdenticalCSS("h1 { font-family: Arial, sans-serif }");
    }

    @Test
    public void supportIdentifiersWithDashes() {
        assertThatMarkupGeneratesIdenticalCSS("h1 { font-family: sans-serif }");
    }

    // support detecting overflow
    // support nested comments
    // support value functions
    // support rule-set functions
    // support selector functions
    // check expression types
    // comments are ignored inside string literals

    private static void assertThatMarkup(String markup, Matcher<String> matcher) {
        String css = Cissa.generate(markup);
        assertThat(css, matcher);
    }

    private static Matcher<String> generatesCSS(final String expected) {
        return CoreMatchers.equalTo(expected);
    }

    private static void assertThatMarkupGeneratesIdenticalCSS(String markup) {
        assertThatMarkup(markup, generatesCSS(markup));
    }
}

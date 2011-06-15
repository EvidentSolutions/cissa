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
import fi.evident.cissa.utils.Require;

import java.util.ArrayList;
import java.util.List;

import static fi.evident.cissa.model.Selector.simple;
import static fi.evident.cissa.parser.CharacterClass.*;

final class SelectorParser {

    private final Lexer lexer;
    static final CharacterClass SELECTOR_START = noneOf("{},");
    private static final CharacterClass SPEC_START = anyOf("#.[:");
    private static final CharacterClass ID_CHAR = or(LETTER, DIGIT, anyOf("-_"));

    SelectorParser(final Lexer lexer) {
        Require.argumentNotNull("lexer", lexer);
        
        this.lexer = lexer;
    }

    // selectors:
    //      selector (',' selector)*
    public List<Selector> parseSelectors() {
        List<Selector> result = new ArrayList<Selector>();

        result.add(parseSelector());
        while (lexer.consumeTokenIf(','))
            result.add(parseSelector());

        return result;
    }

    // selector:
    //      simpleSelector
    private Selector parseSelector() {
        Selector selector = parseSimpleSelector();

        if (lexer.consumeTokenIf('>')) {
            return Selector.compound(selector, ">", parseSelector());
        } else if (lexer.consumeTokenIf('+')) {
            return Selector.compound(selector, "+", parseSelector());
        } else if (lexer.nextCharacterIs(SELECTOR_START)) {
            return Selector.compound(selector, "", parseSelector());
        } else {
            return selector;
        }
    }

    // simpleSelector:
    //      elementName spec* | spec+
    private Selector parseSimpleSelector() {
        String elementName;
        if (lexer.consumeTokenIf('*'))
            elementName = "*";
        else if (lexer.nextCharacterIs(LETTER))
            elementName = lexer.parseIdentifier().getValue();
        else
            elementName = "";

        List<String> specs = parseSpecs(elementName.isEmpty());
        
        return simple(elementName, specs);
    }

    private List<String> parseSpecs(boolean required) {
        List<String> result = new ArrayList<String>();

        if (required)
            result.add(parseSpec());

        while (lexer.nextCharacterIs(SPEC_START))
            result.add(parseSpec());

        lexer.skipSpaces();

        return result;
    }

    private String parseSpec() {
        if (lexer.nextCharacterIs('#')) {
            lexer.read();
            return "#" + readIdentifier();

        } else if (lexer.nextCharacterIs('.')) {
            lexer.read();
            return "." + readIdentifier();

        } else if (lexer.nextCharacterIs('[')) {
            StringBuilder sb = new StringBuilder();
            while (!lexer.nextCharacterIs(']'))
                sb.append(lexer.read());

            sb.append(lexer.read()); // final ']'
            return sb.toString();

        } else if (lexer.nextCharacterIs(':')) {
            lexer.read();
            return ":" + readIdentifier();
            
        } else {
            throw lexer.parseError("spec");
        }
    }

    private String readIdentifier() {
        StringBuilder sb = new StringBuilder();

        while (lexer.nextCharacterIs(ID_CHAR))
            sb.append(lexer.read());

        if (sb.length() == 0)
            throw lexer.parseError("identifier");

        return sb.toString();
    }
}

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

import fi.evident.cissa.model.CSSValue;
import fi.evident.cissa.model.Selector;
import fi.evident.cissa.template.*;

import java.util.ArrayList;
import java.util.List;

import static fi.evident.cissa.parser.Lexer.VARIABLE_PREFIX;
import static fi.evident.cissa.parser.SelectorParser.SELECTOR_START;

public final class CissaParser {

    private final Lexer lexer;
    private final SelectorParser selectorParser;
    private static final CharacterClass LITERAL_START = CharacterClass.noneOf("(){};!");
    private static final CharacterClass VALUE_START = CharacterClass.noneOf(";{}!");

    private CissaParser(String source) {
        this.lexer = new Lexer(source);
        this.selectorParser = new SelectorParser(lexer);
    }

    public static DocumentTemplate parse(String s) {
        return new CissaParser(s).parseDocumentTemplate();
    }

    // document:
    //      spaces* variableDefinition* ruleSetTemplate*
    private DocumentTemplate parseDocumentTemplate() {
        lexer.skipSpaces();

        List<VariableDefinition> variableDefinitions = parseVariableDefinitions();
        List<RuleSetTemplate> ruleSetTemplates = parseRuleSetTemplates();

        return new DocumentTemplate(variableDefinitions, ruleSetTemplates);
    }

    // variableDefinition:
    //     variable ':' value ';'
    private List<VariableDefinition> parseVariableDefinitions() {
        List<VariableDefinition> result = new ArrayList<VariableDefinition>();

        while (lexer.nextCharacterIs(VARIABLE_PREFIX)) {
            String variable = lexer.parseVariable().getValue();
            lexer.assumeToken(':');

            ValueExpression value = parseValue();
            lexer.assumeToken(';');

            result.add(new VariableDefinition(variable, value));
        }

        return result;
    }

    // ruleSetTemplate:
    //      selectors { variableDefinition* attributes ruleSetTemplate* }
    private List<RuleSetTemplate> parseRuleSetTemplates() {
        List<RuleSetTemplate> result = new ArrayList<RuleSetTemplate>();

        while (lexer.nextCharacterIs(SELECTOR_START)) {
            List<Selector> selectors = selectorParser.parseSelectors();

            lexer.assumeToken('{');

            List<VariableDefinition> bindings = parseVariableDefinitions();
            List<AttributeTemplate> attributes = parseAttributes();
            List<RuleSetTemplate> children = parseRuleSetTemplates();

            lexer.assumeToken('}');

            result.add(new RuleSetTemplate(selectors, bindings, attributes, children));
        }

        return result;
    }

    // attributes:
    //      ';' *
    //    | attribute (';' attribute)* ';'*
    private List<AttributeTemplate> parseAttributes() {
        List<AttributeTemplate> result = new ArrayList<AttributeTemplate>();

        while (isStartOfAttribute()) {
            result.add(parseAttribute());

            if (lexer.consumeTokenIf(';')) {
            } else {
                break;
            }
        }

        while (lexer.consumeTokenIf(';')) {
            // nada
        }

        return result;
    }

    private boolean isStartOfAttribute() {
        // We need some look-ahead when deciding whether to parse more attributes or not.
        // If we have an identifier followed by colon, then we have an attribute. Just an
        // identifier could a selector in start of a nested rule. The following is a very
        // crude way of performing the look-ahead, but works..
        int position = lexer.savePosition();
        try {
            lexer.parseIdentifier();
            lexer.assumeToken(':');

            // We have an identifier followed by colon, which would seem to be an
            // attribute, but actually we might be in a nested rule where we colon
            // in the selector (such as "foo:hover { ... }"). Therefore, we'll go
            // back to the start and try to parse this as selector followed by block.
            // Only if that fails, we have an identifier. Phew..

            lexer.restorePosition(position);
            try {
                selectorParser.parseSelectors();
                lexer.assumeToken('{');
                return false;

            } catch (ParseException e) {
                return true;
            }

        } catch (ParseException e) {
            return false;
        } finally {
            lexer.restorePosition(position);
        }
    }

    // attribute:
    //      identifier ':' attributeValues '!important'?
    private AttributeTemplate parseAttribute() {
        String identifier = lexer.parseIdentifier().getValue();
        lexer.assumeToken(':');
        List<ValueExpression> values = parseValues();
        boolean important = parseImportant();

        return new AttributeTemplate(identifier, values, important);
    }

    private boolean parseImportant() {
        if (lexer.consumeTokenIf('!')) {
            if (lexer.parseIdentifier().getValue().equals("important"))
                return true;
            else
                throw lexer.parseError("important");
        } else
            return false;
    }

    private List<ValueExpression> parseValues() {
        List<ValueExpression> result = new ArrayList<ValueExpression>();
        result.add(parseValue());

        while (lexer.nextCharacterIs(VALUE_START))
            result.add(parseValue());

        return result;
    }

    // value:
    //      expression (',' expression)*
    private ValueExpression parseValue() {
        ValueExpression exp = parseExpression();
        if (lexer.nextCharacterIs(',')) {
            List<ValueExpression> exps = new ArrayList<ValueExpression>();
            exps.add(exp);
            while (lexer.consumeTokenIf(','))
                exps.add(parseExpression());

            return ValueExpression.list(exps);
        } else {
            return exp;
        }
    }

    // expression:
    //      term ('+'|'-' term)*
    private ValueExpression parseExpression() {
        ValueExpression term = parseTerm();

        while (true) {
            if (lexer.nextCharacterIs('+')) {
                SourceRange range = lexer.assumeTokenWithSource('+');
                ValueExpression t2 = parseTerm();
                term = ValueExpression.binary(term, BinaryOperator.ADD, t2, range);
            } else if (lexer.nextCharacterIs('-')) {
                SourceRange range = lexer.assumeTokenWithSource('-');
                ValueExpression t2 = parseTerm();
                term = ValueExpression.binary(term, BinaryOperator.SUBTRACT, t2, range);
            } else {
                break;
            }
        }

        return term;
    }

    // term:
    //      factor ('*'|'/' factor)*
    private ValueExpression parseTerm() {
        ValueExpression term = parseFactor();

        while (true) {
            if (lexer.nextCharacterIs('*')) {
                SourceRange range = lexer.assumeTokenWithSource('*');
                ValueExpression t2 = parseFactor();
                term = ValueExpression.binary(term, BinaryOperator.MULTIPLY, t2, range);
            } else if (lexer.nextCharacterIs('/')) {
                SourceRange range = lexer.assumeTokenWithSource('/');
                ValueExpression t2 = parseFactor();
                term = ValueExpression.binary(term, BinaryOperator.DIVIDE, t2, range);
            } else {
                break;
            }
        }

        return term;
    }

    // factor:
    //      variable | '-' factor | '(' expression ')' | literalOrApply
    private ValueExpression parseFactor() {
        if (lexer.nextCharacterIs(VARIABLE_PREFIX)) {
            Token<String> token = lexer.parseVariable();
            return ValueExpression.variable(token.getValue(), token.getRange());

        } else if (lexer.nextCharacterIs('-')) {
            SourceRange range = lexer.assumeTokenWithSource('-');
            ValueExpression exp = parseFactor();
            return ValueExpression.binary(ValueExpression.ZERO, BinaryOperator.SUBTRACT, exp, range);

        } else if (lexer.consumeTokenIf('(')) {
            ValueExpression exp = parseExpression();
            lexer.assumeToken(')');
            return exp;

        } else {
            return parseLiteralOrApply();
        }
    }

    // literalOrApply:
    //      number | color | string | identifier '(' args ')' | identifier
    private ValueExpression parseLiteralOrApply() {
        if (lexer.nextCharacterIs(CharacterClass.DIGIT)) {
            return ValueExpression.literal(CSSValue.amount(lexer.parseDimension()));

        } else if (lexer.nextCharacterIs('#')) {
            return ValueExpression.literal(lexer.parseHexColor());

        } else if (lexer.nextCharacterIs(CharacterClass.QUOTE)) {
            return ValueExpression.literal(CSSValue.string(lexer.parseString()));

        } else {
            Token<String> id = lexer.parseIdentifier();
            if (lexer.consumeTokenIf('(')) {

                List<ValueExpression> args = new ArrayList<ValueExpression>();
                if (lexer.nextCharacterIs(LITERAL_START)) {
                    args.add(parseLiteralOrApply());
                    while (lexer.consumeTokenIf(','))
                        args.add(parseLiteralOrApply());
                }

                lexer.assumeToken(')');
                return ValueExpression.apply(id.getValue(), args, id.getRange());
            } else {
                return ValueExpression.literal(CSSValue.token(id.getValue()));
            }
        }
    }
}

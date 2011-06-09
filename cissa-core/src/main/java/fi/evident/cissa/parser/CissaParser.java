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

import fi.evident.cissa.model.*;
import fi.evident.cissa.template.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.isLetter;

public final class CissaParser {

    private final Lexer lexer;

    public CissaParser(String source) {
        this.lexer = new Lexer(source);
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

        while (lexer.nextCharacterIs('@')) {
            String variable = lexer.parseVariable().getValue();
            lexer.consumeToken(":");

            ValueExpression value = parseValue();
            lexer.consumeToken(";");

            result.add(new VariableDefinition(variable, value));
        }

        return result;
    }

    // ruleSetTemplate:
    //      selectors { variableDefinition* attributes ruleSetTemplate* }
    private List<RuleSetTemplate> parseRuleSetTemplates() {
        List<RuleSetTemplate> result = new ArrayList<RuleSetTemplate>();

        while (lexer.nextCharacterIsNot('}')) {
            List<Selector> selectors = parseSelectors();

            lexer.consumeToken("{");

            List<VariableDefinition> bindings = parseVariableDefinitions();
            List<AttributeTemplate> attributes = parseAttributes();
            List<RuleSetTemplate> children = parseRuleSetTemplates();

            lexer.consumeToken("}");

            result.add(new RuleSetTemplate(selectors, bindings, attributes, children));
        }

        return result;
    }

    // selectors:
    //      selector (',' selector)*
    private List<Selector> parseSelectors() {
        List<Selector> result = new ArrayList<Selector>();

        // TODO: idiotic implementation but with current functionality the output is identical
        // even though we don't parse the selectors properly
        StringBuilder sb = new StringBuilder();
        while (lexer.nextCharacterIsNot('{')) {
            if (lexer.consumeTokenIf(",")) {
                String selector = sb.toString().trim();

                if (selector.isEmpty())
                    throw lexer.parseError("selector");

                result.add(Selector.simple(selector));
                sb.setLength(0);
            } else {
                sb.append(lexer.read());
            }
        }

        String selector = sb.toString().trim();
        if (!selector.isEmpty())
            result.add(Selector.simple(selector));

        return result;
    }

    // attributes:
    //      ';' *
    //    | attribute (';' attribute)* ';'*
    private List<AttributeTemplate> parseAttributes() {
        List<AttributeTemplate> result = new ArrayList<AttributeTemplate>();

        while (isStartOfAttribute()) {
            result.add(parseAttribute());

            if (lexer.consumeTokenIf(";")) {
            } else {
                break;
            }
        }

        while (lexer.consumeTokenIf(";")) {
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
            lexer.consumeToken(":");

            // We have an identifier followed by colon, which would seem to be an
            // attribute, but actually we might be in a nested rule where we colon
            // in the selector (such as "foo:hover { ... }"). Therefore, we'll go
            // back to the start and try to parse this as selector followed by block.
            // Only if that fails, we have an identifier. Phew..

            lexer.restorePosition(position);
            try {
                parseSelectors();
                lexer.consumeToken("{");
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
        String identifier = lexer.parseIdentifier();
        lexer.consumeToken(":");
        List<ValueExpression> values = parseValues();
        boolean important = lexer.consumeTokenIf("!important");

        return new AttributeTemplate(identifier, values, important);
    }

    private List<ValueExpression> parseValues() {
        List<ValueExpression> result = new ArrayList<ValueExpression>();
        result.add(parseValue());
        while (lexer.nextCharacterIsNot(';') && lexer.nextCharacterIsNot('}') && lexer.nextCharacterIsNot('!'))
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
            while (lexer.consumeTokenIf(","))
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
                SourceRange range = lexer.consumeTokenWithSource("+");
                ValueExpression t2 = parseTerm();
                term = ValueExpression.binary(term, BinaryOperator.ADD, t2, range);
            } else if (lexer.nextCharacterIs('-')) {
                SourceRange range = lexer.consumeTokenWithSource("-");
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
                SourceRange range = lexer.consumeTokenWithSource("*");
                ValueExpression t2 = parseFactor();
                term = ValueExpression.binary(term, BinaryOperator.MULTIPLY, t2, range);
            } else if (lexer.nextCharacterIs('/')) {
                SourceRange range = lexer.consumeTokenWithSource("/");
                ValueExpression t2 = parseFactor();
                term = ValueExpression.binary(term, BinaryOperator.DIVIDE, t2, range);
            } else {
                break;
            }
        }

        return term;
    }

    // factor:
    //      variable | literal | '-' factor | '(' expression ')'
    private ValueExpression parseFactor() {
        if (lexer.nextCharacterIs('@')) {
            Token<String> token = lexer.parseVariable();
            return ValueExpression.variable(token.getValue(), token.getRange());

        } else if (lexer.nextCharacterIs('-')) {
            SourceRange range = lexer.consumeTokenWithSource("-");
            ValueExpression exp = parseFactor();
            return ValueExpression.binary(ValueExpression.ZERO, BinaryOperator.SUBTRACT, exp, range);

        } else if (lexer.consumeTokenIf("(")) {
            ValueExpression exp = parseExpression();
            lexer.consumeToken(")");
            return exp;

        } else {
            CSSValue literal = parseLiteral();
            return ValueExpression.literal(literal);
        }
    }

    // literal:
    //      number | color | string | identifier '(' args ')' | identifier
    private CSSValue parseLiteral() {
        if (lexer.nextCharacterIsDigit()) {
            return CSSValue.amount(lexer.parseDimension());

        } else if (lexer.nextCharacterIs('#')) {
            return lexer.parseHexColor();

        } else if (lexer.nextCharacterIs('"') || lexer.nextCharacterIs('\'')) {
            return CSSValue.string(lexer.readString());

        } else {
            String id = lexer.parseIdentifier();
            if (lexer.consumeTokenIf("(")) {

                List<CSSValue> args = new ArrayList<CSSValue>();
                if (lexer.nextCharacterIsNot(')')) {
                    args.add(parseLiteral());
                    while (lexer.consumeTokenIf(","))
                        args.add(parseLiteral());
                }

                lexer.consumeToken(")");
                return CSSValue.apply(id, args);
            } else {
                return CSSValue.token(id);
            }
        }
    }
}

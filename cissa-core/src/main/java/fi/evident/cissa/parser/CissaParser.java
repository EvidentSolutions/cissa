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
import fi.evident.cissa.template.*;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Pair;
import org.codehaus.jparsec.functors.Tuple3;

import java.util.List;

import static fi.evident.cissa.parser.CissaLexer.*;
import static org.codehaus.jparsec.Parsers.sequence;
import static org.codehaus.jparsec.Parsers.tuple;

public final class CissaParser {

    public static DocumentTemplate parse(String s) {
        return document().parse(s);
    }

    // document:
    //      spaces* variableDefinitions ruleSet*
    private static Parser<DocumentTemplate> document() {
        Parser<List<VariableDefinition>> variables = sequence(optSpaces, variableDefinition().many());

        return tuple(variables, ruleSet().many()).map(new Map<Pair<List<VariableDefinition>, List<RuleSetTemplate>>, DocumentTemplate>() {
            public DocumentTemplate map(Pair<List<VariableDefinition>, List<RuleSetTemplate>> pair) {
                return new DocumentTemplate(pair.a, pair.b);
            }
        });
    }

    // ruleSet:
    //      selectors { variableDefinitions attributes ruleSet* }
    private static Parser<RuleSetTemplate> ruleSet() {
        Parser.Reference<RuleSetTemplate> self = Parser.newReference();

        Parser<Tuple3<List<VariableDefinition>, List<AttributeTemplate>, List<RuleSetTemplate>>> attributes = inBraces(tuple(variableDefinition().many(), attributes(), self.lazy().many()));

        self.set(tuple(SelectorParser.selectors(), attributes).map(new Map<Pair<List<Selector>, Tuple3<List<VariableDefinition>, List<AttributeTemplate>, List<RuleSetTemplate>>>, RuleSetTemplate>() {
            public RuleSetTemplate map(Pair<List<Selector>, Tuple3<List<VariableDefinition>, List<AttributeTemplate>, List<RuleSetTemplate>>> p) {
                return new RuleSetTemplate(p.a, p.b.a, p.b.b, p.b.c);
            }
        }));

        return self.get();
    }

    // variableDefinition:
    //      variable ':' value ';'
    private static Parser<VariableDefinition> variableDefinition() {
        return tuple(variable().followedBy(colon), ExpressionParser.value().followedBy(semicolon)).map(new Map<Pair<String, ValueExpression>, VariableDefinition>() {
            public VariableDefinition map(Pair<String, ValueExpression> p) {
                return new VariableDefinition(p.a, p.b);
            }
        });
    }

    // attributes:
    //      ';' ?
    //    | attribute (';' attribute)* ';'?
    private static Parser<List<AttributeTemplate>> attributes() {
        return attribute().atomic().sepBy(semicolon).followedBy(semicolon.optional());
    }

    private static Parser<AttributeTemplate> attribute() {
        Parser<Boolean> important = token("!important").retn(true).optional(false);

        return tuple(token(identifier).followedBy(colon), attributeValues().followedBy(optSpaces), important).map(new Map<Tuple3<String, List<ValueExpression>, Boolean>, AttributeTemplate>() {
            public AttributeTemplate map(Tuple3<String, List<ValueExpression>, Boolean> t) {
                return new AttributeTemplate(t.a, t.b, t.c);
            }
        }).label("attribute");
    }

    private static Parser<List<ValueExpression>> attributeValues() {
        return ExpressionParser.value().sepBy(optSpaces);
    }
}

package fi.evident.cissa.parser;

import fi.evident.cissa.model.CSSValue;
import fi.evident.cissa.model.Dimension;
import fi.evident.cissa.model.DimensionUnit;
import fi.evident.cissa.model.Selector;
import fi.evident.cissa.template.*;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Pair;
import org.codehaus.jparsec.functors.Tuple3;
import org.codehaus.jparsec.pattern.Pattern;
import org.codehaus.jparsec.pattern.Patterns;

import java.util.List;

import static org.codehaus.jparsec.Parsers.sequence;
import static org.codehaus.jparsec.Parsers.tuple;
import static org.codehaus.jparsec.Scanners.pattern;
import static org.codehaus.jparsec.pattern.Patterns.string;

public class CissaParser {

    private static final Parser<?> optSpaces =
        Parsers.or(Scanners.WHITESPACES, Scanners.JAVA_LINE_COMMENT, Scanners.JAVA_BLOCK_COMMENT).many_();
    private static final Parser<?> colon = token(Scanners.isChar(':'));
    private static final Parser<?> comma = token(Scanners.isChar(','));
    private static final Parser<?> semicolon = token(Scanners.isChar(';'));
    private static final Parser<?> openingBrace = token(Scanners.isChar('{'));
    private static final Parser<?> closingBrace = token(Scanners.isChar('}'));
    private static final Parser<String> identifier =
        Scanners.pattern(Patterns.regex("-?[a-zA-Z][-a-zA-Z0-9_]*"), "identifier").source();


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
        Parser<Pair<List<VariableDefinition>, List<AttributeTemplate>>> attributes = inBraces(tuple(variableDefinition().many(), attributes()));

        return tuple(selectors(), attributes).map(new Map<Pair<List<Selector>, Pair<List<VariableDefinition>, List<AttributeTemplate>>>, RuleSetTemplate>() {
            public RuleSetTemplate map(Pair<List<Selector>, Pair<List<VariableDefinition>, List<AttributeTemplate>>> pair) {
                return new RuleSetTemplate(pair.a, pair.b.a, pair.b.b);
            }
        });
    }

    private static Parser<VariableDefinition> variableDefinition() {
        return tuple(variable().followedBy(colon), value().followedBy(semicolon)).map(new Map<Pair<String, ValueExpression>, VariableDefinition>() {
            public VariableDefinition map(Pair<String, ValueExpression> p) {
                return new VariableDefinition(p.a, p.b);
            }
        });
    }

    private static Parser<String> variable() {
        return token(sequence(Scanners.isChar('@'), identifier)).label("variable");
    }

    private static Parser<List<AttributeTemplate>> attributes() {
        return attribute().sepBy(semicolon).followedBy(optSpaces).followedBy(semicolon.optional());
    }

    private static Parser<AttributeTemplate> attribute() {
        return tuple(identifier.followedBy(colon), attributeValues()).map(new Map<Pair<String, List<ValueExpression>>, AttributeTemplate>() {
            public AttributeTemplate map(Pair<String, List<ValueExpression>> pair) {
                return new AttributeTemplate(pair.a, pair.b, false);
            }
        }).label("attribute");
    }

    private static Parser<List<ValueExpression>> attributeValues() {
        return value().sepBy(optSpaces);
    }

    private static Parser<ValueExpression> value() {
        return factor();
    }

    private static Parser<ValueExpression> factor() {
        return variableValue().or(literalExpression());
    }

    private static Parser<ValueExpression> variableValue() {
        return variable().map(new Map<String, ValueExpression>() {
            public ValueExpression map(String s) {
                return ValueExpression.variable(s);
            }
        });
    }

    private static Parser<ValueExpression> literalExpression() {
        Parser<CSSValue> literal = tokenValue().or(numericValue());
        return literal.map(new Map<CSSValue, ValueExpression>() {
            public ValueExpression map(CSSValue value) {
                return ValueExpression.literal(value);
            }
        });
    }

    private static Parser<CSSValue> numericValue() {
        return dimension().map(new Map<Dimension, CSSValue>() {
            public CSSValue map(Dimension value) {
                return CSSValue.amount(value);
            }
        });
    }

    private static Parser<Dimension> dimension() {
        return tuple(Scanners.DECIMAL, dimensionUnit()).map(new Map<Pair<String, DimensionUnit>, Dimension>() {
            public Dimension map(Pair<String, DimensionUnit> p) {
                return Dimension.dimension(p.a, p.b);
            }
        });
    }

    private static Parser<DimensionUnit> dimensionUnit() {
        // TODO: all units here
        Pattern units = Patterns.or(string("px"), string("pt"));
        return pattern(units.optional(), "dimension unit").source().map(new Map<String, DimensionUnit>() {
            public DimensionUnit map(String s) {
                return s.isEmpty() ? DimensionUnit.EMPTY : DimensionUnit.forName(s);
            }
        });
    }

    private static Parser<CSSValue> tokenValue() {
        return identifier.map(new Map<String, CSSValue>() {
            public CSSValue map(String s) {
                return CSSValue.token(s);
            }
        });
    }

    // selectors:
    //      selector
    //    | selector, selectors
    private static Parser<List<Selector>> selectors() {
        return commaSep(selector()).followedBy(optSpaces);
    }

    private static Parser<Selector> selector() {
        Parser<String> simpleSelector = Scanners.isChar('*').source().or(identifier);

        return simpleSelector.map(new Map<String, Selector>() {
            public Selector map(String s) {
                return new Selector(s);
            }
        });
    }

    private static <T> Parser<T> inBraces(Parser<T> p) {
        return Parsers.tuple(openingBrace, p, closingBrace).map(new Map<Tuple3<Object, T, Object>, T>() {
            public T map(Tuple3<Object, T, Object> p) {
                return p.b;
            }
        });
    }

    private static <T> Parser<T> token(Parser<T> p) {
        return p.followedBy(optSpaces);
    }

    private static <T> Parser<List<T>> commaSep(Parser<T> p) {
        return p.sepBy(comma);
    }
}

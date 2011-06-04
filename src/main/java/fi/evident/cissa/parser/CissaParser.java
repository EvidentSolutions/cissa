package fi.evident.cissa.parser;

import fi.evident.cissa.Dimension;
import fi.evident.cissa.DimensionUnit;
import fi.evident.cissa.model.*;
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


    public static Document parse(String s) {
        return document().parse(s);
    }

    // document:
    //      spaces* variableDefinitions ruleSet*
    private static Parser<Document> document() {
        // TODO: add variable definitions
        return sequence(optSpaces, ruleSet().many()).map(new Map<List<RuleSet>, Document>() {
            public Document map(List<RuleSet> ruleSets) {
                return new Document(ruleSets);
            }
        });
    }

    // ruleSet:
    //      selectors { variableDefinitions attributes ruleSet* }
    private static Parser<RuleSet> ruleSet() {
        Parser<List<Selector>> selectors = selectors().followedBy(optSpaces).followedBy(openingBrace).followedBy(optSpaces).label("selectors");
        Parser<List<Attribute>> attributes = attributes().followedBy(optSpaces).followedBy(closingBrace).label("attributes");

        return tuple(selectors, attributes).map(new Map<Pair<List<Selector>, List<Attribute>>, RuleSet>() {
            public RuleSet map(Pair<List<Selector>, List<Attribute>> pair) {
                return new RuleSet(pair.a, pair.b);
            }
        }).label("rule-set");
    }

    private static Parser<List<Attribute>> attributes() {
        return attribute().sepBy(semicolon);
    }

    private static Parser<Attribute> attribute() {
        return tuple(identifier.followedBy(colon), attributeValues()).map(new Map<Pair<String, List<CSSValue>>, Attribute>() {
            public Attribute map(Pair<String, List<CSSValue>> pair) {
                return new Attribute(pair.a, pair.b, false);
            }
        }).label("attribute");
    }

    private static Parser<List<CSSValue>> attributeValues() {
        return attributeValue().sepBy(optSpaces);
    }

    private static Parser<CSSValue> attributeValue() {
        return tokenValue().or(numericValue()).label("attribute value");
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
        return commaSep(selector());
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

package fi.evident.cissa.parser;

import fi.evident.cissa.model.*;
import fi.evident.cissa.template.*;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Pair;
import org.codehaus.jparsec.functors.Tuple3;
import org.codehaus.jparsec.pattern.Pattern;
import org.codehaus.jparsec.pattern.Patterns;

import java.util.ArrayList;
import java.util.List;

import static org.codehaus.jparsec.Parsers.*;
import static org.codehaus.jparsec.Scanners.*;
import static org.codehaus.jparsec.pattern.Patterns.regex;
import static org.codehaus.jparsec.pattern.Patterns.string;

public class CissaParser {

    private static final Parser<?> optSpaces =
        or(Scanners.WHITESPACES, Scanners.JAVA_LINE_COMMENT, Scanners.JAVA_BLOCK_COMMENT).many_();
    private static final Parser<?> colon = token(':');
    private static final Parser<?> comma = token(',');
    private static final Parser<?> semicolon = token(';');
    private static final Parser<?> openingBrace = token('{');
    private static final Parser<?> closingBrace = token('}');
    private static final Parser<?> openingParen = token('(');
    private static final Parser<?> closingParen = token(')');
    private static final Parser<?> openingBracket = token('[');
    private static final Parser<?> closingBracket = token(']');

    private static final Parser<BinaryOperator> multiply = token('*').retn(BinaryOperator.MULTIPLY);
    private static final Parser<BinaryOperator> divide = token('/').retn(BinaryOperator.DIVIDE);
    private static final Parser<BinaryOperator> plus = token('+').retn(BinaryOperator.ADD);
    private static final Parser<BinaryOperator> minus = token('-').retn(BinaryOperator.SUBTRACT);
    private static final Parser<String> combinator = token(isChar('+').or(isChar('>')).source());

    private static final Parser<String> identifier =
        Scanners.pattern(regex("-?[a-zA-Z][-a-zA-Z0-9_]*"), "identifier").source();


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

        self.set(tuple(selectors(), attributes).map(new Map<Pair<List<Selector>, Tuple3<List<VariableDefinition>, List<AttributeTemplate>, List<RuleSetTemplate>>>, RuleSetTemplate>() {
            public RuleSetTemplate map(Pair<List<Selector>, Tuple3<List<VariableDefinition>, List<AttributeTemplate>, List<RuleSetTemplate>>> p) {
                return new RuleSetTemplate(p.a, p.b.a, p.b.b, p.b.c);
            }
        }));

        return self.get();
    }

    private static Parser<VariableDefinition> variableDefinition() {
        return tuple(variable().followedBy(colon), value().followedBy(semicolon)).map(new Map<Pair<String, ValueExpression>, VariableDefinition>() {
            public VariableDefinition map(Pair<String, ValueExpression> p) {
                return new VariableDefinition(p.a, p.b);
            }
        });
    }

    private static Parser<String> variable() {
        return token(sequence(isChar('@'), identifier)).label("variable");
    }

    private static Parser<List<AttributeTemplate>> attributes() {
        return attribute().atomic().sepBy(semicolon).followedBy(optSpaces).followedBy(semicolon.optional());
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
        return value().sepBy(optSpaces);
    }

    private static Parser<ValueExpression> value() {
        return tuple(exp(), sequence(comma, exp()).many()).map(new Map<Pair<ValueExpression, List<ValueExpression>>, ValueExpression>() {
            public ValueExpression map(Pair<ValueExpression, List<ValueExpression>> pair) {
                if (pair.b.isEmpty())
                    return pair.a;

                List<ValueExpression> exps = new ArrayList<ValueExpression>(1 + pair.b.size());
                exps.add(pair.a);
                exps.addAll(pair.b);
                return ValueExpression.list(exps);
            }
        });
    }

    private static Parser<ValueExpression> exp() {
        Parser.Reference<ValueExpression> exp = Parser.newReference();
        Parser.Reference<ValueExpression> unaryNeg = Parser.newReference();

        Parser<ValueExpression> factor =
            or(variableValue(), literalExpression(), unaryNeg.lazy(), inParens(exp.lazy()));

        unaryNeg.set(sequence(minus, factor).map(new Map<ValueExpression, ValueExpression>() {
            public ValueExpression map(ValueExpression exp) {
                return ValueExpression.binary(ValueExpression.ZERO, BinaryOperator.SUBTRACT, exp);
            }
        }));

        Parser<ValueExpression> term =
            tuple(factor, tuple(multiply.or(divide), factor).many()).map(new Map<Pair<ValueExpression, List<Pair<BinaryOperator, ValueExpression>>>, ValueExpression>() {
                public ValueExpression map(Pair<ValueExpression, List<Pair<BinaryOperator, ValueExpression>>> p) {
                    ValueExpression l = p.a;
                    for (Pair<BinaryOperator,ValueExpression> pp : p.b)
                        l = ValueExpression.binary(l, pp.a, pp.b);
                    return l;
                }
            });

        exp.set(tuple(term, tuple(plus.or(minus), term).many()).map(new Map<Pair<ValueExpression, List<Pair<BinaryOperator, ValueExpression>>>, ValueExpression>() {
            public ValueExpression map(Pair<ValueExpression, List<Pair<BinaryOperator, ValueExpression>>> p) {
                ValueExpression l = p.a;
                for (Pair<BinaryOperator,ValueExpression> pp : p.b)
                    l = ValueExpression.binary(l, pp.a, pp.b);
                return l;
            }
        }));
        return exp.get();
    }

    private static Parser<ValueExpression> variableValue() {
        return variable().map(new Map<String, ValueExpression>() {
            public ValueExpression map(String s) {
                return ValueExpression.variable(s);
            }
        });
    }

    private static Parser<ValueExpression> literalExpression() {
        return literal().map(new Map<CSSValue, ValueExpression>() {
            public ValueExpression map(CSSValue value) {
                return ValueExpression.literal(value);
            }
        });
    }

    private static Parser<CSSValue> literal() {
        Parser.Reference<CSSValue> literal = Parser.newReference();
        literal.set(or(numberLiteral(), colorLiteral(), stringLiteral(), builtinFunctionLiteral(literal.lazy()), identifierLiteral()));
        return literal.get();
    }

    private static Parser<CSSValue> colorLiteral() {
        Parser<CSSValue> hashColor =
            pattern(regex("#([0-9a-zA-Z]{6}|[0-9a-zA-Z]{3})"), "hash-color").source().map(new Map<String, CSSValue>() {
                public CSSValue map(String s) {
                    return CSSColor.parse(s);
                }
            });

        // TODO: rgb colors of form rgb(1,2,3)

        return token(hashColor);
    }

    private static Parser<CSSValue> stringLiteral() {
        return or(DOUBLE_QUOTE_STRING, SINGLE_QUOTE_STRING).map(new Map<String, CSSValue>() {
            public CSSValue map(String s) {
                return CSSValue.string(s.substring(1, s.length()-1));
            }
        }).label("string");
    }

    private static Parser<CSSValue> builtinFunctionLiteral(Parser<CSSValue> literal) {
        Parser<String> start = token(identifier.followedBy(isChar('(')));
        Parser<List<CSSValue>> args = literal.many();
        Parser<String> end = token(")");

        return pair(start, args.followedBy(end)).map(new Map<Pair<String, List<CSSValue>>, CSSValue>() {
            public CSSValue map(Pair<String, List<CSSValue>> p) {
                return CSSValue.apply(p.a, p.b);
            }
        });
    }

    private static Parser<CSSValue> numberLiteral() {
        return dimension().map(new Map<Dimension, CSSValue>() {
            public CSSValue map(Dimension value) {
                return CSSValue.amount(value);
            }
        });
    }

    private static Parser<Dimension> dimension() {
        return token(tuple(Scanners.DECIMAL, dimensionUnit())).map(new Map<Pair<String, DimensionUnit>, Dimension>() {
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

    private static Parser<CSSValue> identifierLiteral() {
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
        Parser.Reference<Selector> self = Parser.newReference();
        Parser<String> comb = combinator.optional("");

        self.set(tuple(simpleSelector(), tuple(comb, self.lazy()).optional()).map(new Map<Pair<Selector, Pair<String, Selector>>, Selector>() {
            public Selector map(Pair<Selector, Pair<String, Selector>> p) {
                return (p.b == null)
                    ? p.a
                    : Selector.compound(p.a, p.b.a, p.b.b);
            }
        }));

        return self.get();
    }

    private static Parser<Selector> simpleSelector() {
        Parser<String> idRef = Scanners.pattern(regex("#[-a-zA-Z0-9_]*"), "idref").source();
        Parser<String> classRef = sequence(isChar('.'), identifier).source();
        Parser<String> pseudo = sequence(isChar(':'), identifier).source();
        Parser<String> spec = or(idRef, classRef, attrib(), pseudo);
        Parser<String> elementName = or(isChar('*').source(), identifier);

        Parser<Selector> withElement =
           tuple(elementName, spec.many()).map(new Map<Pair<String, List<String>>, Selector>() {
               public Selector map(Pair<String, List<String>> p) {
                   return Selector.simple(p.a, p.b);
               }
           });

        Parser<Selector> withoutElement =
            spec.many1().map(new Map<List<String>, Selector>() {
                public Selector map(List<String> specs) {
                    return Selector.simple("", specs);
                }
            });

        return token(or(withElement, withoutElement));
    }

    private static Parser<String> attrib() {
        Parser<String> attributeValue = token(identifier); // TODO: or string

        Parser<String> op = or(token("="), token("~="), token("!="));

        Parser<String> rest = pair(op, attributeValue.optional("")).map(new Map<Pair<String, String>, String>() {
            public String map(Pair<String, String> p) {
                return p.a + p.b;
            }
        });

        Parser<String> contents = pair(token(identifier), rest.optional("")).map(new Map<Pair<String, String>, String>() {
            public String map(Pair<String, String> p) {
                return p.a + p.b;
            }
        });

        return inBrackets(contents).map(new Map<String, String>() {
            public String map(String s) {
                return "[" + s + "]";
            }
        });
    }

    private static <T> Parser<T> inBraces(Parser<T> p) {
        return between(openingBrace, p, closingBrace);
    }

    private static <T> Parser<T> inParens(Parser<T> p) {
        return between(openingParen, p, closingParen);
    }

    private static <T> Parser<T> inBrackets(Parser<T> p) {
        return between(openingBracket, p, closingBracket);
    }

    private static <T> Parser<T> token(Parser<T> p) {
        return p.followedBy(optSpaces);
    }

    private static Parser<?> token(char c) {
        return token(isChar(c));
    }

    private static Parser<String> token(String s) {
        return token(Scanners.string(s).source());
    }

    private static <T> Parser<List<T>> commaSep(Parser<T> p) {
        return p.sepBy(comma);
    }
}
